package sg.lt.obs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.ganjp.glib.core.base.ActivityStack;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.NetworkUtil;
import org.ganjp.glib.core.util.StringUtil;
import org.ganjp.glib.core.view.RefreshableView;

import java.util.List;
import java.util.Map;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.activity.ObsActivity;
import sg.lt.obs.common.adapt.BookingVehicleAlarmListAdapter;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsApplication;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.view.TitleView;


public class BookingVehicleAlarmListActivity extends ObsActivity {

    private BookingVehicleAlarmListAdapter mAdapter;
    private List<ObmBookingVehicleItem> mObmBookingVehicleItems;

    private TitleView mTitleView;
    private ListView mListView;
    private RefreshableView refreshableView;

    private Activity mActivity;

    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        Tracker t = ((ObsApplication) getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Broadcast");
        t.send(new HitBuilders.AppViewBuilder().build());

        setContentView(R.layout.booking_vehicle_alarm_list);

        mTitleView = (TitleView)findViewById(R.id.title);
        mTitleView.setTitle(R.string.broadcast);
        mTitleView.hiddenRightButton();
        mTitleView.setLeftButton(R.string.skip, new TitleView.OnLeftButtonClickListener() {
            @Override
            public void onClick(View button) {
                forward();
            }
        });

        mListView = (ListView)findViewById(R.id.booking_vehicle_alarm_lv);
        setListAdapterValue();
        mListView.setAdapter(mAdapter);

        refreshableView = (RefreshableView)findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    if (NetworkUtil.isNetworkAvailable(mActivity)) {
                        Map<String,String> map = ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                        String broadcatBookingVehicleItemIds = map.get(ObsConst.KEY_BROADCAST_BOOKING_VEHICLE_ITEM_IDS);
                        if (StringUtil.hasText(broadcatBookingVehicleItemIds)) {
                            mHandler.obtainMessage(1).sendToTarget();
                        } else {
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

    private void setListAdapterValue() {
        mObmBookingVehicleItems = ObmBookingVehicleItemDAO.getInstance().getBroadcastObmBookingVehicleItems();
        if (mObmBookingVehicleItems==null || mObmBookingVehicleItems.isEmpty()) {
            mHandler.obtainMessage(0).sendToTarget();
        }
        mAdapter = new BookingVehicleAlarmListAdapter(this, mObmBookingVehicleItems);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityStack.setActiveActivity(this);
        if (isFirstTime == false) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Map<String,String> map = ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                        String broadcatBookingVehicleItemIds = map.get(ObsConst.KEY_BROADCAST_BOOKING_VEHICLE_ITEM_IDS);
                        if (StringUtil.hasText(broadcatBookingVehicleItemIds)) {
                            mHandler.obtainMessage(1).sendToTarget();
                        } else {
                            mHandler.obtainMessage(0).sendToTarget();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        isFirstTime = false;
    }

    @Override
    protected void onPause() {
        ActivityStack.setActiveActivity(null);
        super.onPause();
    }

    private void showEmptyAlertDialog(String content) {
        DialogUtil.showAlertDialog(this, android.R.drawable.ic_dialog_alert, getString(R.string.title),
            content, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    forward();
                }
            }, new String[]{getString(R.string.ok)});
    }

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {
            if (msg.what==0) {
                showEmptyAlertDialog("Thank you for all your broadcast bookings have been accepted or rejected.");
            } else if (msg.what==1) {
                mAdapter.resetItems(ObmBookingVehicleItemDAO.getInstance().getBroadcastObmBookingVehicleItems());
            }
        }
    };

    public void forward() {
        finish();
        Intent intent = new Intent(BookingVehicleAlarmListActivity.this, ObsBottomTabFragmentActivity.class);
        BookingVehicleAlarmListActivity.this.startActivity(intent);
        transitForward();
    }
}