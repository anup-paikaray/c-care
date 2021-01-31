package com.main.c_care.assessment;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.c_care.R;

public class questionAdapter extends RecyclerView.Adapter<questionAdapter.viewHolder> {
    public static final String TAG = "questionAdapter";
    private String[] questions;
    private OnClickRecyclerListener mOnClickRecyclerListener;

    public questionAdapter(String[] questions, OnClickRecyclerListener onClickRecyclerListener) {
        this.questions = questions;
        this.mOnClickRecyclerListener = onClickRecyclerListener;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_question_layout, parent, false);
        return new viewHolder(view, mOnClickRecyclerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.txtQuestion.setText(questions[position]);
    }

    @Override
    public int getItemCount() {
        return questions.length;
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtQuestion;
        Button btnYes;
        Button btnNo;
        OnClickRecyclerListener onClickRecyclerListener;

        public viewHolder(@NonNull View itemView, OnClickRecyclerListener onClickRecyclerListener) {
            super(itemView);

            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            btnYes = itemView.findViewById(R.id.btnYes);
            btnNo = itemView.findViewById(R.id.btnNo);

            this.onClickRecyclerListener = onClickRecyclerListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            boolean response;
            if (view.getId() == btnYes.getId())
                response = true;
            else if (view.getId() == btnNo.getId())
                response = false;
            else
                return;
            btnYes.setEnabled(!response);
            btnNo.setEnabled(response);
            Log.d(TAG, "onClick: " + response + " " + view.getId()+ " " + btnNo.getId() + " " + getAdapterPosition());
            onClickRecyclerListener.onRecyclerClick(getAdapterPosition(), response ? 1 : 0);
        }
    }

    public interface OnClickRecyclerListener {
        void onRecyclerClick(int position, int response);
    }
}
