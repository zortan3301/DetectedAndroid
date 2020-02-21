package com.devian.detected.utils.ui.popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.devian.detected.R;

public class ResultPopup {
    
    private static final String TAG = "ResultPopup";
    
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAILURE = -1;
    public static final int RESULT_ACTIVATED = -2;
    
    private AlertDialog mDialog;
    private Activity mContext;
    
    private ImageView ivResult;
    private View line;
    private TextView tvInfo;
    private TextView tvReward;
    private ConstraintLayout loading;
    
    @SuppressLint("InflateParams")
    public ResultPopup(Activity context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.popup_result, null);
        mContext = context;
        ivResult = view.findViewById(R.id.popupResult_img);
        line = view.findViewById(R.id.popupResult_line);
        tvInfo = view.findViewById(R.id.popupResult_tvInfo);
        tvReward = view.findViewById(R.id.popupResult_tvReward);
        loading = view.findViewById(R.id.popupResult_progress);
        loading.setVisibility(View.VISIBLE);
        ImageView btnOk = view.findViewById(R.id.popupResult_btnOk);
        btnOk.setOnClickListener(v -> dismiss());
        mBuilder.setView(view);
        mDialog = mBuilder.create();
    }
    
    
    public void show() {
        mDialog.show();
    }
    
    private void dismiss() {
        mDialog.dismiss();
        loading.setVisibility(View.VISIBLE);
    }
    
    public void setResult(int resultCode, int reward) {
        Log.d(TAG, "setResult: ");
        loading.setVisibility(View.INVISIBLE);
        switch (resultCode) {
            case RESULT_SUCCESS:
                tvInfo.setText(mContext.getResources().getString(R.string.result_success));
                tvReward.setVisibility(View.VISIBLE);
                tvReward.setText(String.format(
                        mContext.getResources().getString(R.string.reward), String.valueOf(reward)));
                line.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.green)));
                ivResult.setImageResource(R.drawable.ic_check_circle_green);
                break;
            case RESULT_FAILURE:
                tvInfo.setText(mContext.getResources().getString(R.string.result_failure));
                line.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.red)));
                ivResult.setImageResource(R.drawable.ic_error_red);
                tvReward.setVisibility(View.GONE);
                break;
            case RESULT_ACTIVATED:
                tvInfo.setText(mContext.getResources().getString(R.string.result_activated));
                line.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.red)));
                ivResult.setImageResource(R.drawable.ic_error_red);
                tvReward.setVisibility(View.GONE);
        }
    }
}
