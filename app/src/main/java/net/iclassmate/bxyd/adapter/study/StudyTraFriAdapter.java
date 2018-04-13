package net.iclassmate.bxyd.adapter.study;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iclassmate.bxyd.R;
import net.iclassmate.bxyd.bean.study.CreateBy;
import net.iclassmate.bxyd.bean.study.OriginBulletinInfo;
import net.iclassmate.bxyd.bean.study.Resources;
import net.iclassmate.bxyd.bean.study.StudyMessageItem;
import net.iclassmate.bxyd.utils.BitmapUtils;
import net.iclassmate.bxyd.utils.DensityUtil;
import net.iclassmate.bxyd.view.study.ShapeImageView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by xydbj on 2016/6/6.
 */
public class StudyTraFriAdapter extends BaseAdapter {
    private Context context;
    private List<StudyMessageItem> list;
    private int msgType;
    private int totalType;


    public StudyTraFriAdapter(Context context, List<StudyMessageItem> list) {
        this.context = context;
        this.list = list;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (list != null) {
            ret = list.size();
        }
        return ret;
    }

    //ORIGIN:原创|FORWARD
    @Override
    public int getItemViewType(int position) {
        int ret = 0;
        StudyMessageItem item = list.get(position);
        String bulletinType = item.getBulletinType();
        bulletinType = bulletinType.toUpperCase();
        if (bulletinType.equals("ORIGIN")) {
            ret = 0;
        } else if (bulletinType.equals("FORWARD")) {
            ret = 1;
        }
        ret = 0;
        totalType = ret;
        return ret;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemViewType(i) == 0) {
            view = bindView(i, view, viewGroup);
        } else if (getItemViewType(i) == 1) {
            view = bindViewForward(i, view, viewGroup);
        }
        return view;
    }

    //原创数据
    public View bindView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.study_space_item, null);
            holder = new ViewHolder();
            holder.img_head = (ShapeImageView) view.findViewById(R.id.study_space_item_head);
            holder.tv_name = (TextView) view.findViewById(R.id.study_space_item_name);
            holder.tv_time = (TextView) view.findViewById(R.id.study_space_item_time);
            holder.tv_comment_count = (TextView) view.findViewById(R.id.study_space_item_comment_count);
            holder.tv_like = (TextView) view.findViewById(R.id.study_space_item_like);
            holder.gridView = (GridView) view.findViewById(R.id.study_space_item_gridview);
            holder.img_comment = (ImageView) view.findViewById(R.id.study_space_item_comment_img);
            holder.img_like = (ImageView) view.findViewById(R.id.study_space_item_like_img);
            holder.img_share = (ImageView) view.findViewById(R.id.study_space_item_share);
            holder.linearLayout = (LinearLayout) view.findViewById(R.id.study_space_item_linear);
            holder.tv_commment = (TextView) view.findViewById(R.id.study_space_item_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.linearLayout.setVisibility(View.GONE);
        holder.img_share.setVisibility(View.INVISIBLE);

        StudyMessageItem msg = list.get(i);
        int viewType = 0;
        String bulletinType = msg.getBulletinType().toUpperCase();
        if (bulletinType.equals("ORIGIN")) {
            viewType = 0;
        } else if (bulletinType.equals("FORWARD")) {
            viewType = 1;
        }
        if (viewType == 0) {
            String url = "", name = "";
            if (msg.getCreateBy() != null) {
                url = msg.getCreateBy().getAvatar();
                name = msg.getCreateBy().getName();
            }

            if (url != null) {
                Picasso.with(context).load(url).placeholder(R.mipmap.ic_geren_xuanren).resize(106, 106).into(holder.img_head);
            }
            holder.tv_name.setText(name);
            holder.tv_time.setText(getTime(msg.getCreatedOn()));
            holder.tv_comment_count.setText(msg.getCommented() + "");
            holder.tv_like.setText(msg.getLiked() + "");

            String content = msg.getContent().trim();
            if (content.equals("")) {
                holder.tv_commment.setVisibility(View.GONE);
            } else {
                holder.tv_commment.setVisibility(View.VISIBLE);
                holder.tv_commment.setText(content);
            }

            List<Resources> listStr = msg.getList();
            int ret = 0;
            holder.gridView.setVisibility(View.VISIBLE);
            if (listStr == null || listStr.size() == 0) {
                ret = 0;
                holder.gridView.setVisibility(View.GONE);
            } else if (listStr.size() == 1) {
                ret = 1;
            } else if (listStr.size() == 2 || listStr.size() == 4) {
                ret = 2;
            } else if (listStr.size() >= 3) {
                ret = 3;
            }

            holder.gridView.setNumColumns(ret);
            holder.gridView.setAdapter(new GridAdapter(listStr));
            holder.gridView.setTag(i);
            holder.img_comment.setTag(i);
            holder.img_like.setTag(i);
            holder.img_share.setTag(i);
            holder.img_head.setTag(i);
            holder.linearLayout.setTag(i);
            if (msg.isClickLiked()) {
                holder.img_like.setImageResource(R.mipmap.ic_great_clicked);
            } else {
                holder.img_like.setImageResource(R.mipmap.ic_great);
            }
            int msgType = getMsgType();
            if (msgType == 1) {
                holder.linearLayout.setVisibility(View.GONE);
                holder.img_share.setVisibility(View.GONE);
            }
        } else if (viewType == 1) {
            String url = "", name = "";
            OriginBulletinInfo info = msg.getOriginBulletinInfo();
            if (info.getCreateBy() != null) {
                url = info.getCreateBy().getAvatar();
                name = info.getCreateBy().getName();
            }
            if (url != null) {
                Picasso.with(context).load(url).placeholder(R.mipmap.ic_geren_xuanren).resize(106, 106).into(holder.img_head);
            }
            holder.tv_name.setText(name);
            holder.tv_time.setText(getTime(info.getCreatedOn()));
            // holder.tv_comment_count.setText(info.get + "");
            //holder.tv_like.setText(msg.getLiked() + "");

            String content = msg.getContent().trim();
            if (content.equals("")) {
                holder.tv_commment.setVisibility(View.GONE);
            } else {
                holder.tv_commment.setVisibility(View.VISIBLE);
                holder.tv_commment.setText(content);
            }

            List<Resources> listStr = info.getList();
            int ret = 0;
            holder.gridView.setVisibility(View.VISIBLE);
            if (listStr == null || listStr.size() == 0) {
                ret = 0;
                holder.gridView.setVisibility(View.GONE);
            } else if (listStr.size() == 1) {
                ret = 1;
            } else if (listStr.size() == 2 || listStr.size() == 4) {
                ret = 2;
            } else if (listStr.size() >= 3) {
                ret = 3;
            }

            holder.gridView.setNumColumns(ret);
            holder.gridView.setAdapter(new GridAdapter(listStr));
            holder.gridView.setTag(i);
            holder.img_comment.setTag(i);
            holder.img_like.setTag(i);
            holder.img_share.setTag(i);
            holder.img_head.setTag(i);
            holder.linearLayout.setTag(i);
            if (msg.isClickLiked()) {
                holder.img_like.setImageResource(R.mipmap.ic_great_clicked);
            } else {
                holder.img_like.setImageResource(R.mipmap.ic_great);
            }
            int msgType = getMsgType();
            if (msgType == 1) {
                holder.linearLayout.setVisibility(View.GONE);
                holder.img_share.setVisibility(View.GONE);
            }
        }
        return view;
    }

    //转发数据
    public View bindViewForward(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.study_space_item_forward, null);
            holder = new ViewHolder();
            holder.img_head = (ShapeImageView) view.findViewById(R.id.study_space_item_head);
            holder.tv_name = (TextView) view.findViewById(R.id.study_space_item_name);
            holder.tv_time = (TextView) view.findViewById(R.id.study_space_item_time);
            holder.tv_comment_count = (TextView) view.findViewById(R.id.study_space_item_comment_count);
            holder.tv_like = (TextView) view.findViewById(R.id.study_space_item_like);
            holder.gridView = (GridView) view.findViewById(R.id.study_space_item_gridview);
            holder.img_comment = (ImageView) view.findViewById(R.id.study_space_item_comment_img);
            holder.img_like = (ImageView) view.findViewById(R.id.study_space_item_like_img);
            holder.img_share = (ImageView) view.findViewById(R.id.study_space_item_share);
            holder.linearLayout = (LinearLayout) view.findViewById(R.id.study_space_item_linear);
            holder.tv_commment = (TextView) view.findViewById(R.id.study_space_item_content);

            holder.img_head_forward = (ShapeImageView) view.findViewById(R.id.study_space_item_forward_head);
            holder.tv_name_forward = (TextView) view.findViewById(R.id.study_space_item_forward_name);
            holder.tv_time_forward = (TextView) view.findViewById(R.id.study_space_item_forward_time);
            holder.tv_content_forward = (TextView) view.findViewById(R.id.study_space_item_forward_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        StudyMessageItem msg = list.get(i);

        String url = "", name = "";
        if (msg.getCreateBy() != null) {
            url = msg.getCreateBy().getAvatar();
            name = msg.getCreateBy().getName();
        }
        if (url != null) {
            Picasso.with(context).load(url).placeholder(R.mipmap.ic_geren_xuanren).resize(106, 106).into(holder.img_head);
        }
        holder.tv_name.setText(name);
        holder.tv_time.setText(getTime(msg.getCreatedOn()));
        holder.tv_comment_count.setText(msg.getCommented() + "");
        holder.tv_like.setText(msg.getLiked() + "");

        String content = msg.getContent().trim();
        if (content.equals("")) {
            holder.tv_commment.setVisibility(View.GONE);
        } else {
            holder.tv_commment.setVisibility(View.VISIBLE);
            holder.tv_commment.setText(content);
        }

        //forward
        OriginBulletinInfo info = msg.getOriginBulletinInfo();
        if (info != null) {
            content = info.getContent().trim();
            if (content.equals("")) {
                holder.tv_content_forward.setVisibility(View.GONE);
            } else {
                holder.tv_content_forward.setVisibility(View.VISIBLE);
                holder.tv_content_forward.setText(content);
            }
            holder.tv_time_forward.setText(getTime(info.getCreatedOn()));
            if (info.getCreateBy() != null) {
                CreateBy by = info.getCreateBy();
                holder.tv_name_forward.setText(by.getName());
                url = by.getAvatar();
                url = BitmapUtils.getImageUrl(url);
                Bitmap bm = BitmapUtils.stringtoBitmap(url);
                holder.img_head_forward.setImageBitmap(bm);
            }

            List<Resources> listStr = info.getList();
            int ret = 0;
            holder.gridView.setVisibility(View.VISIBLE);
            holder.gridView.setBackgroundColor(Color.parseColor("#efefef"));
            if (listStr == null || listStr.size() == 0) {
                ret = 0;
                holder.gridView.setVisibility(View.GONE);
            } else if (listStr.size() == 1) {
                ret = 1;
            } else if (listStr.size() == 2 || listStr.size() == 4) {
                ret = 2;
            } else if (listStr.size() >= 3) {
                ret = 3;
            }
            holder.gridView.setNumColumns(ret);
            holder.gridView.setAdapter(new GridAdapter(listStr));
        }

        holder.gridView.setTag(i);
        holder.img_comment.setTag(i);
        holder.img_like.setTag(i);
        holder.img_share.setTag(i);
        holder.img_head.setTag(i);
        holder.linearLayout.setTag(i);
        if (msg.isClickLiked()) {
            holder.img_like.setImageResource(R.mipmap.ic_great_clicked);
        } else {
            holder.img_like.setImageResource(R.mipmap.ic_great);
        }
        int msgType = getMsgType();
        if (msgType == 1) {
            holder.linearLayout.setVisibility(View.GONE);
            holder.img_share.setVisibility(View.GONE);
        }
        return view;
    }

    class ViewHolder {
        ShapeImageView img_head, img_head_forward;
        ImageView img_comment, img_like, img_share;
        TextView tv_name, tv_time, tv_commment, tv_comment_count, tv_like;
        GridView gridView;
        LinearLayout linearLayout;

        TextView tv_name_forward, tv_time_forward, tv_content_forward;
    }

    class GridAdapter extends BaseAdapter {
        private List<Resources> picList;

        public GridAdapter(List<Resources> picList) {
            this.picList = picList;
        }

        @Override
        public int getCount() {
            int ret = 0;
            if (picList != null) {
                ret = picList.size();
            }
            return ret;
        }

        @Override
        public Object getItem(int i) {
            return picList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            int ret = 0;
            Resources resources = picList.get(position);
            String type = resources.getType();
            if (type != null && type.equals("图片")) {
                ret = 0;
            } else {
                ret = 1;
            }
            return ret;
        }

        private int getGridType(int position) {
            int ret = 0;
            if (picList.size() == 0) {
                ret = 0;
            } else if (picList.size() == 1) {
                ret = 1;
            } else if (picList.size() == 2 || picList.size() == 4) {
                ret = 2;
            } else if (picList.size() >= 3) {
                ret = 3;
            }
            return ret;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (getItemViewType(i) == 0) {
                view = bindPicView(i, view, viewGroup);
            } else if (getItemViewType(i) == 1) {
                view = bindFileView(i, view, viewGroup);
            }
            return view;
        }

        public View bindPicView(int position, View view, ViewGroup viewGroup) {
            PicHolder holder = null;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth() - DensityUtil.dip2px(context, 90);
            int height = wm.getDefaultDisplay().getHeight();
            if (totalType == 1) {
                width = wm.getDefaultDisplay().getWidth() - DensityUtil.dip2px(context, 160);
            }
            int type = getGridType(position);

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.study_space_item_image, null);
                if (type > 0) {
                    if (type == 1) {
                        if (totalType == 0) {
                            view.setLayoutParams(new GridView.LayoutParams((int) context.getResources().getDimension(R.dimen.view_109), (int) context.getResources().getDimension(R.dimen.view_109)));
                        } else if (totalType == 1) {
                            view.setLayoutParams(new GridView.LayoutParams((int) context.getResources().getDimension(R.dimen.view_109), (int) context.getResources().getDimension(R.dimen.view_109)));
                        }
                    } else {
                        view.setLayoutParams(new GridView.LayoutParams(width / type, width / type));
                    }
                }
                holder = new PicHolder();
                holder.img = (ImageView) view.findViewById(R.id.study_space_item_img);
                holder.frameLayout = (RelativeLayout) view.findViewById(R.id.img_frame);
                view.setTag(holder);
            } else {
                holder = (PicHolder) view.getTag();
            }
            if (totalType == 0) {
//                if (type == 1) {
//                    para.width = (int) context.getResources().getDimension(R.dimen.view_109);
//                    para.height = (int) context.getResources().getDimension(R.dimen.view_109);
//                } else if (type == 2) {
//                    para.width = width / 2;
//                    para.height = width / 2;
//                } else if (type == 3) {
//                    para.width = width / 3;
//                    para.height = width / 3;
//                }
//                holder.img.setLayoutParams(para);
                holder.frameLayout.setBackgroundColor(Color.parseColor("#efefef"));
            } else if (totalType == 1) {
//                width = wm.getDefaultDisplay().getWidth() - DensityUtil.dip2px(context, 160);
//                if (type == 1) {
//                    para.width = (int) context.getResources().getDimension(R.dimen.view_109);
//                    para.height = (int) context.getResources().getDimension(R.dimen.view_109);
//                } else if (type == 2) {
//                    para.width = width / 2;
//                    para.height = width / 2;
//                } else if (type == 3) {
//                    para.width = width / 3;
//                    para.height = width / 3;
//                }
//                holder.img.setLayoutParams(para);
                holder.frameLayout.setBackgroundColor(Color.parseColor("#ccccd2"));
            }
            //Picasso.with(context).load(picList.get(i)).resize(para.width, para.height).into(holder.img);
            Resources resources = picList.get(position);
            String url = resources.getImage();
            if (url!=null) {
                Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).resize(106, 106).into(holder.img);
            }
            return view;
        }

        public View bindFileView(int position, View view, ViewGroup viewGroup) {
            FileHolder holder = null;
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth() - DensityUtil.dip2px(context, 90);
            int height = wm.getDefaultDisplay().getHeight();
            if (totalType == 1) {
                width = wm.getDefaultDisplay().getWidth() - DensityUtil.dip2px(context, 160);
            }
            int type = getGridType(position);
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.study_listview_grid_item, null);
                if (type > 0) {
                    if (type == 1) {
                        if (totalType == 0) {
                            view.setLayoutParams(new GridView.LayoutParams((int) context.getResources().getDimension(R.dimen.view_109), (int) context.getResources().getDimension(R.dimen.view_109)));
                        } else if (totalType == 1) {
                            view.setLayoutParams(new GridView.LayoutParams((int) context.getResources().getDimension(R.dimen.view_109), (int) context.getResources().getDimension(R.dimen.view_109)));
                        }
                    } else {
                        view.setLayoutParams(new GridView.LayoutParams(width / type, width / type));
                    }
                }
                holder = new FileHolder();
                holder.img = (ImageView) view.findViewById(R.id.grid_item_img);
                holder.tv_name = (TextView) view.findViewById(R.id.grid_item_tv);
                holder.relativeLayout = (RelativeLayout) view.findViewById(R.id.item_relative);
                view.setTag(holder);
            } else {
                holder = (FileHolder) view.getTag();
            }
            Resources resources = picList.get(position);
            String name = resources.getName();
            String url = resources.getImage();
            setImage(holder.img, name, url);
            holder.tv_name.setText(name);

            if (totalType == 0) {
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#efefef"));
            } else if (totalType == 1) {
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#ccccd2"));
            }
            return view;
        }

        public void setImage(ImageView img, String name, String url) {
            if (name.contains(".")) {
                name = name.substring(name.lastIndexOf(".") + 1, name.length());
            }
            name = name.toLowerCase();
            switch (name) {
                case "doc":
                case "docx":
                case "txt":
                case "pdf":
                case "wps":
                    img.setImageResource(R.mipmap.ic_wendang02);
                    break;
                case "wav":
                case "mp3":
                case "wma":
                case "wva":
                case "ogg":
                case "ape":
                case "aif":
                case "au":
                case "ram":
                case "mmf":
                case "amr":
                case "aac":
                case "flac":
                    img.setImageResource(R.mipmap.ic_yinpin02);
                    break;
                case "xls":
                case "xlsx":
                case "et":
                    img.setImageResource(R.mipmap.ic_biaoge02);
                    break;
                case "ppt":
                case "pptx":
                case "dps":
                    img.setImageResource(R.mipmap.ic_ppt);
                    break;
                case "avi":
                case "mpg":
                case "mpeg":
                case "mov":
                case "rm":
                case "rmvb":
                case "mp4":
                case "3gp":
                case "flv":
                    if (url != null) {
                        Picasso.with(context).load(url).placeholder(R.mipmap.img_morentupian).resize(106, 106).into(img);
                    }
                    break;
                default:
                    img.setImageResource(R.mipmap.ic_qita);
                    break;
            }
        }
    }

    class PicHolder {
        ImageView img;
        RelativeLayout frameLayout;
    }

    class FileHolder {
        ImageView img;
        TextView tv_name;
        RelativeLayout relativeLayout;
    }

    public String getTime(String time) {
        String result = "";
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result = dataFormat.format(Long.parseLong(time));
        return result;
    }
}