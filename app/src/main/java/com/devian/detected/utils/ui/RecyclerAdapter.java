package com.devian.detected.utils.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devian.detected.R;
import com.devian.detected.utils.domain.Task;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    
    private List<Task> taskList;
    private OnItemClickListener listener;
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
    
        ViewHolder(View v) {
            super(v);
            view = v;
        }
    
        void bind(final Task task, final OnItemClickListener clickListener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.OnItemClicked(task);
                }
            });
        }
    }
    
    public RecyclerAdapter(List<Task> taskList, OnItemClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlayout_task, parent, false);
    
        return new ViewHolder(v);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (taskList == null)
            return;
        TextView title = holder.view.findViewById(R.id.task_tvTitle);
        TextView points = holder.view.findViewById(R.id.task_tvPoints);
        title.setText(taskList.get(position).getTitle());
        points.setText(String.valueOf(taskList.get(position).getReward()));
        
        ImageView imageView = holder.view.findViewById(R.id.task_imageView);
    
        Picasso.get()
                .load(taskList.get(position).getImgUrl())
                .fit()
                .centerCrop()
                .into(imageView);
    
        holder.bind(taskList.get(position), listener);
        
    }
    
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (taskList == null)
            return 0;
        return taskList.size();
    }
    
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }
}
