/**
 * KnowledgeListAdapter.java
 *
 * Created by Gan Jianping on 09/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package org.ganjp.gdemo.adapt;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ganjp.gdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * <p>Knowledge list adapter</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public class HomeNavDrawListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
    private List<Map<String,String>> mMaps = null;
    private Context mContext;

	public void addItems(List<Map<String,String>> items) {
        mMaps.addAll(0, items);
        notifyDataSetChanged();
    }

    public void resetItems(List<Map<String,String>> maps) {
        mMaps = maps;
        notifyDataSetChanged();
    }

	public HomeNavDrawListAdapter(Context context) {
        super();
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMaps = new ArrayList<Map<String,String>>();
    }

	public HomeNavDrawListAdapter(Context context, List<Map<String,String>> maps) {
        super();
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMaps = maps;
    }

	@Override
	public int getCount() {
		return mMaps.size();
	}

	@Override
	public Map<String,String> getItem(int position) {
		if(position >= mMaps.size()){
	    	return null;
	    }
	    return mMaps.get(position);
	}

    @Override
    public long getItemId(int position) {
        return 0;
    }
    
	public String getItemTitle(int position) {
		if(position < mMaps.size() && mMaps.get(position)!=null){
	    	return mMaps.get(position).get("title");
	    }
	    return "";
	}
	
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			view = mInflater.inflate(R.layout.list_item_home_nav_draw, parent, false);
		}
		final Map<String,String> map = mMaps.get(position);
        TextView titleTv = ViewHolder.get(view, R.id.title_tv);
        titleTv.setText(map.get("title"));

		return view;
    }

    public static class ViewHolder {
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

}
