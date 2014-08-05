package com.yhsg.safeguard;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Gmap extends FragmentActivity implements OnMapClickListener{
	private static final String TAG="Log_gmap";
	
	GoogleMap mGoogleMap;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.gmap);
	    
	    Bundle blat=getIntent().getExtras();
	    Bundle blng=getIntent().getExtras();
	
	    double lat = blat.getDouble("Plat");
	    double lng = blng.getDouble("Plng");
	    
	    Log.v(TAG,Double.toString(lat));
	    Log.v(TAG,Double.toString(lng));
	    // TODO Auto-generated method stub
	    init(lat,lng);
	    OnKeyListener on_KeyEvent = new View.OnKeyListener() {			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == KeyEvent.ACTION_DOWN){					
					if(keyCode == KeyEvent.KEYCODE_BACK){
						finish();
					}
				}
				return false;
			}
		};
	}

///////°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°ÈGoogle Map°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È°È////////////
	public void onMapClick(LatLng point){
		Point screenPt = mGoogleMap.getProjection().toScreenLocation(point);	//notice cur lat,lng
		LatLng latLng = mGoogleMap.getProjection().fromScreenLocation(screenPt);	//
		
		Log.d("∏ ¡¬«•","¡¬«•: ¿ßµµ(" + String.valueOf(point.latitude) + "), ∞Êµµ(" + String.valueOf(point.longitude) + ")");
		Log.d("»≠∏È¡¬«•","»≠∏È¡¬«•: X(" + String.valueOf(screenPt.x) + "), Y(" + String.valueOf(screenPt.y) + ")");
	}
	
	public void init(double lat, double lng){
		Intent getI = getIntent();
		String title=getI.getStringExtra("title");
		
		LatLng position = new LatLng(lat, lng);
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(Gmap.this);
		
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Gmap)).getMap();
		mGoogleMap.setOnMapClickListener(this);		//touch event
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));		//move map location
		mGoogleMap.addMarker(new MarkerOptions().position(position).title(title)).showInfoWindow();		//set marker		
		mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener(){		//marker listener
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
	}
	//////////°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°ËGoogle Map°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë°Ë///////////
}
