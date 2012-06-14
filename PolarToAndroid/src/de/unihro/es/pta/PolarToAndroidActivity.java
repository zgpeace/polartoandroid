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
import android.widget.Toast;

public class PolarToAndroidActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1; 
	private static final UUID Polar_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
					Toast.makeText(this, "Polar gefunden!", Toast.LENGTH_SHORT).show();
					break;
				}
				BluetoothSocket socket = null;
				try {
					socket = device.createRfcommSocketToServiceRecord(Polar_UUID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					socket.connect();
					Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				int heartRate = 0;
				out="";
				InputStream a = null;
				byte[] buffer = new byte[16];
				for(int j = 0; j < 20; j++){
					try {
						a = socket.getInputStream();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					try {
						a.read(buffer);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					for (int i = 0; i < buffer.length - 8; i++) { 

						if (packetValid(buffer, i)) {
							heartRate = buffer[i + 5] & 0xFF;
							out = Integer.toString(heartRate);
							//vText.setText(out);
							break;
						}
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		vText.setText(out);
		Toast.makeText(this, out, Toast.LENGTH_LONG).show();


	}




	public int findNextAlignment(byte[] buffer) {
		// Minimum length Polar packets is 8, so stop search 8 bytes before buffer ends.
		for (int i = 0; i < buffer.length - 8; i++) {
			if (packetValid(buffer,i)) {
				return i;
			}
		}
		return -1;
	}
	private boolean packetValid (byte[] buffer, int i) {
		boolean headerValid = (buffer[i] & 0xFF) == 0xFE;
		boolean checkbyteValid = (buffer[i + 2] & 0xFF) == (0xFF - (buffer[i + 1] & 0xFF));
		boolean sequenceValid = (buffer[i + 3] & 0xFF) < 16;

		return headerValid && checkbyteValid && sequenceValid;
	}



}