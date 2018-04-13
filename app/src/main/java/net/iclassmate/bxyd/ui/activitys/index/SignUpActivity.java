package net.iclassmate.bxyd.ui.activitys.index;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.adapter.SignAdapter;
import net.iclassmate.bxyd.bean.index.Banner;
import net.iclassmate.bxyd.bean.index.PersonInfo;
import net.iclassmate.bxyd.bean.index.Recommend;
import net.iclassmate.bxyd.constant.Constant;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignUpActivity extends FragmentActivity implements View.OnClickListener, OnDateSetListener {
    private Banner banner;
    private Recommend recommend;

    private TextView tv_pay, tv_back, tv_title, tv_time, tv_money, tv_sub, tv_add, tv_count, tv_count_money;
    private ImageView img_back;
    private TextView et_order_phone;

    private int person_count;
    private double price;
    private long startTime, endTime;
    private String adsId;

    private SignAdapter adapter;
    private List<PersonInfo> list;
    private ListView listView;

    private SharedPreferences sharedPreferences;

    SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        initData();

        timePickerDialog = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("请选择日期")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(false)
                .setMinMillseconds(startTime)
                .setMaxMillseconds(endTime)
                .setType(Type.YEAR_MONTH_DAY)
                .setToolBarTextColor(getResources().getColor(R.color.bx_tv_bm_unpress))
                .setThemeColor(getResources().getColor(R.color.bx_tv_bm_unpress))
                .setWheelItemTextNormalColor(getResources().getColor(R.color.bx_tv_bm_unpress))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.black))
                .build();
    }

    private void init() {
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, MODE_PRIVATE);

        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_pay.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.study_message_back);
        img_back.setOnClickListener(this);
        tv_back = (TextView) findViewById(R.id.study_message_left_tv);
        tv_back.setOnClickListener(this);
        tv_back.setText("返回");
        tv_title = (TextView) findViewById(R.id.study_message_title_tv);
        tv_title.setText("报名");

        tv_time = (TextView) findViewById(R.id.sign_tv_time);
        tv_time.setOnClickListener(this);
        tv_money = (TextView) findViewById(R.id.sign_tv_money);

        tv_sub = (TextView) findViewById(R.id.sign_tv_sub);
        tv_sub.setOnClickListener(this);
        tv_add = (TextView) findViewById(R.id.sign_tv_add);
        tv_add.setOnClickListener(this);
        tv_count = (TextView) findViewById(R.id.sign_tv_count);
        tv_count_money = (TextView) findViewById(R.id.sign_tv_count_money);

        et_order_phone = (TextView) findViewById(R.id.sign_tv_order_phone);
        String phone = sharedPreferences.getString(Constant.LOGIN_NUMBER, "");
        et_order_phone.setText(phone);

        listView = (ListView) findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new SignAdapter(this, list);
        PersonInfo personInfo = new PersonInfo();
        personInfo.setMaleSelect(true);
        list.add(personInfo);
        listView.setAdapter(adapter);

        person_count = 1;

        setAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                for (int i = 0; i < list.size(); i++) {
                    PersonInfo info = list.get(i);
                    if (info.getName() == null || info.getPhone() == null || info.getId() == null
                            || info.getName().trim().equals("") || info.getPhone().trim().equals("") || info.getId().equals("")) {
                        Toast.makeText(this, "参与人信息不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intent = new Intent(this, PayMoneyActivity.class);
                intent.putExtra("adsId", adsId);
                intent.putExtra("contactPhone", et_order_phone.getText().toString());
                intent.putExtra("joinAdsTime", tv_time.getText().toString());
                intent.putExtra("userNumber", tv_count.getText().toString());
                intent.putExtra("list", (Serializable) list);
                startActivity(intent);
                break;
            case R.id.study_message_back:
            case R.id.study_message_left_tv:
                finish();
                break;
            case R.id.sign_tv_sub:
                if (person_count < 2) {
                    return;
                }
                person_count--;
                tv_count.setText(person_count + "");
                list.clear();
                for (int i = 0; i < person_count; i++) {
                    PersonInfo info = new PersonInfo();
                    info.setMaleSelect(true);
                    list.add(info);
                }
                adapter.notifyDataSetChanged();

                tv_count_money.setText("合计：￥" + String.format("%.2f", person_count * price));
                break;
            case R.id.sign_tv_add:
                person_count++;
                tv_count.setText(person_count + "");
                list.clear();
                for (int i = 0; i < person_count; i++) {
                    PersonInfo info = new PersonInfo();
                    info.setMaleSelect(true);
                    list.add(info);
                }
                adapter.notifyDataSetChanged();

                tv_count_money.setText("合计：￥" + String.format("%.2f", person_count * price));
                break;
            case R.id.sign_tv_time:
                timePickerDialog.show(getSupportFragmentManager(), "year");
                break;
        }
    }

    private void initData() {
        Intent intent = getIntent();
        Object object = intent.getSerializableExtra("data");
        if (object instanceof Banner) {
            banner = (Banner) object;
        } else if (object instanceof Recommend) {
            recommend = (Recommend) object;
        }

        if (banner != null) {
            long time = banner.getStart_time();
            if (System.currentTimeMillis() > time) {
                time = System.currentTimeMillis();
            }
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy年MM月dd日");
            String start_time = dateFormater.format(time);
            tv_time.setText(start_time);

            price = banner.getPrice();
            tv_money.setText("￥" + String.format("%.2f", price));
            tv_count_money.setText("合计：￥" + String.format("%.2f", price));

            startTime = banner.getStart_time();
            endTime = banner.getEnd_time();
            adsId = banner.getId();
        } else if (recommend != null) {
            long time = recommend.getStart_time();
            if (System.currentTimeMillis() > time) {
                time = System.currentTimeMillis();
            }
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy年MM月dd日");
            String start_time = dateFormater.format(time);
            tv_time.setText(start_time);

            price = recommend.getPrice();
            tv_money.setText("￥" + String.format("%.2f", price));
            tv_count_money.setText("合计：￥" + String.format("%.2f", price));

            startTime = recommend.getStart_time();
            endTime = recommend.getEnd_time();
            adsId = recommend.getId();
        }

    }

    private void setAdapter() {
        adapter.setImgMaleClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = (int) v.getTag();
                PersonInfo info = list.get(postion);
                if (!info.isMaleSelect()) {
                    info.setMaleSelect(true);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter.setImgFemaleClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int postion = (int) v.getTag();
                PersonInfo info = list.get(postion);
                if (info.isMaleSelect()) {
                    info.setMaleSelect(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String text = getDateToString(millseconds);
        tv_time.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }
}
