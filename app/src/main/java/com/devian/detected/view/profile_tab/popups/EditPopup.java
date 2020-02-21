package com.devian.detected.view.profile_tab.popups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.devian.detected.R;
import com.devian.detected.model.domain.DataWrapper;
import com.devian.detected.model.domain.User;
import com.devian.detected.model.domain.network.ServerResponse;
import com.wang.avi.AVLoadingIndicatorView;

import lombok.Getter;

@SuppressLint("InflateParams")
public class EditPopup {

    private static final String TAG = "EditPopup";

    @Getter
    private Button btnOK;
    private EditText etNickname;
    private ImageView ivError;
    private TextView tvWarning;
    private AVLoadingIndicatorView progressBar;
    private Spinner spinner;

    private AlertDialog dialog;
    private FragmentActivity context;

    private User currentUser;


    public EditPopup(FragmentActivity context, User user) {
        currentUser = user;
        this.context = context;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mView = context.getLayoutInflater().inflate(R.layout.popup_edit, null);

        btnOK = mView.findViewById(R.id.changeNickname_btnOk);
        ImageView btnCancel = mView.findViewById(R.id.changeNickname_btnCancel);
        etNickname = mView.findViewById(R.id.changeNickname_etNickname);
        ivError = mView.findViewById(R.id.changeNickname_imgError);
        tvWarning = mView.findViewById(R.id.changeNickname_tvWarning);
        progressBar = mView.findViewById(R.id.changeNickname_progress);
        spinner = mView.findViewById(R.id.spinner);
        
        String[] cities = context.getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.spinner_layout, R.id.spinner_textView, cities);
        spinner.setAdapter(adapter);

        mBuilder.setView(mView);
        dialog = mBuilder.create();

        btnCancel.setOnClickListener(v -> dismiss());
    }

    public void show() {
        dialog.show();
    }

    private void dismiss() {
        dialog.dismiss();
    }

    public void proceedResponse(DataWrapper<User> userDataWrapper) {
        hideProgress();
        if (userDataWrapper.isError())
            displayError(userDataWrapper.getCode());
        else
            dismiss();
    }

    public boolean isInputCorrect() {
        String newNickname = getInput().getDisplayName();
        if (newNickname.length() < 6 || newNickname.length() > 16) {
            displayError(TYPE_INCORRECT_NEW_NICKNAME);
            return false;
        }
        showProgress();
        return true;
    }

    public User getInput() {
        currentUser.setDisplayName(etNickname.getText().toString().toLowerCase());
        return currentUser;
    }

    private void showProgress() {
        Log.d(TAG, "showProgress: ");
        hideError();
        btnOK.setVisibility(View.INVISIBLE);
        progressBar.show();
    }

    private void hideProgress() {
        Log.d(TAG, "hideProgress: ");
        btnOK.setVisibility(View.VISIBLE);
        progressBar.hide();
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
