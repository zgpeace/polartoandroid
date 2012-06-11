package de.unihro.es.pta;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PolarToAndroidActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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

			ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, vText.getId());
			
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
			    for (BluetoothDevice device : pairedDevices) {
			        // Add the name and address to an array adapter to show in a ListView
			        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			    }
			}
			
			String out = "";
			
			// Show the list of available Bluetooth devices
			for(int i=0; i<mArrayAdapter.getCount(); i++){
				out += mArrayAdapter.getItem(i);
			}
			
			vText.setText(out);
			
			
		}
	}
}