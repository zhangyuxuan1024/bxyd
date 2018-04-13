package net.iclassmate.bxyd.view;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.iclassmate.bxyd.R;

import java.util.ArrayList;


/**
 * Created by xyd on 2016/5/17.
 */
public class PopMenu {

    private ArrayList<String> itemList;
    private ArrayList<String> iconList;
    private Activity mContext;
    private PopupWindow popupWindow;
    private ListView listView;

    public PopMenu(Activity mContext) {
        this.mContext = mContext;

        itemList = new ArrayList<>();
        iconList = new ArrayList<>();
        View view = LayoutInflater.from(mContext).inflate(R.layout.popmenu, null);

        //设置listView
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new PopAdapter());
        listView.setFocusableInTouchMode(true);//触摸获取焦点
        listView.setFocusable(true);

//        popupWindow=new PopupWindow(view,100, ViewGroup.LayoutParams.WRAP_CONTENT);//布局，宽度，高度ViewGroup.LayoutParams.WRAP_CONTENT;mContext.getResources().getDimensionPixelSize(R.dimen.popmenu_width)
        popupWindow = new PopupWindow(view, mContext.getResources().getDimensionPixelSize(R.dimen.popmenu_width), mContext.getResources().getDimensionPixelSize(R.dimen.popmenu_height));

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    //设置菜单项点击监听器
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    //批量添加菜单项
    public void addItems(String[] items) {
        for (String s : items) {
            itemList.add(s);
        }
        Log.i("TAG", "弹窗的题目："+itemList.toString());
    }

    public void addIcons(int[] icons) {
        for (int i : icons) {
            iconList.add(i + "");
        }
        Log.i("TAG", "弹窗的图片："+iconList.toString());
    }

    //单个添加菜单项
    public void addItem(String item) {
        itemList.add(item);
    }

    public void addIcon(int icon) {
        iconList.add(icon + "");
    }

    //下拉式 弹出 pop菜单
    public void showAsDropDown(View parent) {
        popupWindow.showAsDropDown(parent, mContext.getResources().getDimensionPixelSize(R.dimen.popmenu_xoff), mContext.getResources().getDimensionPixelSize(R.dimen.popmenu_yoff));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(dw);
        backgroundAlpha(mContext, 0.5f);//0.0-1.0

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                Log.i("OnDismissListener", "---------");
                backgroundAlpha(mContext, 1f);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    // 隐藏菜单
    public void dismiss() {
        popupWindow.dismiss();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                Log.i("OnDismissListener", "---------");
                backgroundAlpha(mContext, 1f);
            }
        });
    }


    private final class PopAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            Log.e("--", itemList.size() + "");
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.popmenu_item, null);
                viewHolder.item = (TextView) convertView.findViewById(R.id.tv_item);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.image_icon);

                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.item.setText(itemList.get(position));
            String iconId = iconList.get(position);
            int icon = Integer.parseInt(iconId);
            viewHolder.icon.setImageResource(icon);

            return convertView;
        }

        private final class ViewHolder {
            ImageView icon;
            TextView item;
        }
    }
}