package sg.lt.obs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.ganjp.glib.core.base.ActivityStack;
import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.FileUtil;
import org.ganjp.glib.core.util.ImageUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.util.SystemUtil;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;

/**
 * http://www.androidhive.info/2013/08/android-working-with-google-maps-v2
 * 
 * @author ganjianping
 *
 */
public class BookingHistoryDetailFragmentActivity extends FragmentActivity implements OnClickListener {
	protected Button mBackBtn = null;
	protected TextView mTitleTv = null;
	protected Button mCopyBtn = null;
	
	private TextView bookingServiceTv;
	private TextView pickupDateTimeTv;
	
	private TextView pickupAddressTv;
    private TextView stop1AddressTv;
    private TextView stop2AddressTv;
	private TextView destinationTv;
	private TextView leadPassengerTv;
	private TextView bookByTv;
    private TextView driverTv;
	private TextView vehicleTv;
	private TextView remarkTv;

    private RelativeLayout stop1Rl;
    private RelativeLayout stop2Rl;

    private LinearLayout signatureLl;

	private Button directionBtn;
    private Button routerStop1Btn;
    private Button routerStop2Btn;
	private Button routerBtn;
	private Button callLeadPassengerBtn;
	private Button callBookByBtn;
    private Button bookingCompleteBtn;
    private Button reassignDriverBtn;
	
	private ObmBookingVehicleItem obmBookingVehicleItem;

    private FragmentActivity mActivity;
    private String driverInfos;

    private ImageView signatureIv;
    private boolean isRefreshHistoryTab = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mActivity = this;

        Tracker t = ((ObsApplication) getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("History Booking Detail");
        t.send(new HitBuilders.AppViewBuilder().build());

		setContentView(R.layout.activity_booking_detail_history);
		
		mBackBtn = (Button)findViewById(R.id.back_btn);
	    mBackBtn.setOnClickListener(this);
	    
	    mTitleTv = (TextView)findViewById(R.id.title_tv);
        mCopyBtn = (Button)findViewById(R.id.fn_btn);
        mCopyBtn.setText("Copy");
        mCopyBtn.setOnClickListener(this);
        mCopyBtn.setVisibility(View.VISIBLE);
	   
		obmBookingVehicleItem = (ObmBookingVehicleItem)getIntent().getExtras().getSerializable(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS);
		mTitleTv.setText(obmBookingVehicleItem.getBookingNumber());
		
		bookingServiceTv = (TextView) findViewById(R.id.booking_service_tv);
		bookingServiceTv.setText(obmBookingVehicleItem.getBookingService());
		
		pickupDateTimeTv = (TextView) findViewById(R.id.pickup_datetime_tv);
		pickupDateTimeTv.setText(DateUtil.formateDate(new Date(obmBookingVehicleItem.getPickupDateTime().getTime()), "EEE, dd MMM yyyy, HH:mm a"));

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

        driverTv = (TextView) findViewById(R.id.driver_tv);
        driverTv.setText(obmBookingVehicleItem.getDriverUserName());
        reassignDriverBtn = (Button) findViewById(R.id.reassign_driver_btn);
        reassignDriverBtn.setOnClickListener(this);

        String vehicle = "<img src='icon_vehicle_black'/> ";
		if (StringUtil.isNotEmpty(obmBookingVehicleItem.getVehicle())) {
            vehicle += obmBookingVehicleItem.getVehicle();
		}
        if (StringUtil.isNotEmpty(obmBookingVehicleItem.getNumberOfPassenger())) {
            vehicle += " - " + obmBookingVehicleItem.getNumberOfPassenger() + " passenger(s)";
        }
        vehicleTv = (TextView) findViewById(R.id.vehicle_tv);
        vehicleTv.setText(Html.fromHtml(vehicle, new ImageGetter(), null));

		if (StringUtil.hasText(obmBookingVehicleItem.getRemark())) {
			remarkTv = (TextView) findViewById(R.id.remark_tv);
			remarkTv.setVisibility(View.VISIBLE);
            String remark = "<img src='icon_comment_red'/> " + obmBookingVehicleItem.getRemark();
			remarkTv.setText(Html.fromHtml(remark, new ImageGetter(), null));
		}

        signatureLl = (LinearLayout) findViewById(R.id.signature_ll);
        signatureIv = (ImageView) findViewById(R.id.signature_iv);
        Float driverClaimPrice = obmBookingVehicleItem.getDriverClaimPrice();
        if (driverClaimPrice!=null && driverClaimPrice>0 && ObsConst.BOOKING_STATUS_CD_COMPLETED.equals(obmBookingVehicleItem.getBookingStatusCd())) {
            if (!StringUtil.hasText(obmBookingVehicleItem.getLeadPassengerSignaturePath())) {
                signatureLl.setVisibility(View.GONE);
            } else {
                signatureLl.setVisibility(View.VISIBLE);
            }
        } else {
            signatureLl.setOnClickListener(this);
            signatureLl.setVisibility(View.VISIBLE);
        }
        String signatureFullPath = ObsUtil.getSignatureFullPath(obmBookingVehicleItem.getLeadPassengerSignaturePath());
        if (new File(signatureFullPath).exists()) {
            ImageUtil.setImgNormal(signatureIv, signatureFullPath);
        }

        bookingCompleteBtn = (Button) findViewById(R.id.booking_complete_btn);
        bookingCompleteBtn.setOnClickListener(this);
        bookingCompleteBtn.setVisibility(View.VISIBLE);

        if (obmBookingVehicleItem.getDriverClaimPrice()!=null && obmBookingVehicleItem.getDriverClaimPrice()>0) {

            if (StringUtil.hasText(obmBookingVehicleItem.getDriverClaimStatus()) && "Paid".equalsIgnoreCase(obmBookingVehicleItem.getDriverClaimStatus())) {
                bookingCompleteBtn.setEnabled(false);
                bookingCompleteBtn.setText("Claim " + obmBookingVehicleItem.getDriverClaimCurrency() + obmBookingVehicleItem.getDriverClaimPrice() + " (Received)");

            } else {
                bookingCompleteBtn.setText("Claim " + obmBookingVehicleItem.getDriverClaimCurrency() + obmBookingVehicleItem.getDriverClaimPrice() + " (Pending)");
            }
        }
	}

