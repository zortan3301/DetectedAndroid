package com.devian.detected.main.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devian.detected.MainActivity;
import com.devian.detected.R;
import com.devian.detected.utils.LevelManager;
import com.devian.detected.utils.network.GsonSerializer;
import com.devian.detected.utils.network.NetworkManager;
import com.devian.detected.utils.network.ServerResponse;
import com.devian.detected.utils.domain.RankRow;
import com.devian.detected.utils.domain.User;
import com.devian.detected.utils.domain.UserStats;
import com.devian.detected.utils.security.AES256;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth;

    private Gson gson = GsonSerializer.getInstance().getGson();

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

    private Call<ServerResponse> callUpdateUserInfo, callUpdateStatistics, callUpdateTop10,
            callUpdateSelfRank, callUpdateEvent, callChangeNickname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);

        refreshLayout.setOnRefreshListener(this);
        mAuth = FirebaseAuth.getInstance();

        init_fab(v);

        checkSavedBundle(savedInstanceState);

        return v;
    }

    private void checkSavedBundle(Bundle inState) {
        Log.d(TAG, "checkSavedBundle");
        if (inState != null) {
            currentUser = (User) inState.getSerializable("currentUser");
            userStats = (UserStats) inState.getSerializable("userStats");
            currentEvent = (String) inState.getSerializable("currentEvent");
            selfRank = inState.getParcelable("selfRank");
            top10 = inState.getParcelableArrayList("top10");
        } else {
            init();
        }
        updateUI();
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

    private void init() {
        Log.d(TAG, "init");
        updateUserInfo();
        updateStatistics();
        updateRankings();
        updateEvent();
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");
        if (currentUser != null) {
            tvName.setText(currentUser.getDisplayName());
        }
        if (userStats != null) {
            tvPoints.setText(String.valueOf(userStats.getPoints()));
            tvLevel.setText(String.valueOf(userStats.getLevel()));
            tvScannedTags.setText(String.valueOf(userStats.getTags()));
            progressLevel.setProgress(LevelManager.getPercentsCompleted(userStats.getPoints()));
        }
        if (top10 != null) {
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
        if (selfRank != null) {
            String rank = String.valueOf(selfRank.getRank());
            tvRating.setText(rank);
        }
        if (currentEvent != null) {
            String text = getString(R.string.current_event) + currentEvent;
            SpannableStringBuilder spannable = new SpannableStringBuilder(text);
            spannable.setSpan(
                    new ForegroundColorSpan(Color.BLUE),
                    0, 1,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvEvent.setText(spannable);
        }
        refreshLayout.setRefreshing(false);
    }

    private void updateUserInfo() {
        Log.d(TAG, "updateUserInfo");
        Map<String, String> headers = NetworkManager.getInstance().proceedHeader(mAuth.getUid());
        callUpdateUserInfo = NetworkManager.getInstance().getApi().getUserInfo(headers);
        callUpdateUserInfo.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "updateUserInfo onResponse: response body is null");
                    return;
                }
                try {
                    if (response.body().getType() == ServerResponse.TYPE_AUTH_SUCCESS) {
                        currentUser = gson.fromJson(
                                NetworkManager.getInstance().proceedResponse(response.body()),
                                User.class);
                        Log.d(TAG, "updateUserInfo onResponse: current user = " +
                                response.body().getData());
                        updateUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled())
                    Log.d(TAG, "callUpdateUserInfo is cancelled");
                else
                    t.printStackTrace();
            }
        });
    }

    private void updateStatistics() {
        Log.d(TAG, "updateStatistics");
        Map<String, String> headers = NetworkManager.getInstance().proceedHeader(mAuth.getUid());
        callUpdateStatistics = NetworkManager.getInstance().getApi().getStats(headers);
        callUpdateStatistics.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                Log.d(TAG, "updateStatistics onResponse: " + gson.toJson(response.body()));
                if (response.body() == null)
                    return;
                try {
                    if (response.body().getType() == ServerResponse.TYPE_STATS_EXISTS) {
                        userStats = gson.fromJson(
                                NetworkManager.getInstance().proceedResponse(response.body()),
                                UserStats.class);
                        updateUI();
                    } else {
                        Log.e(TAG, "onResponse: user stats does not exist on the server");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled())
                    Log.d(TAG, "callUpdateStatistics is cancelled");
                else
                    t.printStackTrace();
            }
        });
    }

    private void updateRankings() {
        Log.d(TAG, "updateRankings");
        // Get top 10 users
        callUpdateTop10 = NetworkManager.getInstance().getApi().getRankTop10();
        callUpdateTop10.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "updateRankings (top 10) onResponse: response body is null");
                    return;
                }
                try {
                    if (response.body().getType() == ServerResponse.TYPE_RANK_SUCCESS) {
                        top10 = gson.fromJson(
                                NetworkManager.getInstance().proceedResponse(response.body()),
                                new TypeToken<ArrayList<RankRow>>() {
                                }.getType());
                        updateUI();
                    } else {
                        Log.e(TAG, "onResponse (top10): type != TYPE_RANK_SUCCESS");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled())
                    Log.d(TAG, "callUpdateTop10 is cancelled");
                else
                    t.printStackTrace();
            }
        });

        // Get personal rank
        Map<String, String> headers = NetworkManager.getInstance().proceedHeader(mAuth.getUid());
        callUpdateSelfRank = NetworkManager.getInstance().getApi().getPersonalRank(headers);
        callUpdateSelfRank.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "updateRankings (personal rank) onResponse: response body is null");
                    return;
                }
                try {
                    if (response.body().getType() == ServerResponse.TYPE_RANK_SUCCESS) {
                        selfRank = gson.fromJson(
                                NetworkManager.getInstance().proceedResponse(response.body()),
                                RankRow.class);
                        updateUI();
                    } else
                        Log.e(TAG, "onResponse (personal rank): type != TYPE_RANK_SUCCESS");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled())
                    Log.d(TAG, "callUpdateSelfRank is cancelled");
                else
                    t.printStackTrace();
            }
        });
    }

    private void updateEvent() {
        Log.d(TAG, "updateEvent: ");

        callUpdateEvent = NetworkManager.getInstance().getApi().getEvent();
        callUpdateEvent.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "updateEvent onResponse: response body is null");
                    return;
                }
                try {
                    if (response.body().getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                        currentEvent = NetworkManager.getInstance().proceedResponse(response.body());
                        updateUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled())
                    Log.d(TAG, "callUpdateEvent is cancelled");
                else
                    t.printStackTrace();
            }
        });
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.popup_change, null);
        Button btnOK = mView.findViewById(R.id.changeNickname_btnOk);
        Button btnCancel = mView.findViewById(R.id.changeNickname_btnCancel);
        EditText etNickname = mView.findViewById(R.id.changeNickname_etNickname);
        ImageView imgError = mView.findViewById(R.id.changeNickname_imgError);
        TextView tvWarning = mView.findViewById(R.id.changeNickname_tvWarning);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        btnOK.setOnClickListener(v -> {
            String newNickname = etNickname.getText().toString().toLowerCase();
            if (newNickname.length() < 6 || newNickname.length() > 16) {
                imgError.setVisibility(View.VISIBLE);
                tvWarning.setVisibility(View.VISIBLE);
                tvWarning.setText(getResources().getString(R.string.nickname_warning));
            } else {
                Log.d(TAG, "changeNickname");
                currentUser.setDisplayName(newNickname);
                Map<String, String> headers =
                        NetworkManager.getInstance().proceedHeader(gson.toJson(currentUser));

                callChangeNickname = NetworkManager.getInstance().getApi().changeNickname(headers);
                callChangeNickname.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call,
                                           @NonNull Response<ServerResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "changeNickname onResponse: response body is null");
                            return;
                        }
                        try {
                            if (response.body().getType() == ServerResponse.TYPE_CHANGE_NICKNAME_SUCCESS) {
                                dialog.dismiss();
                            } else if (response.body().getType() == ServerResponse.TYPE_CHANGE_NICKNAME_EXISTS) {
                                imgError.setVisibility(View.VISIBLE);
                                Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.nickname_exists),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(
                                    getContext(),
                                    getResources().getString(R.string.try_later),
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call,
                                          @NonNull Throwable t) {
                        if (call.isCanceled())
                            Log.d(TAG, "callChangeNickname is cancelled");
                        else
                            t.printStackTrace();
                        Toast.makeText(
                                getContext(),
                                getResources().getString(R.string.try_later),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                updateUI();
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
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
        init();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (callUpdateUserInfo != null)
            callUpdateUserInfo.cancel();
        if (callUpdateStatistics != null)
            callUpdateStatistics.cancel();
        if (callUpdateTop10 != null)
            callUpdateTop10.cancel();
        if (callUpdateSelfRank != null)
            callUpdateSelfRank.cancel();
        if (callUpdateEvent != null)
            callUpdateEvent.cancel();
        if (callChangeNickname != null)
            callChangeNickname.cancel();
    }

    private Activity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("null returned from getActivity()");
        }
    }
}
