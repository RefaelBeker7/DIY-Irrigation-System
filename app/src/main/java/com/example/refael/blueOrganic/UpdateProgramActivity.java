package com.example.refael.blueOrganic;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class UpdateProgramActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static String STR_Separator = " , ";
    String[] daysWasChoice;
    String[] arrayDays = {"Sun", "Mon", "Tue", "Wen", "Thu", "Fri", "Sat"};
    DatabaseHelper myDB = new DatabaseHelper(this);
    String program_name_tap;
    String program_id_tap;
    ArrayList<Integer> listOfMin, listOfHour, listOfEndMin, listOfEndHour;
    ArrayList<String> listOfDays, selection = new ArrayList<String>();
    int startHour, startMin, duration, id, countOfList = 0;
    TextView ProgramName;
    EditText etduration;
    TextView tvstarttime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_program);

        //the Strings from Tap1
        program_id_tap = getIntent().getStringExtra("PROGRAM_ID");
        id = 1 + Integer.parseInt(program_id_tap);

        program_name_tap = getIntent().getStringExtra("PROGRAM_NAME");
        String start_hour_tap = getIntent().getStringExtra("START_HOUR");
        String start_min_tap = getIntent().getStringExtra("START_MIN");
        String duration_tap = getIntent().getStringExtra("DURATION");
        String days_select_tap = getIntent().getStringExtra("DAYS_SELECT");

        // time tap -- Start & End
        listOfEndHour = getIntent().getIntegerArrayListExtra("listOfEndHour");
        listOfEndMin = getIntent().getIntegerArrayListExtra("listOfEndMin");
        listOfHour = getIntent().getIntegerArrayListExtra("listOfHour");
        listOfMin = getIntent().getIntegerArrayListExtra("listOfMin");
        listOfDays = getIntent().getStringArrayListExtra("DAYS_SELECTED");

        /*delete this program from the choice*/
        listOfHour.remove(id-1);
        listOfEndHour.remove(id-1);
        listOfMin.remove(id-1);
        listOfEndMin.remove(id-1);
        listOfDays.remove(id-1);

        //STR separator --> to array by days choice
        daysWasChoice = convertStringToArray(days_select_tap);
        // days to is Selected
        setItemDaysSelected(daysWasChoice);

        //add to selection days was choose..
        for (int i =0; i< daysWasChoice.length; i++)
        {
            selection.add(daysWasChoice[i]);
        }

        // for update integer time
        startHour = Integer.parseInt(start_hour_tap);
        startMin = Integer.parseInt(start_min_tap);

        Button starttime = findViewById(R.id.btnStartTime);

        //set data to values
        SetUpUIViews();
        ProgramName.setText ( program_name_tap );
        etduration.setText(duration_tap);
        tvstarttime.setText("Start Time - " + start_hour_tap + ":" + start_min_tap);

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
        switch (item.getItemId()){

            case R.id.back_btn:
                startActivity(new Intent(this, Tap1.class));
                break;
            case R.id.profile_menu_btn:
                startActivity(new Intent(this, HomeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void Save(View view){
        //ProgramName
        String programName = ProgramName.getText().toString();

        //duratin time
        String NewDuration= etduration.getText().toString();

        if(NewDuration.isEmpty()){
            Toast.makeText(this, "You must enter an Duration", Toast.LENGTH_SHORT).show();
            duration = 0;
        }else {duration = Integer.parseInt(NewDuration);}


        //Pass object Between Activity
        Intent intent = new Intent(UpdateProgramActivity.this, Tap1.class);

        //SQLite -- >> the save and store a string
        String daysString = String.join(STR_Separator, selection);

        if(!checkEmpty(programName,startHour, startMin, duration, daysString)){
            //check time Tap start & End
            if (checkTapTime(startHour, startMin, duration)) {
                UpdateData(programName,startHour, startMin, duration, daysString, program_name_tap);
                // list global object
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
                /* we use countOfList for counting where we are in the list..*/
                String[] daysWasChoice = convertStringToArray(daysWasChoiceForList);
                // now we want to over all choice now and the choice was...
                for(int index = 0; index<daysWasChoice.length; index++) {
                    /* need to fix if is over time like -->
                        sunday to thu 00:00 -- 02:40 equals... */
                    //int indexDaysOFCheck = Arrays.asList(arrayDays).indexOf(daysWasChoice[index]);

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

                                    break;
                                } else
                                    endHour++;
                            }
                        }
                        // check up if time the user use not be used more than ones time...
                        /*for (int indexTime = 0; indexTime < listOfEndHour.size(); indexTime++)
                        {*/
                            if(((listOfHour.get(countOfList) >= StartHour && listOfMin.get(countOfList) >= StartMin)
                                    && (listOfEndHour.get(countOfList) <= endHour && listOfEndMin.get(countOfList) <= endMin))
                                    ||
                                    ((listOfHour.get(countOfList) <= StartHour && listOfMin.get(countOfList) <= StartMin)
                                            && (listOfEndHour.get(countOfList) <= endHour && listOfEndMin.get(countOfList) <= endMin))
                                    ||
                                    ((listOfHour.get(countOfList) >= StartHour && listOfMin.get(countOfList) >= StartMin)
                                            && (listOfEndHour.get(countOfList) == endHour && listOfEndMin.get(countOfList) <= endMin))
                                    ||
                                    ((listOfHour.get(countOfList) <= StartHour && listOfMin.get(countOfList) <= StartMin)
                                            && (listOfEndHour.get(countOfList) >= endHour && listOfEndMin.get(countOfList) >= endMin))
                                    ){
                                Toast.makeText(this, "You have open tap this time..", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        //}
                    }
                }
                countOfList++;
            }countOfList=0; /* --we need count all from the begin--*/
        }
        return true;
    }

    public void UpdateData(String programNameSQL, Integer StartHour, Integer StartMin, Integer Duration,
                           String days, String OldName) {
        // add the data we send to func in DataBaseHelper!!
        myDB.updateName(programNameSQL,StartHour, StartMin, Duration, days, OldName);

    }
    //checked if is a empty choose..
    public boolean checkEmpty(String programNameSQL, Integer StartHour, Integer StartMin, Integer Duration,
                              String days){

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
        tvstarttime.setText("Start Time - " + hourOfDay + ":" + minute);
        this.startHour = hourOfDay;
        this.startMin = minute;
    }

    private void SetUpUIViews() {
        ProgramName = (TextView) findViewById(R.id.etProgramName);
        etduration = findViewById(R.id.etduration);
        tvstarttime = (TextView)findViewById(R.id.tvStartTime);
    }

    public static String[] convertStringToArray(String str){
        if(str == null){return null;}
        String[] array = str.split(STR_Separator);
        return array;
    }
// set selected in days --> checkBox

    public void setItemDaysSelected(String[] daysWasChoice){
        CheckBox checkBox;
        //if(selection.contains(daysWasChoice[0]))

        for (int i =0; i< daysWasChoice.length; i++) {
            switch (daysWasChoice[i]) {
                case "Mon":
                    checkBox = (CheckBox)findViewById(R.id.cbMon);
                    checkBox.setChecked(!checkBox.isChecked());
                    break;
                case "Tue":
                    checkBox = (CheckBox)findViewById(R.id.cbTue);
                    checkBox.setChecked(!checkBox.isChecked());
                    break;
                case "Wen":
                    checkBox = (CheckBox)findViewById(R.id.cbWen);
                    checkBox.setChecked(!checkBox.isChecked());
                    break;
                case "Thu":
                    checkBox = (CheckBox)findViewById(R.id.cbThu);
                    checkBox.setChecked(!checkBox.isChecked());
                    break;
                case "Fri":
                    checkBox = (CheckBox)findViewById(R.id.cbFri);
                    checkBox.setChecked(!checkBox.isChecked());
                    break;
                case "Sat":
                    checkBox = (CheckBox)findViewById(R.id.cbSat);
                    checkBox.setChecked(!checkBox.isChecked());
                    break;
                case "Sun":
                    checkBox = (CheckBox)findViewById(R.id.cbSun);
                    checkBox.setChecked(!checkBox.isChecked());
                    break;
            }
        }
    }
}