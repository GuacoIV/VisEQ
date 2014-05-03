package com.lsu.vizeq;

import java.util.Random;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TabHost.TabSpec;

public class HostProfileActivity extends Activity implements OnItemSelectedListener{
	public String mColor;
	Spinner spinner;
	LinearLayout customSearchLayout;
	OnClickListener submitListener;
	ActionBar actionBar;
	public MyApplication myapp;	
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using parent.getItemAtPosition(pos)
		spinner.setSelection(pos);
		SharedPreferences memory = getSharedPreferences("VizEQ", MODE_PRIVATE);
		mColor = (String) parent.getItemAtPosition(pos);
		SharedPreferences.Editor saver = memory.edit();
		saver.putString("color", mColor);
		saver.putInt("colorPos", pos);
		saver.commit();
				
		actionBar = getActionBar();
		int posi = memory.getInt("colorPos", -1);
		if (posi != -1) 
		{
			VizEQ.numRand = posi;	
			if (VizEQ.numRand == 0){
				Random r = new Random();
				VizEQ.numRand = r.nextInt(5) + 1;
			}			
			
			switch (VizEQ.numRand)
			{
				case 1:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
					break;
				case 2:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));				
					break;
				case 3:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
					break;
				case 4:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));				
					break;
				case 5:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
					break;		
			}
		}
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    	actionBar = getActionBar();
    	SharedPreferences memory = getSharedPreferences("VizEQ", MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		spinner.setSelection(posi);
		if (posi > 0) 
		{
			VizEQ.numRand = posi;	
			switch (VizEQ.numRand)
			{				
				case 1:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
					break;
				case 2:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));				
					break;
				case 3:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
					break;
				case 4:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));				
					break;
				case 5:
					actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
					break;		
			}
		}
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_profile);
		
		actionBar = getActionBar();		
		SharedPreferences memory = getSharedPreferences("VizEQ", MODE_PRIVATE);
		myapp = (MyApplication) this.getApplicationContext();
		
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;	
		
		switch (VizEQ.numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));				
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));				
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;					
		}		
		
		customSearchLayout = (LinearLayout) findViewById(R.id.customSearchLayout);
		final EditText searchText = (EditText) findViewById(R.id.CustomSearchField);
		final OnTouchListener rowTap;   
	    
	    // Color Spinner
	    spinner = (Spinner) findViewById(R.id.colorspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_spinner, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		spinner.setOnItemSelectedListener(this);
		spinner.setAdapter(adapter);
		if (posi == -1) posi = 0;
		spinner.setSelection(posi);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_profile, menu);
		return true;
	}

}
