package de.unihro.es.pta;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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

			
			
			
		}
	}
}