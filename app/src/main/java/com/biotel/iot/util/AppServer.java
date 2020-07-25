package com.biotel.iot.util;

import android.content.Context;
import android.util.Log;

import com.biotel.iot.services.OnQueryListener;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created By Piyush Pandey on 20-07-2020
 */
public class AppServer extends NanoHTTPD {

    static {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> {
                    return hostname.equals("127.0.0.1");
                });
    }

    private OnQueryListener listener;

    public AppServer(Context context) {
        super(8080);
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream keyStoreStream = context.getAssets().open("keystore.bks");
            keyStore.load(keyStoreStream, "myKeyStorePass".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "iotpwd".toCharArray());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return;
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
            makeSecure(NanoHTTPD.makeSSLSocketFactory(keyStore, keyManagerFactory), null);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public void setListener(OnQueryListener queryListener) {
        this.listener = queryListener;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = null;
        if (listener != null) {
            response = newFixedLengthResponse(listener.onQueryParamsReceived(session.getParms()));
        } else {
            response = newFixedLengthResponse("Something Went Wrong");
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }
}
