package com.huashi.bluetuth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.UUID;

public class AcceptThread extends Thread{
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket serverSocket;
    private Handler handler;
    private boolean needExit;

    public AcceptThread(Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
        needExit = false;

        if (bluetoothAdapter != null) {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Const.SERVER_NAME, UUID.fromString(Const.SERVER_UUID));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        while(!needExit) {
            try {
                BluetoothSocket socket = serverSocket.accept();

                Log.d("Unity", "1 client connected.");

                BluetoothDevice device = socket.getRemoteDevice();
                String name = device.getName();
                String address = device.getAddress();

                Message message = handler.obtainMessage();
                message.what = ConnectionMessage.CLIENT_CONNECTED;
                message.obj = name.toString() + "/" + address.toString();
                handler.sendMessage(message);

                ConnectedThread connectedThread = new ConnectedThread(socket, handler);
                connectedThread.start();
            }catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void exit() {
        needExit = true;
    }
}
