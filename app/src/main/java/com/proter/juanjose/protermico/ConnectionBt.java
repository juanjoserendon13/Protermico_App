package com.proter.juanjose.protermico;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.UUID;


public class ConnectionBt extends Observable implements Runnable {

    private static ConnectionBt miClase;
    private Boolean isConnect = true;
    private int bytes;

    private String address = "20:16:12:05:88:65";
    private String data;
    private String bufferData;
    static Thread hilo;


    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private InputStream mmInStream;
    private OutputStream mmOutStream;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean isBtConnected = false;

    private ConnectionBt() {

        //Ejecución de tareas en background e inicio del hilo para la conexion.

        new MyTask().execute();
        hilo = new Thread(this);
        hilo.start();
        Log.i("Connection", " comienza el hilo del Bluetooth");

    }

    public static synchronized ConnectionBt getInstance() {
        Log.i("Connection", " Crea la instancia del singleton");
        if (miClase == null) {
            miClase = new ConnectionBt();
        }
        return miClase;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];

        while (true) {
            //Se realiza un condicional que decide si es necesario volver a llamar el asynctask en caso de que
            // se reintente la conexión.
            if (isConnect == false) {
                Log.i("------------------", "llamado secundario al asynctask");
                new MyTask().execute();
                isConnect = true;
            }
            try {
                if (mmInStream != null) {
                    hilo.sleep(50);
                    bytes = mmInStream.read(buffer);         //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    data = readMessage;
                    Log.i("------------------", "dato entrante: " + data);

                    if (data != bufferData) {
                        setChanged();
                        notifyObservers();
                        clearChanged();
                        bufferData = readMessage;
                    }

                    // Send the obtained bytes to the UI Activity via handler
//            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                }
            } catch (Exception e) {
                break;
            }

        }
    }

    private void flows() {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        if (btSocket != null) {
            try {
                //Create I/O streams for connection
                tmpIn = btSocket.getInputStream();
                tmpOut = btSocket.getOutputStream();
                isConnect = true;
                Log.i("------------------", "crea los flujos");

            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            //Envia un mensaje a protermico, validando el envio y la recepcion de datos.
            write("c");
        }
    }

    public void write(String input) {
        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
        if (btSocket != null) {
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
                Log.i("Connection", " Realiza la escritura");
            } catch (IOException e) {
                Log.i("Connection", " No pudo escribir y notifica");

            }
        }
    }

    public int getByte() {
        return bytes;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(myUUID);

        //creates secure outgoing connecetion with BT device using UUID
    }

    public Boolean getBtSocket() {
        if (btSocket == null || isBtConnected == false) {
            Log.i("------------------", "llamado al asynctask");
            new MyTask().execute();
            return false;
        } else {
            Log.i("------------------", "no es necesario llamar al asynctask");
            return true;
        }

    }

    public Boolean getIsConnect() {
        return isConnect;
    }

    public Boolean getIsBtConnected() {
        return isBtConnected;
    }

    public String getData() {
        return data;
    }

    // Clase asynctask que permite que la interfaz no se detenga mientras el usuario se encuentra interactuando con ella.
    private class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (btSocket == null) {

                //Se crea el adaptador para el bluetooth
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                // Crea el dispositivo y setea la dirección MAC del bluetooth al que nos vamos a conectar.
                BluetoothDevice device = btAdapter.getRemoteDevice(address);

                try {
                    btSocket = createBluetoothSocket(device);
                    Log.i("------------------", "crea el socket");
                } catch (IOException e) {
                }
                // Se establece la conexion socket por bluetooth.
                try {
                    btSocket.connect();
                    isBtConnected = true;
                    isConnect = true;
                    Log.i("------------------", "se conecta");

                } catch (IOException e) {
                    try {
                        isBtConnected = false;
                        isConnect = false;
                        Log.i("------------------", "no se puede conectar");
                        btSocket.close();
                        btSocket = null;
                        setChanged();
                        notifyObservers();
                        clearChanged();
                        this.cancel(true);
                    } catch (IOException e2) {
                        //insert code to deal with this

                    }
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isBtConnected) {
                Log.i("------------------", "valida y crea los flujos");
                //Se crean los flujos de conexion
                flows();
            } else {
                Log.i("------------------", "No se pudo establecer conexion");
            }

        }
    }

}


