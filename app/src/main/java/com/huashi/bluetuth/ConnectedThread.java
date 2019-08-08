package com.huashi.bluetuth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{
    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private final Handler handler;
    private final String remoteAddress;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        this.socket = socket;
        remoteAddress = socket.getRemoteDevice().getAddress();

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        inStream = tmpIn;
        outStream = tmpOut;
        this.handler = handler;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes;

        while(true) {
            try {
                bytes = inStream.read(buffer);

                byte[] content = new byte[bytes];
                System.arraycopy(buffer, 0, content, 0, bytes);

                Message message = handler.obtainMessage();
                message.what = ConnectionMessage.RECEIVE_CLIENT_DATA;
                message.obj = content;
                handler.sendMessage(message);
            }catch (Exception e) {
                if (e instanceof IOException) {
                    Message message = handler.obtainMessage();
                    message.what = ConnectionMessage.REMOTE_SHUTDOWN;
                    message.obj = remoteAddress;
                    handler.sendMessage(message);
                    break;
                }
                else {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Write(byte[] stream) {
        try {
            outStream.write(stream);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Cancel() {
        try {
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
