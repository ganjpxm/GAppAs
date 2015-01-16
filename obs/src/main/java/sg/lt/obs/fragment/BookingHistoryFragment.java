package sg.lt.obs.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.view.RefreshableView;
import org.ganjp.glib.core.view.RefreshableView.PullToRefreshListener;
import org.ganjp.glib.thirdparty.astickyheader.SimpleSectionedListAdapter;
import org.ganjp.glib.thirdparty.astickyheader.SimpleSectionedListAdapter.Section;
import sg.lt.obs.BookingDetailFragmentActivity;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.adapt.BookingVehicleListAdapter;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.common.view.TitleView;
import sg.lt.obs.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BookingHistoryFragment extends Fragment implements OnItemClickListener {

	private ArrayList<String> mHeaderNames = new ArrayList<String>();
	private ArrayList<Integer> mHeaderPositions = new ArrayList<Integer>();
	private BookingVehicleListAdapter mAdapter;
	private List<ObmBookingVehicleItem> mObmBookingVehicleItems;
	private List<ObmBookingVehicleItem> mNewObmBookingVehicleItems;
	private ArrayList<Section> sections = new ArrayList<Section>();
	
	private ListView mListView;
	private View mParent;
	private FragmentActivity mActivity;
	private TitleView mTitleView;
	
	private RefreshableView refreshableView;
	private long mLastFetchTime;
	private String driverUserId;
	private SimpleSectionedListAdapter mSimpleSectionedListAdapter;
	
	
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at 'index'.
	 */
	public static BookingUpcomingFragment newInstance(int index) {
		BookingUpcomingFragment f = new BookingUpcomingFragment();
		
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
		View view = inflater.inflate(R.layout.fragment_booking_history, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		mParent = getView();

		mTitleView = (TitleView) mParent.findViewById(R.id.booking_history_title);
		mTitleView.setTitle(R.string.booking_history_title);
		mTitleView.hiddenLeftButton();
		mTitleView.hiddenRightButton();
		
		initControls();
		
		refreshableView = (RefreshableView) mParent.findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					mHandler.obtainMessage(1).sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 1);
	}
	
	private void initControls() {
		driverUserId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
		mObmBookingVehicleItems = ObmBookingVehicleItemDAO.getInstance().getObmBookingVehicleItems(driverUserId, ObmBookingVehicleItemDAO.FLAG_PAST);
		mLastFetchTime = new Date().getTime();
				
		for (int i=0; i<mObmBookingVehicleItems.size(); i++) {
			ObmBookingVehicleItem obmBookingVehicleItem = mObmBookingVehicleItems.get(i);
			Date pickupDate = obmBookingVehicleItem.getPickupDate();
			String date = "";
			if (DateUtil.isCurrentDate(pickupDate)) {
				date = DateUtil.formateDate(pickupDate, "EEE, dd MMM");
			} else {
				date = DateUtil.formateDate(pickupDate, "EEE, dd MMM yyyy");
			}
			if (mHeaderNames.indexOf(date)==-1) {
				mHeaderNames.add(date);
				mHeaderPositions.add(i);
			}
		}
		mListView = (ListView)mActivity.findViewById(R.id.booking_history_list);
		mAdapter = new BookingVehicleListAdapter(mActivity, mObmBookingVehicleItems);
		for (int i = 0; i < mHeaderPositions.size(); i++) {
			sections.add(new Section(mHeaderPositions.get(i), mHeaderNames.get(i)));
		}
		mSimpleSectionedListAdapter = new SimpleSectionedListAdapter(mActivity, mAdapter, R.layout.booking_vehicle_list_item_header, R.id.header);
		mSimpleSectionedListAdapter.setSections(sections.toArray(new Section[0]));
		mListView.setAdapter(mSimpleSectionedListAdapter);
		mListView.setOnItemClickListener(this);
	}


	private Handler mHandler = new Handler() {  
    	public void handleMessage (Message msg) { 
    		if (msg.what==0) {
    			refreshableView.finishRefreshing();
    		} else if (msg.what==1) {
    			Date currentDate = new Date();
    			mNewObmBookingVehicleItems = ObmBookingVehicleItemDAO.getInstance().getObmBookingVehicleItems(driverUserId, mLastFetchTime, currentDate.getTime());
    			mLastFetchTime = currentDate.getTime();
				if (mNewObmBookingVehicleItems!=null && !mNewObmBookingVehicleItems.isEmpty()) {
					int newLength = mNewObmBookingVehicleItems.size();
					
					for (ObmBookingVehicleItem obmBookingVehicleItem : mNewObmBookingVehicleItems) {
						obmBookingVehicleItem.setNew(true);
					}
					for (int i=0; i<mHeaderPositions.size(); i++) {
						mHeaderPositions.set(i, mHeaderPositions.get(i)+newLength);
					}
					
					if (!mHeaderNames.contains("New Item")) {
						mHeaderNames.add(0, "New Item");
						mHeaderPositions.add(0, 0);
					}

					sections = new ArrayList<Section>();
					for (int i = 0; i < mHeaderPositions.size(); i++) {
						sections.add(new Section(mHeaderPositions.get(i), mHeaderNames.get(i)));
					}
					mAdapter.addItems(mNewObmBookingVehicleItems);
		    		mSimpleSectionedListAdapter.setSections(sections.toArray(new Section[0]));
				}
    		}
    	}  
    }; 
    
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden==false && refreshableView!=null) {
			mHandler.obtainMessage(0).sendToTarget();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int actPosition = position;
		for (int headerPosition : mHeaderPositions) {
			if (headerPosition<actPosition) {
				actPosition -=  1;
			} else {
				break;
			}
		}
		ObmBookingVehicleItem obmBookingVehicleItem = mAdapter.getItem(actPosition);
        if (obmBookingVehicleItem!=null) {
        	Intent intent = new Intent(getActivity(), BookingDetailFragmentActivity.class);
        	intent.putExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS, obmBookingVehicleItem);
        	getActivity().startActivity(intent);
        	getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }


}
