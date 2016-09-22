package org.snupo.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FilteringActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener
{

    EditText user_id;
    CheckBox[] boards = new CheckBox[11];
    String[] boardnames = {"announcements", "cn", "violin1", "violin2", "viola",
            "cello", "bass", "flute", "oboe", "clarinet", "brass"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);

        user_id = (EditText) findViewById(R.id.userid);

        boards[0] = (CheckBox) findViewById(R.id.announcement);
        boards[1] = (CheckBox) findViewById(R.id.cn);
        boards[2] = (CheckBox) findViewById(R.id.violin1);
        boards[3] = (CheckBox) findViewById(R.id.violin2);
        boards[4] = (CheckBox) findViewById(R.id.viola);
        boards[5] = (CheckBox) findViewById(R.id.cello);
        boards[6] = (CheckBox) findViewById(R.id.bass);
        boards[7] = (CheckBox) findViewById(R.id.flute);
        boards[8] = (CheckBox) findViewById(R.id.oboe);
        boards[9] = (CheckBox) findViewById(R.id.clarinet);
        boards[10] = (CheckBox) findViewById(R.id.brass);


        for (int i = 0; i < 11; i++)
        {
            boards[i].setTag(i);
            boards[i].setOnCheckedChangeListener(this);
        }

        //ID설정 로드
        String text = SharedPreferenceUtil.getSharedString(getApplicationContext(), "userid");
        user_id.setText(text);
        //체크박스 설정 로드
        Boolean[] chk= new Boolean[11];
        for(int i=0; i<11; i++)
        {
            chk[i] = SharedPreferenceUtil.getSharedBoolean(getApplicationContext(), boardnames[i]);
            boards[i].setChecked(chk[i]);
        }

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferenceUtil.putSharedString(getApplicationContext(), "userid", user_id.getText().toString());
                sendToDatabase();
                Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
            }

        });

        Button go_back = (Button)findViewById(R.id.goback);
        go_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for(int i=0; i<11; i++)
                {
                    Log.i("filter", boardnames[i]);
                    Log.i("Checked", SharedPreferenceUtil.getSharedBoolean(getApplicationContext(), boardnames[i]).toString());
                }
                finish();
            }
        });

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        CheckBox ncb = (CheckBox) buttonView;
        for(CheckBox tcb : boards)
        {
            if(tcb == ncb)
            {
                int position = (Integer)buttonView.getTag();
                SharedPreferenceUtil.putSharedBoolean(getApplicationContext(), boardnames[position], isChecked);
            }
        }
    }

    public void sendToDatabase()
    {
        String user_id = SharedPreferenceUtil.getSharedString(getApplicationContext(), "userid");
        String token = SharedPreferenceUtil.getSharedString(getApplicationContext(), "token");

        final String strurl = "https://www.snupo.org/?module=push_notification&act" +
                "=insertClient&user_id="+user_id+"&token=" + token;
    //    Log.i("url", strurl);


        new AsyncTask<Void, Void, Void>()
        {
            String result;
            @Override
            protected Void doInBackground(Void... voids)
            {

                URL url = null;
                try {
                    url = new URL(strurl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    Log.i("http", conn.getURL().toString());
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setDefaultUseCaches(false);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream is = conn.getInputStream();        //input스트림 개방
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));  //문자열 셋 세팅
                    reader.readLine();


                }
                catch(MalformedURLException | ProtocolException exception)
                {
                    exception.printStackTrace();
                }
                catch(IOException io)
                {
                    io.printStackTrace();
                }
                return null;
            }
        }.execute();
        return;
    }

}
