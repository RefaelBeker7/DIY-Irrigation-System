package com.example.refael.blueOrganic;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.refael.blueOrganic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private TextView Info;
    private Button Login;

    private TextView userRegistration;
    private int counter = 5;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//find by id
        Email = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Info = findViewById(R.id.tvInfo);
        Login = findViewById(R.id.btnLogin);
        userRegistration = findViewById(R.id.tvRegister);

//custom fonts for buttons
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Xoxoxa.ttf");
        Login.setTypeface(myCustomFont);
        userRegistration.setTypeface(myCustomFont);

        Info.setText("No of attemps remaining: " + String.valueOf(counter));

//Login button click listener
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Email.getText().toString(), Password.getText().toString());
            }
        });

//userRegistration button click listener
        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

    }
//validate email and password
    private void validate(String userName, String userPassword){
        if ((!Email.getText().toString().isEmpty()) && (!Password.getText().toString().isEmpty())){
            mFirebaseAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this, LoadingScreenActivity.class);
                        startActivity(intent);

                    }else{
                        counter--;

                        Info.setText("No of attempts remaining: " + String.valueOf(counter));

                        if(counter == 0 ){
                            Login.setEnabled(false);
                        }
                }
            }


            });
        }

    }
}
