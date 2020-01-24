package com.devian.detected.utils.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devian.detected.R;

public class PermissionsPopup {
    
    private Button allowOption;
    private Button cancelOption;
    private AlertDialog dialog;
    
    @SuppressLint("InflateParams")
    public PermissionsPopup(String message, Activity context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.popup_permissions, null);
        allowOption = view.findViewById(R.id.popup_permissions_btnAllow);
        cancelOption = view.findViewById(R.id.popup_permissions_btnCancel);
        TextView tvMessage = view.findViewById(R.id.popup_permissions_tvInfo);
        tvMessage.setText(message);
        mBuilder.setView(view);
        dialog = mBuilder.create();
    }
    
    public void show() {
        dialog.show();
    }
    
    public void dismiss() {
        dialog.dismiss();
    }
    
    public Button getAllowOption() {
        return allowOption;
    }
    
    public Button getCancelOption() {
        return cancelOption;
    }
}
