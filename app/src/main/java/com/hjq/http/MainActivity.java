package com.hjq.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hjq.http.net.BaseUrl;
import com.hjq.http.net.RestClient;
import com.hjq.http.net.callback.IError;
import com.hjq.http.net.callback.IFailure;
import com.hjq.http.net.callback.ISuccess;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void testNetworkRequest() {
        RestClient.builder()
                .url(BaseUrl.BASE_URL + BaseUrl.GET_HOME)
                .params("key", "value")
                .onSuccess(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                    }
                })
                .onFailure(new IFailure() {
                    @Override
                    public void onFailure() {

                    }
                })
                .onError(new IError() {
                    @Override
                    public void onError(int code, String msg) {

                    }
                })
                .build()
                .get();
    }
}
