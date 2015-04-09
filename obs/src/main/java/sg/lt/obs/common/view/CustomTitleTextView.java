package sg.lt.obs.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import org.ganjp.glib.core.util.SystemUtil;

public class CustomTitleTextView extends TextView {
	
	public CustomTitleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(SystemUtil.getFrutigerRoman(context));
	}

	public CustomTitleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(SystemUtil.getFrutigerRoman(context));
	}

	public CustomTitleTextView(Context context) {
		super(context);
		setTypeface(SystemUtil.getFrutigerRoman(context));
	}
}
