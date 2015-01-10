/**
 * KnowledgeListAdapter.java
 *
 * Created by Gan Jianping on 09/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package sg.lt.obs.common.adapt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.StringUtil;
import sg.lt.obs.common.entity.ObmBookingVehicleItem;
import sg.lt.obs.R;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * <p>Knowledge list adapter</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class BookingVehicleListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
    private List<ObmBookingVehicleItem> mObmBookingVehicleItems = null;

	public void addItems(List<ObmBookingVehicleItem> items) {
		mObmBookingVehicleItems.addAll(0, items);
        notifyDataSetChanged();
    }

	public BookingVehicleListAdapter(Context context) {
        super();
//        this.mInflater = LayoutInflater.from(context);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObmBookingVehicleItems = new ArrayList<ObmBookingVehicleItem>();
    }
	
	public BookingVehicleListAdapter(Context context, List<ObmBookingVehicleItem> items) {
        super();
//        this.mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.booking_vehicle_list_item, parent, false);
		}
		ObmBookingVehicleItem obmBookingVehicleItem = mObmBookingVehicleItems.get(position);
		
		TextView pickupTimeTv = ViewHolder.get(convertView, R.id.pickup_time_tv);
		if (obmBookingVehicleItem.isNew()) {
			Date pickupDateTime = DateUtil.getDate(obmBookingVehicleItem.getPickupDate(), obmBookingVehicleItem.getPickupTime());
			String pickupDateTimeStr = DateUtil.formateDate(new Date(pickupDateTime.getTime()), "dd MMM HH:mm");
			pickupTimeTv.setText(pickupDateTimeStr);
//			pickupTimeTv.setTextSize(14);
//			pickupTimeTv.setPadding(10, 20, 0, 5);
		} else {
			String pickupTime = obmBookingVehicleItem.getPickupTime();
			pickupTimeTv.setText(pickupTime);
		}
		String paymentStatus = obmBookingVehicleItem.getPaymentStatus();
		if (StringUtil.isNotEmpty(paymentStatus)) {
			TextView paymentStateTv = ViewHolder.get(convertView, R.id.payment_state_tv);
			paymentStateTv.setText(paymentStatus);
			if ("Paid".equalsIgnoreCase(paymentStatus)) {
				paymentStateTv.setTextColor(Color.rgb(85,220,76));
			} else {
				paymentStateTv.setTextColor(Color.rgb(255,0,0));
			}
		}
		
		TextView bookingNumberTv = ViewHolder.get(convertView, R.id.booking_number_tv);
		bookingNumberTv.setText(obmBookingVehicleItem.getBookingNumber());
		
		TextView bookingAddressTv = ViewHolder.get(convertView, R.id.booking_address_tv);
		String address = "<b>";
		if ("0101".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
			address += obmBookingVehicleItem.getFlightNumber() + " " + obmBookingVehicleItem.getPickupAddress();
		} else {
			if ("0104".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
				address += obmBookingVehicleItem.getBookingHours() + " " + obmBookingVehicleItem.getPickupAddress();
			} else {
				address += obmBookingVehicleItem.getPickupAddress();
			}
		}
		address += "</b> to ";
		if ("0102".equalsIgnoreCase(obmBookingVehicleItem.getBookingServiceCd())) {
			address += "<b>" + obmBookingVehicleItem.getFlightNumber() + "</b> " + obmBookingVehicleItem.getDestination();
		} else {
			address += obmBookingVehicleItem.getDestination();
		}
		bookingAddressTv.setText(Html.fromHtml(address));
		
		TextView bookingStateTv = ViewHolder.get(convertView, R.id.booking_state_tv);
		String bookingStatus = obmBookingVehicleItem.getBookingStatus();
		if ("Confirmed".equalsIgnoreCase(bookingStatus)) {
			bookingStatus = "<font color='gray'>" + bookingStatus + "</font>";
		}
		bookingStateTv.setText(Html.fromHtml(bookingStatus));
		return convertView;
		
//		View view = mInflater.inflate(R.layout.booking_vehicle_list_item, null);
//    	ImageView imageIv = (ImageView) view.findViewById(R.id.item_image_ib);
//    	if (StringUtil.isNotEmpty(mItems.get(position).getImagePath())) {
//    		JOneUtil.setImageSmall(imageIv, mItems.get(position).getImagePath());
//    	}
//    	
//    	TextView itemTitleTv = (TextView) view.findViewById(R.id.item_title_tv);
//		itemTitleTv.setText(Html.fromHtml(mItems.get(position).getTitle()));
//		TextView itemDescriptionTv = (TextView) view.findViewById(R.id.item_summary_tv);
//		itemDescriptionTv.setText(Html.fromHtml(mItems.get(position).getSummary()));
//      return
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
    
}
