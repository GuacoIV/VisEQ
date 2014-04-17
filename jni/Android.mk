LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := spotify
LOCAL_SRC_FILES := libspotify.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := libspotifywrapper
LOCAL_SRC_FILES := run_loop.cpp tasks.cpp jni_glue.cpp logger.cpp sound_driver.cpp fft.cpp complex.cpp kiss_fft.cpp
LOCAL_LDLIBS += -llog -lOpenSLES
LOCAL_SHARED_LIBRARIES := libspotify
LOCAL_CPPFLAGS = -std=c++0x -D__STDC_INT64__
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
NDK_DEBUG=0

include $(BUILD_SHARED_LIBRARY)
