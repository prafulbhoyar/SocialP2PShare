package com.lnt.p2plibrary;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by 10609693 on 3/27/2016.
 */
public class P2PMessage {
    public int messageType;//1 - ip address information,
    public int messageLength;
    public byte[] Message;
    public final String TAG="P2PMeesgae";
    public final static int ACK_MESSAGE_RES = 0;
    public final static int INTRO_MESSAGE_RES = 1;
    public final static int GET_IP_ADDRESS_REQ = 2;
    public final static int GET_IP_ADDRESS_RES = 3;


    public P2PMessage()
    {

    }
    public P2PMessage(byte[]arrBytes)
    {
        if(arrBytes == null) {
            Log.e(TAG,"attBytes is null");
            return;
        }
        if(arrBytes.length < 8 ) //atleast message type and message length should be there)
        {
            Log.e(TAG,"length is less than 8 bytes for the message");
            return;
        }
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(arrBytes));
        //read the first integer
        try {
            messageType = dis.readInt();
        } catch (IOException e) {
            Log.e(TAG,"unable to read message type");
        }
        Log.e(TAG,"Message type:"+messageType);
        //read the message length
        //read the first integer
        try {
            messageLength = dis.readInt();
        } catch (IOException e) {
            Log.e(TAG,"unable to read message typoe");
        }
        Log.e(TAG,"Message Length:"+messageLength);
        //read the next number of bytes as string
        byte[]arrString = new byte[messageLength];
        try {
            dis.read(arrString,0,messageLength);
        } catch (IOException e) {
            Log.e(TAG,"Error reading ip address");
        }
        Message = arrString;
        Log.d(TAG,Message.toString());
    }

    public byte[]getBytes()
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(this.messageType);
            dataOutputStream.writeInt(this.messageLength);
            dataOutputStream.write(this.Message);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Error encoding P2P Message");
        }
        return byteArrayOutputStream.toByteArray();


    }

    public void decodeMessage()
    {
        Log.d(TAG,"Decoding in the base P2PMessage class");
        Log.d(TAG,"Message is"+ Arrays.toString(Message));

    }

    public void printMessage()
    {
        Log.d(TAG,"Messsage Type:"+messageType);
        Log.d(TAG,"Messsage Length:"+messageLength);
        Log.d(TAG,"Messsage:"+Arrays.toString(Message));

    }
}
