package com.devian.detected.view.profile_tab.popups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.devian.detected.R;
import com.devian.detected.model.domain.User;

public class InfoPopup {
    
    private static final String TAG = "InfoPopup";
    
    private AlertDialog dialog;
    
    @SuppressLint("InflateParams")
    public InfoPopup(FragmentActivity context, User user) {
        Log.d(TAG, "InfoPopup: ");
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = context.getLayoutInflater().inflate(R.layout.popup_info, null);
    
        ImageView btnCancel = mView.findViewById(R.id.popupInfo_btnCancel);
        btnCancel.setOnClickListener(v -> dismiss());
        TextView tvUid = mView.findViewById(R.id.popupInfo_tvUid);
        String uidText = "UID: " + user.getUid();
        tvUid.setText(uidText);
    
        mBuilder.setView(mView);
        dialog = mBuilder.create();
    }
    
    public void show() {
        dialog.show();
    }
    
    private void dismiss() {
        dialog.dismiss();
    }
}
