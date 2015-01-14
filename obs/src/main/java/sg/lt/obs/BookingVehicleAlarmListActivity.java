package sg.lt.obs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.ganjp.glib.core.util.StringUtil;

import java.util.List;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.activity.ObsActivity;
import sg.lt.obs.common.adapt.BookingVehicleAlarmListAdapter;
import sg.lt.obs.common.dao.ObmBookingVehicleItemDAO;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.common.view.TitleView;


public class BookingVehicleAlarmListActivity extends ObsActivity {

    private BookingVehicleAlarmListAdapter mAdapter;
    private List<ObmBookingVehicleItem> mObmBookingVehicleItems;

    private TitleView mTitleView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_vehicle_alarm_list);

        mTitleView = (TitleView)findViewById(R.id.title);
        mTitleView.setTitle(R.string.title);
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
    }

    public void forward() {
        finish();
        Intent intent = new Intent(BookingVehicleAlarmListActivity.this, ObsBottomTabFragmentActivity.class);
        BookingVehicleAlarmListActivity.this.startActivity(intent);
        transitForward();
    }
}