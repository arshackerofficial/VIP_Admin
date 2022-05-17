package com.arshackerofficial.vipadmin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ViewActivity extends AppCompatActivity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        list = (ListView)findViewById(R.id.ViewActivityListView);

        String url = "https://vip-mod-data.000webhostapp.com/yiueftidufudhgeurrhgiudfh/viewKeys.php";
        new geturiContent().execute(url);

    }

    private  class geturiContent extends AsyncTask<String,Void,String[]> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please Wait!");
            progressDialog.setMessage("Getting Data!!");
            progressDialog.show();
        }

        @Override
        protected String[] doInBackground(String... strings) {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

// Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
            }


            String uri = strings[0];
            String contents ="";

            try {
                URLConnection conn = new URL(uri).openConnection();

                InputStream in = conn.getInputStream();
                //contents = convertStreamToString(in);

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(in, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                contents = sb.toString();


            } catch (MalformedURLException e) {
                Log.v("MALFORMED URL EXCEPTION",e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(e.getMessage(), e.getMessage());
                e.printStackTrace();
            }

            String[] splitted = contents.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            StringBuffer sb= new StringBuffer(splitted[(splitted.length) - 1]);

            splitted[(splitted.length) - 1] = String.valueOf(sb.deleteCharAt(sb.length()-1));

            return splitted;

        }

        @Override
        protected void onPostExecute(String[] obj) {
            super.onPostExecute(obj);
            progressDialog.dismiss();

            MyListAdapter adapter=new MyListAdapter(ViewActivity.this, obj);

            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedText = obj[position];
                    ClipboardManager clipboard = (ClipboardManager) ViewActivity.this.getSystemService(ViewActivity.this.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Key", selectedText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ViewActivity.this, "Key Copied", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}