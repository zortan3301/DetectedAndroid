package com.devian.detected.main.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.devian.detected.R;
import com.devian.detected.utils.domain.DataWrapper;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.network.ServerResponse;

@SuppressLint("InflateParams")
class EditPopup {

    private static final String TAG = "EditPopup";

    private Button btnOK;
    private EditText etNickname;
    private ImageView ivError;
    private TextView tvWarning;
    private ProgressBar progressBar;

    private AlertDialog dialog;
    private FragmentActivity context;

    private User currentUser;


    EditPopup(FragmentActivity context, User user) {
        currentUser = user;
        this.context = context;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = context.getLayoutInflater().inflate(R.layout.popup_change, null);

        btnOK = mView.findViewById(R.id.changeNickname_btnOk);
        ImageView btnCancel = mView.findViewById(R.id.changeNickname_btnCancel);
        etNickname = mView.findViewById(R.id.changeNickname_etNickname);
        ivError = mView.findViewById(R.id.changeNickname_imgError);
        tvWarning = mView.findViewById(R.id.changeNickname_tvWarning);
        progressBar = mView.findViewById(R.id.changeNickname_progress);

        mBuilder.setView(mView);
        dialog = mBuilder.create();

        btnCancel.setOnClickListener(v -> dismiss());
    }

    void show() {
        dialog.show();
    }

    private void dismiss() {
        dialog.dismiss();
    }

    Button getBtnOK() {
        return btnOK;
    }

    void proceedResponse(DataWrapper<User> userDataWrapper) {
        hideProgress();
        if (userDataWrapper.isError())
            displayError(userDataWrapper.getCode());
        else
            dismiss();
    }

    boolean isInputCorrect() {
        String newNickname = getInput().getDisplayName();
        if (newNickname.length() < 6 || newNickname.length() > 16) {
            displayError(TYPE_INCORRECT_NEW_NICKNAME);
            return false;
        }
        showProgress();
        return true;
    }

    User getInput() {
        currentUser.setDisplayName(etNickname.getText().toString().toLowerCase());
        return currentUser;
    }

    private void showProgress() {
        Log.d(TAG, "showProgress: ");
        hideError();
        btnOK.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        Log.d(TAG, "hideProgress: ");
        btnOK.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void hideError() {
        Log.d(TAG, "hideError: ");
        ivError.setVisibility(View.GONE);
        tvWarning.setVisibility(View.GONE);
    }

    private void displayError(int errorCode) {
        Log.d(TAG, "displayError: ");
        hideProgress();
        ivError.setVisibility(View.VISIBLE);
        tvWarning.setVisibility(View.VISIBLE);
        switch (errorCode) {
            case ServerResponse.TYPE_CHANGE_NICKNAME_EXISTS:
                tvWarning.setText(context.getResources().getString(R.string.nickname_exists));
                break;
            case ServerResponse.TYPE_CHANGE_NICKNAME_FAILURE:
                tvWarning.setText(context.getResources().getString(R.string.try_later));
                break;
            case TYPE_INCORRECT_NEW_NICKNAME:
                tvWarning.setText(context.getResources().getString(R.string.nickname_warning));
        }
    }

    private static final int TYPE_INCORRECT_NEW_NICKNAME = -19201;
}
