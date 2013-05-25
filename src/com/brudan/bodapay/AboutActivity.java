package com.brudan.bodapay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView tv = (TextView)findViewById(R.id.about_text);
		tv.setText(Html.fromHtml("<h5>Known Issues</h5>" +
            "*Sometimes the GPS might not locate accurately<br/>" +
            "*Sometimes the map doesn't load fully(use the accelerometer for a quick fix, just flip your phone)<br/>" +
            "*If it takes long to load, restart the app<br/>" +
            "*For best results,you have to be on 3G or WiFi<br/>" +
            "<h5>BodaPAy</h5>" +
            "Version 0.0.2<br/>" +
            "Developed by Brudan Digital<br/>" +
            "www.brudan.net"));
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
	    	Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
	        return true;
	    case R.id.menu_map:
	    	intent = new Intent(this, MainActivity.class);
	    	startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	    	startActivity(intent);
	        return true;
	    case R.id.menu_home:
	    	intent = new Intent(this, HomeActivity.class);
	    	startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	    	startActivity(intent);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
