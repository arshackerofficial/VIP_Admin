package com.arshackerofficial.vipadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import soup.neumorphism.NeumorphButton;

public class UploadActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    NeumorphButton uploadBtn;
    Spinner modSelect;
    String dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadBtn = findViewById(R.id.Uploadbtn);
        modSelect = findViewById(R.id.UploadSpinner);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strcase = modSelect.getSelectedItem().toString();
                switch (strcase){
                    case "Mod 1":
                        dest = "mod1";
                        break;
                    case "Mod 2":
                        dest = "mod2";
                        break;
                    case "Mod 3":
                        dest = "mod3";
                        break;
                    case "Mod Paks":
                        dest = "modPak";
                        break;
                    case "Mod Data":
                        dest = "modData";
                        break;
                    default:
                        dest = "";
                }
                if(!dest.isEmpty()){
                    new ChooserDialog(UploadActivity.this)
                            .withFilter(false, false, "zip")
                            .withResources(R.string.choose_mod_tittle, R.string.title_choose, R.string.dialog_cancel)
                            .withIcon(R.mipmap.ic_my_folder)
                            .withFileIconsRes(false, R.mipmap.ic_my_file, R.mipmap.ic_my_folder)
                            .withStartFile(Environment.getExternalStorageDirectory().getPath())
                            .withChosenListener(new ChooserDialog.Result() {
                                @Override
                                public void onChoosePath(String path, File pathFile) {
                                    new UploadFileAsync().execute(path,dest);
                                    //Toast.makeText(UploadActivity.this, "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .withOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                    Toast.makeText(UploadActivity.this, "File Not Selected", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .build()
                            .show();
                }else {
                    Toast.makeText(UploadActivity.this, "Please Select a Mod", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private class UploadFileAsync extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        Boolean flag = false;
        String serverResponseMessage;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UploadActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Uploading");
            progressDialog.setMessage("Please wait a while\nFile is being uploaded!");
            progressDialog.show();
            flag = false;
        }

        @Override
        protected String doInBackground(String... params) {
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

            try {
                String sourceFileUri = params[0];

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = "https://vip-mod-data.000webhostapp.com/yiueftidufudhgeurrhgiudfh/uploadData.php?name=" + params[1] ;

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        //conn.setChunkedStreamingMode(0);
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("bill", sourceFileUri);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                                + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);
                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);
                        int serverResponseCode = conn.getResponseCode();
                        serverResponseMessage = conn.getResponseMessage();

                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                        flag = true;

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            String toastTXT;
            if(flag){
                toastTXT = serverResponseMessage;
            }else {
                toastTXT = "Not Uploaded";
            }
            Toast.makeText(UploadActivity.this, toastTXT, Toast.LENGTH_SHORT).show();
        }
    }
}