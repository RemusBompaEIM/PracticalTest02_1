package ro.pub.cs.systems.eim.practicaltest02_1.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02_1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02_1.model.BitCoinInformation;

public class UpdatingThread extends Thread {
    private ServerThread serverThread;

    public UpdatingThread(ServerThread serverThread) {
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String[] currencies = {Constants.USD, Constants.EUR};
            for(String curr : currencies) {
                String query_address = Constants.WEB_SERVICE_ADDRESS + curr + ".json";
                try {
                    queryServer(query_address, curr);
                } catch (IOException | JSONException e) {
                    Log.e(Constants.TAG, "[Updating THREAD] Execute http query exception!");
                    e.printStackTrace();
                }
            }
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                Log.e(Constants.TAG, "[Updating THREAD] Interrupted exception!");
                e.printStackTrace();
            }
        }

    }

    private void queryServer(String address, String curr) throws IOException, JSONException {
        Log.i(Constants.TAG, "[Updating THREAD] Getting the information from the webservice...");
        HttpClient httpClient = HttpClientBuilder.create().build();
        String pageSourceCode = "";
        HttpGet httpGet = new HttpGet(address);
        HttpResponse httpGetResponse = httpClient.execute(httpGet);
        HttpEntity httpGetEntity = httpGetResponse.getEntity();
        if (httpGetEntity != null) {
            pageSourceCode = EntityUtils.toString(httpGetEntity);

        }

        if (pageSourceCode == null) {
            Log.e(Constants.TAG, "[Updating THREAD] Error getting the information from the webservice!");
            return;
        } else
            Log.i(Constants.TAG, pageSourceCode );

        JSONObject content = new JSONObject(pageSourceCode);

        JSONObject time = content.getJSONObject(Constants.TIME);
        String updated_time = time.getString(Constants.UPDATED);

        JSONObject bpi = content.getJSONObject(Constants.BPI);
        JSONObject currency = bpi.getJSONObject(curr);
        String rate = currency.getString(Constants.RATE);

        BitCoinInformation bitcoinInfo = new BitCoinInformation(updated_time, Double.parseDouble(rate));
        serverThread.setData(curr, bitcoinInfo);

    }

    public void stopThread() {
        interrupt();
    }


}
