package com.rishabh.livemodule.live.player.playeroverlay;

import android.util.Log;

import com.rishabh.livemodule.base.BaseModel;
import com.rishabh.livemodule.network.NetworkResponse;
import com.rishabh.livemodule.pojo.FailureResponse;
import com.rishabh.livemodule.pojo.livecommentresponses.CommentsResponse;
import com.rishabh.livemodule.utils.AppConstants;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

public class PlaybackOverlayModel extends BaseModel<PlayerOverlayModelListener> {
    private Socket mSocket;

    public PlaybackOverlayModel(PlayerOverlayModelListener listener) {
        super(listener);
    }

    @Override
    public void init() {

    }

    public String getUserId() {
      //  return "72";
      return "72";

              //getDataManager().getUserId();

    }

    /*public Bundle getUser(){
        return getDataManager().getUser();
    }*/

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
            options.query = "userId="+  getDataManager().getUserId();
            mSocket = IO.socket(AppConstants.SOCKET_URL, options);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return mSocket;
    }


    public void getComments(String s) {
        getDataManager().getComments(s).enqueue(new NetworkResponse<CommentsResponse>(this) {
            @Override
            public void onSuccess(CommentsResponse body) {
                    Log.e("onSuccess: ",body.getRESULT().toString());
                    if(getListener()!=null){
                        getListener().onGetCommentsResponse(body.getRESULT());
                    }
            }

            @Override
            public void onFailure(int code, FailureResponse baseResponse) {
            }
            @Override
            public void onError(Throwable t) {
            }
        });
    }

    public void reportStream(String reason, String streamId) {
        getDataManager().reportStream(reason,streamId).enqueue(new NetworkResponse<ResponseBody>(this) {
            @Override
            public void onSuccess(ResponseBody body) {

                try {

                   // Log.e("onSuccess: ", body.string());
                    String s = body.string();
                    JSONObject jsonObject = new JSONObject(s);
                    if(jsonObject.getInt("CODE")==200){
                        if(getListener()!=null)
                            getListener().streamReported();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, FailureResponse baseResponse) {

            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }
}
