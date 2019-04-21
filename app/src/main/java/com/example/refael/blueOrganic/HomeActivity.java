package com.example.refael.blueOrganic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.refael.blueOrganic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

public class HomeActivity extends AppCompatActivity {
    //apixu for query
    //https://api.apixu.com/v1/current.json?key=1ee3175037fa4e62b91170544192901&q=tel%20aviv&date=2018
    final String key_api = "1ee3175037fa4e62b91170544192901";
    String query_city = "Tel Aviv";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private Button phonecall;
    private Button eMail;
    private Button tap1;
    private TextView et_temp;

    //For call_us function
    private Context mContext=HomeActivity.this;
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener((mFirebaseAuthListener));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.addAuthStateListener((mFirebaseAuthListener));
    }

    public void singOut(){
        startActivity(new Intent(this, LoginActivity.class));
        mFirebaseAuth.signOut();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SetUpUIViews();

        tap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityTap1();
            }
        });
        eMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_feedback_email();
            }
        });
        phonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_Us_feedback();
            }
        });
// getDate...-->
        Date date = new Date();
        SimpleDateFormat sdf_year  = new SimpleDateFormat("yyyy");
        String string_year = sdf_year.format(date);
        SimpleDateFormat sdf_month  = new SimpleDateFormat("MMM");
        String stringMonth = sdf_month.format(date);
        getMonth(stringMonth);
        getYear(string_year);
// func --- Date
        find_weather(key_api, query_city);
        mFirebaseAuth = FirebaseAuth.getInstance();

        /* ------ not use right ----*/
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

    }

    private void getMonth(String num) {
        TextView textView = (TextView) findViewById(R.id.current_month_view);
        textView.setText(num);
    }

    private void getYear(String num) {
        TextView textView = (TextView) findViewById(R.id.current_year_view);
        textView.setText(num);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page_manu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.sing_out_menu_btn:
                singOut();
                break;
            case R.id.profile_menu_btn:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.about_btntn:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.Report_btn:
                startActivity(new Intent(this, ReportActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void openActivityTap1(){
        Intent intent = new Intent(this, Tap1.class);
        startActivity(intent);
    }

    private void SetUpUIViews() {
        tap1 = findViewById(R.id.btnTap1);
        eMail = findViewById(R.id.btnemail);
        phonecall = findViewById(R.id.btnphone);
        et_temp = (TextView)findViewById(R.id.tempText);
    }

    public void send_feedback_email(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:blueOrganic.rs@gmail.com"));
        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }
    //Required android.Manifest.permission.CALL_PHONE instead of  Manifest.permission.CALL_PHONE
    //for call + check permission
    public void call_Us_feedback() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0377778888"));

        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(callIntent);
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
                    et_temp.setText(Double.toString(temp_c) + "°");
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

}
