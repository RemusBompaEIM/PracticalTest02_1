package ro.pub.cs.systems.eim.practicaltest02_1.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02_1.R;
import ro.pub.cs.systems.eim.practicaltest02_1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02_1.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02_1.network.ServerThread;
import ro.pub.cs.systems.eim.practicaltest02_1.network.UpdatingThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private Spinner informationTypeSpinner = null;
    private Button getWeatherForecastButton = null;
    private TextView weatherForecastTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    private UpdatingThread updatingThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port);
        connectButton = (Button)findViewById(R.id.Connect);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.adresa_client);
        clientPortEditText = (EditText)findViewById(R.id.port_client);
        informationTypeSpinner = (Spinner)findViewById(R.id.spinner);
        getWeatherForecastButton = (Button)findViewById(R.id.Request);
        getWeatherForecastButton.setOnClickListener(getWeatherForecastButtonClickListener);
        weatherForecastTextView = (TextView)findViewById(R.id.view_results);
    }

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }

            updatingThread = new UpdatingThread(serverThread);
            updatingThread.start();
            serverThread.start();
        }

    }

    private GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();
    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String informationType = informationTypeSpinner.getSelectedItem().toString();
            if (informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }
            weatherForecastTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), informationType, weatherForecastTextView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }

        if (updatingThread != null) {
            updatingThread.stopThread();
        }

        super.onDestroy();
    }
}
