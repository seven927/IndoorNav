package com.example.indoornav;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LaunchActivity extends Activity{
	
	private final static int SPLASH_DISPLAY_LENGTH=2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch);
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
			    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);  
                startActivity(mainIntent);  
                finish();  
			}
		}, SPLASH_DISPLAY_LENGTH);
	}
	
	
}
