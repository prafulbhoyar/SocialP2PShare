package com.lnt.wifidirecttest;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

/**
 * Created by 10609693 on 3/20/2016.
 */
public class Webserver extends NanoHTTPD {

    /**
     * Constructs an HTTP server on given port.
     */
    public Webserver()throws IOException {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        File rootDir = Environment.getExternalStorageDirectory();

        String uri = session.getUri();
        File[] filesList = null;
        Map<String,String>parameters = session.getParms();
        Map<String,String>files = session.getHeaders();//?????
        String filepath = "";
        if (uri.trim().isEmpty()) {
            filesList = rootDir.listFiles();
        } else {
            filepath = uri.trim();
        }
        filesList = new File(filepath).listFiles();
        String answer = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>sdcard0 - TECNO P5 - WiFi File Transfer Pro</title>";
        if (new File(filepath).isDirectory()) {
            for (File detailsOfFiles : filesList) {
                answer += "<a href=\"" + detailsOfFiles.getAbsolutePath()
                        + "\" alt = \"\">"
                        + detailsOfFiles.getAbsolutePath() + "</a><br>";
            }
        } else {
        }
        answer += "</head></html>" + "uri: " + uri + " \nfiles " + files
                + " \nparameters " + parameters + " \nheader ";
        return  newFixedLengthResponse(answer);



    }


}