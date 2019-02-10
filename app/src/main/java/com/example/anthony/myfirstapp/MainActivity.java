package com.example.anthony.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnIncrease, btnDecrease, btnDisconnect;
    View number;
    int mInteger = 23;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private View integer_number1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newInt = getIntent();
        address = newInt.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_main);

        btnIncrease = findViewById(R.id.increase);
        btnDecrease = findViewById(R.id.decrease);
        number = findViewById(R.id.integer_number);
        btnDisconnect = findViewById(R.id.button2);


        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btnIncrease.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                increaseInteger(v);
                Increase(); //method to increase

            }
        });
        btnDecrease.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                decreaseInteger(v);
                Decrease(); //method to decrease

            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });

    }

    private void Increase()
    {
        if (btSocket != null)
        {
            try
            {
                btSocket.getOutputStream().write((Integer.toString(9).getBytes()));
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
    private void Decrease()
    {
        if (btSocket != null)
        {
            try
            {
                btSocket.getOutputStream().write((Integer.toString(1).getBytes()));
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //returns to device list layout

    }

    public void increaseInteger(View view)
    {
        mInteger = mInteger + 1;
        display(mInteger);
    }
    public void decreaseInteger(View view)
    {
        mInteger = mInteger - 1;
        display(mInteger);
    }

    private void display(int number)
    {
        TextView displayInteger = findViewById(R.id.integer_number);
        displayInteger.setText("" + number);
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice disPositive = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = disPositive.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}



