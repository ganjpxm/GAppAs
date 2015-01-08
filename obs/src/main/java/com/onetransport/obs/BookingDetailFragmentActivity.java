package com.onetransport.obs;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ganjp.glib.core.ActivityStack;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;
import com.onetransport.obs.common.ObsConst;
import com.onetransport.obs.common.entity.ObmBookingVehicleItem;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * http://www.androidhive.info/2013/08/android-working-with-google-maps-v2
 * 
 * @author ganjianping
 *
 */
public class BookingDetailFragmentActivity extends FragmentActivity implements OnClickListener, LocationListener {
	protected Button mBackBtn = null;
	protected TextView mTitleTv = null;
	protected Button mNextBtn = null;
	
	private TextView bookingServiceTv;
	private TextView pickupDateTimeTv;
	
	private TextView pickupAddressTv;
	private TextView destinationTv;
	private TextView leadPassengerTv;
	private TextView bookByTv;
	private TextView assignByTv;
	private TextView remarkTv;
	
	private Button directionBtn;
	private Button routerBtn;
	private Button callLeadPassengerBtn;
	private Button callBookByBtn;
	private Button callAssignByBtn;
	
	private ObmBookingVehicleItem obmBookingVehicleItem;
	
	private GoogleMap mMap;
	private LocationManager locationManager;
	private LatLng mCurrentLatLng;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_detail);
		
		mBackBtn = (Button)findViewById(R.id.back_btn);
	    mBackBtn.setOnClickListener(this);
	    
	    mTitleTv = (TextView)findViewById(R.id.title_tv);
	    mNextBtn = (Button)findViewById(R.id.next_btn);
	    mNextBtn.setVisibility(View.INVISIBLE);
	   
		obmBookingVehicleItem = (ObmBookingVehicleItem)getIntent().getExtras().getSerializable(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBSD);
		mTitleTv.setText(obmBookingVehicleItem.getBookingNumber());
		
		bookingServiceTv = (TextView) findViewById(R.id.booking_service_tv);
		bookingServiceTv.setText(obmBookingVehicleItem.getBookingService());
		
		pickupDateTimeTv = (TextView) findViewById(R.id.pickup_datetime_tv);
		pickupDateTimeTv.setText(DateUtil.formateDate(new Date(obmBookingVehicleItem.getPickupDateTime().getTime()), "EEE, dd MMM yyyy, HH:mm"));
		
		String address = "";
		if ("0101".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
			address += "<b>" + obmBookingVehicleItem.getFlightNumber() + "</b> " + obmBookingVehicleItem.getPickupAddress();
		} else {
			if ("0104".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
				address += "<b>" + obmBookingVehicleItem.getBookingHours() + " </b> " + obmBookingVehicleItem.getPickupAddress();
			} else {
				address += obmBookingVehicleItem.getPickupAddress();
			}
		}
		pickupAddressTv = (TextView) findViewById(R.id.pickup_address_tv);
		pickupAddressTv.setText(Html.fromHtml(address));
		directionBtn = (Button) findViewById(R.id.direction_btn);
		directionBtn.setOnClickListener(this);
		
		address = "";
		if ("0102".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
			address += "<b>" + obmBookingVehicleItem.getFlightNumber() + "</b> " + obmBookingVehicleItem.getDestination();
		} else {
			address += obmBookingVehicleItem.getDestination();
		}
		destinationTv = (TextView) findViewById(R.id.destination_tv);
		destinationTv.setText(Html.fromHtml(address));
		routerBtn = (Button) findViewById(R.id.route_btn);
		routerBtn.setOnClickListener(this);
		
		String leadPassenger = obmBookingVehicleItem.getLeadPassengerGender() + " " + obmBookingVehicleItem.getLeadPassengerFirstName() + " " + 
				obmBookingVehicleItem.getLeadPassengerLastName();
		leadPassengerTv = (TextView) findViewById(R.id.lead_passenger_tv);
		leadPassengerTv.setText(leadPassenger);
		callLeadPassengerBtn = (Button) findViewById(R.id.call_lead_passenger_btn);
		callLeadPassengerBtn.setOnClickListener(this);
		
		String bookBy = obmBookingVehicleItem.getBookingUserGender() + " " + obmBookingVehicleItem.getBookingUserName();
		bookByTv = (TextView) findViewById(R.id.book_by_tv);
		bookByTv.setText(bookBy);
		callBookByBtn = (Button) findViewById(R.id.call_book_by_btn);
		callBookByBtn.setOnClickListener(this);
		
		String assignBy =  "";
		if (StringUtil.isNotEmpty(obmBookingVehicleItem.getAgentUserName()) && !"null".equalsIgnoreCase(obmBookingVehicleItem.getAgentUserName())) {
			assignBy = obmBookingVehicleItem.getAgentUserName() + "<br/>";
		} else if (!"null".equalsIgnoreCase(obmBookingVehicleItem.getOperatorName())) {
			assignBy = obmBookingVehicleItem.getOperatorName() + "<br/>";
		}
		assignBy += "from " + obmBookingVehicleItem.getOrgName();
		assignByTv = (TextView) findViewById(R.id.assign_by_tv);
		assignByTv.setText(Html.fromHtml(assignBy));
		callAssignByBtn = (Button) findViewById(R.id.call_assign_by_btn);
		callAssignByBtn.setOnClickListener(this);
		
		if (StringUtil.isNotEmpty(obmBookingVehicleItem.getRemark())) {
			remarkTv = (TextView) findViewById(R.id.remark_tv);
			remarkTv.setVisibility(View.VISIBLE);
			remarkTv.setText(obmBookingVehicleItem.getRemark());
		}
	}

	@Override
    public void onClick(View view) {
    	if (view == mBackBtn) {
    		startActivity(new Intent(this, ObsBottomTabFragmentActivity.class));
    		overridePendingTransition(org.ganjp.glib.R.anim.in_from_left, org.ganjp.glib.R.anim.out_to_right);
		} else if (view == callLeadPassengerBtn) {
			DialogUtil.showCallDialog(this, "Call Lead Passenger", obmBookingVehicleItem.getLeadPassengerMobileNumber(), 
					obmBookingVehicleItem.getLeadPassengerMobileNumber());
		} else if (view == callBookByBtn) {
			DialogUtil.showCallDialog(this, "Call Booker", obmBookingVehicleItem.getBookingUserMobileNumber(), 
					obmBookingVehicleItem.getBookingUserMobileNumber());
		} else if (view == callAssignByBtn) {
			DialogUtil.showCallDialog(this, "Call Assigner", obmBookingVehicleItem.getAgentMobileNumber(), 
					obmBookingVehicleItem.getAgentMobileNumber());
		} else if (view == directionBtn) {
			if (mCurrentLatLng!=null) {
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s", 
						mCurrentLatLng.latitude,  mCurrentLatLng.longitude, obmBookingVehicleItem.getPickupAddress());
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				startActivity(intent);
			} else {
				DialogUtil.showAlertDialog(this, "Sorry can't get current postion");
			}
			
//			Uri geoLocation = Uri.parse("geo:latitude,longitude" + mCurrentLatLng.latitude + "," + mCurrentLatLng.longitude + "?z=15&q="+obmBookingVehicleItem.getPickupAddress());
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//		    intent.setData(geoLocation);
//		    if (intent.resolveActivity(getPackageManager()) != null) {
//		        startActivity(intent);
//		    }
		} else if (view == routerBtn) {
			String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s", 
						obmBookingVehicleItem.getPickupAddress(), obmBookingVehicleItem.getDestination());
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
			
