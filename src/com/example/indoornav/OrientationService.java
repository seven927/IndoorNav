package com.example.indoornav;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class OrientationService extends Service{
	public static final String NEW_LOC_MSG = "New_Loc_Msg";
	private static final float ALPHA = 0.15f;
	SensorManager mSensorManager;
	 
	int orientationSensor1; 
	int orientationSensor2; 
	
	float headingAngle; 
	float pitchAngle; 
	float rollAngle;
	
	float[] inR = new float[16];
    float[] outR= new float[16];
    float[] I = new float[16];
    float[] gravity = new float[3];
    float[] geomag = new float[3];
    float[] orientVals = new float[3];

	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		 mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
         mSensorManager.registerListener(seventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL );
         mSensorManager.registerListener(seventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
         mSensorManager.registerListener(seventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
		return super.onStartCommand(intent, flags, startId);
	}
	
	SensorEventListener seventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub	
			
		}

		@Override
		public void onSensorChanged(SensorEvent sensorEvent) {
			// TODO Auto-generated method stub
		/*	if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			    return;   */
			// Gets the value of the sensor that has been changed
			switch (sensorEvent.sensor.getType()){  	
				case Sensor.TYPE_ROTATION_VECTOR:
				{
			           SensorManager.getRotationMatrixFromVector(inR, sensorEvent.values);
					   SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
					   SensorManager.getOrientation(outR, orientVals);
					   headingAngle = orientVals[0];
					   pitchAngle = orientVals[1];
					   rollAngle = orientVals[2];

		//			    Log.d(TAG, "Heading: " + String.valueOf(headingAngle)); 
	    //               Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle)); 
	    //               Log.d(TAG, "Roll: " + String.valueOf(rollAngle));  
					   
					   Intent intent = new Intent(NEW_LOC_MSG);
	                   intent.putExtra("heading", String.valueOf(headingAngle));
	                   intent.putExtra("pitch", String.valueOf(pitchAngle));
	                   intent.putExtra("roll", String.valueOf(rollAngle));
	                   sendBroadcast(intent);
				}
			} 
		}
		
	};
	
	protected void lowPass( float[] input, float[] output ) {
	    if ( output == null ) 
	         output=input;
	    for ( int i=0; i<input.length; i++ ) {
	        output[i] = output[i] + ALPHA * (input[i] - output[i]);
	    }
	}
	
	public void OnDestroy(){
		mSensorManager.unregisterListener(seventListener);
		super.onDestroy();	
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
