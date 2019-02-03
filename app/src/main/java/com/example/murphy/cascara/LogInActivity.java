package com.example.murphy.cascara;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
/** Activity to Log In a user that already has an account */
public class LogInActivity extends Activity {

    private EditText emailAddressTxt;
    private EditText passwordTxt;
    private Button loginButton;
    private API api;
    private TextView register;

    private String email, password;
    private CascaraApplication app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);
        app = (CascaraApplication) getApplication();

        emailAddressTxt = findViewById(R.id.emailEditText);
        passwordTxt = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //eventually verify these for input
                email = emailAddressTxt.getText().toString();
                password = passwordTxt.getText().toString();

                /** FOR TEST ONLY -- ROOT LOGON IF SERVER IS NOT RUNNING/ACCESSIBLE */
                if(email.equals("root") && password.equals("root")) {
                    app.getRepo().setActiveUser(new User(0,"Hermione","Granger","hgranger@hogwarts.edu",10));
                    app.getRepo().setOffline(true);
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    login(email, password);
                    Log.e("LOGGING IN", "dispatched login method");
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                startActivity(i);
            }
        });
    }

    /** Sends Volley network service to validate user information and return a user object */
    private void login(String theEmail, String thePassword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_LOG_IN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            Log.e("LOGGING IN","in the try catch method");
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONObject obj = new JSONObject(response);
                            Log.e("LOGGING IN",obj.getString("message"));

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response
                                Log.e("LOGGING IN","not getting an error from server");
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("userID"),
                                        userJson.getString("first_name"),
                                        userJson.getString("last_name"),
                                        userJson.getString("email"),
                                        userJson.getInt("total_checkins")
                                );
                                Log.e("LOGGING IN","created user from object");

                                Toast.makeText(getApplicationContext(), "Logged in " + user.getFirstName(), Toast.LENGTH_LONG).show();
                                app.getRepo().setActiveUser(user);

                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                //storing the user in shared preferences
                                //SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.e("LOGGING IN","got an error from server");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("LOGGING IN","couldn't process JSON " + e.getMessage() );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR", "error" + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.e("LOGGING IN","attached paramaters email" + theEmail + " and pass " + thePassword);
                params.put("email", TextUtils.htmlEncode(theEmail));
                params.put("password", TextUtils.htmlEncode(thePassword));
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Log.e("LOGGING IN","should have sent out request");
    }

}
