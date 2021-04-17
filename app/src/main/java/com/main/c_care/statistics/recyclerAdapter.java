package com.main.c_care.statistics;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.c_care.R;

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
        int tag = Integer.valueOf(data[position][0]);
        String[] TAG = {"Home", "Hotspot", "Workplace"};

        holder.txtTitle.setText(TAG[tag]);
        holder.txtLastVisit.setText("Last Visited: " + data[position][1]);
        holder.txtCount.setText(data[position][2]);
        Resources res = holder.itemView.getContext().getResources();

        if (Integer.valueOf(data[position][2]) >= 2 && tag != 0) {
            holder.txtCount.setBackground(res.getDrawable(R.drawable.bg_circle_red));
        }
        else {
            holder.txtCount.setBackground(res.getDrawable(R.drawable.bg_circle_green));
        }

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
        TextView txtLastVisit;
        TextView txtCount;
        OnClickRecyclerListener onClickRecyclerListener;

        public viewHolder(@NonNull View itemView, OnClickRecyclerListener onClickRecyclerListener) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLastVisit = itemView.findViewById(R.id.txtLastVisit);
            txtCount = itemView.findViewById(R.id.txtCount);

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
