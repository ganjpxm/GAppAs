package com.onetransport.obs.fragment;

import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.StringUtil;
import com.onetransport.obs.DriverLoginActivity;
import com.onetransport.obs.common.ObsConst;
import com.onetransport.obs.common.dao.ObmBookingVehicleItemDAO;
import com.onetransport.obs.common.other.ObsUtil;
import com.onetransport.obs.common.other.PreferenceUtil;
import com.onetransport.obs.common.view.TitleView;
import com.onetransport.obs.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class MoreFragment extends Fragment implements OnClickListener {

	private View mParent;
	private FragmentActivity mActivity;
	private TitleView mTitleView;
	
	private ToggleButton pushNotificationTb;
	private RelativeLayout clearDataRl;
	private RelativeLayout aboutRl;
	private RelativeLayout logoutRl;
	
	private String regId;
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at 'index'.
	 */
	public static MoreFragment newInstance(int index) {
		MoreFragment f = new MoreFragment();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		mParent = getView();

		mTitleView = (TitleView) mParent.findViewById(R.id.title);
		mTitleView.setTitle(R.string.more);
		mTitleView.hiddenLeftButton();
		mTitleView.hiddenRightButton();
		
		pushNotificationTb = (ToggleButton) mParent.findViewById(R.id.push_notification_tb);
		
		clearDataRl = (RelativeLayout) mParent.findViewById(R.id.clear_data_rl);
		clearDataRl.setOnClickListener(this);
		
		aboutRl = (RelativeLayout) mParent.findViewById(R.id.about_rl);
		aboutRl.setOnClickListener(this);
		
		logoutRl = (RelativeLayout) mParent.findViewById(R.id.logout_rl);
		logoutRl.setOnClickListener(this);
		
		
	}
	
	@Override
    public void onClick(View view) {
    	if (view == logoutRl) {
    		DialogUtil.showConfirmDialog(mActivity, "Alert", "Are you confirm to logout?", 
    			new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				if (which==-1) {
    					startActivity(new Intent(mActivity, DriverLoginActivity.class));	 
    				}
    			}
    		});
		} else if (view == clearDataRl) {
			DialogUtil.showConfirmDialog(mActivity, "Alert", "Are you confirm to clear all data?", 
	    		new DialogInterface.OnClickListener() {
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    				if (which==-1) {
	    					PreferenceUtil.clear();
	    					ObmBookingVehicleItemDAO.getInstance().delete();
	    				}
	    			}
	    	});
		} else if (view == aboutRl) {
			DialogUtil.showAlertDialog(mActivity, android.R.drawable.ic_dialog_alert, "Driver App", "Version 0.1.0", null, 
					mActivity.getString(R.string.ok));;
		}
    }

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden==false) {
			if (StringUtil.isEmpty(regId)) {
				regId = PreferenceUtil.getString(ObsConst.KEY_REG_ID_OBSD);
			} 
			if (StringUtil.isNotEmpty(regId)) {
				String regIdVale = PreferenceUtil.getString(regId);
				if (ObsConst.VALUE_YES.equalsIgnoreCase(regIdVale)) {
					pushNotificationTb.setChecked(true);
				} else {
					pushNotificationTb.setChecked(false);
				}
				pushNotificationTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				    	new Thread(new Runnable() {
				    		public void run() {
				    			try {
				    				if (isChecked) {
							        	String result = ObsUtil.registDevice(regId, ObsConst.VALUE_NO);
								    	if (ObsConst.VALUE_SCCESS.equalsIgnoreCase(result)) {
								    		PreferenceUtil.saveString(regId, ObsConst.VALUE_YES);
								    		mHandlerDialog.obtainMessage(1).sendToTarget();
								    	} else {
								    		mHandlerDialog.obtainMessage(2).sendToTarget();
								    	}
							        } else {
							        	String result = ObsUtil.registDevice(regId, ObsConst.VALUE_YES);
								    	if (ObsConst.VALUE_SCCESS.equalsIgnoreCase(result)) {
								    		PreferenceUtil.saveString(regId, ObsConst.VALUE_NO);
								    		mHandlerDialog.obtainMessage(0).sendToTarget();
								    	} else {
								    		mHandlerDialog.obtainMessage(2).sendToTarget();
								    	}
							        }
				    			} catch (Exception e) {
				    				e.printStackTrace();
				    				DialogUtil.dismissProgressDialog();
				    				mHandlerDialog.obtainMessage(2).sendToTarget();
				    			}
				    		}
				    	}).start();
				    }
				});
			}
		}
	}

	private Handler mHandlerDialog = new Handler() {  
    	public void handleMessage (Message msg) { 
    		if (msg.what==0) {
    			DialogUtil.showAlertDialog(mActivity, "Swich off push notification success");
    		} else if (msg.what==1) {
    			DialogUtil.showAlertDialog(mActivity, "Swich on push notification success");
    		} else if (msg.what==2) {
    			DialogUtil.showAlertDialog(mActivity, "Fail");
    		}
    	}  
    }; 
    
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
