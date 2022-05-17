package com.arshackerofficial.vipadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class DeleteKeyActivity extends AppCompatActivity {
    NeumorphButton deleteKey;
    EditText key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_key);

        key = findViewById(R.id.DeleteKeyeditText);
        deleteKey = findViewById(R.id.DeleteKeybtn);

        String url = "https://vip-mod-data.000webhostapp.com/yiueftidufudhgeurrhgiudfh/deleteKey.php";

        deleteKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!key.getText().toString().trim().isEmpty()){
                    String uri = url + "?key=" + key.getText().toString().trim();
                    new geturiContent().execute(uri);
                }else {
                    key.setError("Fill Details");
                    Toast.makeText(DeleteKeyActivity.this, "Fill Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  class geturiContent extends AsyncTask<String,Void,String[]> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DeleteKeyActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please Wait! \n Deleting Key!!");
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
                key.setError(obj[1]);
                Toast.makeText(DeleteKeyActivity.this, obj[1], Toast.LENGTH_SHORT).show();
            } else if(obj[0].equals("1")){
                Toast.makeText(DeleteKeyActivity.this, obj[1], Toast.LENGTH_SHORT).show();
            }
        }
    }
}