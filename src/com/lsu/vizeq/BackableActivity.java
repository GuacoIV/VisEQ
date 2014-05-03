package com.lsu.vizeq;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class BackableActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
	    TextView yourTextView = (TextView) findViewById(titleId);
	    yourTextView.setTextColor(Color.WHITE);
	    Typeface titleFont = Typeface.createFromAsset(getAssets(), "Mohave-SemiBold.otf");
	    yourTextView.setTypeface(titleFont);
	    yourTextView.setTextSize(22);
	    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
