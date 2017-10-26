/**
 *  FlashAirRequest.java
 *
 *  Created by Nahoko Uwabe, Fixstars Corporation on 2013/05/20.
 * 
 *  Copyright (c) 2013, TOSHIBA CORPORATION    
 *  All rights reserved.
 *  Released under the BSD 2-Clause license.
 *  http://flashair-developers.com/documents/license.html
 */
package com.max.flashairdemo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class FlashAirRequest {    
    static public String getString(String command) {    
        String result = "";
        try{
            URL url = new URL(command);
            URLConnection urlCon = url.openConnection();
            urlCon.connect();
            InputStream inputStream = urlCon.getInputStream();         
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuffer strbuf = new StringBuffer();
            String str;
            while ((str = bufreader.readLine()) != null) {
                if(strbuf.toString() != "") strbuf.append("\n");
                strbuf.append(str);
            }
            result =  strbuf.toString();                                                
        }catch(MalformedURLException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        catch(IOException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        return result;                        
    }

    static public Bitmap getBitmap(String command) {            
        Bitmap resultBitmap = null;
        try{
            URL url = new URL(command);
            URLConnection urlCon = url.openConnection();
            urlCon.connect();
            InputStream inputStream = urlCon.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byteChunk = new byte[1024];
            int bytesRead = 0;
            while( (bytesRead = inputStream.read(byteChunk)) != -1) {
                byteArrayOutputStream.write(byteChunk, 0, bytesRead);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inPurgeable = true;
            resultBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, bfOptions);
            byteArrayOutputStream.close();
            inputStream.close();
        }catch(MalformedURLException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        catch(IOException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        return resultBitmap;                        
    }
    
    static public String upload(String command, String filename, String saveString) {            
        String result = "";
        final String boundary = "========================";
        try {
            URL url = new URL(command);
            HttpURLConnection httpUrlCon = (HttpURLConnection)url.openConnection();
            httpUrlCon.setDoInput(true);
            httpUrlCon.setDoOutput(true);
            httpUrlCon.setUseCaches(false);
            httpUrlCon.setRequestMethod("POST");    
            httpUrlCon.setRequestProperty("Charset", "UTF-8");
            httpUrlCon.setRequestProperty("Content-Type", "multipart/form-data;boundary="+ boundary);                                    
            DataOutputStream ds = new DataOutputStream(httpUrlCon.getOutputStream());
            ds.writeBytes("--" + boundary + "\r\n");
            ds.writeBytes("Content-Disposition: form-data; name=\"upload.cgi\";filename=\"" + filename +"\""+"\r\n");
            ds.writeBytes( "\r\n" );               
            ds.write(saveString.getBytes("UTF-8"));               
            ds.writeBytes( "\r\n" );     
            ds.writeBytes("--" + boundary + "--" + "\r\n");
            ds.flush();
            ds.close();
            if(httpUrlCon.getResponseCode() == HttpURLConnection.HTTP_OK){
                StringBuffer sb = new StringBuffer();
                InputStream is = httpUrlCon.getInputStream();    
                byte[] data = new byte[1024];
                int leng = -1;
                while((leng = is.read(data)) != -1) {
                    sb.append(new String(data, 0, leng));
                }                
                result = sb.toString();    
            }
        } catch (MalformedURLException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        }
        return result;                        
    }    
    
}