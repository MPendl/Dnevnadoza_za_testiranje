package com.dnevnadoza.screens.doggo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dnevnadoza.R;
import com.dnevnadoza.network.ApiServiceFactory;
import com.dnevnadoza.network.models.DoggoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoggoActivity extends AppCompatActivity {

    private Call<DoggoResponse> getDoggoCall;
    private ImageView doggoImage;
    private TextView exitButton;
    private TextView newButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doggo);
        initViews();
        initListeners();
        getDoggo();
    }

    private void initViews() {
        doggoImage = findViewById(R.id.doggoImage);
        exitButton = findViewById(R.id.exitButton);
        newButton = findViewById(R.id.newButton);
    }

    private void initListeners(){
        exitButton.setOnClickListener(view -> onBackPressed());
        newButton.setOnClickListener(view -> getDoggo());
    }

    private void getDoggo() {
        getDoggoCall = ApiServiceFactory.getDoggoApiService().getDoggo();
        getDoggoCall.enqueue(new Callback<DoggoResponse>() {
            @Override
            public void onResponse(Call<DoggoResponse> call, Response<DoggoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Glide.with(doggoImage)
                            .load(response.body().getMessage())
                            .into(doggoImage);
                }
            }
            @Override
            public void onFailure(Call<DoggoResponse> call, Throwable t) {
            }
        });
    }

    //Ovo sluzi za cancelanje requesta u slucaju da se prilikom izvodenja izade iz activitya(inace bi se crashao app)
    @Override
    protected void onStop() {
        super.onStop();

        if (getDoggoCall != null) {
            getDoggoCall.cancel();
        }
    }
}
