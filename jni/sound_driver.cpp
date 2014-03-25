/*
 Copyright (c) 2012, Spotify AB
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of Spotify AB nor the names of its contributors may
 be used to endorse or promote products derived from this software
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL SPOTIFY AB BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Simple layer between libspotify and OpenSL.
 *
 * Libspotify delivers raw PCM data in the music_delivery callback. This data will be put in one of two buffers and
 * enqueued to openSL through the enqueu method. There is only two buffers and changing the size of them will affect the
 * application a lot. OpenSL will tell when it has consumed one buffer through the bqPlayerCallback callback. Since the buffers
 * are shared among two different threads (libspotify and internal openSL thread) they share a mutex. The size of the buffers are
 * used to notify when a buffer is filled. A buffer with a nonzero size is assumed to be filled.
 *
 * The two buffers have a fixed size which could casue problems if libspotify is delivering more data then it fits. In this case the
 * parts that cant fit is rejected which casues sound-glitches, but should be rare if the buffers are big (half a second or more).
 *
 * When intitializing the soundbuffer the PCM format is set in a static fasion. This should be changed if Spotify supports other PCM
 * formats.
 */

#include <assert.h>
#include <pthread.h>
// for native audio
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <string.h>
#include <sstream>

#include <api.h>

#include "tasks.h"
#include "run_loop.h"
#include "logger.h"
#include "jni_glue.h"
#include "fft.h"
#include "complex.h"
#include <stdlib.h>
#include "kiss_fft.h"

static SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;

// Size of the buffer is one second divided by SECOND_DEVIDER. Increase the number to make the buffer smaller.
// Bigger buffers will produce slow feedback for actions like pause and seek.
static const int SECOND_DEVIDER = 7;
static const int SAMPLE_RATE = 44100;
static const int SAMPLES_PER_BUFFER = 6144;

static const int NR_CHANNELS = 2;

// Make buffers four times as big because we guess that music_delivery never delivers more
// pcm data. If it does the overflowing data will not be put in the buffer, resulting in
// temporary audio weirdness
static const int BUFFER_SIZE = SAMPLES_PER_BUFFER * sizeof(int16_t) * NR_CHANNELS * 4;

// The two sound buffers
static int16_t buffer1[BUFFER_SIZE];
static int16_t buffer2[BUFFER_SIZE];

// buffers and sizes shared by libspotify and openSL
static int buffer1_size = 0;
static int buffer2_size = 0;
static int position = 0;
static short *next_buffer = buffer2;
static int *current_buffer_size = &buffer1_size;
static int *next_buffer_size = &buffer2_size;

static pthread_mutex_t g_buffer_mutex;
bool beatOccurrence;

static kiss_fft_cfg cfg;

static const int EVERY_NTH_SAMPLE = 1;

/// Round up to next higher power of 2 (return x if it's already a power
/// of 2).
inline int
pow2roundup (int x)
{
    if (x < 0)
        return 0;
    --x;
    x |= x >> 1;
    x |= x >> 2;
    x |= x >> 4;
    x |= x >> 8;
    x |= x >> 16;
    return x+1;
}

// contains the last 20 frames (~1 second)
const int HISTORY_LENGTH = 7;
static int history_pos = 0;
static double C = 6;
static int fftBufSize;
const int NUM_BANDS = 128;
static double band_energy_history_r[HISTORY_LENGTH][NUM_BANDS];
static double band_energy_history_l[HISTORY_LENGTH][NUM_BANDS];

