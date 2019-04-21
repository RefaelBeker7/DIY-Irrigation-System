package com.example.refael.blueOrganic;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.refael.blueOrganic.Utils.DatabaseHelper;
import com.example.refael.blueOrganic.Utils.TimePickerFragment;

import java.util.ArrayList;

public class Program extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static String STR_Separator = " , ";
    EditText etduration;
    TextView ProgramName;
    TextView tvstarttime;

    DatabaseHelper myDB = new DatabaseHelper(this);

    ArrayList<Integer> listOfMin, listOfHour, listOfEndMin, listOfEndHour;
    ArrayList<String> listOfDays, selection = new ArrayList<String>();
    int startHour, startMin, duration, idName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);
        myDB = new DatabaseHelper(this);
        Button starttime = findViewById(R.id.btnStartTime);
        SetUpUIViews();

        //ID name for program
        idName = getIntent().getIntExtra("flag_ID_Name", 1);
        ProgramName.setText("PROGRAM_" + idName);

        // time tap -- Start & End
        listOfEndHour = getIntent().getIntegerArrayListExtra("listOfEndHour");
        listOfEndMin = getIntent().getIntegerArrayListExtra("listOfEndMin");
        listOfHour = getIntent().getIntegerArrayListExtra("listOfHour");
        listOfMin = getIntent().getIntegerArrayListExtra("listOfMin");
        listOfDays = getIntent().getStringArrayListExtra("DAYS_SELECT");
        //STR separator --> to array by days choice

        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_program, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.back_btn:
                startActivity(new Intent(this, Tap1.class));
                break;
            case R.id.profile_menu_btn:
                startActivity(new Intent(this, HomeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void Save(View view) {
        //ProgramName
        String programName = ProgramName.getText().toString();

        //duratin time
        String NewDuration = etduration.getText().toString();
        // checked if duration null --> if notNull put to Integer..
        if (NewDuration.isEmpty()) {
            Toast.makeText(this, "You must enter an Duration", Toast.LENGTH_SHORT).show();
            duration = 0;
        } else {
            duration = Integer.parseInt(NewDuration);
        }

        //Pass object Between Activity
        Intent intent = new Intent(Program.this, Tap1.class);


        //SQLite -- >> the save and store a string
        String daysString = String.join(STR_Separator, selection);
        if (!checkEmpty(programName, startHour, startMin, duration, daysString)) {
            //check time Tap start & End
            if (checkTapTime(startHour, startMin, duration)) {
                AddData(programName, startHour, startMin, duration, daysString);
                // list global object
                // for save value -- cant use with static
                SharedPreferences.Editor editor = getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
                editor.putInt("myInt", ++idName);
                editor.apply();
                startActivity(intent);
            }
        }
    }

    public int checkEndHour(Integer minutes) {
        int count = 0;
        while (minutes > 59) {
            minutes %= 60;
            count++;
        }
        return count;
    }

    //checkTapTime
    public boolean checkTapTime(Integer StartHour, Integer StartMin, Integer Duration) {
        //convert to string
        //String[] daysChoose = selection.toArray(new String[0]);
        int endMin = StartMin + Duration;
        int count, endHour = StartHour;
        // this days the user now choose
        for (String daysChoice : selection)
        {// list of what use user choose of days..
            for (String daysWasChoiceForList : listOfDays){
                String[] daysWasChoice = convertStringToArray(daysWasChoiceForList);
                // now we want to over all choice now and the choice was...
                for(int index = 0; index<daysWasChoice.length; index++) {
                    // now we want know if we have equal !
                    if (daysChoice.equals(daysWasChoice[index]))
                    {
                        if (endMin > 59) {
                            count = checkEndHour(endMin);
                            while (endMin > 59) {
                                endMin %= 60;
                            }
                            for (int i = 0; i < count; i++) {
                                if (endHour > 23) {
                                    endHour = (count - i) -1;
                                    /* need to fix if is over time like -->
                                    sunday to thu 00:00 -- 02:40 equals... */
                                    for (int indexTime = 0; indexTime < listOfEndHour.size(); indexTime++)
                                    {
                                    if(((listOfHour.get(indexTime) >= StartHour && listOfMin.get(indexTime) >= StartMin)&&
                                    (listOfEndHour.get(indexTime) <= endHour && listOfEndMin.get(indexTime) <= endMin))
                                    ||
                                    ((listOfHour.get(indexTime) <= StartHour && listOfMin.get(indexTime) <= StartMin)
                                    &&(listOfEndHour.get(indexTime) <= endHour && listOfEndMin.get(indexTime) <= endMin))
                                    ||
                                    ((listOfEndMin.get(indexTime) >= StartHour && listOfEndMin.get(indexTime) >= StartMin)
                                    && (listOfEndHour.get(indexTime) <= endHour && listOfEndMin.get(indexTime) <= endMin))
                                    ||
                                    ((listOfHour.get(indexTime) <= StartHour && listOfMin.get(indexTime) <= StartMin)
                                    && (listOfEndHour.get(indexTime) >= endHour && listOfEndMin.get(indexTime) >= endMin))
                                    ){
                                    Toast.makeText(this, "You have open tap this time..", Toast.LENGTH_SHORT).show();
                                    return false;
                                    }
                                }
                                    break;
                                } else
                                    endHour++;
                            }
                        }
                // check up if time the user use not be used more than ones time...
                        for (int indexTime = 0; indexTime < listOfEndHour.size(); indexTime++)
                        {
                            if(((listOfHour.get(indexTime) >= StartHour && listOfMin.get(indexTime) >= StartMin)
                            && (listOfEndHour.get(indexTime) <= endHour && listOfEndMin.get(indexTime) <= endMin))
                            ||
                            ((listOfHour.get(indexTime) <= StartHour && listOfMin.get(indexTime) <= StartMin)
                            && (listOfEndHour.get(indexTime) <= endHour && listOfEndMin.get(indexTime) <= endMin))
                            ||
                            ((listOfEndMin.get(indexTime) >= StartHour && listOfEndMin.get(indexTime) >= StartMin)
                            && (listOfEndHour.get(indexTime) <= endHour && listOfEndMin.get(indexTime) <= endMin))
                            ||
                            ((listOfHour.get(indexTime) <= StartHour && listOfMin.get(indexTime) <= StartMin)
                            && (listOfEndHour.get(indexTime) >= endHour && listOfEndMin.get(indexTime) >= endMin))
                            ){
                                Toast.makeText(this, "You have open tap this time..", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    }
                }
        }   }
            return true;
        }

    public void AddData(String programNameSQL, Integer StartHour, Integer StartMin, Integer Duration, String days) {
        // add the data we send to func in DataBaseHelper!!
        boolean insertData = myDB.addData(programNameSQL,StartHour, StartMin, Duration, days);
        //checked if the data store..
        if(insertData==true){
            Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Something went wrong :(...", Toast.LENGTH_LONG).show();
        }
    }

    //checked if is a empty choose..
    public boolean checkEmpty(String programNameSQL, Integer StartHour, Integer StartMin, Integer Duration,
                              String days){
/*
        if(programNameSQL.isEmpty()){
            //error name is empty
            Toast.makeText(this, "You must enter a name of program", Toast.LENGTH_SHORT).show();
            return true;
        }
*/
        if(StartHour < 0){
            Toast.makeText(this, "You must enter an Start Hour", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(StartMin < 0){
            Toast.makeText(this, "You must enter an Start Min", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(Duration <= 0){
            Toast.makeText(this, "You must enter an Duration", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(days.isEmpty()){
            Toast.makeText(this, "You must choose days", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    // select by checkBox how choose
    public void selectItem(View view){
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId())
        {
            case R.id.cbMon:
                if(checked){
                    selection.add("Mon");
                }
                else {selection.remove("Mon");}
                break;
            case R.id.cbTue:
                if(checked)
                {
                    selection.add("Tue");
                }
                else {selection.remove("Tue");}
                break;
            case R.id.cbWen:
                if(checked)
                {
                    selection.add("Wen");
                }
                else {selection.remove("Wen");}
                break;
            case R.id.cbThu:
                if(checked)
                {
                    selection.add("Thu");
                }
                else {selection.remove("Thu");}
                break;
            case R.id.cbFri:
                if(checked)
                {
                    selection.add("Fri");
                }
                else {selection.remove("Fri");}
                break;
            case R.id.cbSat:
                if(checked)
                {
                    selection.add("Sat");
                }
                else {selection.remove("Sat");}
                break;
            case R.id.cbSun:
                if(checked)
                {
                    selection.add("Sun");
                }
                else {selection.remove("Sun");}
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tvstarttime = (TextView)findViewById(R.id.tvStartTime);
        tvstarttime.setText("Start Time - " + hourOfDay + ":" + minute);
        this.startHour = hourOfDay;
        this.startMin = minute;
    }

    private void SetUpUIViews() {
        etduration = findViewById(R.id.etduration);
        tvstarttime = (TextView)findViewById(R.id.tvStartTime);
        ProgramName = (TextView) findViewById(R.id.etProgramName);
    }
    public static String[] convertStringToArray(String str){
        String[] array = str.split(STR_Separator);
        return array;
    }
}
