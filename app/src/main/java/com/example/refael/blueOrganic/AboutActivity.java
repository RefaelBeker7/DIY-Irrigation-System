package com.example.refael.blueOrganic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboutActivity extends AppCompatActivity {
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private TextView txview;

    //For call_us function
    private Context mContext=AboutActivity.this;

    String[] names = new String[] {
      "Send Feedback", "GitHub","Play Store", "Call Us"
    };
    int[] image_icon = new int[]{
            R.drawable.ic_mail,
            R.drawable.ic_github,
            R.drawable.ic_playstore,
            R.drawable.ic_phone
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        txview = (TextView)findViewById(R.id.txAbout);

        if(getString(R.string.text_about) != null && getString(R.string.title_about) != null)
            txview.setText(getResources().getString(R.string.title_about)+ "\r\n\n"
                    + getResources().getString(R.string.text_about)+ "\r\n" );
        else
            Toast.makeText(this, "No existing string..",Toast.LENGTH_LONG).show();

        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<names.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("list_tx_About", names[i]);
            hm.put("icon_about", Integer.toString(image_icon[i]) );
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "list_tx_About","icon_about"};

        // Ids of views in listview_layout
        int[] to = { R.id.list_tx_About,R.id.icon_about};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout_about, from, to);

        // Getting a reference to listview of main.xml layout file
        ListView listView = ( ListView ) findViewById(R.id.listAbout);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position){
                    case 0:
                        send_feedback_email();
                        break;
                    case 1:
                        GitHub_feedback();
                        break;
                    case 2:
                        playstore_feedback();
                        break;
                    case 3:
                        call_Us_feedback();
                }
            }
        });
    }
    public void send_feedback_email(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:blueOrganic.rs@gmail.com"));
        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }
    public void playstore_feedback() {
        final String appPackageName = "BlueOrganic"; // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=blueorganic" + appPackageName)));
        }
    }
    public void GitHub_feedback() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(String.format("https://github.com/refaelbeker7")));
        startActivity(intent);
    }

    //Required android.Manifest.permission.CALL_PHONE instead of  Manifest.permission.CALL_PHONE
    //for call + check permission
    public void call_Us_feedback() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0377778888"));

        if (ActivityCompat.checkSelfPermission(AboutActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "The app was not allowed to call.", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(callIntent);
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

}
