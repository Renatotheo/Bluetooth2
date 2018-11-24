package com.example.theobaldo.bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Set;

public class ListaDispositivos extends ListActivity {

    private BluetoothAdapter AdaptadorBTF = null;
    static String EndMACPUB =  null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        AdaptadorBTF = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivospareados = AdaptadorBTF.getBondedDevices();

        if(dispositivospareados.size() > 0){
            for(BluetoothDevice dispositivo : dispositivospareados){
                String nomeBtf = dispositivo.getName();
                String  MAC = dispositivo.getAddress();
                ArrayBluetooth.add(nomeBtf + "\n" + MAC);

            }

        }
        setListAdapter(ArrayBluetooth);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String GERAL = ((TextView) v).getText().toString();

        String EndMac = GERAL.substring(GERAL.length()-17);
        Intent RetornaMAC = new Intent();
        RetornaMAC.putExtra(EndMACPUB, EndMac);
        setResult(RESULT_OK, RetornaMAC);
        finish();

    }
}