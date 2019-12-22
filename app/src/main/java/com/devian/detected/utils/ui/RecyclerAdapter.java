package com.devian.detected.utils.ui;

import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devian.detected.R;
import com.devian.detected.utils.domain.Task;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    
    private List<Task> taskList;
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }
    
    public RecyclerAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }
    
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlayout_task, parent, false);
        
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (taskList == null)
            return;
        TextView title = holder.view.findViewById(R.id.task_tvTitle);
        TextView points = holder.view.findViewById(R.id.task_tvPoints);
        title.setText(taskList.get(position).getTitle());
        points.setText(String.valueOf(taskList.get(position).getReward()));
        
        ImageView imageView = holder.view.findViewById(R.id.task_imageView);
        
        Picasso.with(holder.view.getContext())
                .load(taskList.get(position).getImgUrl())
                .fit()
                .centerCrop()
                .into(imageView);
        
    }
    
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (taskList == null)
            return 0;
        return taskList.size();
    }
    
    public void addItem(Task task) {
        taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }
    
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }
    
    
    
}