//			Uri geoLocation = Uri.parse("geo:latitude,longitude" + mCurrentLatLng.latitude + "," + mCurrentLatLng.longitude + "?z=15&q="+obmBookingVehicleItem.getDestination());
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//		    intent.setData(geoLocation);
//		    if (intent.resolveActivity(getPackageManager()) != null) {
//		        startActivity(intent);
//		    }
		}
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        ActivityStack.setActiveActivity(this);
        setUpMapIfNeeded();
    }
	
	@Override
	protected void onPause() {
        super.onPause();
        ActivityStack.setActiveActivity(null);
    }
	
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
	
	private void setUpMap() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getAllProviders();
		for (String provider : providers) {
			System.out.println(provider);
		}
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//获取精准位置
        criteria.setCostAllowed(true);//允许产生开销
        criteria.setPowerRequirement(Criteria.POWER_HIGH);//消耗大的话，获取的频率高
        criteria.setSpeedRequired(true);//手机位置移动
        criteria.setAltitudeRequired(true);//海拔
        String bestProvider = locationManager.getBestProvider(criteria, true);
        System.out.println("The best Provider : "+bestProvider);
        
        locationManager.requestLocationUpdates(bestProvider,60000,100, this);
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomGesturesEnabled(true);
		mMap.getUiSettings().setCompassEnabled(true);
		mMap.getUiSettings().setRotateGesturesEnabled(true);
    }
	
	@Override
    public void onLocationChanged(Location location) {
		if (mCurrentLatLng==null) {
			mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			CameraPosition cameraPosition = new CameraPosition.Builder().target(mCurrentLatLng).zoom(15).build();
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			Marker currentMarker = mMap.addMarker(new MarkerOptions().position(
						new LatLng(location.getLatitude(), location.getLongitude())).title("You are here"));
			currentMarker.showInfoWindow();
		}
        Log.d("Current Position : ", "Latitude:" + location.getLatitude() + ", Longitude:"  + location.getLongitude());
    }
 
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }
 
    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
    
    public LatLng getLatLng(String address) {
	    List<Address> foundGeocode;
	    try {
	    	foundGeocode = new Geocoder(this).getFromLocationName(address, 1);
	        LatLng latLng = new LatLng(foundGeocode.get(0).getLatitude(), foundGeocode.get(0).getLongitude());
	        return latLng;
	    } catch(Exception ex) {
	    	ex.printStackTrace();
	    }
	    return mCurrentLatLng;
    }
}
