/**
 * KnowledgeListAdapter.java
 *
 * Created by Gan Jianping on 09/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.adapt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ganjp.glib.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import sg.lt.obs.R;
import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;

/**
 * <p>Knowledge list adapter</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class BookingVehicleHistoryListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
    private List<ObmBookingVehicleItem> mObmBookingVehicleItems = null;
    private Context mContext;

	public void addItems(List<ObmBookingVehicleItem> items) {
		mObmBookingVehicleItems.addAll(0, items);
        notifyDataSetChanged();
    }

    public void resetItems(List<ObmBookingVehicleItem> items) {
        mObmBookingVehicleItems = items;
        notifyDataSetChanged();
    }

	public BookingVehicleHistoryListAdapter(Context context) {
        super();
//        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObmBookingVehicleItems = new ArrayList<ObmBookingVehicleItem>();
    }

	public BookingVehicleHistoryListAdapter(Context context, List<ObmBookingVehicleItem> items) {
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
    public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = mInflater.inflate(R.layout.booking_vehicle_list_item, parent, false);
		}
		ObmBookingVehicleItem obmBookingVehicleItem = mObmBookingVehicleItems.get(position);

        TextView pickupTimeTv = ViewHolder.get(view, R.id.pickup_time_tv);
        String pickupTime = obmBookingVehicleItem.getPickupTime();
        pickupTimeTv.setText(pickupTime);

        String paymentMode = obmBookingVehicleItem.getPaymentMode();
        if (StringUtil.hasText(paymentMode) && "Cash".equalsIgnoreCase(paymentMode)) {
            TextView paymentStateTv = ViewHolder.get(view, R.id.payment_state_tv);
            paymentStateTv.setText(obmBookingVehicleItem.getPriceUnit() + " " + obmBookingVehicleItem.getPrice() + "\nCash");
            paymentStateTv.setTextColor(Color.RED);
        } else {
            String paymentStatus = obmBookingVehicleItem.getPaymentStatus();
            if (StringUtil.isNotEmpty(paymentStatus)) {
                TextView paymentStateTv = ViewHolder.get(view, R.id.payment_state_tv);
                if (StringUtil.hasText(paymentMode) && "Invoice".equalsIgnoreCase(paymentMode)) {
                    paymentStateTv.setText(paymentStatus + "\nInvoice");
                    paymentStateTv.setTextColor(Color.RED);
                } else {
                    paymentStateTv.setText(paymentStatus);
                    if ("Paid".equalsIgnoreCase(paymentStatus)) {
                        paymentStateTv.setTextColor(Color.BLACK);
                    } else {
                        paymentStateTv.setTextColor(Color.RED);
                    }
                }
            }
        }
		
		TextView bookingNumberTv = ViewHolder.get(view, R.id.booking_number_tv);
        if (ObsConst.BOOKING_STATUS_CD_ENQUIRY.equals(obmBookingVehicleItem.getBookingStatusCd()) ||
                ObsConst.BOOKING_STATUS_CD_UNSUCCESSFUL.equals(obmBookingVehicleItem.getBookingStatusCd()) ||
                ObsConst.BOOKING_STATUS_CD_PENDING.equals(obmBookingVehicleItem.getBookingStatusCd())) {
            bookingNumberTv.setText(Html.fromHtml("<big><font color='#ff0000'>Booking Enquiry</font></big>"));
        } else {
            String firstLine = obmBookingVehicleItem.getBookingNumber() + "<font color='gray'> - " + obmBookingVehicleItem.getBookingService() + "</font>";
            bookingNumberTv.setText(Html.fromHtml(firstLine));
        }

        ImageView remarkIv = ViewHolder.get(view, R.id.remark_iv);
        if (StringUtil.hasText(obmBookingVehicleItem.getRemark())) {
            remarkIv.setVisibility(View.VISIBLE);
        } else {
            remarkIv.setVisibility(View.GONE);
        }
        ImageView stopIv = ViewHolder.get(view, R.id.stop_iv);
        if (StringUtil.hasText(obmBookingVehicleItem.getStop1Address()) || StringUtil.hasText(obmBookingVehicleItem.getStop2Address())) {
            stopIv.setVisibility(View.VISIBLE);
        } else {
            stopIv.setVisibility(View.GONE);
        }

		TextView bookingAddressTv = ViewHolder.get(view, R.id.booking_address_tv);
		String address = "<b>";
		if ("0101".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
            if (StringUtil.hasText(obmBookingVehicleItem.getFlightNumber())) {
                address += "(" + obmBookingVehicleItem.getFlightNumber() + ") ";
            }
			address += obmBookingVehicleItem.getPickupAddress();
		} else {
			if ("0104".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
				address += "(" + obmBookingVehicleItem.getBookingHours() + " hours) " + obmBookingVehicleItem.getPickupAddress();
			} else {
				address += obmBookingVehicleItem.getPickupAddress();
			}
		}
		address += "</b> To ";
		if ("0102".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
			address += "<b>" + obmBookingVehicleItem.getFlightNumber() + "</b> " + obmBookingVehicleItem.getDestination();
		} else {
			address += obmBookingVehicleItem.getDestination();
		}
        address = "<img src='icon_location_black'/>  " + address;
		bookingAddressTv.setText(Html.fromHtml(address, new ImageGetter(), null));
		
		TextView bookingStateTv = ViewHolder.get(view, R.id.booking_state_tv);
		String bookingStatus = obmBookingVehicleItem.getBookingStatus();
		if ("Confirmed".equalsIgnoreCase(bookingStatus)) {

		}
        bookingStatus = "<font color='gray'>" + bookingStatus + " - " + obmBookingVehicleItem.getDriverUserName() + "</font>";
		bookingStateTv.setText(Html.fromHtml(bookingStatus));

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
            }
            else {
                Drawable d = mContext.getResources().getDrawable(id);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        }

    }
    
}
