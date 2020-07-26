package com.main.c_care;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.health.TimerStat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.viewHolder> {
    private String[][] data;
    private OnClickRecyclerListener mOnClickRecyclerListener;

    public recyclerAdapter(String[][] data, OnClickRecyclerListener onClickRecyclerListener) {
        this.data = data;
        this.mOnClickRecyclerListener = onClickRecyclerListener;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_layout, parent, false);
        return new viewHolder(view, mOnClickRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        int id = Integer.valueOf(data[position][0]);
        int tag = id % 10;
        String[] TAG = {"HOME", "HOTSPOT", "WORKPLACE", "GROUND"};
        holder.txtTitle.setText(TAG[tag]);
        Resources res = holder.itemView.getContext().getResources();
        if (tag == 0)
            holder.imgIcon.setImageDrawable(res.getDrawable(R.drawable.ic_home_white));
        else if (tag == 1)
            holder.imgIcon.setImageDrawable(res.getDrawable(R.drawable.ic_hotspot_white));
        else
            holder.imgIcon.setImageDrawable(res.getDrawable(R.drawable.ic_work_24dp));
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgIcon;
        TextView txtTitle;
        OnClickRecyclerListener onClickRecyclerListener;
        public viewHolder(@NonNull View itemView, OnClickRecyclerListener onClickRecyclerListener) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            this.onClickRecyclerListener = onClickRecyclerListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickRecyclerListener.onRecyclerClick(getAdapterPosition());
        }
    }

    public interface OnClickRecyclerListener {
        void onRecyclerClick(int position);
    }
}
