package br.com.plusoftomni.integration.application;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hermeswaldemarin on 18/01/16.
 */
public class TesteRest {

    public static void main (String[] args) throws Exception{

        String param = "{\n" +
                "    \"username\": \"hneto\",\n" +
                "  \t\"password\": \"teste\"\n" +
                "}";

        String url = "http://172.16.28.136:8080/activedirectory/login";
        //String url = "http://189.125.61.2:18080/activedirectory/login";
        //String url = "http://localhost:8080/activedirectory/login";

        String charset = "UTF-8";
        HttpURLConnection httpConn = null;
        URLConnection connection = new URL(url).openConnection();
        httpConn = (HttpURLConnection) connection;
        httpConn.setDoOutput(true); // Triggers POST.
        httpConn.setRequestProperty("Accept-Charset", charset);
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setRequestProperty("Accept", "application/json");

        try (OutputStream output = connection.getOutputStream()) {
            DataOutputStream wr = new java.io.DataOutputStream(httpConn.getOutputStream());
            wr.writeBytes(param);
        }

        int statusCode = httpConn.getResponseCode();

        InputStream is = null;
        if (statusCode >= 200 && statusCode < 400) {
            // Create an InputStream in order to extract the response object
            is = httpConn.getInputStream();
        }
        else {
            is = httpConn.getErrorStream();
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }

        br.close();

        System.out.println(sb);

    }
}
