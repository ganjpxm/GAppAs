package sg.lt.obs;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.ganjp.glib.core.base.ActivityStack;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;

import java.util.Date;
import java.util.Locale;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.ObsUtil;

/**
 * http://www.androidhive.info/2013/08/android-working-with-google-maps-v2
 * 
 * @author ganjianping
 *
 */
public class BookingHistoryDetailFragmentActivity extends FragmentActivity implements OnClickListener {
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

        Tracker t = ((ObsApplication) getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("History Booking Detail");
        t.send(new HitBuilders.AppViewBuilder().build());

		setContentView(R.layout.activity_booking_detail_history);
		
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
			if (ObsUtil.sLastLocation !=null) {
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s",
                        ObsUtil.sLastLocation.getLatitude(),  ObsUtil.sLastLocation.getLongitude(), obmBookingVehicleItem.getPickupMapAddress());
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				startActivity(intent);
			} else {
				DialogUtil.showAlertDialog(this, "Sorry can't get current postion");
			}
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
