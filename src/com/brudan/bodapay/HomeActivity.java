package com.brudan.bodapay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnItemClickListener{
	AutoCompleteTextView autoCompView;
	TextView tv, tv2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		tv = (TextView)findViewById(R.id.home_intro);
		tv.setText(Html.fromHtml("BodaPay is a mobile app that helps you estimate the " +
				"cost of your intended journey while using Boda Bodas <br/>" +
				"Just enter in your Location and destination,the app will do the rest.<br />"));
		tv2 = (TextView)findViewById(R.id.start);
		tv2.setText(MainActivity.message);
		Button calc = (Button)findViewById(R.id.calc_button);
		
		
		autoCompView = (AutoCompleteTextView) findViewById(R.id.end);
	    autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
	    autoCompView.setOnItemClickListener(this);
	    
	    calc.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
		        finish();
		    }
		});
	}
	
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        MainActivity.dAddress = str;
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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
	        return true;
	    case R.id.menu_map:
	    	intent = new Intent(this, MainActivity.class);
	    	startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	        return true;
	    case R.id.menu_home:
	    	Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

}
