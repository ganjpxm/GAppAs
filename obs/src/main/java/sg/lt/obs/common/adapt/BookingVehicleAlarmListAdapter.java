/**
 * KnowledgeListAdapter.java
 *
 * Created by Gan Jianping on 09/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.adapt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ganjp.glib.core.Const;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.lt.obs.ObsBottomTabFragmentActivity;
import sg.lt.obs.R;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;

/**
 * <p>Knowledge list adapter</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class BookingVehicleAlarmListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
    private List<ObmBookingVehicleItem> mObmBookingVehicleItems = null;
    private Context mContext;
    private String mResult;
    private String mAcceptAction;
    private String mRejectAction;

	public void addItems(List<ObmBookingVehicleItem> items) {
		mObmBookingVehicleItems.addAll(0, items);
        notifyDataSetChanged();
    }

    public void resetItems(List<ObmBookingVehicleItem> items) {
        mObmBookingVehicleItems = items;
        notifyDataSetChanged();
    }

	public BookingVehicleAlarmListAdapter(Context context) {
        super();
//        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObmBookingVehicleItems = new ArrayList<ObmBookingVehicleItem>();
    }

	public BookingVehicleAlarmListAdapter(Context context, List<ObmBookingVehicleItem> items) {
        super();
//        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObmBookingVehicleItems = items;
    }

	@Override
	public int getCount() {
		return mObmBookingVehicleItems.size();
	}

	@Override
	public ObmBookingVehicleItem getItem(int position) {
		if(position >= mObmBookingVehicleItems.size()){
	    	return null;
	    }
	    return mObmBookingVehicleItems.get(position);
	}

    @Override
    public long getItemId(int position) {
        return 0;
    }
    
	public String getItemUuid(int position) {
		if(position < mObmBookingVehicleItems.size() && mObmBookingVehicleItems.get(position)!=null){
	    	return mObmBookingVehicleItems.get(position).getBookingVehicleItemId();
	    }
	    return "";
	}
	
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			view = mInflater.inflate(R.layout.booking_vehicle_alarm_list_item, parent, false);
		}
		final ObmBookingVehicleItem obmBookingVehicleItem = mObmBookingVehicleItems.get(position);

        TextView bookingInfoTv = ViewHolder.get(view, R.id.booking_info_tv);
        String driverUserId = PreferenceUtil.getString(ObsConst.KEY_USER_ID_OBS);
        String bookingInfo = "";

        if (ObsConst.BOOKING_STATUS_CD_ENQUIRY.equals(obmBookingVehicleItem.getBookingStatusCd()) ||
                ObsConst.BOOKING_STATUS_CD_UNSUCCESSFUL.equals(obmBookingVehicleItem.getBookingStatusCd()) ||
                ObsConst.BOOKING_STATUS_CD_PENDING.equals(obmBookingVehicleItem.getBookingStatusCd())) {
            bookingInfo += "<b><big>Customer Enquiry</big></b> <br/> ";
            mAcceptAction = ObsConst.ACTION_ACCEPT_ENQUIRY_BOOKING;
            mRejectAction = ObsConst.ACTION_REJECT_ENQUIRY_BOOKING;
        } else {
            if ((StringUtil.hasText(obmBookingVehicleItem.getDriverLoginUserId()) && obmBookingVehicleItem.getDriverLoginUserId().equals(driverUserId))
                    || (StringUtil.hasText(obmBookingVehicleItem.getAssignDriverUserId()) && obmBookingVehicleItem.getAssignDriverUserId().equals(driverUserId))) {
                if (ObsConst.BOOKING_STATUS_CD_CANCELLED.equalsIgnoreCase(obmBookingVehicleItem.getBookingStatusCd())) {
                    bookingInfo += "<b><big>Cancel Booking</big></b> <br/> ";
                    mAcceptAction = ObsConst.ACTION_ACCEPT_CANCEL_BOOKING;
                    mRejectAction = ObsConst.ACTION_REJECT_CANCEL_BOOKING;
                } else {
                    bookingInfo += "<b><big>Booking Update</big></b> <br/> ";
                    mAcceptAction = ObsConst.ACTION_ACCEPT_UPDATE_BOOKING;
                    mRejectAction = ObsConst.ACTION_REJECT_UPDATE_BOOKING;
                }
            } else {
                bookingInfo += "<b><big>New Booking</big></b> <br/> ";
                mAcceptAction = ObsConst.ACTION_ACCEPT_NEW_BOOKING;
                mRejectAction = ObsConst.ACTION_REJECT_NEW_BOOKING;
            }
        }

        bookingInfo += obmBookingVehicleItem.getBookingService() + " (" + obmBookingVehicleItem.getBookingNumber() + ") - ";
        if (StringUtil.hasText(obmBookingVehicleItem.getPaymentMode()) && obmBookingVehicleItem.getPaymentMode().indexOf("Cash")!=-1) {
            bookingInfo += " <font color='#ff0000'>cash job</font>";
        } else {
            bookingInfo += " <font color='#ff0000'>account job</font>";
        }
        bookingInfo += "<br/>Pick-up : <font color='#ff0000'>" + DateUtil.formateDate(obmBookingVehicleItem.getPickupDateTime(), "EEE, dd MMM yyyy HH:mm a") + "</font> <br/>";
        bookingInfo += "<b>From</b> ";
        if ("0101".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
            if (StringUtil.hasText(obmBookingVehicleItem.getFlightNumber())) {
                bookingInfo += "(" + obmBookingVehicleItem.getFlightNumber() + ") ";
            }
            bookingInfo += obmBookingVehicleItem.getPickupAddress();
        } else {
            if ("0104".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
                bookingInfo += "(" + obmBookingVehicleItem.getBookingHours() + " hours) " + obmBookingVehicleItem.getPickupAddress();
            } else {
                bookingInfo += obmBookingVehicleItem.getPickupAddress();
            }
        }
        bookingInfo += " <b>To</b> ";
        if ("0102".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
            bookingInfo += "<b>" + obmBookingVehicleItem.getFlightNumber() + "</b> " + obmBookingVehicleItem.getDestination();
        } else {
            bookingInfo += obmBookingVehicleItem.getDestination();
        }
        bookingInfo += "<br/><img src='icon_vehicle_black'/> " + obmBookingVehicleItem.getVehicle();
        if (StringUtil.hasText(obmBookingVehicleItem.getNumberOfPassenger())) {
            bookingInfo += " - " + obmBookingVehicleItem.getNumberOfPassenger() + " pax";
        }
        if (StringUtil.hasText(obmBookingVehicleItem.getRemark())) {
            bookingInfo += "<br/><img src='icon_comment_red'/> Remark : " + obmBookingVehicleItem.getRemark();
        }
        bookingInfoTv.setText(Html.fromHtml(bookingInfo, new ImageGetter(), null));

        Button acceptBtn = ViewHolder.get(view, R.id.accept_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showConfirmDialog(mContext, mContext.getString(R.string.title), "Are you confirm that you accept the booking "
                                + obmBookingVehicleItem.getBookingNumber() + "?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == -1) { //-2
                                new Thread(new Runnable() {
                                    public void run() {
                                        mResult = ObsUtil.acceptOrRejectBooking(obmBookingVehicleItem.getBookingVehicleItemId(), mAcceptAction);
                                        mHandler.obtainMessage(0, position).sendToTarget();
					    		    }
					    	    }).start();
                            }
                        }
                    }
                );
            }
        });

        Button rejectBtn = ViewHolder.get(view, R.id.reject_btn);
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showConfirmDialog(mContext, mContext.getString(R.string.title), "Are you confirm that you reject the booking "
                                + obmBookingVehicleItem.getBookingNumber() + "?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == -1) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        mResult = ObsUtil.acceptOrRejectBooking(obmBookingVehicleItem.getBookingVehicleItemId(), mRejectAction);
                                        mHandler.obtainMessage(0, position).sendToTarget();
                                    }
                                }).start();
                            }
                        }
                    }
                );
            }
        });

		return view;
    }

    public static class ViewHolder {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;

            id = mContext.getResources().getIdentifier(source, "drawable", mContext.getPackageName());

            if (id == 0) {
                // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
                id = mContext.getResources().getIdentifier(source, "drawable", "android");
            }

            if (id == 0) {
                // prevent a crash if the resource still can't be found
                return null;
            } else {
                Drawable d = mContext.getResources().getDrawable(id);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {
            if (msg.what==0) {
                int position = (int)msg.obj;
                if (Const.VALUE_SUCCESS.equalsIgnoreCase(mResult)) {
                    showInfoDialog(mContext.getString(R.string.success), position);
                } else if (Const.VALUE_ACCEPTED.equalsIgnoreCase(mResult)) {
                    showInfoDialog(mContext.getString(R.string.accepted), position);
                } else if (Const.VALUE_FAIL.equalsIgnoreCase(mResult)) {
                    DialogUtil.showAlertDialog(mContext, mContext.getString(R.string.fail));
                }
            } else if (msg.what==1) {
                int position = (int)msg.obj;
                mObmBookingVehicleItems.remove(position);
                notifyDataSetChanged();
            } else if (msg.what==2) {
                Intent intent = new Intent(mContext, ObsBottomTabFragmentActivity.class);
                mContext.startActivity(intent);
            } else if (msg.what==3) {
                Toast.makeText(mContext, "Update Fail", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void showInfoDialog(String content, final int position) {
        DialogUtil.showAlertDialog(mContext, android.R.drawable.ic_dialog_info, mContext.getString(R.string.title),
            content, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Map<String, String> map = ObsUtil.getBookingVehicleItemsFromWeb(new HttpConnection(false), true);
                                String broadcatBookingVehicleItemIds = map.get(ObsConst.KEY_BROADCAST_BOOKING_VEHICLE_ITEM_IDS);
                                if (StringUtil.hasText(broadcatBookingVehicleItemIds)) {
                                    mHandler.obtainMessage(1, position).sendToTarget();
                                } else {
                                    mHandler.obtainMessage(2).sendToTarget();
                                }
                            } catch(Exception ex) {
                                ex.printStackTrace();
                                mHandler.obtainMessage(3).sendToTarget();
                            }
                        }
                    }).start();
                }
            }, new String[]{mContext.getString(R.string.ok)});
    }


}
