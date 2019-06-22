package com.newmusic.wangkly.newmusic.tasks;

import android.os.AsyncTask;

import com.newmusic.wangkly.newmusic.interfaces.QueryResultListener;
import com.newmusic.wangkly.newmusic.utils.OkHttpUtils;

public class OnlineListTask extends AsyncTask<String ,Integer,String>{


    private QueryResultListener listener;


    public OnlineListTask(QueryResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {

        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();


        String resp =  okHttpUtils.getQuery(strings[0]);

        return resp;
    }




    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {


        listener.onSuccess(s);

        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
