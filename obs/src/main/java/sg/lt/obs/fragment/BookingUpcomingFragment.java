package sg.lt.obs.fragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.view.RefreshableView;
import org.ganjp.glib.core.view.RefreshableView.PullToRefreshListener;
import org.ganjp.glib.thirdparty.astickyheader.SimpleSectionedListAdapter;
import org.ganjp.glib.thirdparty.astickyheader.SimpleSectionedListAdapter.Section;
import sg.lt.obs.BookingDetailFragmentActivity;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.adapt.BookingVehicleListAdapter;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsUtil;
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

public class BookingUpcomingFragment extends Fragment implements OnItemClickListener {

	private ArrayList<String> mHeaderNames = new ArrayList<String>();
	private ArrayList<Integer> mHeaderPositions = new ArrayList<Integer>();
    private ArrayList<String> mBookingVehilceItemIds = new ArrayList<String>();
	private BookingVehicleListAdapter mAdapter;
	private List<ObmBookingVehicleItem> mObmBookingVehicleItems;
	private List<ObmBookingVehicleItem> mNewObmBookingVehicleItems;
	private ArrayList<Section> mSections = new ArrayList<Section>();
	private SimpleSectionedListAdapter mSimpleSectionedListAdapter;
	
	private ListView mListView;
	private View mView;
	private FragmentActivity mActivity;
	private TitleView mTitleView;
	
	private RefreshableView refreshableView;
	
	
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
		View view = inflater.inflate(R.layout.fragment_booking_upcoming, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		mView = getView();

		mTitleView = (TitleView) mView.findViewById(R.id.title);
		mTitleView.setTitle(R.string.booking_upcoming_title);
		mTitleView.hiddenLeftButton();
		mTitleView.hiddenRightButton();
		
//		mTitleView.setRightButton(R.string.sign_in, new OnRightButtonClickListener() {
//			@Override
//			public void onClick(View button) {
//				goSignInActivity();
//			}
//		});
		mListView = (ListView)mActivity.findViewById(R.id.list);
		initControls();
		mListView.setAdapter(mSimpleSectionedListAdapter);
		mListView.setOnItemClickListener(this);
		
		refreshableView = (RefreshableView) mView.findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					if (NetworkUtil.isNetworkAvailable(mActivity)) {
						ObmBookingVehicleItem[] obmBookingVehicleItems = ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
						if (obmBookingVehicleItems!=null) {
							int newLength = obmBookingVehicleItems.length;
							mNewObmBookingVehicleItems = new ArrayList<ObmBookingVehicleItem>();
							for (ObmBookingVehicleItem obmBookingVehicleItem : obmBookingVehicleItems) {
								obmBookingVehicleItem.setNew(true);
								obmBookingVehicleItem.setPickupDateTime(new Timestamp(DateUtil.getDate(obmBookingVehicleItem.getPickupDate(), obmBookingVehicleItem.getPickupTime()).getTime()));
								mNewObmBookingVehicleItems.add(obmBookingVehicleItem);
							}
							for (int i=0; i<mHeaderPositions.size(); i++) {
								mHeaderPositions.set(i, mHeaderPositions.get(i)+newLength);
							}

							if (!mHeaderNames.contains("New Item")) {
								mHeaderNames.add(0, "New Item");
								mHeaderPositions.add(0, 0);
							}

							mSections = new ArrayList<Section>();
							for (int i = 0; i < mHeaderPositions.size(); i++) {
								mSections.add(new Section(mHeaderPositions.get(i), mHeaderNames.get(i)));
							}
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
		}, 0);
	}

	private Handler mHandler = new Handler() {  
    	public void handleMessage (Message msg) { 
//    		initValue();
    		mAdapter.addItems(mNewObmBookingVehicleItems);
    		mSimpleSectionedListAdapter.setSections(mSections.toArray(new Section[0]));
    	}  
    }; 
	
	private void initControls() {
		initValue();
		mAdapter = new BookingVehicleListAdapter(mActivity, mObmBookingVehicleItems);
		for (int i = 0; i < mHeaderPositions.size(); i++) {
			mSections.add(new Section(mHeaderPositions.get(i), mHeaderNames.get(i)));
		}
		mSimpleSectionedListAdapter = new SimpleSectionedListAdapter(mActivity, mAdapter,
				R.layout.booking_vehicle_list_item_header, R.id.header);
		mSimpleSectionedListAdapter.setSections(mSections.toArray(new Section[0]));
	}
	
	private void initValue() {
		String driverUserId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
		mObmBookingVehicleItems = ObmBookingVehicleItemDAO.getInstance().getObmBookingVehicleItems(driverUserId, ObmBookingVehicleItemDAO.FLAG_UPCOMING);
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
            mBookingVehilceItemIds.add(obmBookingVehicleItem.getBookingVehicleItemId());
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
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
        	intent.putExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_OBSD, obmBookingVehicleItem);
        	getActivity().startActivity(intent);
        	getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }

}
