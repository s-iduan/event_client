package com.example.event_cord.RestClient;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.example.event_cord.R;
import com.example.event_cord.model.Constants;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private static final String TAG = "RestClient";
    private static Retrofit retrofit;

    private static Certificate getCertificate(Context context) throws Exception {
        Resources res = context.getResources();
        InputStream inputStream = res.openRawResource(R.raw.selfsigned);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        try {
            ca = cf.generateCertificate(inputStream);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            inputStream.close();
        }
        return ca;
    }

    private static SSLSocketFactory getSSLConfig(Context context) throws Exception {

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        Certificate ca = getCertificate(context);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        X509TrustManager mTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        AdditionalKeyStoresSSLSocketFactory factory = new AdditionalKeyStoresSSLSocketFactory(keyStore);

        return factory;
    }

    public static Retrofit getRetrofit(Context context) {
        try {
            SSLSocketFactory factory = getSSLConfig(context);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(factory, (X509TrustManager) ((AdditionalKeyStoresSSLSocketFactory) factory).TrustManager[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.REST_API_BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return null;
    }
}
