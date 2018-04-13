package net.iclassmate.bxyd.ui.activitys.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.ui.activitys.chat.ChatActivity;
import net.iclassmate.bxyd.ui.activitys.chat.DiscussionActivity;
import net.iclassmate.bxyd.ui.activitys.chat.GroupChatActivity;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;
import net.iclassmate.bxyd.view.TitleBar.TitleOnClickListener;
import net.iclassmate.bxyd.view.calendar.CalendarMonthCard;
import net.iclassmate.bxyd.view.calendar.CustomDate;
import net.iclassmate.bxyd.view.calendar.ScrollerMonth;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 选择日期页面
 */
public class RecordCalendarActivity extends FragmentActivity implements TitleOnClickListener{

    private Context mContext;
    private TitleBar titleBar;
    private ScrollerMonth scrollerMonth;

    private String targetId, from, name, author;
    private int sessionType;    //类型   单聊1  群聊2  群组3
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record_calendar);
        mContext=this;
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleBar= (TitleBar) findViewById(R.id.record_date_title_bar);
        titleBar.setTitle("按日期查找");
        titleBar.setLeftIcon(R.mipmap.ic_fanhui,"返回");
        scrollerMonth = (ScrollerMonth) findViewById(R.id.scroller_month);

        sessionType = getIntent().getIntExtra("sessionType", 0);
    }

    private void initListener() {
        titleBar.setTitleClickListener(this);
        scrollerMonth.setCallBackListener(new CalendarMonthCard.OnCellBackListener() {
            @Override
            public void clickDate(CustomDate date) {
                if(!daxiao(date)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(date.getYear() + "-");
                    if (date.getMonth() < 10) {
                        stringBuilder.append("0" + date.getMonth() + "-");
                    } else {
                        stringBuilder.append(date.getMonth() + "-");
                    }

                    if (date.getDay() < 10) {
                        stringBuilder.append("0" + date.getDay());
                    } else {
                        stringBuilder.append(date.getDay());
                    }
                    Toast.makeText(RecordCalendarActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                    if (sessionType > 0) {
                        if (from.equals("person")) {
                            Intent intent = new Intent(UIUtils.getContext(), ChatActivity.class);
                            intent.putExtra("targetId", targetId);
                            intent.putExtra("time", stringBuilder.toString());
                            intent.putExtra("from", "RecordCalendarActivity");
                            intent.putExtra("author", author);
                            intent.putExtra("name", name);
//                    intent.putExtra("demand", "time");
                            startActivity(intent);
                        } else if (from.equals("group")) {
                            Intent intent = null;
                            if (sessionType == 2) {
                                intent = new Intent(UIUtils.getContext(), DiscussionActivity.class);
                            } else if (sessionType == 3 || sessionType == 4) {
                                intent = new Intent(UIUtils.getContext(), GroupChatActivity.class);
                            }
                            intent.putExtra("targetId", targetId);
                            intent.putExtra("time", stringBuilder.toString());
                            intent.putExtra("from", "RecordCalendarActivity");
                            intent.putExtra("author", author);
                            intent.putExtra("name", name);
//                    intent.putExtra("demand", "time");
                            startActivity(intent);
                        }
                    }
                }else{
                    Toast.makeText(RecordCalendarActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void changeDate(CustomDate date) {
            }
        });
    }

    private void initData() {
        from = getIntent().getStringExtra("from");
        targetId = getIntent().getStringExtra("targetId");
        author = getIntent().getStringExtra("author");
        name = getIntent().getStringExtra("name");
    }

    public boolean daxiao(CustomDate customDate){
        boolean b = false;
        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyyMMdd");
        Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(customDate.getYear()+"");
        if(customDate.getMonth() < 10){
            stringBuilder.append("0" + customDate.getMonth());
        }else {
            stringBuilder.append(customDate.getMonth());
        }
        if(customDate.getDay() < 10){
            stringBuilder.append("0" + customDate.getDay());
        }else {
            stringBuilder.append(customDate.getDay());
        }
        String date = stringBuilder.toString();
        int date1 = Integer.valueOf(str).intValue();
        int date2 = Integer.valueOf(stringBuilder.toString()).intValue();
        Log.i("hi","当前时间:"+str+",stringBuilder:"+date2);
        if(date2 - date1 > 0){
            Log.v("hi","大于,date1:"+date1+",date2"+date2);
            b = true;
        }else{
            Log.v("hi","小于,date1:"+date1+",date2"+date2);
            b = false;
        }
        return b;
    }

    @Override
    public void leftClick() {
        finish();
    }

    @Override
    public void rightClick() {

    }

    @Override
    public void titleClick() {

    }

    @Override
    public void innerleftClick() {

    }

    @Override
    public void innerRightClick() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RecordCalendarActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RecordCalendarActivity");
        MobclickAgent.onPause(this);
    }
}