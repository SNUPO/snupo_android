package org.snupo.android;

import android.content.Intent;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    private WebView mWebView; // 웹뷰 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		//getSupportActionBar().hide();



        String directurl="https://www.snupo.org/";
        Boolean first =SharedPreferenceUtil.getSharedBoolean(getApplicationContext(), "first");
        Log.i("first", first.toString());
        if (first==false)
        {
            String document_srl = SharedPreferenceUtil.getSharedString(getApplicationContext(), "document_srl");
            String comment_srl = SharedPreferenceUtil.getSharedString(getApplicationContext(), "comment_srl");
            if (document_srl != null)
                directurl = directurl + document_srl;
            if (comment_srl != null)
                directurl = directurl + "#comment-" + comment_srl;
        }
        SharedPreferenceUtil.putSharedBoolean(getApplicationContext(), "first", true);
        // Log.i("wdocument_srl", document_srl);
        // Log.i("wcomment_srl", comment_srl);
        Log.i("wurl", directurl);
        mWebView = (WebView)findViewById(R.id.WebView1);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);       // 웹뷰에서 자바 스크립트 사용
        mWebView.getSettings().setBuiltInZoomControls(true);     // 멀티터치 줌 인/아웃 허용
        mWebView.getSettings().setSupportZoom(true);

		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);

        mWebView.getSettings().setDisplayZoomControls(false);    // 확대/축소 버튼 제거
        mWebView.loadUrl(directurl);             // 웹뷰에서 불러올 URL 입력
        mWebView.setWebViewClient(new SnupoWebViewClient());

        FirebaseMessaging.getInstance().subscribeToTopic("news");

    }

    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class SnupoWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            // 왜 deprecated가 뜨는건지..
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, FilteringActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
