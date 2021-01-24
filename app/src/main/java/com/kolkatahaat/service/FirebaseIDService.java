package com.kolkatahaat.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.kolkatahaat.BuildConfig;
import com.kolkatahaat.R;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.SharedPrefsUtils;
import  com.kolkatahaat.GlobalApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class FirebaseIDService extends FirebaseMessagingService {
    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    private int numMessages = 0;
    private static final String AUTH_KEY = "key="+ BuildConfig.SERVER_KEY;
    //private static final String AUTH_KEY = "key=AAAAChB7Pz4:APA91bH5jOVnTRLrbN1-oYlDEm5VSyh3-NiTCFG_kGa7IenMD2hHGNWwOeN0xpy58DMR7v5yM7k3M9SE9JPCwi58UUZRP3Ph_sjdhAkOgADLtvyv0eKWy3A5eSWT4LAk8HdApRsrGD_Y";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        Log.d("FROM", remoteMessage.getFrom());
        sendNotification(notification, data);
    }

    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Bundle bundle = new Bundle();
        bundle.putString(FCM_PARAM, data.get(FCM_PARAM));

        Intent intent = new Intent();
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
                .setContentIntent(pendingIntent)
                //.setContentInfo("Hello")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_app_logo))
                .setColor(getResources().getColor(R.color.colorAccent))
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setNumber(++numMessages)
                .setSmallIcon(R.drawable.ic_stat_app_logo);

        try {
            String picture = data.get(FCM_PARAM);
            if (picture != null && !"".equals(picture)) {
                URL url = new URL(picture);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notification.getBody())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
    }


    /*public void sendWithOtherThread(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(token);
            }
        }).start();
    }

    private void pushNotification(String token) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Google I/O 2016");
            jNotification.put("body", "Firebase Cloud Messaging (App)");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");

            jData.put("picture", "https://miro.medium.com/max/1400/1*QyVPcBbT_jENl8TGblk52w.png");


            JSONArray ja = new JSONArray();
            //ja.put("AAAAwbciNOw:APA91bG1e6pf6pywW29Li-KTwgTiqpEPYM97ZkLJb4smBNMhWUrs_8cCKiB819_ROqPfHELlvRFN06yhPovwx46lh1ybLS17_OuNmXEAfDeAP9XNNdxvae07ST-0UtfUBsnOQ7Y3CLid");
            ja.put(token); //===> device token
            jPayload.put("registration_ids", ja);


            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }*/


    public void sendWithOtherThread(final String token, final String orderItemSize) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(token, orderItemSize);
            }
        }).start();
    }

    private void pushNotification(String mToken, String orderItemSize) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        //JSONObject jData = new JSONObject();
        try {


            Gson gson = new Gson();
            Object userDetial = SharedPrefsUtils.getFromPrefs(GlobalApplication.getInstance().getApplicationContext(), SharedPrefsUtils.USER_DETAIL, "");
            Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);

            jNotification.put("title", "You have an order from "+obj.getUserName());
            jNotification.put("body", "He has purchased " + orderItemSize + " items");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");

            //jData.put("picture", "https://miro.medium.com/max/1400/1*QyVPcBbT_jENl8TGblk52w.png");

            JSONArray ja = new JSONArray();
            ja.put(mToken); //===> device token
            jPayload.put("registration_ids", ja);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            //jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    //Log.e("=======>",resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
