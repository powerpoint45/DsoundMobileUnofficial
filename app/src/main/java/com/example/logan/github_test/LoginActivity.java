package com.example.logan.github_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.InvalidKeyException;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText passwordEditText;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.Username);
        passwordEditText = findViewById(R.id.PostingKey);
    }

    public void loginButtonPressed(View v){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        try {
            Account account = Tools.loginIfPossible(username,password,this);
            if (account!=null) {
                Tools.saveUserCredentials(username, password, this, Encryption.CYPHER_PASSWORD);
                setResult(RESULT_OK);
                finish();
                Toast.makeText(this, R.string.login_succeeded, Toast.LENGTH_SHORT).show();
            }else
                throw new InvalidKeyException();
        }catch (InvalidKeyException e){
            e.printStackTrace();
            Toast.makeText(this,R.string.login_failed,Toast.LENGTH_LONG).show();
        }

    }


}
