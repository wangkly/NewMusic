package com.newmusic.wangkly.newmusic.utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");


    OkHttpClient mOkHttpClient;

    public OkHttpUtils() {

        mOkHttpClient = new OkHttpClient();
    }



    public static OkHttpUtils getInstance(){

        return  new OkHttpUtils();
    }


    public String getQuery(String url){

        Request request = new Request.Builder()
                                .url(url)
//                                .header("referer", "https://webpack.js.org/concepts")
                                .build();
        Response response;

        try {
            response = mOkHttpClient.newCall(request).execute();
            return  response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return  "";
    }



    public String post(String url,String json){

        RequestBody requestBody = RequestBody.create(json,JSON);

        Request request = new Request.Builder().url(url).post(requestBody).build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            return  response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


}
