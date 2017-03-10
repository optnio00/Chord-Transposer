package com.tricktekno.chordtransposer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.tricktekno.chordtransposer.helper.PackageHelper;
import com.tricktekno.chordtransposer.util.UtilLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutActivity extends Activity implements OnClickListener {
	
	private static UtilLogger log = new UtilLogger(AboutActivity.class);
	
	private Button okButton;
	private WebView aboutWebView;
	private ProgressBar progressBar;
	private Handler handler = new Handler();
	private View topPanel;
	
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);

		topPanel = findViewById(R.id.topPanel);
		topPanel.setVisibility(View.GONE);
		okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		okButton.setVisibility(View.GONE);
				
		aboutWebView = (WebView) findViewById(R.id.aboutTextWebView);

		aboutWebView.setVisibility(View.GONE);
		
		progressBar = (ProgressBar) findViewById(R.id.aboutProgressBar);

		aboutWebView.setBackgroundColor(0);
		
		aboutWebView.setWebViewClient(new AboutWebClient());
		
		initializeWebView();				
	}
	
	
	public void initializeWebView() {
		
		aboutWebView.loadData(loadTextFile(R.raw.about_body), "text/html", "utf-8");
	}




	private String loadTextFile(int resourceId) {
		
		InputStream is = getResources().openRawResource(resourceId);
		
		BufferedReader buff = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		
		try {
			while (buff.ready()) {
				sb.append(buff.readLine()).append("\n");
			}
		} catch (IOException e) {
			Log.e("AboutActivity","This should not happen",e);
		}
		
		String result = sb.toString();
		
		// format the version into the string
		return String.format(result, PackageHelper.getVersionName(this));
	}
	
	private void loadExternalUrl(String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW"); 
		intent.setData(Uri.parse(url));
		
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		finish();
	}
	
	private class AboutWebClient extends WebViewClient {

		

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, final String url) {
			log.d("shouldOverrideUrlLoading");
			
			// XXX hack to make the webview go to an external url if the hyperlink is 
			// in my own HTML file - otherwise it says "Page not available" because I'm not calling
			// loadDataWithBaseURL.  But if I call loadDataWithBaseUrl using a fake URL, then
			// the links within the page itself don't work!!  Arggggh!!!
			
			if (url.startsWith("http") || url.startsWith("mailto") || url.startsWith("market")) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						loadExternalUrl(url);
					}
				});
				return true;
			}		
			return false;
		}


		@Override
		public void onPageFinished(WebView view, String url) {
			// dismiss the loading bar when the page has finished loading
			handler.post(new Runnable(){

				@Override
				public void run() {
					progressBar.setVisibility(View.GONE);
					aboutWebView.setVisibility(View.VISIBLE);
					topPanel.setVisibility(View.VISIBLE);
					okButton.setVisibility(View.VISIBLE);
					
				}});
			super.onPageFinished(view, url);
		}
	}
}
