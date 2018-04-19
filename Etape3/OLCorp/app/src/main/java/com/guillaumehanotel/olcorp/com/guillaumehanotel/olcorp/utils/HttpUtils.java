package com.guillaumehanotel.olcorp.com.guillaumehanotel.olcorp.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtils {


    private static HttpUtils instance;
    public Retrofit retrofit;

    private HttpUtils(){
        String HOME_IP = "192.168.1.29";
        String YNOV_IP = "10.33.3.20";


        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + YNOV_IP + ":8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }



    public static HttpUtils getInstance(){
        if(instance == null){
            instance = new HttpUtils();
        }
        return instance;
    }



















    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public String startAsyncTask(String url) {
        try {
            return new CallAPI().execute(url).get();
        } catch (InterruptedException e) {
            e.getMessage();
        } catch (ExecutionException ex) {
            ex.getMessage();
        }
        return null;
    }



    private class CallAPI extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
        }
    }




}
