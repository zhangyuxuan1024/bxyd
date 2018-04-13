package net.iclassmate.bxyd.view.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;


@SuppressLint("ClickableViewAccessibility") 
public class ScrollerMonth extends LinearLayout{

	private Context mContext;
	private LinearLayout layoutWeek;
	private RecyclerViewMonth scrollerCalendar;
	
	public ScrollerMonth(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setOrientation(VERTICAL);
		createView();
	}
	
	private void createView(){
		//创建周表头容器
		layoutWeek = new LinearLayout(mContext);
		layoutWeek.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		layoutWeek.setOrientation(HORIZONTAL);
		layoutWeek.setPadding(0, ConstantCalendar.dip2px(mContext, 10), 0, ConstantCalendar.dip2px(mContext, 10));
		layoutWeek.setBackgroundColor(Color.parseColor(ConstantCalendar.COLOR_TITLE_BACKGROUND));
		addView(layoutWeek);
		//创建周表头元素
		for (int i = 0; i < 7; i++){
			TextView tvWeek = new TextView(mContext);
			tvWeek.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
			tvWeek.setGravity(Gravity.CENTER);
			tvWeek.setText(DateUtil.WEEKS_NAME[i]);
			tvWeek.setTextSize(13);
			tvWeek.setTextColor(Color.parseColor(ConstantCalendar.COLOR_TEXT_TITLE_BLACK));
			layoutWeek.addView(tvWeek);
		}
		scrollerCalendar = new RecyclerViewMonth(mContext);
		addView(scrollerCalendar);
	}

	public void setCallBackListener(CalendarMonthCard.OnCellBackListener callBackListener){
		scrollerCalendar.setOnCellClickListener(callBackListener);
	}
	
	public void scrollToToday(){
		scrollerCalendar.scrollToToday();
	}
	
	public void scrollToToday(CustomDate day){
		scrollerCalendar.scrollToDay(day);
	}
	
	public void updateScroll(){
		scrollerCalendar.updateScroll();
	}
}
