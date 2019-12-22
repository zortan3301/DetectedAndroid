package com.devian.detected.utils.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemDecorator extends RecyclerView.ItemDecoration {
    private int space;
    
    public RecyclerItemDecorator(int space) {
        this.space = space;
    }
    
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        boolean isLast = position == state.getItemCount()-1;
        if(isLast){
            outRect.bottom = space;
            outRect.top = 0; //don't forget about recycling...
        }
    }
}
