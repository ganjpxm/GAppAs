package sg.lt.obs;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.ganjp.glib.core.base.ActivityStack;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsUtil;

/**
 * http://www.androidhive.info/2013/08/android-working-with-google-maps-v2
 * 
 * @author ganjianping
 *
 */
public class BookingDetailFragmentActivity extends FragmentActivity implements OnClickListener, OnMapReadyCallback {
	protected Button mBackBtn = null;
	protected TextView mTitleTv = null;
	protected Button mNextBtn = null;
	
	private TextView bookingServiceTv;
	private TextView pickupDateTimeTv;
	
	private TextView pickupAddressTv;
    private TextView stop1AddressTv;
    private TextView stop2AddressTv;
	private TextView destinationTv;
	private TextView leadPassengerTv;
	private TextView bookByTv;
	private TextView vehicleTv;
	private TextView remarkTv;

    private RelativeLayout stop1Rl;
    private RelativeLayout stop2Rl;
	
	private Button directionBtn;
    private Button routerStop1Btn;
    private Button routerStop2Btn;
	private Button routerBtn;
	private Button callLeadPassengerBtn;
	private Button callBookByBtn;
	
	private ObmBookingVehicleItem obmBookingVehicleItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_detail);
		
		mBackBtn = (Button)findViewById(R.id.back_btn);
	    mBackBtn.setOnClickListener(this);
	    
	    mTitleTv = (TextView)findViewById(R.id.title_tv);
	    mNextBtn = (Button)findViewById(R.id.next_btn);
	    mNextBtn.setVisibility(View.INVISIBLE);
	   
		obmBookingVehicleItem = (ObmBookingVehicleItem)getIntent().getExtras().getSerializable(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS);
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

        if (StringUtil.hasText(obmBookingVehicleItem.getStop1Address())) {
            stop1Rl = (RelativeLayout) findViewById(R.id.stop1_rl);
            stop1AddressTv = (TextView) findViewById(R.id.stop1_address_tv);
            stop1AddressTv.setText(obmBookingVehicleItem.getStop1Address());
            routerStop1Btn = (Button) findViewById(R.id.router_stop1_btn);
            routerStop1Btn.setOnClickListener(this);
            stop1Rl.setVisibility(View.VISIBLE);
        }

        if (StringUtil.hasText(obmBookingVehicleItem.getStop2Address())) {
            stop2Rl = (RelativeLayout) findViewById(R.id.stop2_rl);
            stop2AddressTv = (TextView) findViewById(R.id.stop2_address_tv);
            stop2AddressTv.setText(obmBookingVehicleItem.getStop2Address());
            routerStop2Btn = (Button) findViewById(R.id.router_stop2_btn);
            routerStop2Btn.setOnClickListener(this);
            stop2Rl.setVisibility(View.VISIBLE);
        }

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
		
		String bookBy = obmBookingVehicleItem.getBookingUserGender() + " " + obmBookingVehicleItem.getBookingUserFirstName() + " " + obmBookingVehicleItem.getBookingUserLastName();
		bookByTv = (TextView) findViewById(R.id.book_by_tv);
		bookByTv.setText(bookBy);
		callBookByBtn = (Button) findViewById(R.id.call_book_by_btn);
		callBookByBtn.setOnClickListener(this);

        String vehicle = "<img src='icon_vehicle_black'/> ";
		if (StringUtil.isNotEmpty(obmBookingVehicleItem.getVehicle())) {
            vehicle += obmBookingVehicleItem.getVehicle();
		}
        if (StringUtil.isNotEmpty(obmBookingVehicleItem.getNumberOfPassenger())) {
            vehicle += " - " + obmBookingVehicleItem.getNumberOfPassenger() + " passenger(s)";
        }
        vehicleTv = (TextView) findViewById(R.id.vehicle_tv);
        vehicleTv.setText(Html.fromHtml(vehicle, new ImageGetter(), null));

		if (StringUtil.isNotEmpty(obmBookingVehicleItem.getRemark())) {
			remarkTv = (TextView) findViewById(R.id.remark_tv);
			remarkTv.setVisibility(View.VISIBLE);
            String remark = "<img src='icon_comment_red'/> " + obmBookingVehicleItem.getRemark();
			remarkTv.setText(Html.fromHtml(remark, new ImageGetter(), null));
		}

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
		} else if (view == directionBtn) {
			if (ObsUtil.sCurrentLocation!=null) {
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s",
                        ObsUtil.sCurrentLocation.getLatitude(),  ObsUtil.sCurrentLocation.getLongitude(), obmBookingVehicleItem.getPickupMapAddress());
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
        } else if (view == routerStop1Btn) {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s",
                    obmBookingVehicleItem.getPickupAddress(), obmBookingVehicleItem.getStop1MapAddress());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
		} else if (view == routerStop2Btn) {
            String saddress = obmBookingVehicleItem.getPickupMapAddress();
            if (StringUtil.hasText(obmBookingVehicleItem.getStop1MapAddress())) {
                saddress = obmBookingVehicleItem.getStop1MapAddress();
            }
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s",
                    saddress, obmBookingVehicleItem.getStop2MapAddress());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } else if (view == routerBtn) {
            String saddress = obmBookingVehicleItem.getPickupMapAddress();
            if (StringUtil.hasText(obmBookingVehicleItem.getStop2MapAddress())) {
                saddress = obmBookingVehicleItem.getStop2MapAddress();
            } else if (StringUtil.hasText(obmBookingVehicleItem.getStop1MapAddress())) {
                saddress = obmBookingVehicleItem.getStop1MapAddress();
            }
			String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s",
                    saddress, obmBookingVehicleItem.getDestinationMapAddress());
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityStack.setActiveActivity(this);
    }
	
	@Override
	protected void onPause() {
        super.onPause();
        ActivityStack.setActiveActivity(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        if (obmBookingVehicleItem!=null) {
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
            if (StringUtil.hasText(obmBookingVehicleItem.getPickupMapAddress())) {
                LatLng pickupAddressLL = getLatLng(obmBookingVehicleItem.getPickupMapAddress());
                if (pickupAddressLL!=null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupAddressLL, 11));
                    map.addMarker(new MarkerOptions().title("Pick-up Address").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_1))
                            .snippet(obmBookingVehicleItem.getPickupMapAddress()).position(pickupAddressLL));
                    polylineOptions.add(pickupAddressLL);
                }
            }
            if (StringUtil.hasText(obmBookingVehicleItem.getStop1MapAddress())) {
                LatLng stop1AddressLL = getLatLng(obmBookingVehicleItem.getStop1MapAddress());
                if (stop1AddressLL!=null) {
                    map.addMarker(new MarkerOptions().title("Stop 1 Address").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location_2))
                            .snippet(obmBookingVehicleItem.getStop1MapAddress()).position(stop1AddressLL));
                    polylineOptions.add(stop1AddressLL);
                }
            }
            if (StringUtil.hasText(obmBookingVehicleItem.getStop2MapAddress())) {
                int icon = R.drawable.icon_location_2;
                if (StringUtil.hasText(obmBookingVehicleItem.getStop1MapAddress())) {
                    icon = R.drawable.icon_location_3;
                }
                LatLng stop2AddressLL = getLatLng(obmBookingVehicleItem.getStop2MapAddress());
                if (stop2AddressLL!=null) {
                    map.addMarker(new MarkerOptions().title("Stop 2 Address").icon(BitmapDescriptorFactory.fromResource(icon))
                            .snippet(obmBookingVehicleItem.getStop2MapAddress()).position(stop2AddressLL));
                    polylineOptions.add(stop2AddressLL);
                }
            }
            if (StringUtil.hasText(obmBookingVehicleItem.getDestinationMapAddress())) {
                int icon = R.drawable.icon_location_2;
                if (StringUtil.hasText(obmBookingVehicleItem.getStop1MapAddress())) {
                    if (StringUtil.hasText(obmBookingVehicleItem.getStop2MapAddress())) {
                        icon = R.drawable.icon_location_4;
                    } else {
                        icon = R.drawable.icon_location_3;
                    }
                } else {
                    if (StringUtil.hasText(obmBookingVehicleItem.getStop2MapAddress())) {
                        icon = R.drawable.icon_location_3;
                    }
                }
                LatLng destinationAddressLL = getLatLng(obmBookingVehicleItem.getDestinationMapAddress());
                if (destinationAddressLL!=null) {
                    map.addMarker(new MarkerOptions().title("Drop-off Address").icon(BitmapDescriptorFactory.fromResource(icon))
                            .snippet(obmBookingVehicleItem.getDestinationMapAddress()).position(destinationAddressLL));
                    polylineOptions.add(destinationAddressLL);
                }
            }
            map.addPolyline(polylineOptions);
        }
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
	    return null;
    }

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;

            id = getResources().getIdentifier(source, "drawable", getPackageName());

            if (id == 0) {
                // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
                id = getResources().getIdentifier(source, "drawable", "android");
            }

            if (id == 0) {
                // prevent a crash if the resource still can't be found
                return null;
            } else {
                Drawable d = getResources().getDrawable(id);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        }

    }

}
