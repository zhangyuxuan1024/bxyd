package net.iclassmate.bxyd.adapter.teachlearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.message.MessageFile;
import net.iclassmate.bxyd.utils.FileUtils;
import net.iclassmate.bxyd.utils.HttpManager;

import java.util.List;

/**
 * Created by xyd on 2016/8/26.
 */
public class ChatFileAdapter extends BaseAdapter {

    private Context context;
    private List<MessageFile> list;
    private HttpManager httpManager = new HttpManager();
    private View.OnClickListener onClickIsSeleck;
    private Handler handler = new Handler();

    public ChatFileAdapter(Context context, List<MessageFile> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickIsSeleck(View.OnClickListener onClickIsSeleck){
        this.onClickIsSeleck = onClickIsSeleck;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_file, null);
            holder.name = (TextView) convertView.findViewById(R.id.chat_file_name);
            holder.select = (ImageView) convertView.findViewById(R.id.chat_file_select);
            holder.iv = (ImageView) convertView.findViewById(R.id.chat_file_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MessageFile messageFile = list.get(position);
        if (messageFile.getObjectName().equals("RC:TxtMsg")) {
            if (messageFile.getContentType() == 2 || messageFile.getContentType() == 4) {
                holder.iv.setTag(messageFile.getFileId());
                setIcon(messageFile.getFileId(), holder.iv);
            } else {
                FileUtils.setImage(holder.iv, messageFile.getFileName(), null, context);
            }
        } else if (messageFile.getObjectName().equals("RC:ImgMsg")) {
            Picasso.with(context).load(messageFile.getUri()).config(Bitmap.Config.RGB_565).placeholder(R.mipmap.img_morentupian)
                    .error(R.mipmap.img_morentupian).into(holder.iv);
        }

        holder.name.setText(messageFile.getFileName());

        if(messageFile.isVisibility()){
            holder.select.setVisibility(View.VISIBLE);
        }else{
            holder.select.setVisibility(View.GONE);
        }

        if (messageFile.isCheck()) {
            holder.select.setImageResource(R.mipmap.ic_selected);
        } else {
            holder.select.setImageResource(R.mipmap.ic_circle);
        }
        holder.select.setTag(position);
        holder.select.setOnClickListener(onClickIsSeleck);
        return convertView;
    }

    private void setIcon(final String fileId, final ImageView icon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = httpManager.getThumbnailIconUrl(fileId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String tag = (String) icon.getTag();
                        if (tag.equals(fileId)) {
                            if(null != url && !TextUtils.isEmpty(url)) {
                                Picasso.with(context).load(url).resize((int) context.getResources().getDimension(R.dimen.view_43),
                                        (int) context.getResources().getDimension(R.dimen.view_43))
                                        .placeholder(R.mipmap.img_morentupian).error(R.mipmap.img_morentupian).config(Bitmap.Config.RGB_565).into(icon);
                            }else{
                                icon.setImageResource(R.mipmap.img_morentupian);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    class ViewHolder {
        TextView name;
        ImageView select, iv;
    }

}
