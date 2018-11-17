package com.rishabh.livemodule.live.live.cameraOverlay;

import com.rishabh.livemodule.base.BaseModel;
import com.rishabh.livemodule.network.NetworkResponse;
import com.rishabh.livemodule.pojo.FailureResponse;
import com.rishabh.livemodule.utils.AppConstants;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Created by Rishabh Saxena.
 */

public class CameraOverlayModel extends BaseModel<CameraOverlayModelListener> {

    private Socket mSocket;

    public CameraOverlayModel(CameraOverlayModelListener listener) {
        super(listener);
    }

    @Override
    public void init() {

    }

    public String getUserId() {
        return getDataManager().getUserId();
    }


    /**
     * To get socket singleton instance.
     *
     * @return socket instance
     */
    public Socket getSocket() {
        try {
            HostnameVerifier myHostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            SSLContext mySSLContext = SSLContext.getInstance("TLS");
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }
            }};

            mySSLContext.init(null, trustAllCerts, new java.security.SecureRandom());

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(myHostnameVerifier)
                    .sslSocketFactory(mySSLContext.getSocketFactory(), new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    })
                    .build();


            // HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);
            IO.Options options = new IO.Options();
            //options.webSocketFactory = okHttpClient;
            //options.secure = true;
            //options.transports = new String[]{WebSocket.NAME};
//            options.reconnection = true;
            ///options.forceNew = true;
          //  options.callFactory = okHttpClient;
          //  options.webSocketFactory = okHttpClient;
            options.query = "userId=" + getDataManager().getUserId();
            mSocket = IO.socket(AppConstants.SOCKET_URL, options);
            //mSocket.connect();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return mSocket;
    }

    public void saveStreamAndShare(HashMap<String, String> saveStreamMap) {
        getDataManager().hitSaveStreamApi(saveStreamMap).enqueue(new NetworkResponse<ResponseBody>(this) {
            @Override
            public void onSuccess(ResponseBody body) {
                if(getListener()!=null){
                    getListener().onStreamShared();
                }
            }
            @Override
            public void onFailure(int code, FailureResponse baseResponse) {
                if (getListener() != null)
                    getListener().onErrorOccurred(baseResponse);
            }

            @Override
            public void onError(Throwable t) {
                if (getListener() != null)
                    getListener().onErrorOccurred(null);
            }


        });
    }


}


