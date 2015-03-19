package sg.lt.obs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.StringUtil;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.activity.ObsActivity;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.fragment.BookingHistoryFragment;

public class BookingCompleteActivity extends ObsActivity {

    private static final String[] currencyArr ={"SGD","MYR"};

    private TextView titleTv;
    private TextView tipTv;
    private Spinner currencySpinner;
    private EditText priceEt;
	private Button bookingCompleteBtn;
    private Button cancelBtn;
	
	private ObmBookingVehicleItem obmBookingVehicleItem;
    private ArrayAdapter<String> adapter;

    private Activity mActivity;
    public BookingCompleteActivity() {

    }
    @Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_booking_complete);
		super.onCreate(savedInstanceState);
        mActivity = this;

        obmBookingVehicleItem = (ObmBookingVehicleItem)getIntent().getExtras().getSerializable(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS);

        currencySpinner = (Spinner) findViewById(R.id.currency_spinner);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, currencyArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        currencySpinner.setVisibility(View.VISIBLE);

        priceEt = (EditText) findViewById(R.id.price_et);

        bookingCompleteBtn = (Button) findViewById(R.id.booking_complete_btn);
        bookingCompleteBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);

        if (obmBookingVehicleItem.getDriverClaimPrice()!=null && obmBookingVehicleItem.getDriverClaimPrice()>0) {
            titleTv = (TextView) findViewById(R.id.title);
            titleTv.setText("Change your claim amount?");
            tipTv = (TextView) findViewById(R.id.tip);
            tipTv.setVisibility(View.GONE);
            priceEt.setText(obmBookingVehicleItem.getDriverClaimPrice()+"");
            currencySpinner.setSelection(adapter.getPosition(obmBookingVehicleItem.getDriverClaimCurrency()));
            bookingCompleteBtn.setText("Submit");
        }

        //this.setFinishOnTouchOutside(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

	@Override
    public void onClick(View view) {
    	super.onClick(view);
    	if (view == mBackBtn) {
    		//startActivity(new Intent(this, ObsBottomTabFragmentActivity.class));
    		super.transitBack();
		} else if (view == cancelBtn) {
            finish();
        } else if (view == bookingCompleteBtn){
            final String currency = currencySpinner.getSelectedItem().toString();
            final String price = priceEt.getText().toString();
            if (StringUtil.hasText(price)) {
                DialogUtil.showProcessingDialog(this);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String result = ObsUtil.changeDriverClaim(obmBookingVehicleItem.getBookingVehicleItemId(), currency, price);
                            if (Const.VALUE_SUCCESS.equalsIgnoreCase(result)) {
                                ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                                mHandlerDialog.obtainMessage(0).sendToTarget();
                            } else {
                                mHandlerDialog.obtainMessage(1).sendToTarget();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mHandlerDialog.obtainMessage(1).sendToTarget();
                        }
                    }
                }).start();
            } else {
                DialogUtil.showAlertDialog(this, "Please enter your claim amount first.");
            }
        }
    }

    private Handler mHandlerDialog = new Handler() {
        public void handleMessage (Message msg) {
            DialogUtil.dismissProgressDialog();
            if (msg.what==0) {
                DialogUtil.showAlertDialog(mActivity, android.R.drawable.ic_dialog_info, getString(R.string.title), getResources().getString(R.string.success),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==-1) {
                                    PreferenceUtil.saveString(ObsConst.KEY_DETAIL_NEEED_REFRESH, Const.VALUE_YES);
                                    PreferenceUtil.saveString(ObsConst.KEY_HISTORY_LIST_NEEED_REFRESH, Const.VALUE_YES);
                                    finish();
                                }
                            }
                        }, new String[]{getString(R.string.ok)});
            } else if (msg.what==1) {
                DialogUtil.showInfoDialog(mActivity, getResources().getString(R.string.fail));
            }
        }
    };

    class SpinnerSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //currencyArr[arg2]);
        }
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }
}
