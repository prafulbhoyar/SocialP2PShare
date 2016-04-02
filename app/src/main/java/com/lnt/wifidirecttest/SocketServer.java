package com.lnt.wifidirecttest;

/**
 * Created by 10609693 on 3/27/2016.
 */

    import android.util.Log;

    import com.lnt.p2plibrary.P2PIntroMessage;
    import com.lnt.p2plibrary.P2PMessage;

    import java.io.DataInputStream;
    import java.io.DataOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.io.PrintStream;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;

public class SocketServer {

        ServerSocket serverSocket;
        String message = "";
        static final int socketServerPORT = 9080;
        String TAG = "SocketServer";
        List<MacToIpMapping> listOfDevices = Collections.synchronizedList(new ArrayList<MacToIpMapping>(2));

        public SocketServer() {


        }

        public void startServer()
        {
            Thread socketServerThread = new Thread(new SocketServerThread());
            socketServerThread.start();
        }

        public int getPort() {
            return socketServerPORT;
        }

        public void stopServer() {
            if (serverSocket != null) {
                Log.d(TAG,"Stop Server Called");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private class SocketServerThread extends Thread {

            int count = 0;

            @Override
            public void run() {
                try {
                    // create ServerSocket using specified port
                    serverSocket = new ServerSocket(socketServerPORT);

                    while (true) {
                        // block the call until connection is created and return
                        // Socket object

                        Socket socket = serverSocket.accept();
                        count++;
                        Log.d(TAG,"starting server");
                        message += "#" + count + " from "
                                + socket.getInetAddress() + ":"
                                + socket.getPort() + "\n";
                        Log.d(TAG,"connection from:"+message);

                        SocketServerReplyThread socketServerReplyThread =
                                new SocketServerReplyThread(socket, count);
                        socketServerReplyThread.run();

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private class SocketServerReplyThread extends Thread {

            private Socket hostThreadSocket;
            int cnt;

            SocketServerReplyThread(Socket socket, int c) {
                hostThreadSocket = socket;
                cnt = c;
            }

            @Override
            public void run() {

                //each client would send it's ip address and name, need to store that
                // this will be returned to any one asking for ip address of a peer by passing
                // the name
                InputStream inputStream;
                OutputStream outputStream;


                try {
                    inputStream = hostThreadSocket.getInputStream();
                    //outputStream = hostThreadSocket.getOutputStream();
                    //read the length to be sent by client
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    int lengthOfMessage = dataInputStream.readInt();
                    //try to read length of byte as length of message
                    byte[]arrBytes = new byte[lengthOfMessage];
                    dataInputStream.read(arrBytes, 0, lengthOfMessage);
                    P2PMessage p2PMessage = new P2PMessage(arrBytes);
                    p2PMessage.printMessage();

                    if(p2PMessage.messageType == P2PMessage.INTRO_MESSAGE_RES)
                    {

                        handleIntroMessage(p2PMessage,hostThreadSocket);
                    }
                    else if(p2PMessage.messageType == P2PMessage.GET_IP_ADDRESS_REQ)
                    {
                        //in this case the message will be
                    }


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    message += "Something wrong! " + e.toString() + "\n";
                }


            }

        }

        private void handleIntroMessage(P2PMessage p2PMessage,Socket socket)
        {
            Log.d(TAG,"Handing intro message");
            P2PIntroMessage p2PIntroMessage = new P2PIntroMessage(p2PMessage);
            p2PIntroMessage.decodeMessage();
            p2PIntroMessage.printMessage();
            MacToIpMapping macToIpMapping = new MacToIpMapping();
            macToIpMapping.ipAddress = p2PIntroMessage.ipAddress;
            macToIpMapping.macAddress = p2PIntroMessage.nameOfDevice;
            //add to the list of devices
            listOfDevices.add(macToIpMapping);
            //send a ack to close the channel
            P2PMessage pMessage = new P2PMessage();
            pMessage.messageType = P2PMessage.ACK_MESSAGE_RES;
            String message = "OK";
            p2PMessage.Message = message.getBytes();
            p2PMessage.messageLength = p2PMessage.Message.length;
            byte[]arrMessage = p2PMessage.getBytes();
            try {
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                //write the length first
                dataOutputStream.writeInt(arrMessage.length);
                dataOutputStream.write(arrMessage);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"Error sending the ack message");
            }


        }

        public String getIpAddressForMac(String macAddress)
        {
            Log.d(TAG,"Get IP Address for:"+macAddress);
            //run through the list in a for loop, the group is not going to be of more than 10-15 members
            for (int i=0;i<listOfDevices.size();i++)
            {
                MacToIpMapping macToIpMapping = listOfDevices.get(i);
                Log.d(TAG,"i:"+i+" mac:"+macToIpMapping.macAddress + " ip address:"+macToIpMapping.ipAddress);
                if(macAddress.equalsIgnoreCase(macToIpMapping.macAddress))
                    return macToIpMapping.ipAddress;
            }
            return null;
        }

        private class MacToIpMapping
        {
            public String macAddress;
            public String ipAddress;
            public MacToIpMapping()
            {
                //give the strings some values
                macAddress = " ";
                ipAddress = " ";
            }
        }




}
