package com.example.indoornav;


import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	
	public static final Uri CONTENT_URI = Uri.parse("content://com.example.indoordb.provider/location");

	Spinner spCur;
	Spinner spDes;
	Button btn;
	ContentResolver contentResolver;
	ArrayList<CharSequence> list;
	Cursor cursor;
	
	Algorithm algorithm;
	double slat,slng,elat,elng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		list=new ArrayList<CharSequence>();
		algorithm = new Algorithm();
		spCur=(Spinner)findViewById(R.id.spinner1);
		spDes=(Spinner)findViewById(R.id.spinner2);
		btn=(Button)findViewById(R.id.button1);
		contentResolver=this.getContentResolver();
		Thread thread=new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				cursor = contentResolver.query(CONTENT_URI, new String[]{"_id","room"}, null, null, null);
				while(cursor.moveToNext()){
					String room=cursor.getString(cursor.getColumnIndex("room"));
					list.add(room);
				}
				myhandler.sendMessage(Message.obtain(myhandler, 0));
				
			}
			
		});
		thread.start();
/*		ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, list);
		ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, list);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCur.setAdapter(adapter1);
		spDes.setAdapter(adapter2);   
*/
		
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(spCur.getSelectedItem()==null||spDes.getSelectedItem()==null){
					Toast.makeText(getApplicationContext(), "Please make sure you choose both current position and destination", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				Thread queryThread = new Thread(new QueryThread());
				queryThread.start();
			}
			
		});
	
	}
	
	public class QueryThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String[] str1 = new String[]{spCur.getSelectedItem().toString()};
			String[] str2 = new String[]{spDes.getSelectedItem().toString()};
			Cursor cursor1 = contentResolver.query(CONTENT_URI, new String[]{"_id","room","latlng"}, "room=?", str1 , null);
			Cursor cursor2 = contentResolver.query(CONTENT_URI, new String[]{"_id","room","latlng"}, "room=?", str2 , null);
			while(cursor1.moveToNext()){
                String locStart = cursor1.getString(cursor1.getColumnIndex("latlng"));
                slat=algorithm.latResolve(locStart);
                slng=algorithm.lngResolve(locStart);
			}
			
			while(cursor2.moveToNext()){
				String locEnd = cursor2.getString(cursor2.getColumnIndex("latlng"));
				elat=algorithm.latResolve(locEnd);
				elng=algorithm.lngResolve(locEnd);
			}
			
			Intent intent = new Intent(getApplicationContext(),ARActivity.class);
			Bundle bundle = new Bundle();
			bundle.putDouble("slat", slat);
			bundle.putDouble("slng",slng);
			bundle.putDouble("elat",elat);
			bundle.putDouble("elng",elng);
			intent.putExtra("Location",bundle);
		    startActivity(intent);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	Handler myhandler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(MainActivity.this, R.layout.simple_spinner_dropdown_item, list);
				ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(MainActivity.this, R.layout.simple_spinner_dropdown_item, list);
				adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				spCur.setAdapter(adapter1);
				spDes.setAdapter(adapter2);
			}
		}
	
	};

}
