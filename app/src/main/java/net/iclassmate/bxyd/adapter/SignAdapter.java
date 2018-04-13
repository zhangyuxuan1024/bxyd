package net.iclassmate.bxyd.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.index.PersonInfo;

import java.util.List;

/**
 * Created by xydbj on 2016/11/16.
 */
public class SignAdapter extends BaseAdapter {
    private Context context;
    private List<PersonInfo> list;

    private View.OnClickListener imgMaleClick;
    private View.OnClickListener imgFemaleClick;
    private View.OnTouchListener etNameListener;
    private View.OnTouchListener etPhoneListener;
    private View.OnTouchListener etIdListener;

    public SignAdapter(Context context, List<PersonInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (list != null) {
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int index = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sign_up, parent, false);
            holder = new ViewHolder();
            holder.img_male = (ImageView) convertView.findViewById(R.id.img_male);
            holder.img_female = (ImageView) convertView.findViewById(R.id.img_female);
            holder.et_name = (EditText) convertView.findViewById(R.id.sign_et_name);
            holder.et_phone = (EditText) convertView.findViewById(R.id.sign_et_phone);
            holder.et_id = (EditText) convertView.findViewById(R.id.sign_et_id);
            holder.tv_person = (TextView) convertView.findViewById(R.id.sign_tv_person);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PersonInfo info = list.get(position);
        holder.tv_person.setText("参加人信息 - " + (position + 1));
        if (info.isMaleSelect()) {
            holder.img_male.setImageResource(R.mipmap.ic_checked);
            holder.img_female.setImageResource(R.mipmap.ic_unchecked);
        } else {
            holder.img_male.setImageResource(R.mipmap.ic_unchecked);
            holder.img_female.setImageResource(R.mipmap.ic_checked);
        }

        holder.img_male.setTag(position);
        holder.img_male.setOnClickListener(imgMaleClick);
        holder.img_female.setTag(position);
        holder.img_female.setOnClickListener(imgFemaleClick);
        holder.et_name.setTag(position);
        holder.et_name.setOnTouchListener(etNameListener);
        holder.et_phone.setTag(position);
        holder.et_phone.setOnTouchListener(etPhoneListener);
        holder.et_id.setTag(position);
        holder.et_id.setOnTouchListener(etIdListener);

        holder.et_name.addTextChangedListener(new MyTextWatcher(holder.et_name));
        holder.et_phone.addTextChangedListener(new MyTextWatcher(holder.et_phone));
        holder.et_id.addTextChangedListener(new MyTextWatcher(holder.et_id));
        return convertView;
    }

    public void setImgMaleClick(View.OnClickListener imgMaleClick) {
        this.imgMaleClick = imgMaleClick;
    }

    public void setImgFemaleClick(View.OnClickListener imgFemaleClick) {
        this.imgFemaleClick = imgFemaleClick;
    }

    public void setEtNameListener(View.OnTouchListener etNameListener) {
        this.etNameListener = etNameListener;
    }

    public void setEtPhoneListener(View.OnTouchListener etPhoneListener) {
        this.etPhoneListener = etPhoneListener;
    }

    public void setEtIdListener(View.OnTouchListener etIdListener) {
        this.etIdListener = etIdListener;
    }

    class ViewHolder {
        EditText et_name, et_phone, et_id;
        ImageView img_male, img_female;
        TextView tv_person;
    }

    class MyTextWatcher implements TextWatcher {
        private EditText et;

        public MyTextWatcher(EditText et) {
            this.et = et;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !"".equals(s.toString())) {
                int position = (int) et.getTag();
                if (et.getId() == R.id.sign_et_name) {
                    list.get(position).setName(s.toString());
                } else if (et.getId() == R.id.sign_et_phone) {
                    list.get(position).setPhone(s.toString());
                } else if (et.getId() == R.id.sign_et_id) {
                    list.get(position).setId(s.toString());
                }
            }
        }

    }
}
