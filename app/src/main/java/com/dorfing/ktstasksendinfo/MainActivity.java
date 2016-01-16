package com.dorfing.ktstasksendinfo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

/*
    Code by Jordan Marx for KTS code assignment.

    This application automatically connects to a socket server when started.  The user
    enters in their name and date of birth.  Clicking the SEND button will send their
    name and date of birth to the socket server in the form of a string.

 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Servers IP and port
    private static final String SERVER_IP = "10.0.0.4";
    private static final int SERVERPORT = 8880;

    // Main description of what application does
    TextView mainTextView;

    // The EditText for user input on name and birth date
    EditText nameEditText;
    EditText birthDateEditText;

    // CALENDAR variables for storing date
    int year;
    int month;
    int day;

    // Button used to send info to server
    Button sendButton;

    // Socket for connecting to server
    Socket s = null;
    Boolean connectedToServer = false;

    // PrintWriter for sending msg
    PrintWriter out = null;

    // Buffered Reader for reading received msg
    BufferedReader in = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Acces the TextView defined in content XML
        mainTextView = (TextView) findViewById(R.id.textdescription);

        // Access the name EditText defined in content XML
        nameEditText = (EditText) findViewById(R.id.editnametext);

        // Access the birth date EditText defined in content XML
        birthDateEditText = (EditText) findViewById(R.id.editbirthtext);

        // OnClick for birth date edit text
        birthDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //To show current date in the datepicker
                Calendar mcurrentDate=Calendar.getInstance();
                year = mcurrentDate.get(Calendar.YEAR);
                month = mcurrentDate.get(Calendar.MONTH);
                day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // Set the date to the birth date edit text
                        // Getting the current month has January start at 0 therefore I must add 1 to the month
                        birthDateEditText.setText((selectedMonth + 1) + "-" + selectedDay + "-" + selectedYear);
                    }
                },year, month, day);
                mDatePicker.setTitle("Select Birth Date");
                mDatePicker.show();  }
        });

        // Access the send button defined in content XML and set OnClick to this
        sendButton = (Button) findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(this);

        // Connect to server
        Runnable connect = new connectSocket();
        new Thread(connect).start();

    }


    @Override
    public void onClick(View v) {

        /*
            SEND button has been clicked!
         */


        // If name or birth date field is empty then don't send data
        if(nameEditText.getText().toString().isEmpty() ||
                birthDateEditText.getText().toString().isEmpty())
        {
            // Displays pop-msg
            Toast.makeText(this, "Field is Vaccant", Toast.LENGTH_LONG).show();
        }
        // If connceted to server then send info
        else if(connectedToServer)
        {
            // Send the msg to the server
            Runnable send = new sendThatMsg();
            new Thread(send).start();
        }
        else
        {
            // Displays pop-up msg
            Toast.makeText(this, "ERROR: Could not connect to socket!", Toast.LENGTH_LONG).show();
        }

    }


    // ----------------------------------------------------------------------------//
    //////////////////////////////////// CLASSES ////////////////////////////////////


    // Connect to socket server
    class connectSocket implements Runnable {

        @Override
        public void run() {
            try {
                // Connect to the socket s
                s = new Socket(SERVER_IP, SERVERPORT);

                // You connected to server!
                connectedToServer = true;

                // Get the output stream from the socket s
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

                // Get the input stream from the socket s
                in = new BufferedReader((new InputStreamReader((s.getInputStream()))));
            } catch (IOException e) {

                // You didn't connect to server :(
                connectedToServer = false;
                e.printStackTrace();
            }
        }
    }

    // Send msg to server
    class sendThatMsg implements Runnable {

        @Override
        public void run() {
            // Add the string name and send it to server
            out.println(nameEditText.getText().toString());
            out.flush();

            // Add the string birth date and send it to server
            out.println(birthDateEditText.getText().toString());
            out.flush();
        }
    }
}
