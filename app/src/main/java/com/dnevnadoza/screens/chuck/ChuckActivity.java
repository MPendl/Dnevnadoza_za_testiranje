package com.dnevnadoza.screens.chuck;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.TextView;

import com.dnevnadoza.R;
import com.dnevnadoza.network.ApiServiceFactory;
import com.dnevnadoza.network.models.Chuck;
import com.dnevnadoza.network.models.ChuckResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChuckActivity extends AppCompatActivity {

    private RecyclerView chuckRecyclerView;
    private SnapHelper snapHelper;

    private ArrayList<Chuck> jokes = new ArrayList<>();
    private JokesAdapter adapter;

    private Call<ChuckResponse> getJokesCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuck);
        initViews();
        initRecyclerView();
        getJokes();
    }

    private void initViews() {
        chuckRecyclerView = findViewById(R.id.chuckRecyclerView);
        TextView exitButton = findViewById(R.id.exitButton);
        TextView nextButton = findViewById(R.id.nextButton);
        exitButton.setOnClickListener(view -> onBackPressed());
        nextButton.setOnClickListener(view -> nextJoke());
    }

    private void nextJoke() {
        chuckRecyclerView.smoothScrollToPosition(getCurrentPosition()+1);
    }

    private void initRecyclerView() {
        chuckRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new JokesAdapter(jokes);
        chuckRecyclerView.setAdapter(adapter);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(chuckRecyclerView);
        chuckRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (getCurrentPosition() == jokes.size()-1) {
                    getJokes();
                }
            }
        });
    }

    private int getCurrentPosition() {
        View snapView = snapHelper.findSnapView(chuckRecyclerView.getLayoutManager());
        return chuckRecyclerView.getLayoutManager().getPosition(snapView);
    }

    private void getJokes() {
        getJokesCall = ApiServiceFactory.getChuckApiService().getJokes();
        getJokesCall.enqueue(new Callback<ChuckResponse>() {
            @Override
            public void onResponse(Call<ChuckResponse> call, Response<ChuckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jokes.addAll(response.body().getValue());
                    adapter.notifyItemRangeInserted(jokes.size() - response.body().getValue().size(),
                            response.body().getValue().size());
                }
            }

            @Override
            public void onFailure(Call<ChuckResponse> call, Throwable t) {
            }
        });
    }


    //Ovo sluzi za cancelanje requesta u slucaju da se prilikom izvodenja izade iz activitya(inace bi se crashao app)
    @Override
    protected void onStop() {
        super.onStop();
        if (getJokesCall != null) {
            getJokesCall.cancel();
        }
    }
}
