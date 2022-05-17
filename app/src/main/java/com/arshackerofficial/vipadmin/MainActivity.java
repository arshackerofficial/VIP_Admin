package com.arshackerofficial.vipadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;

public class MainActivity extends AppCompatActivity {
    TextView TotalActiveKeys;
    String ActiveKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TotalActiveKeys = findViewById(R.id.MainactiveKeysText);

        String url = "https://vip-mod-data.000webhostapp.com/yiueftidufudhgeurrhgiudfh/generalInfo.php";
        new geturiContent().execute(url);

        NeumorphButton uploadneumorphButton = findViewById(R.id.MainuploadBtn);
        uploadneumorphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UploadActivity.class));
            }
        });

        NeumorphButton addDayneumorphButton = findViewById(R.id.MainadddaysBtn);
        addDayneumorphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddDaysActivity.class));
            }
        });

        NeumorphButton addKeyneumorphButton = findViewById(R.id.MainaddkeyBtn);
        addKeyneumorphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddKeyActivity.class));
            }
        });

        NeumorphButton deleteKeyneumorphButton = findViewById(R.id.MaindeleteKeyBtn);
        deleteKeyneumorphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DeleteKeyActivity.class));
            }
        });

        NeumorphButton ResetAllneumorphButton = findViewById(R.id.MainresetAllBtn);
        ResetAllneumorphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetAllActivity.class).putExtra("activeKeys", ActiveKeys));
            }
        });

        NeumorphButton resetKetneumorphButton = findViewById(R.id.MainresetKeyBtn);
        resetKetneumorphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetKeyActivity.class));
            }
        });

        NeumorphCardView viewKeysCardView = findViewById(R.id.MainviewKeys);
        viewKeysCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewActivity.class));
            }
        });
    }

    private  class geturiContent extends AsyncTask<String,Void,String[]> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please Wait!");
            progressDialog.setMessage("Getting Regular Information!");
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
            String[] values = new String[4];
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

            try {
                JSONObject obj = new JSONObject(contents);
                values[0] = obj.getString("msg");
                values[1] = obj.getString("code");
                values[2] = obj.getString("totalActiveKeys");
                values[3] = obj.getString("totalKeys");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return values;

        }

        @Override
        protected void onPostExecute(String[] obj) {
            super.onPostExecute(obj);
            progressDialog.dismiss();
            ActiveKeys = obj[2];
            TotalActiveKeys.setText("Total Active Keys: " + obj[3]);
        }
    }
}