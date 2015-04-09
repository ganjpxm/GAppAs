package sg.lt.obs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.ganjp.glib.core.base.ActivityStack;
import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.entity.Response;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.FileUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.ImageUtil;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.util.SystemUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
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
public class BookingUpcomingDetailFragmentActivity extends FragmentActivity implements OnClickListener, OnMapReadyCallback {
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

    private ImageView signatureIv;

    private RelativeLayout stop1Rl;
    private RelativeLayout stop2Rl;

    private LinearLayout signatureLl;

	private Button directionBtn;
    private Button routerStop1Btn;
    private Button routerStop2Btn;
	private Button routerBtn;
	private Button callLeadPassengerBtn;
	private Button callBookByBtn;

    private Button okBtn;
    private Button reassignDriverBtn;
	
	private ObmBookingVehicleItem obmBookingVehicleItem;

    private FragmentActivity mActivity;
    private boolean isRefreshUpcomingTab = false;

    private String driverInfos;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mActivity = this;

        Tracker t = ((ObsApplication) getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Upcoming Booking Detail");
        t.send(new HitBuilders.AppViewBuilder().build());

		setContentView(R.layout.activity_booking_detail_upcoming);
		
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

		if (StringUtil.isNotEmpty(obmBookingVehicleItem.getRemark())) {
			remarkTv = (TextView) findViewById(R.id.remark_tv);
			remarkTv.setVisibility(View.VISIBLE);
            String remark = "<img src='icon_comment_red'/> " + obmBookingVehicleItem.getRemark();
			remarkTv.setText(Html.fromHtml(remark, new ImageGetter(), null));
		}

        okBtn = (Button) findViewById(R.id.ok_btn);
        String today = DateUtil.getDateString(new Date());
        String tomorrowDateStr = DateUtil.getTomorrowDateStr();
        String pickupDate = DateUtil.getDateString(obmBookingVehicleItem.getPickupDate());
        if ((today.equalsIgnoreCase(pickupDate) || tomorrowDateStr.equalsIgnoreCase(pickupDate))
                && !ObsConst.DRIVER_ACTION_OK.equalsIgnoreCase(obmBookingVehicleItem.getDriverAction())) {
            okBtn.setOnClickListener(this);
            okBtn.setVisibility(View.VISIBLE);
        }

        signatureLl = (LinearLayout) findViewById(R.id.signature_ll);
        signatureLl.setOnClickListener(this);
        signatureIv = (ImageView) findViewById(R.id.signature_iv);
        String signatureFullPath = ObsUtil.getSignatureFullPath(obmBookingVehicleItem.getLeadPassengerSignaturePath());
        if (new File(signatureFullPath).exists()) {
            ImageUtil.setImgNormal(signatureIv, signatureFullPath);
        }

        if (NetworkUtil.isNetworkAvailable(this)) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
	}

	@Override
    public void onClick(View view) {
    	if (view == mBackBtn) {
            startActivity(new Intent(this, ObsBottomTabFragmentActivity.class));
            Intent intent = new Intent(this, ObsBottomTabFragmentActivity.class);
            if (isRefreshUpcomingTab) {
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
            bookingInfo += "\n" + obmBookingVehicleItem.getPickupAddress();
            bookingInfo += "\nto" + obmBookingVehicleItem.getDestination();
            if (StringUtil.hasText(obmBookingVehicleItem.getStop1Address())) {
                bookingInfo += "\nStop1 " + obmBookingVehicleItem.getStop1Address();
            }
            if (StringUtil.hasText(obmBookingVehicleItem.getStop2Address())) {
                bookingInfo += "\nStop2 " + obmBookingVehicleItem.getStop2Address();
            }
            bookingInfo += "\n\n" + obmBookingVehicleItem.getVehicle() + " - " + obmBookingVehicleItem.getNumberOfPassenger() + " pax";
            bookingInfo += "\nPassenger : " +  obmBookingVehicleItem.getLeadPassengerGender() + " " + obmBookingVehicleItem.getLeadPassengerName()
                        + " " + obmBookingVehicleItem.getLeadPassengerMobileNumber();
            bookingInfo += "\n\nFrom " + PreferenceUtil.getString(ObsConst.KEY_USER_NAME_OBS);
            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(bookingInfo);
            DialogUtil.showInfoDialog(this, "Copy Success");
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
		} else if (view == okBtn) {
            DialogUtil.showConfirmDialog(this, getString(R.string.title), "Are you ok for the job (" + obmBookingVehicleItem.getBookingNumber() + ")?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == -1) {
                                DialogUtil.showProcessingDialog(mActivity);
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            String result = ObsUtil.driverOk(obmBookingVehicleItem.getBookingVehicleItemId());
                                            if (Const.VALUE_SUCCESS.equalsIgnoreCase(result)) {
                                                mHandlerDialog.obtainMessage(0).sendToTarget();
                                                ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                                            } else {
                                                mHandlerDialog.obtainMessage(1).sendToTarget();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            mHandlerDialog.obtainMessage(1).sendToTarget();
                                        }
                                    }
                                }).start();
                            }
                        }
                    });
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
                DialogUtil.showInfoDialog(BookingUpcomingDetailFragmentActivity.this, getString(R.string.insertSD));
                return;
            }
            Intent intent = new Intent(BookingUpcomingDetailFragmentActivity.this, SignatureActivity.class);
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
            if (msg.what==0) {
                DialogUtil.dismissProgressDialog();
                DialogUtil.showInfoDialog(mActivity, getResources().getString(R.string.success));
                okBtn.setVisibility(View.GONE);
                isRefreshUpcomingTab = true;
            } else if (msg.what==1) {
                DialogUtil.dismissProgressDialog();
                DialogUtil.showInfoDialog(mActivity, getResources().getString(R.string.fail));
            } else if (msg.what==2) {
                DialogUtil.dismissProgressDialog();
                Intent intent = new Intent(mActivity, BookingAssignDriverActivity.class);
                intent.putExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS, obmBookingVehicleItem);
                intent.putExtra(ObsConst.KEY_DRIVER_INFO_OBS, driverInfos);
                intent.putExtra(ObsConst.KEY_IS_PAST, Const.VALUE_NO);

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
    protected void onStop() {
        super.onStop();
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
                isRefreshUpcomingTab = true;
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        String isNeedRefresh = PreferenceUtil.getString(ObsConst.KEY_DETAIL_NEEED_REFRESH);
        if (StringUtil.hasText(isNeedRefresh) && Const.VALUE_YES.equalsIgnoreCase(isNeedRefresh)) {
            String bookingVehicleItemId = obmBookingVehicleItem.getBookingVehicleItemId();
            obmBookingVehicleItem = ObmBookingVehicleItemDAO.getInstance().getObmBookingVehicleItem(bookingVehicleItemId);
            driverTv.setText(obmBookingVehicleItem.getDriverUserName());
            PreferenceUtil.saveString(ObsConst.KEY_DETAIL_NEEED_REFRESH, Const.VALUE_NO);
        }
        ActivityStack.setActiveActivity(this);
        isRefreshUpcomingTab = false;
    }
	
	@Override
	protected void onPause() {
        super.onPause();
        ActivityStack.setActiveActivity(null);
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
