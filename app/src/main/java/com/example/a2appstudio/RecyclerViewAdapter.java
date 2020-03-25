package com.example.a2appstudio;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<Website> mData;
    private Context context;
    private List<String> links = new ArrayList<>();

    public RecyclerViewAdapter(List<Website> mData) {
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.item_weblist, parent, false);

        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_link.setText(mData.get(position).getLink());
//        holder.img.setImageResource(mData.get(position).getImage());
        Picasso.get().load(mData.get(position).getImage()).into(holder.img);

        String link = mData.get(position).getLink();
        if (links.contains(link)) {
            holder.rootView.setForeground(new ColorDrawable(ContextCompat.getColor(context,R.color.colorControlActivated)));
        } else {
            holder.rootView.setForeground(new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Website getItem(int position){
        return mData.get(position);
    }

    public void setLinks(List<String> links) {
        this.links = links;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_link;
        private ImageView img;
        private LinearLayout rootView;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.title_weblist);
            tv_link = (TextView) itemView.findViewById(R.id.link_weblist);
            img = (ImageView) itemView.findViewById(R.id.img_weblist);
            rootView = (LinearLayout) itemView.findViewById(R.id.rootview_weblist);
        }
    }

}