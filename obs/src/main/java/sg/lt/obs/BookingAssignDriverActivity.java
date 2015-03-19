package sg.lt.obs;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.ganjp.glib.core.base.Const;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.activity.ObsActivity;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;

public class BookingAssignDriverActivity extends ObsActivity {

    private Spinner driverSpinner;
	private Button submitBtn;
    private Button cancelBtn;

    private List<Map<String,String>> driverInfos = new ArrayList<Map<String,String>>();
    private String isPast;

	private ObmBookingVehicleItem obmBookingVehicleItem;
    private ArrayAdapter<String> adapter;

    private Activity mActivity;
    public BookingAssignDriverActivity() {

    }
    @Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_reassign_driver);
		super.onCreate(savedInstanceState);
        mActivity = this;

        obmBookingVehicleItem = (ObmBookingVehicleItem)getIntent().getExtras().getSerializable(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS);
        isPast = getIntent().getStringExtra(ObsConst.KEY_IS_PAST);

        String data = getIntent().getStringExtra(ObsConst.KEY_DRIVER_INFO_OBS);
        String[] driverInfoArr = data.split(";");
        List<String> driverList = new ArrayList<String>();
        int selPosition = 0;
        for (int i = 0; i < driverInfoArr.length; i++) {
            String[] infoArr = driverInfoArr[i].split("_");
            driverList.add(infoArr[2]);
            Map<String,String> map = new HashMap<String,String>();
            map.put("vehicleId", infoArr[0]);
            map.put("driverLoginUserId", infoArr[1]);
            map.put("driverUserName", infoArr[2]);
            map.put("driverMobileNumber", infoArr[3]);
            map.put("driverVehicle", infoArr[4]);
            map.put("assignDriverUserId", infoArr[5]);
            map.put("assignDriverUserName", infoArr[6]);
            map.put("assignDriverMobilePhone", infoArr[7]);

            if (StringUtil.hasText(obmBookingVehicleItem.getDriverLoginUserId()) &&
                    obmBookingVehicleItem.getDriverLoginUserId().equals(infoArr[1])) {
                selPosition = i;
            }
            driverInfos.add(map);
        }
        driverSpinner = (Spinner) findViewById(R.id.driver_spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, driverList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(adapter);
        driverSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        driverSpinner.setVisibility(View.VISIBLE);
        driverSpinner.setSelection(selPosition);

        submitBtn = (Button) findViewById(R.id.booking_complete_btn);
        submitBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);

        //this.setFinishOnTouchOutside(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

	@Override
    public void onClick(View view) {
    	super.onClick(view);
    	if (view == mBackBtn) {
    		super.transitBack();
		} else if (view == cancelBtn) {
            finish();
        } else if (view == submitBtn){
            int position = driverSpinner.getSelectedItemPosition();
            Map<String,String> driverInfo = driverInfos.get(position);
            final String driverUserName = driverInfo.get("driverUserName");
            final String driverLoginUserId = driverInfo.get("driverLoginUserId");
            final String vehicleId = driverInfo.get("vehicleId");
            final String driverMobileNumber = driverInfo.get("driverMobileNumber");
            final String driverVehicle = driverInfo.get("driverVehicle");
            final String assignDriverUserId = driverInfo.get("assignDriverUserId");
            final String assignDriverUserName = driverInfo.get("assignDriverUserName");
            final String assignDriverMobilePhone = driverInfo.get("assignDriverMobilePhone");
            DialogUtil.showProcessingDialog(this);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String result = ObsUtil.reassignDriver(obmBookingVehicleItem.getBookingVehicleItemId(), vehicleId, driverUserName, driverMobileNumber,
                                driverVehicle, driverLoginUserId, assignDriverUserId, assignDriverUserName, assignDriverMobilePhone, isPast);
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
                                    if (Const.VALUE_YES.equalsIgnoreCase(isPast)) {
                                        PreferenceUtil.saveString(ObsConst.KEY_HISTORY_LIST_NEEED_REFRESH, Const.VALUE_YES);
                                    } else {
                                        PreferenceUtil.saveString(ObsConst.KEY_UPCOMING_LIST_NEEED_REFRESH, Const.VALUE_YES);
                                    }
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
