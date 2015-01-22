package sg.lt.obs.fragment;

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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.view.RefreshableView;
import org.ganjp.glib.core.view.RefreshableView.PullToRefreshListener;
import org.ganjp.glib.thirdparty.astickyheader.SimpleSectionedListAdapter;
import org.ganjp.glib.thirdparty.astickyheader.SimpleSectionedListAdapter.Section;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import sg.lt.obs.BookingHistoryDetailFragmentActivity;
import sg.lt.obs.BookingVehicleAlarmListActivity;
import sg.lt.obs.R;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.adapt.BookingVehicleHistoryListAdapter;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.common.view.TitleView;

public class BookingHistoryFragment extends Fragment implements OnItemClickListener {

	private ArrayList<String> mHeaderNames = new ArrayList<String>();
	private ArrayList<Integer> mHeaderPositions = new ArrayList<Integer>();
	private BookingVehicleHistoryListAdapter mAdapter;
	private List<ObmBookingVehicleItem> mObmBookingVehicleItems;
	private ArrayList<Section> mSections = new ArrayList<Section>();
	
	private ListView mListView;
	private View mParent;
	private FragmentActivity mActivity;
	private TitleView mTitleView;
	
	private RefreshableView refreshableView;
	private SimpleSectionedListAdapter mSimpleSectionedListAdapter;

    private boolean isFirstTime = true;
	
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

        mListView = (ListView)mActivity.findViewById(R.id.booking_history_list);
        resetListWithSectionValue();
        mListView.setAdapter(mSimpleSectionedListAdapter);
        mListView.setOnItemClickListener(this);
		
		refreshableView = (RefreshableView) mParent.findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    if (NetworkUtil.isNetworkAvailable(mActivity)) {
                        Map<String, String> resultMap = ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                        String updateSize = resultMap.get("updateSize");
                        String broadcastBookingVehicleItemIds = resultMap.get("broadcastBookingVehicleItemIds");
                        if (StringUtil.hasText(broadcastBookingVehicleItemIds)) {
                            mActivity.finish();
                            Intent intent = new Intent(mActivity, BookingVehicleAlarmListActivity.class);
                            mActivity.startActivity(intent);
                        }
                        if (StringUtil.hasText(updateSize)) {
                            mHandler.obtainMessage(0).sendToTarget();
                        }
                    } else {
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 1);

        refreshableView.clearFocus();
	}

    private void resetListWithSectionValue() {
        mHeaderNames = new ArrayList<String>();
        mHeaderPositions = new ArrayList<Integer>();
        mSections = new ArrayList<Section>();
        String driverUserId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
        mObmBookingVehicleItems = ObmBookingVehicleItemDAO.getInstance().getObmBookingVehicleItems(driverUserId, ObmBookingVehicleItemDAO.FLAG_PAST);
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
        if (mAdapter == null) {
            mAdapter = new BookingVehicleHistoryListAdapter(mActivity, mObmBookingVehicleItems);
        } else {
            mAdapter.resetItems(mObmBookingVehicleItems);
        }
        for (int i = 0; i < mHeaderPositions.size(); i++) {
            mSections.add(new Section(mHeaderPositions.get(i), mHeaderNames.get(i)));
        }
        if (mSimpleSectionedListAdapter==null) {
            mSimpleSectionedListAdapter = new SimpleSectionedListAdapter(mActivity, mAdapter, R.layout.booking_vehicle_list_item_header, R.id.header);
        }
        mSimpleSectionedListAdapter.setSections(mSections.toArray(new Section[0]));
    }


	private Handler mHandler = new Handler() {  
    	public void handleMessage (Message msg) {
            if (msg.what==0) {
                resetListWithSectionValue();
            }
    	}  
    }; 
    
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
        if (hidden==false) {
            if (isFirstTime==true) {
                if (NetworkUtil.isNetworkAvailable(mActivity)) {
                    new Thread(new Runnable() {
                        public void run() {
                            mHandler.obtainMessage(0).sendToTarget();
                        }
                    }).start();
                }
            } else {
                isFirstTime = false;
            }
            Tracker t = ((ObsApplication) mActivity.getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
            t.setScreenName("History Booking");
            t.send(new HitBuilders.AppViewBuilder().build());
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
        	Intent intent = new Intent(getActivity(), BookingHistoryDetailFragmentActivity.class);
        	intent.putExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBS, obmBookingVehicleItem);
        	getActivity().startActivity(intent);
        	getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }


}
