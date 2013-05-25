package com.brudan.bodapay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		TextView tv = (TextView)findViewById(R.id.home_intro);
		tv.setText(Html.fromHtml("BodaPay is a mobile app that helps you estimate the " +
				"cost of your intended journey while using Boda Bodas <br/>" +
				"Just enter in your Location and destination,the app will do the rest.<br />"));
		TextView tv2 = (TextView)findViewById(R.id.start);
		tv2.setText(MainActivity.message);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		Intent intent;
		switch (item.getItemId()) {
	    case R.id.menu_about:
	    	intent = new Intent(this, AboutActivity.class);
	    	startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	    	startActivity(intent);
	        return true;
	    case R.id.menu_map:
	    	intent = new Intent(this, MainActivity.class);
	    	startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	    	startActivity(intent);
	        return true;
	    case R.id.menu_home:
	    	Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

}
