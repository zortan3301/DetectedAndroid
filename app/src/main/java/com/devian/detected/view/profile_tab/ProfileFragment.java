package com.devian.detected.view.profile_tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devian.detected.R;
import com.devian.detected.model.domain.RankRow;
import com.devian.detected.model.domain.User;
import com.devian.detected.model.domain.UserStats;
import com.devian.detected.utils.LevelManager;
import com.devian.detected.utils.LocalStorage;
import com.devian.detected.utils.ui.popups.DefaultPopup;
import com.devian.detected.view.interfaces.OnLogoutListener;
import com.devian.detected.view.profile_tab.popups.EditPopup;
import com.devian.detected.view.profile_tab.popups.InfoPopup;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth;

    private ProfileViewModel viewModel;
    private LocalStorage localStorage;
    
    private OnLogoutListener logoutCallback;

    private User currentUser;
    private UserStats userStats;
    private RankRow selfRank;
    private ArrayList<RankRow> top10;
    private String currentEvent;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
    
        setupView();
        bindView();

        return v;
    }
    
    private void setupView() {
        mAuth = FirebaseAuth.getInstance();
        viewModel = ViewModelProviders.of(getActivityNonNull()).get(ProfileViewModel.class);
        localStorage = new LocalStorage(getActivityNonNull());
        refreshLayout.setOnRefreshListener(this);
        fab_edit.setOnClickListener(this);
        fab_exit.setOnClickListener(this);
        fab_info.setOnClickListener(this);
    }

    private void bindView() {
        viewModel.bindUserInfo().observe(this, userDataWrapper -> {
            hideProgress();
            if (userDataWrapper.getObject() != null) {
                currentUser = userDataWrapper.getObject();
                displayUserInfo(currentUser);
            }
        });
        viewModel.bindUserStats().observe(this, userStatsDataWrapper -> {
            hideProgress();
            if (userStatsDataWrapper.getObject() != null) {
                userStats = userStatsDataWrapper.getObject();
                displayUserStats(userStats);
            }
        });
        viewModel.bindSelfRank().observe(this, selfRankDataWrapper -> {
            hideProgress();
            if (selfRankDataWrapper.getObject() != null) {
                selfRank = selfRankDataWrapper.getObject();
                displaySelfRank(selfRank);
            }
        });
        viewModel.bindTop10().observe(this, top10DataWrapper -> {
            hideProgress();
            if (top10DataWrapper.getObject() != null) {
                top10 = new ArrayList<>(top10DataWrapper.getObject());
                displayTop10(top10);
            }
        });
        viewModel.bindEvent().observe(this, eventDataWrapper -> {
            hideProgress();
            if (!eventDataWrapper.getObject().isEmpty()) {
                currentEvent = eventDataWrapper.getObject();
                displayEvent(currentEvent);
            }
        });
    }

    private void updateInformation() {
        Log.d(TAG, "updateInformation: ");
        showProgress();
        viewModel.updateUserInfo(mAuth.getUid());
        viewModel.updateUserStats(mAuth.getUid());
        viewModel.updateSelfRank(mAuth.getUid());
        viewModel.updateTop10();
        viewModel.updateEvent();
    }

    private void displayUserInfo(User user) {
        Log.d(TAG, "displayUserInfo");
        if (user != null)
            tvName.setText(user.getDisplayName());
    }

    private void displayUserStats(UserStats stats) {
        Log.d(TAG, "displayUserStats");
        if (stats == null)
            return;
        tvPoints.setText(String.valueOf(stats.getPoints()));
        tvLevel.setText(String.valueOf(LevelManager.getLevelByPoints(stats.getPoints())));
        tvScannedTags.setText(String.valueOf(stats.getTags()));
        progressLevel.setProgress(LevelManager.getPercentsCompleted(stats.getPoints()));
    }

    private void displaySelfRank(RankRow selfRank) {
        Log.d(TAG, "displaySelfRank");
        if (selfRank == null)
            return;
        tvRating.setText(String.valueOf(selfRank.getRank()));
    }

    private void displayTop10(ArrayList<RankRow> top10) {
        Log.d(TAG, "displayTop10");
        if (top10 == null || top10.isEmpty()) {
            return;
        }
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
        tvRating1.setText(rating1);
        tvRating2.setText(rating2);
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
        if (savedInstanceState != null)
            proceedInstanceState(savedInstanceState);
        else {
            proceedLocalStorage();
            updateInformation();
        }
        displayUserInfo(currentUser);
        displayUserStats(userStats);
        displayEvent(currentEvent);
        displaySelfRank(selfRank);
        displayTop10(top10);
    }

    private void proceedInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(TAG, "proceedInstanceState: ");
        User tmpUserInfo = (User) savedInstanceState.getSerializable("currentUser");
        if (tmpUserInfo == null)
            return;
        currentUser = tmpUserInfo;
        userStats = (UserStats) savedInstanceState.getSerializable("userStats");
        currentEvent = (String) savedInstanceState.getSerializable("currentEvent");
        selfRank = savedInstanceState.getParcelable("selfRank");
        top10 = savedInstanceState.getParcelableArrayList("top10");
        refreshLayout.setRefreshing(savedInstanceState.getBoolean("refresh"));
    }

    private void proceedLocalStorage() {
        Log.d(TAG, "proceedLocalStorage: ");
        User tmpUserInfo = localStorage.getData("user_info", User.class);
        if (tmpUserInfo == null)
            return;
        currentUser = tmpUserInfo;
        userStats = localStorage.getData("user_stats", UserStats.class);
        selfRank = localStorage.getData("self_rank", RankRow.class);
        currentEvent = localStorage.getData("event", String.class);
        RankRow[] tmp_top10 = localStorage.getData("top10", RankRow[].class);
        if (tmp_top10 != null)
            top10 = new ArrayList<>(Arrays.asList(localStorage.getData("top10", RankRow[].class)));
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
        outState.putBoolean("refresh", refreshLayout.isRefreshing());
    }

    private void logout() {
        Log.d(TAG, "logout");
        logoutCallback.onLogout();
    }

    @SuppressLint("InflateParams")
    private void popup_logout() {
        DefaultPopup popup = new DefaultPopup(
                getResources().getString(R.string.popup_logout_info),
                getActivityNonNull());
        popup.setIcon(R.drawable.ic_exit_yellow);
        popup.setButtonsText(getResources().getString(R.string.yes), getResources().getString(R.string.no));
        popup.getPositiveOption().setOnClickListener(v -> {
            logout();
            popup.dismiss();
        });
        popup.show();
    }

    @SuppressLint("InflateParams")
    private void popup_change() {
        EditPopup popup = new EditPopup(getActivityNonNull(), currentUser);
        viewModel.bindUserInfo().observe(this, popup::proceedResponse);
        popup.getBtnOK().setOnClickListener(v -> {
            if (popup.isInputCorrect()) {
                viewModel.changeNickname(popup.getInput());
            }
        });
        popup.show();
    }
    
    private void popup_info() {
        Log.d(TAG, "popup_info: ");
        InfoPopup popup = new InfoPopup(getActivityNonNull(), currentUser);
        popup.show();
    }
    
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
        switch (view.getId()) {
            case R.id.fab_edit:
                popup_change();
                break;
            case R.id.fab_exit:
                popup_logout();
                break;
            case R.id.fab_info:
                popup_info();
        }
    }
    
    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        if (isRefreshAvailable()) {
            showProgress();
            updateInformation();
        } else {
            hideProgress();
        }
    }

    private Date lastRefresh = new Date();

    private boolean isRefreshAvailable() {
        Date currTime = new Date();
        if (currTime.getTime() - lastRefresh.getTime() >= getResources().getInteger(R.integer.refresh_delay)) {
            lastRefresh = currTime;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        localStorage.putData("user_info", currentUser);
        localStorage.putData("user_stats", userStats);
        localStorage.putData("self_rank", selfRank);
        localStorage.putData("top10", top10);
        localStorage.putData("event", currentEvent);
    }

    private FragmentActivity getActivityNonNull() {
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
    
    public void setOnLogoutListener(OnLogoutListener listener) {
        logoutCallback = listener;
    }
    
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
    @BindView(R.id.fab_exit)
    FloatingActionButton fab_exit;
    @BindView(R.id.fab_edit)
    FloatingActionButton fab_edit;
    @BindView(R.id.fab_info)
    FloatingActionButton fab_info;
}