void analyze_samples(short *buffer, int size) {
	kiss_fft_cpx fin_l[fftBufSize];
	kiss_fft_cpx fout_l[fftBufSize];
	kiss_fft_cpx fin_r[fftBufSize];
	kiss_fft_cpx fout_r[fftBufSize];

	double freqMagn_l[fftBufSize];
	double freqMagn_r[fftBufSize];

	int band_width = fftBufSize/NUM_BANDS;

	log("%d", fftBufSize);

	memset(fin_r, 0, sizeof(fin_r));
	memset(fin_l, 0, sizeof(fin_l));

	for (int i = 0; i < SAMPLES_PER_BUFFER/EVERY_NTH_SAMPLE/NR_CHANNELS; i++) {
		fin_l[i].r = buffer[2*(i*EVERY_NTH_SAMPLE)];
	}
	for (int i = 0; i < SAMPLES_PER_BUFFER/EVERY_NTH_SAMPLE/NR_CHANNELS; i++) {
		fin_r[i].r = buffer[2*(i*EVERY_NTH_SAMPLE) + 1];
	}

	kiss_fft(cfg, fin_l, fout_l);
	kiss_fft(cfg, fin_r, fout_r);

	for (int i = 0; i < fftBufSize; i++) {
		float im2 = fout_l[i].i*fout_l[i].i;
		float re2 = fout_l[i].r*fout_l[i].r;
		freqMagn_l[i] = im2 + re2;
	}
	for (int i = 0; i < fftBufSize; i++) {
		float im2 = fout_r[i].i*fout_r[i].i;
		float re2 = fout_r[i].r*fout_r[i].r;
		freqMagn_r[i] = im2 + re2;
	}

	bool foundBeat = false;

	int startBand = 0;
	int endBand = NUM_BANDS;

	for (int j = startBand; j < endBand; j++) {
		double instant_energy = 0;

		for (int i = 0; i < band_width; i++) {
			instant_energy += freqMagn_l[j*band_width + i];
		}
		instant_energy /= 1000.;
		band_energy_history_l[history_pos][j] = instant_energy;
		double local_avg_energy = 0;
		for (int i = 0; i < HISTORY_LENGTH; i++) {
			local_avg_energy += band_energy_history_l[i][j];
		}
		local_avg_energy /= (double)HISTORY_LENGTH;

		//double variance = 0;
//		for (int i = 0; i < HISTORY_LENGTH; i++) {
//			double thing = local_avg_energy - band_energy_history_l[i][j];
//			variance += thing*thing;
//		}
		//variance /= (double)HISTORY_LENGTH;
		//log("variance = %f", variance);

		//C = -.0025714*variance + 3.51;

		if (instant_energy > C * local_avg_energy) {
			foundBeat = true;
			log("beat l band %d", j);
		}
	}

	for (int j = startBand; j < endBand; j++) {
		double instant_energy = 0;

		for (int i = 0; i < band_width; i++) {
			instant_energy += freqMagn_r[j*band_width + i];
		}
		instant_energy /= 1000.;
		band_energy_history_r[history_pos][j] = instant_energy;
		double local_avg_energy = 0;
		for (int i = 0; i < HISTORY_LENGTH; i++) {
			local_avg_energy += band_energy_history_r[i][j];
		}
		local_avg_energy /= (double)HISTORY_LENGTH;

		//double variance = 0;
//		for (int i = 0; i < HISTORY_LENGTH; i++) {
//			double thing = local_avg_energy - band_energy_history_l[i][j];
//			variance += thing*thing;
//		}
		//variance /= (double)HISTORY_LENGTH;
		//log("variance = %f", variance);

		//C = -.0025714*variance + 3.51;

		if (instant_energy > C * local_avg_energy) {
			foundBeat = true;
			log("beat right band %d", j);
		}
	}

	if (foundBeat) {
		beatOccurrence = true;
		log("nigga");
	}
	if (++history_pos > HISTORY_LENGTH - 1) {
		history_pos = 0;
	}
}

// Tell openSL to play the filled buffer and switch to filling the other buffer
void enqueue(short *buffer, int size) {
	// Play the buffer and flip to the other buffer

	logPlayback("Consume buffer %d", (buffer == buffer1) ? 1 : 2);
	SLresult result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, buffer, size);
	assert(SL_RESULT_SUCCESS != result);

	current_buffer_size = (buffer == buffer1) ? &buffer1_size : &buffer2_size;
	next_buffer = (buffer == buffer1) ? buffer2 : buffer1;
	next_buffer_size = (buffer == buffer1) ? &buffer2_size : &buffer1_size;



//	for (int i = 0; i < fftBufSize; i++) {
//		log("output: %f", fout[i].r);
//	}

	// FFT values range from 0 Hz to SAMPLE_RATE/2 = 22050 Hz
	// frequency interval is SAMPLE_RATE/(2*SAMPLES_PER_BUFFER) = 2 Hz
	// Take the magnitude of output[i] to get amplitude of corresponding frequency


