package com.devian.detected.main.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devian.detected.MainActivity;
import com.devian.detected.R;
import com.devian.detected.utils.LevelManager;
import com.devian.detected.utils.domain.RankRow;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.domain.UserStats;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, EditPopupFragment.OnProfileChanged {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth;

    private ProfileViewModel viewModel;
    private FragmentActivity mContext;

    @BindView(R.id.profile_tvName)
    TextView tvName;
    @BindView(R.id.profile_tvPoints)
    TextView tvPoints;
    @BindView(R.id.profile_tvLevel)
    TextView tvLevel;
    @BindView(R.id.profile_tvScannedTags)
    TextView tvScannedTags;
    @BindView(R.id.profile_progressLevel)
    ProgressBar progressLevel;
    @BindView(R.id.profile_tvEvent)
    TextView tvEvent;
    @BindView(R.id.profile_tvRating)
    TextView tvRating;
    @BindView(R.id.profile_tvRating1)
    TextView tvRating1;
    @BindView(R.id.profile_tvRating2)
    TextView tvRating2;
    @BindView(R.id.profile_refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private FloatingActionButton fab_settings, fab_exit, fab_edit;
    private TextView tv_fab_exit, tv_fab_edit;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private Boolean fab_settings_open = false;

    private User currentUser;
    private UserStats userStats;
    private RankRow selfRank;
    private ArrayList<RankRow> top10;
    private String currentEvent;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        refreshLayout.setOnRefreshListener(this);
        mAuth = FirebaseAuth.getInstance();
        init_fab(v);

        return v;
    }

    private void updateInformation() {
        getUserInfo();
        getUserStats();
        getSelfRank();
        getRankTop10();
        getEvent();
    }

    private void getUserInfo() {
        Log.d(TAG, "getUserInfo: ");
        showProgress();
        viewModel.getUserInfo(mAuth.getUid()).observe(this, userDataWrapper -> {
            hideProgress();
            currentUser = userDataWrapper.getObject();
            displayUserInfo(currentUser);
        });
    }

    private void getUserStats() {
        Log.d(TAG, "getUserStats: ");
        showProgress();
        viewModel.getUserStats(mAuth.getUid()).observe(this, userStatsDataWrapper -> {
            hideProgress();
            userStats = userStatsDataWrapper.getObject();
            displayUserStats(userStats);
        });
    }

    private void getSelfRank() {
        Log.d(TAG, "getSelfRank: ");
        showProgress();
        viewModel.getSelfRank(mAuth.getUid()).observe(this, selfRankDataWrapper -> {
            hideProgress();
            selfRank = selfRankDataWrapper.getObject();
            displaySelfRank(selfRank);
        });
    }

    private void getRankTop10() {
        Log.d(TAG, "getRankTop10: ");
        showProgress();
        viewModel.getTop10().observe(this, top10DataWrapper -> {
            hideProgress();
            top10 = new ArrayList<>(top10DataWrapper.getObject());
            displayTop10(top10);
        });
    }

    private void getEvent() {
        Log.d(TAG, "getEvent: ");
        showProgress();
        viewModel.getEvent().observe(this, eventDataWrapper -> {
            hideProgress();
            currentEvent = eventDataWrapper.getObject();
            displayEvent(currentEvent);
        });
    }

    private void displayUserInfo(User user) {
        Log.d(TAG, "displayUserInfo");
        tvName.setText(user.getDisplayName());
    }

    private void displayUserStats(UserStats stats) {
        Log.d(TAG, "displayUserStats");
        tvPoints.setText(String.valueOf(stats.getPoints()));
        tvLevel.setText(String.valueOf(stats.getLevel()));
        tvScannedTags.setText(String.valueOf(stats.getTags()));
        progressLevel.setProgress(LevelManager.getPercentsCompleted(stats.getPoints()));
    }

    private void displaySelfRank(RankRow selfRank) {
        Log.d(TAG, "displaySelfRank");
        tvRating.setText(String.valueOf(selfRank.getRank()));
    }

    private void displayTop10(ArrayList<RankRow> top10) {
        Log.d(TAG, "displayTop10");
        StringBuilder
                rating1 = new StringBuilder(),
                rating2 = new StringBuilder();
        for (int i = 0; i < top10.size(); i++) {
            rating1.append(i + 1);
            rating1.append(". ");
            rating1.append(top10.get(i).getNickname());
            rating2.append(top10.get(i).getPoints());
            if (i != top10.size() - 1) {
                rating1.append("\n");
                rating2.append("\n");
            }
        }
        SpannableStringBuilder spannable1 = new SpannableStringBuilder(rating1);
        spannable1.setSpan(
                new ForegroundColorSpan(Color.RED),
                3, top10.get(0).getNickname().length() + 3,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SpannableStringBuilder spannable2 = new SpannableStringBuilder(rating2);
        spannable2.setSpan(
                new ForegroundColorSpan(Color.RED),
                0, String.valueOf(top10.get(0).getPoints()).length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvRating1.setText(spannable1);
        tvRating2.setText(spannable2);
    }

    private void displayEvent(String event) {
        Log.d(TAG, "displayEvent");
        String text = getString(R.string.current_event) + event;
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.setSpan(
                new ForegroundColorSpan(Color.BLUE),
                0, 1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvEvent.setText(spannable);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if (savedInstanceState != null) {
            currentUser = (User) savedInstanceState.getSerializable("currentUser");
            userStats = (UserStats) savedInstanceState.getSerializable("userStats");
            currentEvent = (String) savedInstanceState.getSerializable("currentEvent");
            selfRank = savedInstanceState.getParcelable("selfRank");
            top10 = savedInstanceState.getParcelableArrayList("top10");
            displayUserInfo(currentUser);
            displayUserStats(userStats);
            displayEvent(currentEvent);
            displaySelfRank(selfRank);
            displayTop10(top10);
        } else {
            updateInformation();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentUser", currentUser);
        outState.putSerializable("userStats", userStats);
        outState.putSerializable("currentEvent", currentEvent);
        outState.putParcelable("selfRank", selfRank);
        outState.putParcelableArrayList("top10", top10);
    }

    private void logout() {
        Log.d(TAG, "logout");
        mAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivityNonNull(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivityNonNull(),
                task -> {
                    Intent intent = new Intent(getActivityNonNull(), MainActivity.class);
                    startActivity(intent);
                });
    }

    @SuppressLint("InflateParams")
    private void popup_logout() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.popup_logout, null);
        Button btnYes = mView.findViewById(R.id.popup_logout_btnYes);
        Button btnNo = mView.findViewById(R.id.popup_logout_btnNo);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        btnYes.setOnClickListener(v -> {
            logout();
            dialog.dismiss();
        });
        btnNo.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("InflateParams")
    private void popup_change() {
        EditPopupFragment popup = new EditPopupFragment(currentUser);
        popup.setOnProfileChangedListener(this);
        popup.show(mContext.getSupportFragmentManager(), "editPopup");
    }

    private void init_fab(View v) {
        Log.d(TAG, "init_fab");

        fab_settings = v.findViewById(R.id.fab_settings);
        fab_exit = v.findViewById(R.id.fab_exit);
        fab_edit = v.findViewById(R.id.fab_edit);

        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);

        tv_fab_exit = v.findViewById(R.id.profile_tv_fab_exit);
        tv_fab_edit = v.findViewById(R.id.profile_tv_fab_edit);

        fab_settings.setOnClickListener(view -> {
            if (fab_settings_open) {
                fab_exit.startAnimation(fab_close);
                fab_edit.startAnimation(fab_close);
                fab_settings.startAnimation(fab_anticlock);

                fab_exit.setClickable(false);
                fab_edit.setClickable(false);

                tv_fab_exit.setVisibility(View.INVISIBLE);
                tv_fab_edit.setVisibility(View.INVISIBLE);
                tv_fab_exit.startAnimation(fab_close);
                tv_fab_edit.startAnimation(fab_close);

                fab_settings_open = false;
            } else {
                fab_exit.startAnimation(fab_open);
                fab_edit.startAnimation(fab_open);
                fab_settings.startAnimation(fab_clock);

                fab_exit.setClickable(true);
                fab_edit.setClickable(true);

                tv_fab_exit.setVisibility(View.VISIBLE);
                tv_fab_edit.setVisibility(View.VISIBLE);
                tv_fab_exit.startAnimation(fab_open);
                tv_fab_edit.startAnimation(fab_open);

                fab_settings_open = true;
            }
        });

        fab_exit.setOnClickListener(view -> popup_logout());
        fab_edit.setOnClickListener(view -> popup_change());
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        refreshLayout.setRefreshing(true);
        updateInformation();
    }

    private Activity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("null returned from getActivity()");
        }
    }

    private void showProgress() {
        refreshLayout.setRefreshing(true);
    }

    private void hideProgress() {
        refreshLayout.setRefreshing(false);
    }

    public void displayError(Throwable t) {
        Log.d(TAG, "displayError");
        hideProgress();
    }

    public void displayError(int errorCode) {
        Log.d(TAG, "displayError");
        hideProgress();
    }

    @Override
    public void onDisplayNameChanged(String displayName) {
        Log.d(TAG, "onDisplayNameChanged: ");
        currentUser.setDisplayName(displayName);
        tvName.setText(currentUser.getDisplayName());
    }
}
