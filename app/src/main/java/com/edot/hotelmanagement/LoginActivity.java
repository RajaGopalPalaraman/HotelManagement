package com.edot.hotelmanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.edot.hotelmanagement.common.AppConstants;
import com.edot.network.NetworkHelperUtil;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText userID;
    private EditText password;

    private RegisterTextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userID = findViewById(R.id.userID);
        password = findViewById(R.id.password);
        registerTextView = findViewById(R.id.registration_text);
    }

    private void openRegistrationPage() {
        Intent intent = new Intent(this,RegistrationActivity.class);
        startActivity(intent);
    }

    public void onLogin(final View view) {
        if(!(userID.getText().toString().isEmpty() || password.getText().toString().isEmpty())) {
            Log.d(AppConstants.LOG_TAG, "@onLogin");
            if ("admin".equals(userID.getText().toString())) {
                if ("admin".equals(password.getText().toString())) {
                    Toast.makeText(this, R.string.adminLoginSuccess, Toast.LENGTH_SHORT).show();
                    AppConstants.currentLoggedInUserID = "admin";
                    AppConstants.currentLoggedInUserName = "admin";
                    resetAll();
                    Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.invalidLoginCredentials, Toast.LENGTH_SHORT).show();
                    resetPassword();
                }
            }
            else
            {
                view.setClickable(false);
                registerTextView.setClickable(false);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        HashMap<String,String> paramsMap = new HashMap<>();
                        paramsMap.put("userid",userID.getText().toString());
                        paramsMap.put("password",password.getText().toString());
                        return NetworkHelperUtil.readData("http://autoiot2019-20.000webhostapp.com" +
                                "/FoodApp/login.php",paramsMap);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        view.setClickable(true);
                        registerTextView.setClickable(true);
                        if (s == null || "401".equals(s))
                        {
                            Toast.makeText(LoginActivity.this, R.string.somethingWentWrong, Toast.LENGTH_SHORT).show();
                        }
                        else if ("null".equals(s))
                        {
                            Toast.makeText(LoginActivity.this, R.string.invalidLoginCredentials, Toast.LENGTH_SHORT).show();
                            resetPassword();
                        }
                        else
                        {
                            Log.d(AppConstants.LOG_TAG, s + " Logged in");
                            Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_SHORT).show();
                            AppConstants.currentLoggedInUserID = userID.getText().toString();
                            AppConstants.currentLoggedInUserName = s;
                            resetAll();

                            Intent intent = new Intent(LoginActivity.this,RoomBookingActivity.class);
                            startActivity(intent);
                        }
                    }
                }.execute();
            }
        }
        else
        {
            Toast.makeText(this,R.string.loginFieldsEmpty,Toast.LENGTH_SHORT).show();
            resetPassword();
        }
    }

    public void onRegister(View view) {
        Log.d(AppConstants.LOG_TAG,"@onRegister");
        openRegistrationPage();
    }

    private void resetPassword()
    {
        password.setText("");
    }

    private void resetAll()
    {
        userID.setText("");
        password.setText("");
    }
}