//	double instant_energy = 0;
//	for (int i = 0; i < SAMPLES_PER_BUFFER; i++) {
//		instant_energy += buffer[i]*buffer[i];
//	}
//	instant_energy /= 100000000000.;
//	energy_history[history_pos] = instant_energy;
//
//	double local_avg_energy = 0;
//	for (int i = 0; i < HISTORY_LENGTH; i++) {
//		local_avg_energy += energy_history[i];
//	}
//	local_avg_energy /= (double)HISTORY_LENGTH;
//
//	double variance = HISTORY_LENGTH;
//	for (int i = 0; i < HISTORY_LENGTH; i++) {
//		double thing = local_avg_energy - energy_history[i];
//		variance += thing*thing;
//	}
//	variance /= (double)HISTORY_LENGTH;
//
//	C = -.0025714*variance + 1.51;
//
//	log("C = %f", C);
//
//	if (instant_energy > C * local_avg_energy) {
//		beatOccurrence = true;
//		log("instant_energy: %f, local_avg_energy: %f", instant_energy, local_avg_energy);
//	}



//	complex pSignal[p];
//	complex output[p];
//
//	for (int i = 0; i < SAMPLES_PER_BUFFER; i++) {
//		pSignal[i].m_re = buffer[i];
//		pSignal[i].m_im = 0;
//	}
//
//
//	CFFT::Forward(pSignal, output, p);

//	for (int i = 0; i < SAMPLES_PER_BUFFER; i++) {
//		log("Input: %f", pSignal[i].m_re);
//		log("Output: %f", output[i].m_re);
//	}


//	int s = size/sizeof(int16_t);
//	log("%d", s);
//	for (int i = 0; i < SAMPLES_PER_BUFFER; i++) {
//		log("value: %.0f, i: %d", pSignal[i].m_re, i);
//	}
//
	}

int music_delivery(sp_session *sess, const sp_audioformat *format, const void *frames, int num_frames) {
	if (num_frames == 0) {
		logPlayback("No more audio");
		return 0; // Audio discontinuity, do nothing
	}

	pthread_mutex_lock(&g_buffer_mutex);

	assert(format->channels == NR_CHANNELS);
	assert(format->sample_rate == SAMPLE_RATE);
	static short *current_buffer = buffer1;
	static int *current_buffer_size = &buffer1_size;

	// Fill the current buffer only if it has been consumed
	if (*current_buffer_size == 0) {

		static int total_samples = 0;

		// fill the current buffer with frames
		int size = num_frames * sizeof(int16_t) * format->channels;

		// if we dont exceed buffer size then dont copy anything more into the buffer (should be rare) and will
		// result in audio loss
		if ((position + size) >= BUFFER_SIZE) {
			log("Buffer to small... Buffer size %d, try to fill with size %d", BUFFER_SIZE, size);
		} else {
			memcpy(current_buffer + position, frames, size);
			// advance by half the size because buffer1 is of size int16_t
			position += size / sizeof(int16_t);
			total_samples += num_frames;
		}

		// If one second of data has been filled that make it available to the consumer and start fill the other buffer
		if (total_samples > SAMPLES_PER_BUFFER) {
			int total_size = total_samples * sizeof(int16_t) * format->channels;

			analyze_samples(current_buffer, total_size);

			// play the buffer if both buffers are empty (no sound is playing)
			if (buffer1_size == 0 && buffer2_size == 0) {
				logPlayback("No sound, play buffer %d", (current_buffer == buffer1) ? 1 : 2);
				enqueue(current_buffer, total_size);

			} else {
				// Make it possible for the consumer to use this buffer
				logPlayback("Produced buffer %d", (current_buffer == buffer1) ? 1 : 2);
			}
			*current_buffer_size = total_size;

			total_samples = 0;
			position = 0;

			// start fill the next buffer
			current_buffer_size = (current_buffer == buffer1) ? &buffer2_size : &buffer1_size;
			current_buffer = (current_buffer == buffer1) ? buffer2 : buffer1;
		}
	} else {
		// if both buffers are filled then tell libspotify it cant get more.
		logPlayback("Both buffers are filled");
		num_frames = 0;
	}



	pthread_mutex_unlock(&g_buffer_mutex);
	return num_frames;

}

