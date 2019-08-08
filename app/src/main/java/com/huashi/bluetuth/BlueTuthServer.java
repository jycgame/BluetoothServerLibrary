package com.huashi.bluetuth;

import android.content.Context;
import android.content.Intent;

public class BlueTuthServer {
    private static Context context;

    public static void init(Context context) {
        BlueTuthServer.context = context;
    }

    public static void startServer() {
        if (BlueTuthServer.context != null) {
            Intent intent = new Intent(BlueTuthServer.context, BlueTuthService.class);
            BlueTuthServer.context.startService(intent);
        }
    }

    public static void stopServer() {
        if (BlueTuthServer.context != null) {
            Intent intent = new Intent(BlueTuthServer.context, BlueTuthService.class);
            BlueTuthServer.context.stopService(intent);
        }
    }
}
