package com.huashi.bluetuth;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class BlueTuthService extends Service {

    private Method sendMessageMethod;
    private AcceptThread acceptThread;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Log.d("Unity", "Get message, what: " + message.what);

            switch (message.what) {
                case 1:
                    clientConnectMessage(message);
                    break;
                case 2:
                    receiveClientDataMessage(message);
                    break;
                case 3:
                    remoteClientShutdonwMessage(message);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        init();
        start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Unity", "BlueTuthService onDestroy()");

        acceptThread.exit();
    }

    private void init() {
        Log.d("Unity", "BlueTuthService init()");

        try {
            Class playerClass = Class.forName("com.unity3d.player.UnityPlayer");
            sendMessageMethod = playerClass.getMethod("UnitySendMessage", String.class, String.class, String.class);

            Log.d("Unity", sendMessageMethod.getName() + " | " + sendMessageMethod.toString());

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {
        Log.d("Unity", "BlueTuthService start()");
        // start a thread for accept clients
        acceptThread = new AcceptThread(handler);
        acceptThread.start();
    }

    private void sendMessage(byte tag, byte[] content) {
        byte[] result = new byte[content.length + 1];
        result[0] = tag;

        Log.d("Unity", "sendMessage, tag now is: " + tag);

        System.arraycopy(content, 0, result, 1, content.length);

        String strResult = new String(result);

        try {
            sendMessageMethod.invoke(null, Const.UNITY_MSG_RECEIVER_NAME, Const.UNITY_MSG_RECEIVER_METHOD, strResult);
        }catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Unity", "Send message to C#: " + tag);
    }

    private void clientConnectMessage(Message message) {
        if (sendMessageMethod != null) {
            try {
                byte[] content = ((String)message.obj).getBytes();
                sendMessage((byte) message.what, content);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveClientDataMessage(Message message) {
        if (sendMessageMethod != null) {
            try {
                sendMessage((byte)message.what, (byte[])message.obj);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void remoteClientShutdonwMessage(Message message) {
        if (sendMessageMethod != null) {
            try {
                byte[] content = ((String)message.obj).getBytes();
                sendMessage((byte) message.what, content);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
