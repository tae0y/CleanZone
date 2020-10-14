package com.github.solarbeam.cleanzone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private TextView mTextMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.textMessage);
        Connect c = new Connect(MainActivity.this);
        c.execute();
    }

    private class Connect extends AsyncTask {
        private Context mContext = null;
        private StringBuilder str = new StringBuilder();

        Connect(Context context){
            this.mContext = context;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.busan.go.kr/covid19/Course01.do").get();
                Elements element = doc.select("div[class=corona_list]");
                ArrayList<ConfirmedPatient> cplist = ConfirmedPatient.getConfirmedPatient(element);
                HashMap<String, Boolean> szmap = ConfirmedPatient.getSafeZone(cplist, "부산");
                for(Map.Entry<String, Boolean> entry : szmap.entrySet()){
                    if(entry.getValue()) str.append(entry.getKey()+" SAFE\n");
                    //else System.out.println(entry.getKey()+" NOT SAFE");
                }
            } catch (Exception e) {
                str.append(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            mTextMessage.setText(str.toString().trim());
        }
    }

}
