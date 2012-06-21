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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * @author maikklasen, stefanwendt
 *
 */
public class PolarToAndroidActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1; 
	private static final UUID Polar_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	BluetoothSocket socket;
	TextView vText; 
	Handler handler;
	String out; 
	BluetoothDevice device = null;
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		vText = (TextView) findViewById(R.id.bla);
		out = "";


		new uiUpdate().execute(out);


	}



	/**
	 * Closes BluetoothSocket on destroying the activity.
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO logging
			e.printStackTrace();
		}
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


	private class uiUpdate extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			//			Init Bluetooth
			if (mBluetoothAdapter == null) {
				vText.setText("Bluetooth is not supported.");
			}else{

				//			vText.setText("Everything's cool.");
				//	Enable Bluetooth if neccessary
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}


				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
				// If there are paired devices
				if (pairedDevices.size() > 0) {
					// Loop through paired devices and find Polar iWL
					for (BluetoothDevice deviceTemp : pairedDevices) {
						if(deviceTemp.getName().equalsIgnoreCase("Polar iWL"))
							device = deviceTemp;
						//Toast.makeText(this, "Polar gefunden!", Toast.LENGTH_SHORT).show();
						break;
					}
					socket = null;
					try {
						socket = device.createRfcommSocketToServiceRecord(Polar_UUID);

						try {
							socket.connect();
							//Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

							int heartRate = 0;
							InputStream a = null;
							byte[] buffer = new byte[16];
							//						while(true){
							for(int j = 0; j < 20; j++){
								try {
									a = socket.getInputStream();
								} catch (IOException e2) {
									// TODO logging
									e2.printStackTrace();
								}

								try {
									a.read(buffer);
								} catch (IOException e2) {
									// TODO logging
									e2.printStackTrace();
								}

								for (int i = 0; i < buffer.length - 8; i++) { 

									if (packetValid(buffer, i)) {
										heartRate = buffer[i + 5] & 0xFF;
										if(heartRate != 0){
											out = Integer.toString(heartRate);
											//new uiUpdate().execute(out);
											publishProgress(out);
										}
										//Toast.makeText(this, out, Toast.LENGTH_LONG).show();
										//								vText.setText(out);
										//return out;
										//break;
									}
								}
							}

						} catch (IOException e) {
							// TODO logging
							e.printStackTrace();
							return out = "Socket not connected.";
						}




					} catch (IOException e) {
						// TODO logging
						e.printStackTrace();
						return out = "Socket not created.";
					}


				}

			}
			//vText.setText(out);
			//Toast.makeText(this, out, Toast.LENGTH_LONG).show();



			return out;
		}


		@Override
		protected void onPostExecute (String result){

			vText.setText(out);
			new uiUpdate().execute(out);
		}
		

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			vText.setText(out);

			super.onProgressUpdate(values);
		}



	}

}