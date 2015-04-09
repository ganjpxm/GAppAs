package sg.lt.obs.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import org.ganjp.glib.core.util.SystemUtil;

public class CustomeFontButton extends Button {

	public CustomeFontButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(SystemUtil.getFrutigerRoman(context));
	}
	
	public CustomeFontButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(SystemUtil.getFrutigerRoman(context));
	}

	public CustomeFontButton(Context context) {
		super(context);
		setTypeface(SystemUtil.getFrutigerRoman(context));
	}
}
