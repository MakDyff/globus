package service;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Dispatcher {

    public String request() throws Exception {
        String TAG = "TEST";
        HttpURLConnection connection = openConnection();
        StringBuffer response = new StringBuffer();
        int responseCode = -1;
        byte[] contents = new byte[1024];
        int bytesRead = 0;


        try {
            connection.connect();

            responseCode = connection.getResponseCode();
            Log.i(TAG, "Response code: $responseCode");

            while((bytesRead = connection.getInputStream().read(contents)) != -1) {
                response.append(new String(contents, 0, bytesRead));
            }

            if(responseCode > 400)
                throw new IOException();
        } catch (MalformedURLException el) {
            el.printStackTrace();
        } catch (IOException el) {
            el.printStackTrace();

            switch (responseCode) {
                case 500: {
                    if(connection.getErrorStream() != null) {
                        while((bytesRead = connection.getErrorStream().read(contents)) != -1) {
                            response.append(new String(contents, 0, bytesRead));
                        }
                    }
                    if(response.length() == 0)
                        throw new Exception("Сервис в данный момент не доступен.");
                }
                default: throw new Exception("Нестабильное соединение, попробуйте обновить данные позже.");
            }
        } finally {
            connection.disconnect();
        }

        return response.toString();
    }

    private HttpURLConnection openConnection() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://www.cbr.ru/scripts/XML_daily.asp").openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        connection.addRequestProperty("Content-Type", "text/xml; charset=utf-8");

        return connection;
    }
}
