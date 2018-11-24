package com.example.theobaldo.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Principal extends AppCompatActivity {

    Button Conectar;
    Button LED1;
    Button LED2;
    Button LED3;


    private static final int AtivaBTF = 1;
    private static final int SolicitaConexao = 2;
    private static final int MESSAGE_READ = 3;

    ConnectedThread ConnectedThread;

    Handler mHandler;
    StringBuilder dadosBTF = new StringBuilder();

    BluetoothAdapter BTFadaptador = null;
    BluetoothDevice meuaDispositivo =null;
    BluetoothSocket meuSocket = null;
    private static String MAC = null;

    boolean conexao = false;

    UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //Botões


        Conectar = (Button) findViewById(R.id.ConectarID);
        LED1 = (Button) findViewById(R.id.led1ID);
        LED2 = (Button) findViewById(R.id.led2ID);
        LED3 = (Button) findViewById(R.id.led3ID);
        BTFadaptador = BluetoothAdapter.getDefaultAdapter();
        final Boolean Conexao = false;

        if (BTFadaptador == null){

            Toast.makeText(getApplicationContext(), "Seu Dispositivo não suporta Bluetooth", Toast.LENGTH_LONG).show();
        } if (!BTFadaptador.isEnabled()){
            Intent HabilitarBTF = new Intent(BTFadaptador.ACTION_REQUEST_ENABLE);
            startActivityForResult(HabilitarBTF, AtivaBTF );
        }

        Conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(conexao){
                    //desconectar

                    try{
                        meuSocket.close();
                        conexao = false;

                        Conectar.setText("Conectar");
                        Toast.makeText(getApplicationContext(), "BTF Desconectado", Toast.LENGTH_LONG).show();


                    }catch (IOException erro) {

                        Toast.makeText(getApplicationContext(), "Ocorreu um Erro" + erro, Toast.LENGTH_LONG).show();
                    }

                }else{
                    //conectar

                    Intent abrelista = new Intent(Principal.this, ListaDispositivos.class);
                    startActivityForResult(abrelista, SolicitaConexao);

                }

            }
        });

        LED1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(conexao){
                       ConnectedThread.enviar("led1");
                }else{

                    Toast.makeText(getApplicationContext(), "BTF NÃO está conectado", Toast.LENGTH_LONG).show();
                }

            }
        });

        LED2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(conexao){
                       ConnectedThread.enviar("led2");
                }else{

                    Toast.makeText(getApplicationContext(), "BTF NÃO está conectado", Toast.LENGTH_LONG).show();
                }

            }
        });

        LED3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(conexao){
                        ConnectedThread.enviar("led3");
                }else{

                    Toast.makeText(getApplicationContext(), "BTF NÃO está conectado", Toast.LENGTH_LONG).show();
                }

            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if(msg.what == MESSAGE_READ ){

                    String recebidos = (String) msg.obj;

                    dadosBTF.append(recebidos);

                    int fiminform = dadosBTF.indexOf("}");

                    if(fiminform > 0){

                        String dadoscompletos = dadosBTF.substring(0 , fiminform);

                        int tamanhoinform = dadoscompletos.length();

                        if(dadosBTF.charAt(0) == '{'){

                            String dadosfinais = dadosBTF.substring(1 , tamanhoinform);

                            Log.d("Recebidos", dadosfinais);

                            if(dadosfinais.contains("l1on")){
                                LED1.setText("1 Ligado");
                            }else if (dadosfinais.contains("l1off")){
                                LED1.setText("1 Desligado");
                            }

                            if(dadosfinais.contains("l2on")){
                                LED2.setText("2 Ligado");
                                Log.d("led2", "Ligado");
                            }else if (dadosfinais.contains("l2off")){
                                LED2.setText("2 Desligado");
                                Log.d("led2", "Desligado");
                            }

                            if(dadosfinais.contains("l3on")){
                                LED3.setText("3 Ligado");
                            }else if (dadosfinais.contains("l3off")){
                                LED3.setText("3 Desligado");
                            }

                        }

                       dadosBTF.delete(0,dadosBTF.length());

                    }

                }

            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case AtivaBTF:
                if (resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Bluetooth Ativado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Bluetooth Não ativado", Toast.LENGTH_LONG).show();
                }
                break;

            case SolicitaConexao:
                if (resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(ListaDispositivos.EndMACPUB);
                    Toast.makeText(getApplicationContext(), "MAC OK:  " +
                            ""  + MAC, Toast.LENGTH_LONG).show();

                    meuaDispositivo = BTFadaptador.getRemoteDevice(MAC);
                    Conectar.setText("Desconectar");

                    try{
                        meuSocket = meuaDispositivo.createRfcommSocketToServiceRecord(MEU_UUID);

                        meuSocket.connect();

                        conexao = true;

                        ConnectedThread = new ConnectedThread(meuSocket);
                        ConnectedThread.start();

                        Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_LONG).show();


                    }catch (IOException erro){


                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu Um erro" + erro, Toast.LENGTH_LONG).show();

                    }

                }else{
                    Toast.makeText(getApplicationContext(), "FALHA AO OBTER MAC", Toast.LENGTH_LONG).show();


                }

        }
    }



    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {


            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String dadosbt = new String(buffer, 0 , bytes);

                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosbt).sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device
        public void enviar(String dadosenviar) {
            byte [] msgBuffer = dadosenviar.getBytes();

            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

       /*  Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }*/
    }


}