package com.newmusic.wangkly.newmusic.tasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.newmusic.wangkly.newmusic.utils.OkHttpUtils;

public class OnlineListTask extends AsyncTask<String ,Integer,String>{


    private Handler handler;


    public OnlineListTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected String doInBackground(String... strings) {

        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();


        String resp =  okHttpUtils.getQuery(strings[0]);


        System.out.println(resp);



        return resp;
    }




    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {

        Bundle bundle = new Bundle();

        bundle.putString("resp",s);

        Message msg = new Message();

        msg.setData(bundle);

        handler.sendMessage(msg);

        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
