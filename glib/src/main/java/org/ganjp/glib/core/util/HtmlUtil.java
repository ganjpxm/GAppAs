/**
 * HtmlUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

/**
 * <p>Html Utility</p>
 *
 * @author GanJianping
 * @since 1.0
 */
public class HtmlUtil {
	
	/**
	 * 
	 * @param html
	 * @return
	 */
	public static String convertHtml(String html) {
		html = html.replace("%", "%25");
		html = html.replace("#", "%23");
		html = html.replace("\\", "%27");
		html = html.replace("?", "%3f");
		return html;
	}
	
	/**
	 * 
	 * @param errorMessage
	 * @return
	 */
	public static String getErrorHtml(String errorMessage) {
		String html = "<html><body><div style='top:20%; margin:5%; font-size:15px;line-height:2;'>" + errorMessage + "</div></body></head>";
		return convertHtml(html);
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String encodeURL(String url) {
		if (url != null) {
			url = url.replaceAll(" ", "%20");
		}
		return url;
	}
}
