package org.ganjp.gdemo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import org.ganjp.gdemo.R;
import org.ganjp.glib.core.util.DialogUtil;

public class UiInterfaceFragment extends Fragment implements OnClickListener {

	private View mParent;
	private FragmentActivity mActivity;

	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at 'index'.
	 */
	public static UiInterfaceFragment newInstance(int index) {
		UiInterfaceFragment uiInterfaceFragment = new UiInterfaceFragment();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		uiInterfaceFragment.setArguments(args);

		return uiInterfaceFragment;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ui_interface, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		mParent = getView();

	}
	
	@Override
    public void onClick(View view) {

    }

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	private Handler mHandlerDialog = new Handler() {  
    	public void handleMessage (Message msg) { 
    		if (msg.what==0) {
    			DialogUtil.showAlertDialog(mActivity, "Swich off push mNotificationContent success");
    		} else if (msg.what==1) {
    			DialogUtil.showAlertDialog(mActivity, "Swich on push mNotificationContent success");
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
