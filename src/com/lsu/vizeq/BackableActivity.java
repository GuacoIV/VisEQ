package com.lsu.vizeq;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class BackableActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // etc...
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
