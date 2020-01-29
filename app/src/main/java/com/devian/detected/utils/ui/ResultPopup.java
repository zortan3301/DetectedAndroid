package com.devian.detected.utils.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.devian.detected.R;

public class ResultPopup {
    
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAILURE = -1;
    public static final int RESULT_ACTIVATED = -2;
    
    private AlertDialog dialog;
    
    @SuppressLint("InflateParams")
    public ResultPopup(int resultCode, int reward, Activity context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.popup_result, null);
        
        ImageView imgResult = view.findViewById(R.id.popupResult_img);
        View line = view.findViewById(R.id.popupResult_line);
        TextView tvInfo = view.findViewById(R.id.popupResult_tvInfo);
        TextView tvReward = view.findViewById(R.id.popupResult_tvReward);
        Button btnOk = view.findViewById(R.id.popupResult_btnOk);
        
        btnOk.setOnClickListener(v -> dismiss());
        
        switch (resultCode) {
            case RESULT_SUCCESS:
                tvInfo.setText(context.getResources().getString(R.string.result_success));
                tvReward.setVisibility(View.VISIBLE);
                tvReward.setText(String.format(
                        context.getResources().getString(R.string.reward), String.valueOf(reward)));
                line.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.green)));
                imgResult.setImageResource(R.drawable.ic_check_circle_green);
                break;
            case RESULT_FAILURE:
                tvInfo.setText(context.getResources().getString(R.string.result_failure));
                line.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.red)));
                imgResult.setImageResource(R.drawable.ic_error_red);
                tvReward.setVisibility(View.GONE);
                break;
            case RESULT_ACTIVATED:
                tvInfo.setText(context.getResources().getString(R.string.result_activated));
                line.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.red)));
                imgResult.setImageResource(R.drawable.ic_error_red);
                tvReward.setVisibility(View.GONE);
        }
        
        mBuilder.setView(view);
        dialog = mBuilder.create();
    }
    
    public void show() {
        dialog.show();
    }
    
    private void dismiss() {
        dialog.dismiss();
    }
}
