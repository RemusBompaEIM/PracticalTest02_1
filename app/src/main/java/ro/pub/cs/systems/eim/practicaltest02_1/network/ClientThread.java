package ro.pub.cs.systems.eim.practicaltest02_1.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02_1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02_1.general.Utilities;

public class ClientThread extends Thread{
    private String address;
    private int port;
    private String currency;
    private TextView view_results;

    private Socket socket;

    public ClientThread(String address, int port, String currency, TextView view_results) {
        this.address = address;
        this.port = port;
        this.currency = currency;
        this.view_results = view_results;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(currency);
            printWriter.flush();
            String bitcoinInformation;
            while ((bitcoinInformation = bufferedReader.readLine()) != null) {
                final String finalizedbitcoinInformation = bitcoinInformation;
                view_results.post(new Runnable() {
                    @Override
                    public void run() {
                        view_results.setText(finalizedbitcoinInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}