package com.devian.detected.utils.ui.popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.devian.detected.R;

import lombok.Getter;

public class DefaultPopup {
    
    @Getter
    private Button positiveOption;
    @Getter
    private Button negativeOption;
    private ImageView icon;
    
    private AlertDialog dialog;
    
    @SuppressLint("InflateParams")
    public DefaultPopup(String message, Activity context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.popup_default, null);
        positiveOption = view.findViewById(R.id.popup_permissions_btnAllow);
        negativeOption = view.findViewById(R.id.popup_permissions_btnCancel);
        TextView tvMessage = view.findViewById(R.id.popup_permissions_tvInfo);
        icon = view.findViewById(R.id.popup_permissions_ivIcon);
        tvMessage.setText(message);
        mBuilder.setView(view);
        dialog = mBuilder.create();
        
        negativeOption.setOnClickListener(v -> dismiss());
    }
    
    public void show() {
        dialog.show();
    }
    
    public void dismiss() {
        dialog.dismiss();
    }
    
    public void setIcon(int resId) {
        icon.setImageResource(resId);
    }

    public void setButtonsText(String positiveText, String negativeText) {
        positiveOption.setText(positiveText);
        negativeOption.setText(negativeText);
    }
}
