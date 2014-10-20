package com.example.indoornav;

public class Algorithm {
	public double latResolve(String str){                 //Resolve latitude
		String lati="";
		for(Character c: str.toCharArray()){
			if(c.equals(','))
				break;
			lati=lati+c;
		}
		
		return Double.parseDouble(lati);
	}
	
	public double lngResolve(String str){                 //Resolve longitude
		String lngi="";
		int i;
		Boolean blng=false;
		char[] a=str.toCharArray();
		for(i=0;i<str.length();i++){
			if(a[i]==','){
				blng=true;
				continue;
			}
			if(blng)
				lngi=lngi+a[i];
		}
		return Double.parseDouble(lngi);
	}
	
	public double getLocation(double heading, double slat, double slng, double elat, double elng){
		
		
		double pi=Math.PI;
		double x=0.0;                  // Azimuth of the victim in the coordinate system with the camera feed as the origin of coordinates
		double difflng= (Math.toRadians(elng-slng))*6371*Math.cos(Math.toRadians(Math.abs(slat)));            // The latitude distance vector of the victim and the camera
		double difflat= (Math.toRadians(elat-slat))*6371;            // The longitude distance vector of the victim and the camera
		double leftedge=heading+Math.toRadians(56.0);      //Azimuth of the left edge of camera field in the coordinate system with the camera feed as the origin of coordinates             
		double y;                                         // The radian difference between x and the left edge of the camera field
		
		if(difflat==0)
			if(difflng>0)
				x=pi/2;                     // On the positive x-axis;
			else
				x=3*pi/2;                   // On the negative x-axis;
		if(difflng==0)
			if(difflat>0)
				x=0;                        // On the positive y-axis;
			else 
				x=pi;                       // On the negative y-axis;
		if(difflat>0&&difflng>0)            
		x= Math.atan2(difflng,difflat);               // In the first quadrant
		
		if(difflat<0&&difflng>0)
			x=pi/2+Math.atan2(Math.abs(difflat),difflng);  // In the fourth quadrant
		
		if(difflat<0&&difflng<0)
			x=pi+Math.atan2(Math.abs(difflng),Math.abs(difflat));  // In the third quadrant
		
		if(difflat>0&&difflng<0)
			x=3*pi/2+Math.atan2(difflat,Math.abs(difflng));      // In the second quadrant
		
		y=x-leftedge;                                  
		
		if(y<0)
			y=y+2*pi;
		if(y>=2*pi)
			y=y-2*pi;
		
		if(y>Math.toRadians(68.0))
			y=-1;
	
        return y;
	}

	public double getDistance(double slat, double slng, double elat, double elng){
	
		double difflng= (Math.toRadians(elng-slng))*6371004*Math.cos(Math.toRadians(Math.abs(slat)));            // The latitude distance vector of the victim and the camera
		double difflat= (Math.toRadians(elat-slat))*6371004;            // The longitude distance vector of the victim and the camera
    
		double distance= Math.sqrt((Math.pow(difflat,2)+Math.pow(difflng, 2)));
	
		return distance;
		}
}
