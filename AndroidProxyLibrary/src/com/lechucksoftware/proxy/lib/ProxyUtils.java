package com.lechucksoftware.proxy.lib;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.io.HttpResponseWriter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class ProxyUtils
{
    public static final String TAG = "ProxyUtils";

    public static Intent getProxyIntent()
    {
        if (Build.VERSION.SDK_INT >= 12) // Honeycomb 3.1
        {
            return getAPProxyIntent();
        } else
        {
            return getGlobalProxyIntent();
        }
    }

    /**
     * For API < 12
     * */
    private static Intent getGlobalProxyIntent()
    {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.ProxySelector"));

        return intent;
    }

    /**
     * For API >= 12
     * */
    private static Intent getAPProxyIntent()
    {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);

        return intent;
    }

    public static boolean isHostReachable(Proxy proxy)
    {
        int exitValue;
        Runtime runtime = Runtime.getRuntime();
        Process proc;

        try
        {
            proc = runtime.exec("ping -c 1   "
                    + ((InetSocketAddress) proxy.address()).getHostName());
            proc.waitFor();
            exitValue = proc.exitValue();

            Log.d(TAG, "Ping exit value: " + exitValue);

            if (exitValue == 0)
                return true;
            else
                return false;
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static String getURI(URI uri, Proxy proxy)
    {
        try
        {
        	URL url = uri.toURL();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);

            int response = httpURLConnection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK)
            {
                // Response successful
                InputStream inputStream = httpURLConnection.getInputStream();

                // Parse it line by line
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder sb = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null)
                {
                    Log.d(TAG, temp);
                    sb.append(temp);
                }
                
                return sb.toString();
            } 
            else
            {
                Log.e(TAG, "INCORRECT RETURN CODE: " + response);
                return null;
            }
        } 
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
    
    public static boolean isWebReachable(Proxy proxy)
    {     	
    	try
		{
			String result = getURI(new URI("http://www.google.com/"), proxy);
			if (result != null)
				return true;			
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return false;
    }
}
