package com.devian.detected.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.devian.detected.R;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.network.GsonSerializer;
import com.devian.detected.utils.network.ServerResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditPopupFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "EditPopupFragment";

    private ProfileViewModel viewModel;
    private OnProfileChanged callback;

    @BindView(R.id.changeNickname_btnOk)
    Button btnOk;
    @BindView(R.id.changeNickname_btnCancel)
    Button btnCancel;
    @BindView(R.id.changeNickname_etNickname)
    EditText etNickname;
    @BindView(R.id.changeNickname_imgError)
    ImageView ivError;
    @BindView(R.id.changeNickname_tvWarning)
    TextView tvWarning;
    @BindView(R.id.changeNickname_progress)
    ProgressBar progressBar;

    private User currentUser;

    EditPopupFragment(User currentUser) {
        this.currentUser = currentUser;
    }

    @Nullable
    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_change, null);
        ButterKnife.bind(this, v);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        return v;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.changeNickname_btnOk:
                String newNickname = etNickname.getText().toString().toLowerCase();
                changeNickname(newNickname);
                break;
            case R.id.changeNickname_btnCancel:
                dismiss();
        }
    }

    private void changeNickname(String newNickname) {
        if (newNickname.length() < 6 || newNickname.length() > 16) {
            displayError(TYPE_INCORRECT_NEW_NICKNAME);
        } else {
            showProgress();
            User user = new User(currentUser.getUid(), newNickname, currentUser.getEmail());
            viewModel.changeNickname(user).observe(this, (userDataWrapper) -> {
                Log.d(TAG, "changeNickname: " + GsonSerializer.getInstance().getGson().toJson(userDataWrapper));
                hideProgress();
                if (userDataWrapper.isError())
                    displayError(userDataWrapper.getCode());
                else {
                    currentUser = userDataWrapper.getObject();
                    callback.onDisplayNameChanged(newNickname);
                    dismiss();
                }
            });
        }
    }

    private void showProgress() {
        hideError();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void hideError() {
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
                tvWarning.setText(getResources().getString(R.string.nickname_exists));
                break;
            case ServerResponse.TYPE_CHANGE_NICKNAME_FAILURE:
                tvWarning.setText(getResources().getString(R.string.try_later));
                break;
            case TYPE_INCORRECT_NEW_NICKNAME:
                tvWarning.setText(getResources().getString(R.string.nickname_warning));
        }
    }

    void setOnProfileChangedListener(OnProfileChanged callback) {
        this.callback = callback;
    }

    public interface OnProfileChanged {
        void onDisplayNameChanged(String displayName);
    }

    private static final int TYPE_INCORRECT_NEW_NICKNAME = -19201;

}
