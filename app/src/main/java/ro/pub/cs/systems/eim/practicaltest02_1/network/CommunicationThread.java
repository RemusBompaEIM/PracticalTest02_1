package ro.pub.cs.systems.eim.practicaltest02_1.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02_1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02_1.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02_1.model.BitCoinInformation;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (currency)");
            String currency = bufferedReader.readLine();
            if (currency == null || currency.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (currency)!");
                return;
            }
            HashMap<String, BitCoinInformation> data = serverThread.getData();
            BitCoinInformation bitcoinInfo = null;
            if (data.containsKey(currency)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                bitcoinInfo = data.get(currency);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting the information to reach cache...");
                sleep(60000);
                bitcoinInfo = data.get(currency);
            }
            if (bitcoinInfo == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Info is null!");
                return;
            }

            printWriter.println(bitcoinInfo.toString());
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }

        }
    }
}