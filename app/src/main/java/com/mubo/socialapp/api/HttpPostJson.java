package com.mubo.socialapp.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.CompletionHandler;
import java.util.Iterator;
import java.util.Map;

public class HttpPostJson {
    public static String SendHttpPost(String URL, JSONArray array) {
        String don="";
        try {
            java.net.URL connectURL = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(array.toString());
            os.flush();
            String conco = String.valueOf(conn.getResponseCode());
            InputStream is = conn.getInputStream();
            don=convertStreamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return don;
    }
    public static String SendHttpPost(String URL, JSONObject obje,String token) {
        String don="";
        try {
            URL connectURL = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            if(!token.isEmpty())
                conn.setRequestProperty("Authorization","Bearer "+token);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            if(obje!=null) {
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
                os.write(obje.toString());
                os.flush();
            }
            String conco = String.valueOf(conn.getResponseCode());
            if(conn.getResponseCode()==403){
                return "unauthorized";
            }
            InputStream is = conn.getInputStream();
            don=convertStreamToString(is);

        } catch (Exception e) {
            Log.i("PostHata",e.toString());
            e.printStackTrace();
        }
        return don;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String MultiPost(File postFile,String Url, Map<String,String> Parameters) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String data = null;
        Object cData = null;
        String ExceptionData = null;
        int serverResponseCode = -1;
        if (postFile != null && !postFile.isFile()) {
            ExceptionData = "File not exists";
            return ExceptionData;
        } else {

            try {
                MultiPartUtility multipart = new MultiPartUtility(Url, "UTF-8");

                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");

                Iterator<Map.Entry<String, String>> itr = Parameters.entrySet().iterator();

                while (itr.hasNext()) {
                    Map.Entry<String, String> d = itr.next();
                    multipart.addFormField(d.getKey(), d.getValue());
                }
                if (postFile != null) {
                    multipart.addFilePart("file", postFile);
                }
                data = multipart.finish();
                serverResponseCode=multipart.getStatus();
               return data;
            } catch (IOException e) {
                e.printStackTrace();
                ExceptionData = e.getMessage();
                return ExceptionData;
            }
        }
    }
    public static String multipartRequest(String urlTo, Map<String, String> parmas, String filepath, String filefield, String fileMimeType)  {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(q[idx]) + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
               return  "hata";
            }

            inputStream = connection.getInputStream();

            result = convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            return e.toString();
        }

    }
}