// this callback handler is called every time a buffer finishes playing
void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
	static int player_position = 0;
	static int counter = 0;
	pthread_mutex_lock(&g_buffer_mutex);
	assert(bq == bqPlayerBufferQueue);
	assert(NULL == context);

	// reset the buffer that was played so it can be filled again
	*current_buffer_size = 0;
	logPlayback("Buffer %d has been consumed", (next_buffer == buffer1) ? 2 : 1);

	// play next buffer if ready and then flip to the other buffer
	if (*next_buffer_size != 0) {
		enqueue(next_buffer, *next_buffer_size);
	} else {
		logPlayback("There is no new buffer to consume?");
	}
	pthread_mutex_unlock(&g_buffer_mutex);
	if (counter % SECOND_DEVIDER == 0)
		addTask(on_player_position_changed, "player_position_changed");
	counter++;
}

static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;
// output mix interfaces
static SLObjectItf outputMixObject = NULL;
static SLObjectItf bqPlayerObject = NULL;
static SLPlayItf bqPlayerPlay;
void init_audio_player() {

	pthread_mutex_init(&g_buffer_mutex, NULL);

	// create engine
	SLresult result;
	result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);

	// realize the engine
	result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE );
	assert(SL_RESULT_SUCCESS == result);

	// get the engine interface, which is needed in order to create other objects
	result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
	assert(SL_RESULT_SUCCESS == result);

	const SLInterfaceID ids[] = { SL_IID_VOLUME };
	const SLboolean req[] = { SL_BOOLEAN_FALSE };
	result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
	assert(SL_RESULT_SUCCESS == result);
	result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE );
	assert(SL_RESULT_SUCCESS == result);

	// The source is the buffer
	SLDataLocator_AndroidSimpleBufferQueue loc_bufq = { SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2 };
	SLDataFormat_PCM format_pcm = { SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1, SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
			SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT, SL_BYTEORDER_LITTLEENDIAN };
	SLDataSource audioSrc = { &loc_bufq, &format_pcm };

	// configure audio sink as the outputMix
	SLDataLocator_OutputMix loc_outmix = { SL_DATALOCATOR_OUTPUTMIX, outputMixObject };
	SLDataSink audioSnk = { &loc_outmix, NULL };

	// Create audio player with source and sink
	const SLInterfaceID ids1[] = { SL_IID_ANDROIDSIMPLEBUFFERQUEUE };
	const SLboolean req1[] = { SL_BOOLEAN_TRUE };
	result = (*engineEngine)->CreateAudioPlayer(engineEngine, &(bqPlayerObject), &audioSrc, &audioSnk, 1, ids1, req1);
	assert(SL_RESULT_SUCCESS == result);
	result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE );

	// get the play interface
	result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
	assert(SL_RESULT_SUCCESS == result);

	// get the buffer queue interface
	result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE, &bqPlayerBufferQueue);
	assert(SL_RESULT_SUCCESS == result);

	// register callback on the buffer queue
	result = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, NULL);
	assert(SL_RESULT_SUCCESS == result);

	// set the player's state to playing
	result = (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING );

	log("OpenSL was initiated with 16 bit 44100 sample rate and 2 channels");

	log("buffer size is");
	log("%d", BUFFER_SIZE);

	fftBufSize = kiss_fft_next_fast_size(SAMPLES_PER_BUFFER/EVERY_NTH_SAMPLE/NR_CHANNELS);

	cfg = kiss_fft_alloc(fftBufSize, 0, 0, 0);

}

void destroy_audio_player() {

	log("Release audio player");
	// destroy buffer queue audio player object, and invalidate all associated interfaces
	if (bqPlayerObject != NULL) {
		(*bqPlayerObject)->Destroy(bqPlayerObject);
		bqPlayerObject = NULL;
		bqPlayerPlay = NULL;
		bqPlayerBufferQueue = NULL;
	}

	// destroy output mix object, and invalidate all associated interfaces
	if (outputMixObject != NULL) {
		(*outputMixObject)->Destroy(outputMixObject);
		outputMixObject = NULL;
	}

	// destroy engine object, and invalidate all associated interfaces
	if (engineObject != NULL) {
		(*engineObject)->Destroy(engineObject);
		engineObject = NULL;
		engineEngine = NULL;
	}
}
