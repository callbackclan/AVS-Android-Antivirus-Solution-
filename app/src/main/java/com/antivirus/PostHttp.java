package com.antivirus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.preference.PreferenceManager;

import android.util.Log;

import com.antivirus.model.HttpConnection;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class PostHttp{
    private final Context context;
    static String KEYSTORE_DIR = "KeyStore";
    static String KEYSTORE_FILE = "keystore.bks";
    private static final String CERTIFICATE_VERSION = "certificate_version";
    private static final long CURRENT_CERT_VERSION_CODE = 2;
    private KeyStore keyStore = null;
    private static final String uploadUri = Util.getUploadFileUri();
    private static final String mHttpsAlias = Util.getAlias();
    private static final String mHostname = Util.getDomain();
    private static final int mPort = Integer.parseInt(Util.getPort());
    private static final int MAX_UPLOAD_SIZE = 30000000;   //30MB
    private  boolean WILL_UPLOAD_BIG_FILE = false;
    private  String path;

    public PostHttp(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    private HttpsURLConnection setUpHttpsConnection(String urlString) {
        try {
            //SSL certificate verification -- if !verified -> return null
            File dir = context.getDir(KEYSTORE_DIR, Context.MODE_PRIVATE);
            File keyStoreFile = new File(dir + File.separator + KEYSTORE_FILE);
            FileInputStream pubKeyInput = new FileInputStream(keyStoreFile);
            try {
                keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                if (keyStore == null)
                    return null;
            } catch (KeyStoreException e) {
                return null;
            }
            keyStore.load(pubKeyInput, "".toCharArray());
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(1500);
            urlConnection.setReadTimeout(1500);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            return urlConnection;

        } catch (Exception ex) {
            return null;
        }
    }
    private String uploadingFile(HttpURLConnection conn) {
        DataOutputStream request;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024; // 1MB
        File sourceFile;
        if(this.path!=null) {
            try {
                sourceFile = new File(this.path);
                Log.d("test_", sourceFile.getAbsolutePath());
                if (!sourceFile.isFile()) {
                    return "--Source File not exist :" + this.path;
                } else {


                        //Read bytes with InputStream
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        bytesAvailable = fileInputStream.available();
                        if(bytesAvailable<MAX_UPLOAD_SIZE) {
                            byte[] bFile = new byte[(int) sourceFile.length()];
                            fileInputStream.read(bFile);
                            fileInputStream.close();

                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                            conn.setRequestProperty("accept", "application/json");
                            conn.connect();
                            request = new DataOutputStream(conn.getOutputStream());
                            request.writeBytes("--" + boundary + "\r\n");
                            request.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + sourceFile.getName() + "\"\r\n\r\n");
                            request.write(FileUtils.readFileToByteArray(sourceFile));
                            request.writeBytes("\r\n");
                            request.writeBytes("--" + boundary + "--\r\n");
                            request.flush();

                            Log.d("test_", "successful uploading " + this.path);
                            return this.path;
                        }else{
                            return "File size exceeded";
                        }

                    }
            } catch (MalformedURLException ex) {
                return "--MalformedURLException Exception : check script url.";
            } catch (Exception e) {
                return "--Some Fatal exception see logcat";
            }
        }
        else
            return "GET DATA Request";
    }
    public String uploadFile() {

        String httpsMessage = "No Connection";
        if (!WILL_UPLOAD_BIG_FILE) {
            try {
                File f = new File(this.path);

                Log.d("test_", f.getAbsolutePath());

                FileInputStream fis = new FileInputStream(this.path);
                int filSize = fis.available();
                if (filSize > MAX_UPLOAD_SIZE) {
                    return "Upload Skipped for large file " + this.path;
                }
                fis.close();
            } catch (Exception e) {
                Log.d("test_b", e.getMessage());
            }

        }

        if (writeServerCert()) {
            HttpsURLConnection urlConnection = setUpHttpsConnection(uploadUri);
            if (urlConnection != null) {
                urlConnection.setHostnameVerifier((hostname, session) -> {
                    try {
                        X509Certificate cert = (X509Certificate) session.getPeerCertificates()[0];
                        return cert.equals(keyStore.getCertificate(mHttpsAlias));
                    } catch (Exception e) {
                        //e.printStackTrace();
                        return false;
                    }
                });
            }else {
                return "No Connection";
            }
            try {
                SSLSocket sslSocket = (SSLSocket) urlConnection.getSSLSocketFactory().createSocket(mHostname, mPort);
                urlConnection.getHostnameVerifier().verify(mHostname, sslSocket.getSession());
                httpsMessage = uploadingFile(urlConnection);
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    if (!httpsMessage.startsWith("--")) {
                        return httpsMessage ;
                    }
                } else {
                    httpsMessage = "--Conn Failed :" + responseCode;
                }
            } catch (IOException ignored) {
            }
        } else {
            httpsMessage = "--Key could not be imported no point in starting activity";
        }
        return httpsMessage;
    }

    public String startUploading(){

        Future<String> result = null;
        if(isOnline()) {

                ExecutorService service  = Executors.newSingleThreadExecutor();
                result = service.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        //open connection
                        int responseCode = 0;
                        StringBuilder sb = new StringBuilder();
                        HttpURLConnection conn = httpConnection(uploadUri);
                        String out = uploadingFile(conn);
                        Log.d("test_1",out);
                        try {
                            responseCode = conn.getResponseCode();
                            InputStream in = new BufferedInputStream(conn.getInputStream());
                            BufferedReader bin = new BufferedReader(new InputStreamReader(in));

                            String inputLine;
                            while ((inputLine = bin.readLine())!=null) {
                                sb.append(inputLine);
                            }
                            //Log.d("test_",data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            conn.disconnect();
                        }
                        Log.d("test_","Response Code :: "+responseCode + " response :: "+sb.toString());
                        return sb.toString();

                        //return uploadFile();  //https connection
                    }
                });
        }
        try {
            return result.get();
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
        return null;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //SSL Certificate
    private boolean writeServerCert() {

        File dir = context.getDir(KEYSTORE_DIR, Context.MODE_PRIVATE);
        File keyStoreFile = new File(dir + File.separator + KEYSTORE_FILE);

        final SharedPreferences.Editor editor = getPreferences().edit();
        long certVersionCode = getPreferences().getLong(CERTIFICATE_VERSION, 0);
        boolean a = keyStoreFile.exists();
        Log.d("test_",String.valueOf(a));
        if ((!dir.exists() && dir.mkdir()) || (dir.exists() && dir.isDirectory() &&
                certVersionCode < CURRENT_CERT_VERSION_CODE)) {

            if (keyStoreFile.exists())
                keyStoreFile.delete();
            editor.putLong(CERTIFICATE_VERSION, CURRENT_CERT_VERSION_CODE);
            editor.apply();
            return writeDataToFile(keyStoreFile);
        } else return keyStoreFile.exists();
    }

    private boolean writeDataToFile(File keyStoreFile) {
        FileOutputStream out;
        AssetManager am = context.getAssets();
        InputStream in;
        try {
            in = am.open(KEYSTORE_FILE);
            out = new FileOutputStream(keyStoreFile);
            byte[] buff = new byte[1024];
            int read;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException ignored) {

        }
        return false;
    }
    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    private HttpURLConnection httpConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setConnectTimeout(1500);
        urlConnection.setReadTimeout(1500);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        return urlConnection;
    }
}
