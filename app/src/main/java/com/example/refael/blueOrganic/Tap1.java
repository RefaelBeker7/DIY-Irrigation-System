package com.example.refael.blueOrganic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.example.refael.blueOrganic.Utils.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class Tap1 extends AppCompatActivity {
    public static final String PREFS_NAME = "Hygrometer";
    public static final int LIMIT_PROGRAMS = 8;
    private Button checkBluetoothConnect;
    BluetoothSPP bluetooth;
    final String ON = "1";
    final String OFF = "0";
    final String SEND_DATA = "3";
    static public int flag_ID_name;
    private Switch swHygrometer, swOpenTap;
    DatabaseHelper myDB;
    ListAdapter listAdapter;
    private Button addnewprogaram, transferPrograms;
    private ListView list;
    final ArrayList<String> theList = new ArrayList<>();
    final ArrayList<Integer> listOfHour = new ArrayList<>();
    final ArrayList<Integer> listOfMin = new ArrayList<>();
    final ArrayList<Integer> listOfEndHour = new ArrayList<>();
    final ArrayList<Integer> listOfEndMin = new ArrayList<>();
    final ArrayList<Integer> listOfDuration = new ArrayList<>();
    final ArrayList<String> listOfDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap1);
        SetUpUIViews();
        SharedPreferences prefs = getSharedPreferences("PreferencesName", MODE_PRIVATE);
        flag_ID_name = prefs.getInt("myInt", 1); // 1 is default

        swOpenTap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    swOpenTap.setText("Open Tap ON");
                    bluetooth.send(ON, true);
                    //Create_jason_Arduino();
                } else {
                    swOpenTap.setText("Open Tap OFF");
                    bluetooth.send(OFF, true);
                }
            }
        });

        /*check what we choose before... (switch)*/
        swHygrometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    swHygrometer.setText("Hygrometer ON");
                } else {
                    swHygrometer.setText("Hygrometer OFF");
                }
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switch_key", isChecked);
                editor.commit();
            }
        });

        transferPrograms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Arduino json send.. -- JSONArray jsonArray = new JSONArray().put();--*/

                //bluetooth.send(SEND_DATA, true);

                bluetooth.send( SEND_DATA + toGoodJSon(theList) + '\n', true);

            }
        });

        myDB = new DatabaseHelper(this);

        //populate an ArrayList<String> from the database and then view it
        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                theList.add(/*Name --> */ data.getString(1) + "\n" +
                        "Start Time: " + data.getString(2) +
                        ":" + data.getString(3) + "\n"
                        + "Duration: " + data.getString(4) + "\n" +
                        "Days" + ": " + data.getString(5));
                listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                list.setAdapter(listAdapter);
                listOfHour.add(Integer.parseInt(data.getString(2)));
                listOfMin.add(Integer.parseInt(data.getString(3)));
                listOfEndHour.add(Integer.parseInt(data.getString(2)));
                listOfEndMin.add(Integer.parseInt(data.getString(3)));
                listOfDuration.add(Integer.parseInt(data.getString(4)));
                listOfDays.add(data.getString(5));
            }
            getTimeOfTap();
        }
        //Goto Program
        addnewprogaram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(theList.size()<LIMIT_PROGRAMS)
                    openActivityProgram();
                else
                    Toast.makeText(getBaseContext(), "You've passed the limit Of Programs", Toast.LENGTH_LONG).show();
            }
        });

        //Goto bluetoothConect
        checkBluetoothConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnConnected();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                PopupMenu popupMenu = new PopupMenu(Tap1.this, list);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Edit")) {
                            //Toast.makeText(getBaseContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
                            goToUpdateProgramActivity(String.valueOf(id));
                        }
                        if (item.getTitle().equals("Delete")) {
                            //Toast.makeText(getBaseContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
                            DeleteProgramName(String.valueOf(id));
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    // go back to Home Page...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_menu_btn:
                startActivity(new Intent(this, HomeActivity.class));
                bluetooth.stopService(); /* Need to Disconnect a bluetooth */
        }
        return super.onOptionsItemSelected(item);
    }

    private void SetUpUIViews() {
        bluetooth = new BluetoothSPP(this);
        transferPrograms = findViewById(R.id.Transfer_programs);
        addnewprogaram = findViewById(R.id.btnAddNewProgram);
        checkBluetoothConnect = findViewById(R.id.btnConnect);
        list = (ListView) findViewById(R.id.listProgram);
        swHygrometer = (Switch) findViewById(R.id.switch_hygrometer);
        swOpenTap = (Switch) findViewById(R.id.switch_OpenTap);
        //save the choice we are choose..(switch)
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        boolean silent = settings.getBoolean("switch_key", false);
        swHygrometer.setChecked(silent);
        if (silent) swHygrometer.setTextSize(25); // set 25sp displayed text size of Switch
    }

    //move to program activity
    public void openActivityProgram() {
        bluetooth.stopService(); /* Need to Disconnect a bluetooth */
        Intent intent = new Intent(this, Program.class);
        intent.putExtra("flag_ID_Name", theList.size()+1);
        intent.putIntegerArrayListExtra("listOfEndHour", listOfEndHour);
        intent.putIntegerArrayListExtra("listOfEndMin", listOfEndMin);
        intent.putIntegerArrayListExtra("listOfHour", listOfHour);
        intent.putIntegerArrayListExtra("listOfMin", listOfMin);
        intent.putExtra("DAYS_SELECT", listOfDays);
        startActivity(intent);
    }

    //move to Updateprogram activity
    public void goToUpdateProgramActivity(String Id) {
        bluetooth.stopService(); /* Need to Disconnect a bluetooth */
        Intent goToUpdate = new Intent(this, UpdateProgramActivity.class);
        // get the id for edit..
        int getIDsql = Integer.parseInt(Id);
        // use id for update the activity
        myDB.getNameProgram(Id);
        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) { /* Checked if is any program to edit... */
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            data.moveToPosition(getIDsql); /* get the string from sql */
            Toast.makeText(getBaseContext(), "Your choice edit " + data.getString(1), Toast.LENGTH_LONG).show();
            // update of the program
            goToUpdate.putExtra("PROGRAM_ID", Id);
            goToUpdate.putExtra("PROGRAM_NAME", data.getString(1));
            goToUpdate.putExtra("START_HOUR", data.getString(2));
            goToUpdate.putExtra("START_MIN", data.getString(3));
            goToUpdate.putExtra("DURATION", data.getString(4));
            goToUpdate.putExtra("DAYS_SELECT", data.getString(5));
            // list for check update
            goToUpdate.putIntegerArrayListExtra("listOfEndHour", listOfEndHour);
            goToUpdate.putIntegerArrayListExtra("listOfEndMin", listOfEndMin);
            goToUpdate.putIntegerArrayListExtra("listOfHour", listOfHour);
            goToUpdate.putIntegerArrayListExtra("listOfMin", listOfMin);
            goToUpdate.putExtra("DAYS_SELECTED", listOfDays);
            myDB.close();
            startActivity(goToUpdate);

        }
    }

    //Delete program By ID && Name
    public void DeleteProgramName(String Id) {
        bluetooth.stopService(); /* Need to Disconnect a bluetooth */
        Intent RefreshTap = new Intent(this, Tap1.class);
        int getIDsql = Integer.parseInt(Id);
        // use id for update the activity
        myDB.getNameProgram(Id);
        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) { /* Checked if is any program to edit... */
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            data.moveToPosition(getIDsql); /* get the string from sql */
            Toast.makeText(getBaseContext(), "Your choice delete " + data.getString(1), Toast.LENGTH_LONG).show();
            myDB.deleteName(data.getString(1));
            startActivity(RefreshTap);
        }
    }

    //getTimeOfTap --> take time
    public void getTimeOfTap() {
        int countHour = 0;
        for (int i = 0; i < listOfDuration.size(); i++) {
            while (listOfDuration.get(i) != 0) {
                while (listOfDuration.get(i) > 59) {
                    listOfEndHour.set(i, listOfEndHour.get(i) + 1);
                    listOfDuration.set(i, listOfDuration.get(i) % 60);
                }
                listOfEndMin.set(i, listOfEndMin.get(i) + listOfDuration.get(i));
                listOfDuration.set(i, 0);
            }
            while (listOfEndMin.get(i) > 59) {
                listOfEndHour.set(i, listOfEndHour.get(i) + 1);
                listOfEndMin.set(i, listOfEndMin.get(i) % 60);
            }

            while (listOfEndHour.get(i) > 23) {
                listOfEndHour.set(i, countHour);
                countHour++;
            }
            countHour = 0;
        }
    }

    // for the button checked bluetooth connect
    public void OnConnected() {
        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                checkBluetoothConnect.setText("Connected to " + name);
            }

            public void onDeviceDisconnected() {
                checkBluetoothConnect.setText("Connection lost");
            }

            public void onDeviceConnectionFailed() {
                checkBluetoothConnect.setText("Unable to connect");
            }
        });

        checkBluetoothConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }


    public void onDestroy() {
        super.onDestroy();
        bluetooth.stopService(); /* Need to Disconnect a bluetooth */
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bluetooth.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public List<String> SearchDays(List<String> stringDays){
        //int intIndex = stringDays.indexOf("mon");
        List <String> listClone = new ArrayList<>();
        for (String days : stringDays) {
            if(days.contains("Mon"))
                listClone.add("1");
            if(days.contains("Tue"))
                listClone.add("2");
            if(days.contains("Wen"))
                listClone.add("3");
            if(days.contains("Thu"))
                listClone.add("4");
            if(days.contains("Fri"))
                listClone.add("5");
            if(days.contains("Sat"))
                listClone.add("6");
            if(days.contains("Sun"))
                listClone.add("0");
        }

        return listClone;
    }

// send to Arduino with Json
    public String toGoodJSon(ArrayList<String> allList) {
        /* if the list is empty..*/
        if (allList.isEmpty()) {
            return null;
        }
        try {
            // Here we convert Java Object to JSON
            // We add the object to the main object
            JSONObject jsonAdd;
            JSONObject realjsonAdd = new JSONObject();
            for (int i = 0; i < allList.size(); i++) {
                jsonAdd = new JSONObject();
                String[] splitedValue = allList.get(i).split("\n");
                // after the split now we create json
                // we need to use json array to add the strings
                jsonAdd.put("t", timeSplitJSON(splitedValue[1].split(":")));//start time
                jsonAdd.put("d", durationSplitJSON(splitedValue[2].split(":"))); //duration
                jsonAdd.put("y", SearchDays(daysSplitJSON(splitedValue[3].split(":"))));//days
                //allListforJSON.add(splitedValuenew.toString());
               // we need another object to store the address
               // Set the first name/pair
               /* send each program... */
                realjsonAdd.put("pro" + i, jsonAdd);
            }
            // send json for arduino
            return realjsonAdd.toString();
        }
        // if we have exception!!
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<String> timeSplitJSON(String[] stringList) {
        if (stringList.length == 0){
            return null;
        }
        List<String> myList = new ArrayList<String>(Arrays.asList(stringList));
        myList.remove("Start Time");
        return myList;
    }


    public List<String> daysSplitJSON(String[] stringList) {
        if (stringList.length == 0){
            return null;
        }
        List<String> myList = new ArrayList<String>(Arrays.asList(stringList));
        myList.remove("Days");
        return myList;
    }


    public List<String> durationSplitJSON(String[] stringList) {
        if (stringList.length == 0){
            return null;
        }
        List<String> myList = new ArrayList<String>(Arrays.asList(stringList));
        myList.remove("Duration");
        return myList;
    }


}