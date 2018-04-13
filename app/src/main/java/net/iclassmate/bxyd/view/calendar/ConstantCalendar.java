package net.iclassmate.bxyd.view.calendar;

import android.content.Context;

public class ConstantCalendar {
	public static final String COLOR_TITLE_BACKGROUND="#ccccd2";
	/**
	 * 标题文字的颜色
	 */
	public static final String COLOR_TEXT_TITLE_BLACK = "#8c8c98";
	/**
	 * 文字默认颜色
	 */
	public static final String COLOR_TEXT_DEFAULT = "#202025";
	/**
	 * 分割线颜色
	 */
	public static final String COLOR_LINE_DEFAULT = "#efefef";
	/**
	 * 日历 今天 颜色
	 */
	public static final String COLOR_TODAY = "#65caff";
	/**
	 * 日历激活（选中）颜色
	 */
	public static final String COLOR_ACTIVE = "#65caff";
	/**
	 * 矩形填充色
	 */
	public static final String COLOR_RECT = "#e8f8f2";
	/**
	 * 小点距离底边的像素
	 */
	public static final int MARGIN_BOTTOM = 14;
	/**
	 * 小点的半径
	 */
	public static final int CIRCLE_SMALL = 6;
	
	/** dip转换px */
	public static int dip2px(Context context, int dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}
}