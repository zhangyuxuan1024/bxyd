package net.iclassmate.bxyd.ui.activitys.constacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.ui.activitys.study.ScanActivity;
import net.iclassmate.bxyd.utils.UIUtils;
import net.iclassmate.bxyd.view.TitleBar;

public class AddFriendActivity extends FragmentActivity implements TitleBar.TitleOnClickListener, View.OnClickListener {
    private Context mContext;
    private TitleBar titleBar;
    private EditText addFriend;
    private ImageView mDelete;
    private TextView mPerson, mGroup, mOrg;
    private LinearLayout addLayout, searchLayout;
    private RelativeLayout sao, phoneContacts, addPerson, addGroup, addOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.addFriend_title_bar);
        titleBar.setLeftIcon("取消");
        titleBar.setTitleClickListener(this);

        mPerson = (TextView) findViewById(R.id.person_code);
        mGroup = (TextView) findViewById(R.id.group_code);
        mOrg = (TextView) findViewById(R.id.org_code);
        mDelete = (ImageView) findViewById(R.id.addFriend_delete);
        addFriend = (EditText) findViewById(R.id.addFriend_et);
        sao = (RelativeLayout) findViewById(R.id.add_sao_layout);
        phoneContacts = (RelativeLayout) findViewById(R.id.phone_contacts_layout);
        addLayout = (LinearLayout) findViewById(R.id.addFriend_layout1);
        searchLayout = (LinearLayout) findViewById(R.id.addFriend_layout2);
        addPerson = (RelativeLayout) findViewById(R.id.person_contacts_layout);
        addGroup = (RelativeLayout) findViewById(R.id.group_contacts_layout);
        addOrg = (RelativeLayout) findViewById(R.id.org_contacts_layout);
    }

    private void initData() {

    }

    private void initListener() {
        sao.setOnClickListener(this);
        phoneContacts.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        addPerson.setOnClickListener(this);
        addGroup.setOnClickListener(this);
        addOrg.setOnClickListener(this);

        addFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    addLayout.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.VISIBLE);
                    mDelete.setVisibility(View.VISIBLE);
                    String code = addFriend.getText().toString().trim();
                    mPerson.setText(code);
                    mGroup.setText(code);
                    mOrg.setText(code);
                } else {
                    addLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                    mDelete.setVisibility(View.INVISIBLE);
                }

                if (s.length() == 11 && isNum(s.toString())) {
                    addPerson.setVisibility(View.VISIBLE);
                    addGroup.setVisibility(View.GONE);
                    addOrg.setVisibility(View.GONE);
                } else if (s.length() == 18 && isNum(s.toString())) {
                    addPerson.setVisibility(View.GONE);
                    addGroup.setVisibility(View.GONE);
                    addOrg.setVisibility(View.VISIBLE);
                } else {
                    addPerson.setVisibility(View.VISIBLE);
                    addGroup.setVisibility(View.VISIBLE);
                    addOrg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchByNumber(final String s) {

        new Thread(new Runnable() {
            @Override
            public void run() {
//                httpManager.findUserInfo(s);
            }
        }).start();

    }


    @Override
    public void leftClick() {
        close();
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_sao_layout:
                Intent intentScan = new Intent(mContext, ScanActivity.class);
                startActivity(intentScan);
                break;
            case R.id.phone_contacts_layout:
                Intent intent = new Intent(AddFriendActivity.this, PhoneContactActivity.class);
                startActivity(intent);
                break;
            case R.id.person_contacts_layout://找朋友
                String personKey = addFriend.getText().toString().trim();
                Intent searchPerson = new Intent(UIUtils.getContext(), FriendResActivity.class);
                searchPerson.putExtra("type", "person");
                searchPerson.putExtra("key", personKey);
                startActivity(searchPerson);
                break;
            case R.id.group_contacts_layout://找群组
                String groupKey = addFriend.getText().toString().trim();
                Intent searchGroup = new Intent(UIUtils.getContext(), FriendResActivity.class);
                searchGroup.putExtra("type", "group");
                searchGroup.putExtra("key", groupKey);
                startActivity(searchGroup);
                break;
            case R.id.org_contacts_layout://找机构
                String orgKey = addFriend.getText().toString().trim();
                Intent searchOrg = new Intent(UIUtils.getContext(), FriendResActivity.class);
                searchOrg.putExtra("type", "org");
                searchOrg.putExtra("key", orgKey);
                startActivity(searchOrg);
                break;
            case R.id.addFriend_delete:
                addFriend.setText("");
                break;
        }
    }

    private boolean isNum(String str) {
        boolean ret = true;
        if (str == null || str.length() < 1) {
            ret = false;
        } else {
            for (int i = 0; i < str.length(); i++) {
                char at = str.charAt(i);
                if (at < '0' || at > '9') {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddFriendActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddFriendActivity");
        MobclickAgent.onPause(this);
    }
}
