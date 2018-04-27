package com.proter.juanjose.protermico;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ConnectionBt extends Observable implements Runnable {

    private static ConnectionBt miClase;
    private Boolean isConnect = false;
    private String address = "20:16:12:05:88:65";
    private String data;
    Thread hilo;
    Context appContext;

    BluetoothAdapter btAdapter = null;
    BluetoothSocket btSocket = null;

    private InputStream mmInStream;
    private OutputStream mmOutStream;


    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean isBtConnected = false;

    private ConnectionBt() {

        //Se crea el adaptador para el bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // Crea el dispositivo y setea la dirección MAC del bluetooth al que nos vamos a conectar.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
//            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Se establece la conexion socket por bluetooth.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }

        hilo = new Thread(this);
        hilo.start();

        //Se crean los flujos de conexion
        flows();
        //Se prueba la conexion enviador un caracter con el dispositivo emparejado, esperando una respuesta de este
        write("c");
        isConnect = true;
        Log.i("Connection", "comienza el hilo del Bluetooth");

    }

    public static synchronized ConnectionBt getInstance() {
        if (miClase == null) {
            miClase = new ConnectionBt();
        }
        return miClase;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];
        int bytes;
        while (true) {

            try {
                hilo.sleep(50);
                bytes = mmInStream.read(buffer);         //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                data = readMessage;

                setChanged();
                notifyObservers();
                clearChanged();

                // Send the obtained bytes to the UI Activity via handler
//            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                recibir();

            } catch (Exception e) {
                break;
            }
        }
    }

    public void recibir() {

        if (isConnect) {
//            msg("entra al singleton");
            /*setChanged();
            notifyObservers();
            clearChanged();*/
            isConnect = false;
            Log.i("ConnectionRec", "entra al recibir");
        }
    }

    private void flows() {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            //Create I/O streams for connection
            tmpIn = btSocket.getInputStream();
            tmpOut = btSocket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void write(String input) {
        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
        } catch (IOException e) {
            //if you cannot write, close the application
//            Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
//            finish();

        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(myUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    public void msg(String s) {
        Toast.makeText(appContext, s, Toast.LENGTH_LONG).show();
    }

    public Boolean getIsConnect() {
        return isConnect;
    }

    public String getData() {
        return data;
    }

    public void setAppContext(Context context) {
        this.appContext = context;
    }
}
