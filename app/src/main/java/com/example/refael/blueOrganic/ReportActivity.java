package com.example.refael.blueOrganic;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {
    private BluetoothAdapter myBluetooth = null;
    ProgressBar progressBar_humidity, progressBar_precip;
    private TextView etConnectBluetooth, etHubStatus,
            etTextDate, et_temp, et_precip_mm, et_humidity;

    final String key_api = "1ee3175037fa4e62b91170544192901";
    String query_city = "Tel Aviv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        SetUpUIViews();
        //get+set font
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Xoxoxa.ttf");
        etTextDate.setTypeface(myCustomFont);
        etHubStatus.setTypeface(myCustomFont);

        // getDate...-->
        Date date = new Date();
        SimpleDateFormat sdf_year  = new SimpleDateFormat("yyyy-dd");
        SimpleDateFormat sdf_month  = new SimpleDateFormat("MMM");
        String stringMonth =  sdf_year.format(date) + "\r\n" + sdf_month.format(date);
        getMonth(stringMonth);
        // func --- Date

        //progressBar.setProgress(25);

        find_weather(key_api, query_city);
        getDevice();

    }
    // go back to Home Page...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_back, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_menu_btn:
                startActivity(new Intent(this, HomeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void SetUpUIViews() {
        etConnectBluetooth = (TextView)findViewById(R.id.connect_bluetooth);
        etTextDate = (TextView)findViewById(R.id.etDate);
        etHubStatus = (TextView)findViewById(R.id.etHub_status);
        et_temp = (TextView)findViewById(R.id.temp_);
        et_precip_mm = (TextView)findViewById(R.id.et_precip);
        et_humidity = (TextView)findViewById(R.id.etHumidity);
        progressBar_humidity = (ProgressBar) findViewById(R.id.progressBar_humidity);
        progressBar_humidity.setMax(100);
        progressBar_precip = (ProgressBar) findViewById(R.id.progressBar_precip);
        progressBar_precip.setMax(300);
    }

    private void getMonth(String num) {
        TextView textView = (TextView) findViewById(R.id.current_month_view);
        textView.setText(num);
    }
    private void getDevice() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish(); //finish apk
            etConnectBluetooth.setText("NOT \nCONNECT");
        }
        else
        {
            if (myBluetooth.isEnabled())
            {etConnectBluetooth.setText("CONNECT"); }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }
    }

    public void find_weather(String api_key, String query) {
        // String url = "http://api.apixu.com/v1/current.json?key={0}&q={1}".format(Strings.url(api_key), URLEncoder.encode(query));
        String url = Uri.parse("https://api.apixu.com/v1/current.json")
                .buildUpon()
                .appendQueryParameter("key", api_key)
                .appendQueryParameter("q", query)
                .build().toString();

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject main_object = response.getJSONObject("current");
                    Double temp_c = main_object.getDouble("temp_c");
                    Double precip_mm = main_object.getDouble("precip_mm");
                    Double humidity = main_object.getDouble("humidity");

                    et_temp.setText(Double.toString(temp_c) + "°");
                    et_precip_mm.setText("Rainfall: " + Double.toString(precip_mm));
                    et_humidity.setText("Humidity: " + Double.toString(humidity) + "%");

                    progressBar_precip.setProgress(precip_mm.intValue());
                    progressBar_humidity.setProgress(humidity.intValue());

                }catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), "can't reload data", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    public void open_BlueTooth_option(View view) { /* need to check if bluetooth settings*/
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.android.settings",
                "com.android.settings.bluetooth.BluetoothSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
