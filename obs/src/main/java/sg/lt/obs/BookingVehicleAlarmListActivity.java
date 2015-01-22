package sg.lt.obs;

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
import org.ganjp.glib.core.util.StringUtil;

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
    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tracker t = ((ObsApplication) getApplication()).getTracker(ObsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Broadcast");
        t.send(new HitBuilders.AppViewBuilder().build());

        setContentView(R.layout.booking_vehicle_alarm_list);

        mTitleView = (TitleView)findViewById(R.id.title);
        mTitleView.setTitle(R.string.broadcast);
        mTitleView.hiddenLeftButton();
        mTitleView.setRightButton(R.string.skip, new TitleView.OnRightButtonClickListener() {
            @Override
            public void onClick(View button) {
                forward();
            }
        });

        mListView = (ListView)findViewById(R.id.booking_vehicle_alarm_lv);
        setListAdapterValue();
        mListView.setAdapter(mAdapter);
    }

    private void setListAdapterValue() {
        mObmBookingVehicleItems = ObmBookingVehicleItemDAO.getInstance().getBroadcastObmBookingVehicleItems();
        mAdapter = new BookingVehicleAlarmListAdapter(this, mObmBookingVehicleItems);
        if (mObmBookingVehicleItems==null || mObmBookingVehicleItems.isEmpty()) {
            mHandler.obtainMessage(0).sendToTarget();
        }
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
                            mAdapter.resetItems(ObmBookingVehicleItemDAO.getInstance().getBroadcastObmBookingVehicleItems());
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