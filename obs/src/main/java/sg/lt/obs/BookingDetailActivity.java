package sg.lt.obs;

import java.util.Date;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.activity.ObsActivity;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookingDetailActivity extends ObsActivity {
	private TextView bookingServiceTv;
	private TextView pickupDateTimeTv;
	
	private TextView fromTv;
	private TextView pickupAddressTv;
	private TextView toTv;
	private TextView destinationTv;
	private TextView leadPassengerTv;
	private TextView bookByTv;
	private TextView assignByTv;
	private TextView remarkTv;
	
	private Button callLeadPassengerBtn;
	private Button callBookByBtn;
	private Button callAssignByBtn;
	
	private ObmBookingVehicleItem obmBookingVehicleItem;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_booking_detail);
		super.onCreate(savedInstanceState);
		
		mNextBtn.setVisibility(View.INVISIBLE);
		
		obmBookingVehicleItem = (ObmBookingVehicleItem)getIntent().getExtras().getSerializable(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBSD);
		mTitleTv.setText(obmBookingVehicleItem.getBookingNumber());
		
		bookingServiceTv = (TextView) findViewById(R.id.booking_service_tv);
		bookingServiceTv.setText(obmBookingVehicleItem.getBookingService());
		
		pickupDateTimeTv = (TextView) findViewById(R.id.pickup_datetime_tv);
		pickupDateTimeTv.setText(DateUtil.formateDate(new Date(obmBookingVehicleItem.getPickupDateTime().getTime()), "EEE, dd MMM yyyy, HH:mm"));
		//		listView_2.setOnItemClickListener(this);
		
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
		
		address = "";
		if ("0102".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
			address += "<b>" + obmBookingVehicleItem.getFlightNumber() + "</b> " + obmBookingVehicleItem.getDestination();
		} else {
			address += obmBookingVehicleItem.getDestination();
		}
		destinationTv = (TextView) findViewById(R.id.destination_tv);
		destinationTv.setText(Html.fromHtml(address));
		
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
		
		String assignBy =  "";
//		if (StringUtil.isNotEmpty(obmBookingVehicleItem.getAgentUserName()) && !"null".equalsIgnoreCase(obmBookingVehicleItem.getAgentUserName())) {
//			assignBy = obmBookingVehicleItem.getAgentUserName() + "<br/>";
//		} else if (!"null".equalsIgnoreCase(obmBookingVehicleItem.getOperatorName())) {
//			assignBy = obmBookingVehicleItem.getOperatorName() + "<br/>";
//		}
//		assignBy += "from " + obmBookingVehicleItem.getOrgName();
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
    	super.onClick(view);
    	if (view == mBackBtn) {
    		startActivity(new Intent(this, ObsBottomTabFragmentActivity.class));
    		super.transitBack();
		} else if (view == callLeadPassengerBtn) {
			DialogUtil.showCallDialog(this, "Call Lead Passenger", obmBookingVehicleItem.getLeadPassengerMobileNumber(), 
					obmBookingVehicleItem.getLeadPassengerMobileNumber());
		} else if (view == callBookByBtn) {
			DialogUtil.showCallDialog(this, "Call Booker", obmBookingVehicleItem.getBookingUserMobileNumber(), 
					obmBookingVehicleItem.getBookingUserMobileNumber());
		}
    }
}
