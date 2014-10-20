package com.example.indoornav;

import java.text.DecimalFormat;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ARActivity extends Activity{
	SurfaceView cameraPreview; 
    SurfaceHolder previewHolder; 
    Camera camera; 
    boolean inPreview;
    String orienHeading = "0.0", orienPitch = "", orienRoll = "", orienHeadingOld="0.0";
    OrientationReceiver orientationReceiver;
    IntentFilter filterOrien;
    
    RelativeLayout main;
    Button poI;
    
	private double heading;
	
	double slat, slng, elat, elng;
	
	int destleft;
	String destDis;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ar);
		
         main=(RelativeLayout)findViewById(R.id.relativelayout);
		 
		 poI = new Button(ARActivity.this);
		 main.addView(poI);
		 poI.setVisibility(View.INVISIBLE);
		
		 inPreview = false;          
	     cameraPreview = (SurfaceView)findViewById(R.id.cameraPreview); 
	     previewHolder = cameraPreview.getHolder(); 
	     previewHolder.addCallback(surfaceCallback); 
	     filterOrien = new IntentFilter(OrientationService.NEW_LOC_MSG);
	     orientationReceiver = new OrientationReceiver();
	    
	     Intent intent = new Intent();
	     Bundle bundle = getIntent().getBundleExtra("Location");
	     slat=bundle.getDouble("slat");
	     slng=bundle.getDouble("slng");
	     elat=bundle.getDouble("elat");
	     elng=bundle.getDouble("elng");
	}
	
	public void onResume(){
		super.onResume();
		camera=getCameraInstance();
		if(camera==null)
			Toast.makeText(getApplicationContext(), "Camera is not available", Toast.LENGTH_SHORT).show();	
		
		 startService(new Intent(getApplicationContext(),OrientationService.class));   
		 registerReceiver(orientationReceiver, filterOrien);
	}
	
	public class OrientationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			orienHeading = intent.getStringExtra("heading");
			orienPitch = intent.getStringExtra("pitch");
			orienRoll = intent.getStringExtra("roll");
			//Log.d("ARActivity", "Broadcast Heading Received: " + orienHeading);
			//Log.d("ARActivity", "Broadcast Pitch Received: " + orienPitch);
			//Log.d("ARActivity", "Broadcast Roll Received: " + orienRoll);	
			heading = Double.parseDouble(orienHeading);				
			handler.sendMessage(Message.obtain(handler,0,"good"));
		}   	 
    }
	
	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() { 
        public void surfaceCreated(SurfaceHolder holder) { 
        	try { 
        		camera.setPreviewDisplay(previewHolder); 
            }
            catch (Throwable t) { 
                Log.e("IndoorAugmentedReality", "Exception in setPreviewDisplay()", t); 
            } 
        } 
        
        private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) { 
        	Camera.Size result=null; 
        	
        	for (Camera.Size size : parameters.getSupportedPreviewSizes()) { 
        		if (size.width<=width && size.height<=height) { 
        			if (result==null) { 
        				result=size; 
        			} 
        			else { 
        				int resultArea=result.width*result.height; 
        				int newArea=size.width*size.height; 
        		 
        				if (newArea>resultArea) { 
        					result=size; 
        				} 
        			} 
        		} 
        	} 
        	return(result); 
        }

		public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
			// TODO Auto-generated method stub
			Camera.Parameters parameters=camera.getParameters(); 
	        Camera.Size size=getBestPreviewSize(width, height, parameters); 
	 
	        if (size!=null) { 
	        	parameters.setPreviewSize(size.width, size.height); 
	            camera.setParameters(parameters);
	            camera.startPreview(); 
	            inPreview=true; 
	        }
		}

		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			camera.stopPreview(); 
            camera.release(); 
            camera=null;
		} 
    };
    
    public void onPause(){
    	if (inPreview) { 
	        camera.stopPreview();  
	      }
		stopService(new Intent(getApplicationContext(),OrientationService.class));          
	    unregisterReceiver(orientationReceiver);
	    super.onPause();
    }
    
    Handler handler = new Handler(){
    	
    	public void handleMessage (Message msg){
    		try 
    		{
    			//Anti-Shake
    			double abs = Double.parseDouble(orienHeading)-Double.parseDouble(orienHeadingOld);
    			
    			if (orienHeading!="")
				{ 
					//Obtain the screen resolution
    				DisplayMetrics displaysMetrics = new DisplayMetrics();
			        getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
					
			        //Initialize algorithm object
					Algorithm algorithm = new Algorithm();
					
					//Show point of interest
					DecimalFormat df = new DecimalFormat("00.0");
					//obtain the distance between the victim and device
					String num = df.format(algorithm.getDistance(slat, slng, elat,elng)); 
					//Obtain the position of the button
					destleft = (int) ((algorithm.getLocation(heading, slat, slng, elat, elng)/Math.toRadians(68.0))*displaysMetrics.widthPixels);
					destDis = num +"m";
					
					poI.setText(destDis);
	    	   //     victim.setTextColor(textColor);
	    	        poI.setPadding(5, 5, 5, 5);
	    	        poI.setTextSize(20);
	    	        poI.setBackgroundColor(Color.RED);
	    	        RelativeLayout.LayoutParams victimlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);    	        
	    	        victimlp.setMargins(destleft,displaysMetrics.heightPixels/2, 0, 0); 
	    	        poI.setLayoutParams(victimlp); 
	    	        poI.setVisibility(View.VISIBLE);
					
					
					orienHeadingOld = orienHeading;
				}				    			    	        
    		}
    		catch (Exception e){
    			Log.e("Error in Handler", e.getMessage());
    		}
    	}
    }; 
    
    
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	

}
