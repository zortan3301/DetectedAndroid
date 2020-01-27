package com.devian.detected.main.task_list;

import android.graphics.Rect;
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

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private List<Task> taskList;
    private TaskListContract.OnTaskItemClickListener listener;

    TaskListAdapter(List<Task> taskList, TaskListContract.OnTaskItemClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlayout_task, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tvTitle.setText(taskList.get(position).getTitle());
        holder.tvPoints.setText(String.valueOf(taskList.get(position).getReward()));

        Picasso.get()
                .load(taskList.get(position).getImgUrl())
                .fit()
                .centerCrop()
                .into(holder.ivCover);

        holder.bind(taskList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        if (taskList == null)
            return 0;
        return taskList.size();
    }

    void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvTitle, tvPoints;
        ImageView ivCover;

        ViewHolder(View v) {
            super(v);
            view = v;
            tvTitle = view.findViewById(R.id.task_tvTitle);
            tvPoints = view.findViewById(R.id.task_tvPoints);
            ivCover = view.findViewById(R.id.task_imageView);
        }

        void bind(final Task task, final TaskListContract.OnTaskItemClickListener clickListener) {
            view.setOnClickListener(view -> clickListener.OnItemClicked(task));
        }
    }

    public static class TaskListItemDecorator extends RecyclerView.ItemDecoration {
        private int space;

        TaskListItemDecorator(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            boolean isLast = position == state.getItemCount() - 1;
            if (isLast) {
                outRect.bottom = space;
                outRect.top = 0;
            }
        }
    }
}
