package com.lnt.wifidirecttest;

import android.util.Log;

import com.lnt.p2plibrary.P2PMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by 10609693 on 3/30/2016.
 */
public class ClientSocket extends Thread {

    private String mServerAddress = " ";
    private int mPort = 0;
    private Socket socket = null;
    private String TAG = "ClientSocket";
    private String mMyName ;
    private String mMyAddress;
    public ClientSocket(String addr, int port,String myName)
    {
        mServerAddress = addr;
        mPort = port;
        mMyName = myName;
        Log.d(TAG,"Addr:"+mServerAddress + " port:"+port);
    }

    public void run()
    {
        try {
            socket = new Socket(mServerAddress,mPort);
            Log.d(TAG,"Connecting to Server:"+mServerAddress+" port:"+mPort);
            //first message is to send self ip address
            mMyAddress = socket.getLocalAddress().getHostAddress();

            Log.d(TAG,"Connecting to Server myaddress:"+mMyAddress);
            sendIntroMessage(socket);
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in client side socket");
        }

    }

    private void sendIntroMessage(Socket socket)
    {
        P2PMessage p2PMessage = new P2PMessage();
        p2PMessage.messageType = P2PMessage.INTRO_MESSAGE_RES;
        byte[] arrIntro = getIntroMsgString();
        if(arrIntro == null)
        {
            Log.e(TAG,"No message to send not sending anything !!");
            return;
        }
        int length = arrIntro.length;

        p2PMessage.messageLength = length;
        p2PMessage.Message = arrIntro;
        Log.d(TAG,"client side message being sent");
        p2PMessage.printMessage();
        byte[]arrMessage = p2PMessage.getBytes();
        //write the length of this message on the socket
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(arrMessage.length);
            dataOutputStream.write(arrMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //wait for the ack message
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            int ackMsgLen  = dataInputStream.readInt();
            byte[]arrBytes = new byte[ackMsgLen];
            P2PMessage ackMessage = new P2PMessage(arrBytes);
            ackMessage.printMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private byte[] getIntroMsgString()
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        byte[] retArray = null;
        //write firs the length of my name
        try {
            dataOutputStream.writeInt(mMyName.length());
            dataOutputStream.writeBytes(mMyName);
            dataOutputStream.writeInt(mMyAddress.length());
            dataOutputStream.writeBytes(mMyAddress);
            retArray = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retArray;
    }
}