	@Override
    public void onClick(View view) {
    	if (view == mBackBtn) {
            Intent intent = new Intent(this, ObsBottomTabFragmentActivity.class);
            if (isRefreshHistoryTab) {
                intent.putExtra("isRefresh", true);
            }
            startActivity(intent);
            overridePendingTransition(org.ganjp.glib.R.anim.in_from_left, org.ganjp.glib.R.anim.out_to_right);
		} else if (view == mCopyBtn) {
            String bookingInfo = obmBookingVehicleItem.getBookingNumber();
            if ("Cash".equalsIgnoreCase(obmBookingVehicleItem.getPaymentMode())) {
                bookingInfo += "\n" +  "collect " + obmBookingVehicleItem.getPriceUnit() + obmBookingVehicleItem.getPrice() + " Cash";
            } else if ("Paid".equalsIgnoreCase(obmBookingVehicleItem.getPaymentStatus())) {
                bookingInfo += "\nPaid";
            } else {
                bookingInfo += "\nNot Paid";
            }
            bookingInfo += "\n\n" + DateUtil.formateDate(new Date(obmBookingVehicleItem.getPickupDateTime().getTime()), "EEE, dd MMM yyyy, HH:mm a");
            bookingInfo += "\n";
            if (ObsConst.VALUE_BOOKING_SERVICE_CD_ARRIVAL.equals(obmBookingVehicleItem.getBookingServiceCd()) && StringUtil.hasText(obmBookingVehicleItem.getFlightNumber())) {
                bookingInfo += "(" + obmBookingVehicleItem.getFlightNumber() + ") ";
            }
            bookingInfo += obmBookingVehicleItem.getPickupAddress();
            bookingInfo += "\nto ";
            if (ObsConst.VALUE_BOOKING_SERVICE_CD_DEPARTURE.equals(obmBookingVehicleItem.getBookingServiceCd()) && StringUtil.hasText(obmBookingVehicleItem.getFlightNumber())) {
                bookingInfo += "(" + obmBookingVehicleItem.getFlightNumber() + ") ";
            }
            if (StringUtil.hasText(obmBookingVehicleItem.getStop1Address())) {
                bookingInfo += "\nStop1 " + obmBookingVehicleItem.getStop1Address();
            }
            if (StringUtil.hasText(obmBookingVehicleItem.getStop2Address())) {
                bookingInfo += "\nStop2 " + obmBookingVehicleItem.getStop2Address();
            }
            bookingInfo += "\n\n" + obmBookingVehicleItem.getVehicle() + " - " + obmBookingVehicleItem.getNumberOfPassenger() + " pax";
            bookingInfo += "\nPassenger : " +  obmBookingVehicleItem.getLeadPassengerGender() + " " + obmBookingVehicleItem.getLeadPassengerName()
                    + " " + obmBookingVehicleItem.getLeadPassengerMobileNumber();
            bookingInfo += "\nBook by : " +  obmBookingVehicleItem.getBookingUserGender()+ " " + obmBookingVehicleItem.getBookingUserName()
                    + " " + obmBookingVehicleItem.getBookingUserMobileNumber();
            bookingInfo += "\n\nFrom " + PreferenceUtil.getString(ObsConst.KEY_USER_NAME_OBS);
            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(bookingInfo);
            DialogUtil.showInfoDialog(this, "Copy Success");
//            if (clipboardManager.hasText()){
//                clipboardManager.getText();
//            }
//            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
//            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "内容"));
//            if (clipboardManager.hasPrimaryClip()){
//                clipboardManager.getPrimaryClip().getItemAt(0).getText();
//            }
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
		} else if (view == bookingCompleteBtn) {
            Intent intent = new Intent(this, BookingCompleteActivity.class);
            intent.putExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS, obmBookingVehicleItem);
            this.startActivity(intent);
            this.overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_in_bottom);
        } else if (view == reassignDriverBtn) {
            DialogUtil.showProcessingDialog(mActivity);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String result = ObsUtil.getRelateDriverInfos(obmBookingVehicleItem.getVehicleCd());
                        if (!Const.VALUE_FAIL.equalsIgnoreCase(result)) {
                            driverInfos = result;
                            mHandlerDialog.obtainMessage(2).sendToTarget();
                        } else {
                            mHandlerDialog.obtainMessage(3).sendToTarget();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandlerDialog.obtainMessage(3).sendToTarget();
                    }
                }
            }).start();
        } else if (view == signatureLl) {
            if (!SystemUtil.hasSdcard()) {
                DialogUtil.showInfoDialog(BookingHistoryDetailFragmentActivity.this, getString(R.string.insertSD));
                return;
            }
            Intent intent = new Intent(BookingHistoryDetailFragmentActivity.this, SignatureActivity.class);
            String dateStr = "";
            try {
                dateStr = DateUtil.getYYYYMmDdHhMmFormate(obmBookingVehicleItem.getPickupDateTime().getTime());
            } catch (Exception ex) {

            }
            String signaturePath = ObsUtil.getSignatureDirPath() + obmBookingVehicleItem.getBookingNumber() + dateStr + ".jpg";
            FileUtil.createFile(signaturePath);
            intent.putExtra(ObsConst.KEY_FILE_FULL_PATH, signaturePath);
            intent.putExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_ID, obmBookingVehicleItem.getBookingVehicleItemId());
            intent.putExtra(ObsConst.KEY_BOOKING_NUMBER, obmBookingVehicleItem.getBookingNumber());
            startActivityForResult(intent, 1);
        }
    }

    private Handler mHandlerDialog = new Handler() {
        public void handleMessage (Message msg) {
            if (msg.what==2) {
                DialogUtil.dismissProgressDialog();
                Intent intent = new Intent(mActivity, BookingAssignDriverActivity.class);
                intent.putExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS, obmBookingVehicleItem);
                intent.putExtra(ObsConst.KEY_DRIVER_INFO_OBS, driverInfos);
                intent.putExtra(ObsConst.KEY_IS_PAST, Const.VALUE_YES);

                startActivity(intent);
                overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_in_bottom);
            } else if (msg.what==3) {
                DialogUtil.dismissProgressDialog();
                DialogUtil.showInfoDialog(mActivity, "Sorry for getting drivers fail.");
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String signatureFullPath = PreferenceUtil.getString(ObsConst.KEY_FILE_FULL_PATH);
        if (StringUtil.hasText(signatureFullPath)) {
            ImageUtil.setImgNormal(signatureIv, signatureFullPath);
            PreferenceUtil.saveString(ObsConst.KEY_FILE_FULL_PATH, "");
            obmBookingVehicleItem.setLeadPassengerSignaturePath(signatureFullPath);
            try {
                ObmBookingVehicleItemDAO.getInstance().update(obmBookingVehicleItem.getBookingVehicleItemId(),
                        ObmBookingVehicleItemDAO.COLUMN_LEAD_PASSENGER_SIGNATURE_PATH, signatureFullPath);
                isRefreshHistoryTab = true;
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String isNeedRefresh = PreferenceUtil.getString(ObsConst.KEY_DETAIL_NEEED_REFRESH);
        if (StringUtil.hasText(isNeedRefresh) && Const.VALUE_YES.equalsIgnoreCase(isNeedRefresh)) {
            String bookingVehicleItemId = obmBookingVehicleItem.getBookingVehicleItemId();
            obmBookingVehicleItem = ObmBookingVehicleItemDAO.getInstance().getObmBookingVehicleItem(bookingVehicleItemId);
            if (obmBookingVehicleItem.getDriverClaimPrice()!=null && obmBookingVehicleItem.getDriverClaimPrice()>0) {
                bookingCompleteBtn.setText("Claim " + obmBookingVehicleItem.getDriverClaimCurrency() + obmBookingVehicleItem.getDriverClaimPrice() + " (Pending)");
            }
            driverTv.setText(obmBookingVehicleItem.getDriverUserName());
            PreferenceUtil.saveString(ObsConst.KEY_DETAIL_NEEED_REFRESH, Const.VALUE_NO);
        }
        ActivityStack.setActiveActivity(this);
        isRefreshHistoryTab = false;
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
