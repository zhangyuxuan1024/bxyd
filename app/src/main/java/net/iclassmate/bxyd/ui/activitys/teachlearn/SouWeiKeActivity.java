package net.iclassmate.bxyd.ui.activitys.teachlearn;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;

import net.iclassmate.bxyd.R;

import java.util.ArrayList;

/**
 * Created by xydbj on 2016.6.6.
 */
public class SouWeiKeActivity extends Activity implements View.OnClickListener{
    private ImageView iv_back,souweike_grade_iv_down_up,souweike_course_iv_down_up,souweike_et_cancel,souweike_iv_search;
    private TextView tv_back,souweike_grade_tv,souweike_course_tv;
    private EditText souweike_et;
    private View vMasker;
    private OptionsPickerView pvOptions_grade,pvOptions_course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souweike);
        initView();
        ShowGrade();
        ShowCourse();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.souweike_grade_iv_down_up:
                souweike_grade_iv_down_up.setImageDrawable(getResources().getDrawable(R.mipmap.ic_up));
                pvOptions_grade.show();
                break;
            case R.id.souweike_course_iv_down_up:
                souweike_course_iv_down_up.setImageDrawable(getResources().getDrawable(R.mipmap.ic_up));
                pvOptions_course.show();
                break;
            case R.id.souweike_et_cancel:
                souweike_et.setText("");
                break;
            case R.id.souweike_iv_back:
                SouWeiKeActivity.this.finish();
                break;
            case R.id.souweike_tv_back:
                SouWeiKeActivity.this.finish();
                break;
        }
    }

    public void ShowCourse(){
        pvOptions_course = new OptionsPickerView(this);

        ArrayList<String> options_course1 = new ArrayList<String>();
        options_course1.add("科目");

        final ArrayList<ArrayList<String>> options_course2 = new ArrayList<ArrayList<String>>();
        ArrayList<String> options_course2_1 = new ArrayList<String>();
        options_course2_1.add("语文");
        options_course2_1.add("数学");
        options_course2_1.add("英语");
        options_course2_1.add("物理");
        options_course2_1.add("化学");
        options_course2_1.add("生物");
        options_course2_1.add("政治");
        options_course2_1.add("历史");
        options_course2_1.add("地理");
        options_course2_1.add("综合");
        options_course2.add(options_course2_1);

        pvOptions_course.setTitle("请选择科目");
        pvOptions_course.setPicker(options_course1, options_course2, false);
        pvOptions_course.setCyclic(false, false, false);
        pvOptions_course.setSelectOptions(1, 1, 1);
        pvOptions_course.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int o1, int o2, int o3) {
                String str = options_course2.get(o1).get(o2);
                souweike_course_tv.setText(str);
                vMasker.setVisibility(View.GONE);
                souweike_course_iv_down_up.setImageDrawable(getResources().getDrawable(R.mipmap.ic_down));
            }

            @Override
            public void onOptionsCancel() {
                souweike_course_iv_down_up.setImageDrawable(getResources().getDrawable(R.mipmap.ic_down));
            }
        });
    }

    public void ShowGrade(){
        pvOptions_grade = new OptionsPickerView(this);

        final ArrayList<String> options1 = new ArrayList<String>();
        options1.add("年级");

        final ArrayList<ArrayList<String>> options2 = new ArrayList<ArrayList<String>>();
        ArrayList<String> options2_1 = new ArrayList<String>();
        options2_1.add("\t\t\t\t");
        options2_1.add("一年级");
        options2_1.add("二年级");
        options2_1.add("三年级");
        options2_1.add("四年级");
        options2_1.add("五年级");
        options2_1.add("六年级");
        options2_1.add("七年级");
        options2_1.add("八年级");
        options2.add(options2_1);

        pvOptions_grade.setPicker(options1, options2, false);
        pvOptions_grade.setTitle("请选择年级");
        pvOptions_grade.setCyclic(false, false, false);
        pvOptions_grade.setSelectOptions(1, 1, 1);
        pvOptions_grade.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int o1, int o2, int o3) {
                String str = options2.get(o1).get(o2);
                souweike_grade_tv.setText(str);
                vMasker.setVisibility(View.GONE);
                souweike_grade_iv_down_up.setImageDrawable(getResources().getDrawable(R.mipmap.ic_down));
            }

            @Override
            public void onOptionsCancel() {
                souweike_grade_iv_down_up.setImageDrawable(getResources().getDrawable(R.mipmap.ic_down));
            }
        });
    }

    public void initView(){
        vMasker = findViewById(R.id.vMasker);
        iv_back = (ImageView) findViewById(R.id.souweike_iv_back);
        tv_back = (TextView) findViewById(R.id.souweike_tv_back);
        souweike_grade_iv_down_up = (ImageView) findViewById(R.id.souweike_grade_iv_down_up);
        souweike_course_iv_down_up = (ImageView) findViewById(R.id.souweike_course_iv_down_up);
        souweike_grade_tv = (TextView) findViewById(R.id.souweike_grade_tv);
        souweike_course_tv = (TextView) findViewById(R.id.souweike_course_tv);
        souweike_et_cancel = (ImageView) findViewById(R.id.souweike_et_cancel);
        souweike_et = (EditText) findViewById(R.id.souweike_et);
        souweike_iv_search = (ImageView) findViewById(R.id.souweike_iv_search);
        souweike_et.addTextChangedListener(mTextWatcher);
        souweike_grade_iv_down_up.setOnClickListener(this);
        souweike_course_iv_down_up.setOnClickListener(this);
        souweike_et_cancel.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_back.setOnClickListener(this);
    }

    public TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(souweike_et.getText().toString() != null && !souweike_et.getText().toString().equals("")){
                souweike_et_cancel.setVisibility(View.VISIBLE);
            } else {
                souweike_et_cancel.setVisibility(View.INVISIBLE);
            }
        }
    };
}