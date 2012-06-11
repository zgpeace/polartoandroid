package de.unihro.es.pta;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PolarToAndroidActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		String out = "";
		TextView vText = (TextView) findViewById(R.id.bla);

		//	Init Bluetooth
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			vText.setText("Bluetooth is not supported.");
		}else{

			vText.setText("Everything's cool.");
			
			//	Enable Bluetooth if neccessary
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}

			
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			BluetoothDevice device = null;
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
			    for (BluetoothDevice deviceTemp : pairedDevices) {
			        // Add the name and address to an array adapter to show in a ListView
			        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			    	if(deviceTemp.getName().equalsIgnoreCase("Polar iWL"))
			    		device = deviceTemp;
			    		out = "Polar gefunden!";
			    	break;
			    }
			    BluetoothSocket socket = null;
			    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			    try {
					socket = device.createRfcommSocketToServiceRecord(MY_UUID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    try {
					socket.connect();
					out = "connected";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
			    try {
					InputStream a = socket.getInputStream();
					byte[] buffer = new byte[1024];
					a.read(buffer);
					out = String.valueOf(buffer);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    
			    
			    
			    try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   
			}
			
			vText.setText(out);
			
			
			
		}
	}
}