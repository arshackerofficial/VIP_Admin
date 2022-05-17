package com.arshackerofficial.vipadmin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class ResetAllActivity extends AppCompatActivity {
    NeumorphButton resetAll;
    TextView keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_all);

        resetAll = findViewById(R.id.ResetAllbtn);
        keys = findViewById(R.id.ResetAllTotalKeys);

        keys.setText("No. of keys with devices linked: " + getIntent().getStringExtra("activeKeys"));

        resetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder at = new AlertDialog.Builder(ResetAllActivity.this);
                at.setCancelable(false);
                at.setTitle("Are you sure to continue!");
                at.setMessage("This will reset all key in the server!");
                at.setNegativeButton("No!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                at.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "https://vip-mod-data.000webhostapp.com/yiueftidufudhgeurrhgiudfh/resetAll.php";
                                new geturiContent().execute(url);

                            }
                        });
                AlertDialog alertDialog = at.create();
                alertDialog.show();
            }
        });
    }

    private  class geturiContent extends AsyncTask<String,Void,String[]> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ResetAllActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please Wait!");
            progressDialog.setMessage("Reseting the Server!");
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
            String[] values = new String[2];
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
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return values;

        }

        @Override
        protected void onPostExecute(String[] obj) {
            super.onPostExecute(obj);
            progressDialog.dismiss();
            if(obj[0].equals("0")){
                Toast.makeText(ResetAllActivity.this, obj[1], Toast.LENGTH_SHORT).show();
            } else if(obj[0].equals("1")){
                Toast.makeText(ResetAllActivity.this, obj[1], Toast.LENGTH_SHORT).show();
            }
        }
    }
}