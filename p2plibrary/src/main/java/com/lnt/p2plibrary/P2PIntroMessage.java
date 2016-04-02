package com.lnt.p2plibrary;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by 10609693 on 4/2/2016.
 */
public class P2PIntroMessage extends P2PMessage {

    public String nameOfDevice;
    public String ipAddress;

    public P2PIntroMessage(P2PMessage p2PMessage)
    {
        this.messageType = p2PMessage.messageType;
        this.messageLength = p2PMessage.messageLength;
        this.Message = p2PMessage.Message;
    }

    @Override
    public void decodeMessage(){

        //the message is written as
        //length of name
        //name
        //length of ip address
        //addreess
        if(Message == null) {
            Log.d(TAG, "the message byte array is null, nothing to decode ");
            return;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Message);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        //read the length
        try {
            int lengthOfName = dataInputStream.readInt();
            //based on length get the name
            if(lengthOfName != 0) {
                byte[] arrName = new byte[lengthOfName];
                dataInputStream.read(arrName, 0, lengthOfName);
                nameOfDevice = new String(arrName);
                Log.d(TAG, "Name of the sender:" +nameOfDevice);
            }
            //read the address of the device
            int lengthOfAddress = dataInputStream.readInt();
            if(lengthOfAddress !=0) {
                byte[] arrAddress = new byte[lengthOfAddress];
                dataInputStream.read(arrAddress,0,lengthOfAddress);
                ipAddress = new String(arrAddress);
                Log.d(TAG, "Address of the sender:" +ipAddress);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void printMessage() {
        Log.d(TAG,"Name of the device:"+nameOfDevice);
        Log.d(TAG,"Address of the device:"+ipAddress);

        super.printMessage();
    }
}
