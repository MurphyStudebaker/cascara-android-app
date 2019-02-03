package com.example.murphy.cascara;

import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
/** Activity to create a User account if user does not already have one */
public class UserRegistrationActivity extends Activity {

    private EditText emailAddress;
    private EditText password;
    private EditText password2;
    private Button registerButton;
    private TextView fname, lname, login;

    private CascaraApplication app;
    private API api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuserregistration);
        app = (CascaraApplication) getApplication();

        emailAddress = findViewById(R.id.newEmailEditText);
        password = findViewById(R.id.newPassEditText);
        password2 = findViewById(R.id.newPass2EditText);
        registerButton = findViewById(R.id.newUserRegisterButton);
        fname = findViewById(R.id.firstNameEdit);
        lname = findViewById(R.id.lastNameEdit);
        login = findViewById(R.id.logInButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = fname.getText().toString();
                String lastName = lname.getText().toString();
                String email = emailAddress.getText().toString();
                String pass = password.getText().toString();

                register(firstName, lastName, email, pass);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            }
        });
    }

    private boolean isEmpty(EditText text)
    {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    /** Starts the Volley network request that adds the user to the database and returns the user information */
    private void register(String inputfname, String inputlname, String inputemail, String inputpassword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("userID"),
                                        userJson.getString("first_name"),
                                        userJson.getString("last_name"),
                                        userJson.getString("email"),
                                        userJson.getInt("total_checkins")
                                );

                                Toast.makeText(getApplicationContext(), "Created account " + user.getFirstName(), Toast.LENGTH_LONG).show();
                                app.getRepo().setActiveUser(user);

                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                //storing the user in shared preferences
                                //SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            } else {
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.e("LOGGING IN","attached paramaters email" + inputemail + " and pass " + inputpassword + " and name " + inputfname + " " + inputlname);
                params.put("email", TextUtils.htmlEncode(inputemail));
                params.put("password", TextUtils.htmlEncode(inputpassword));
                params.put("firstName", TextUtils.htmlEncode(inputfname));
                params.put("lastName", TextUtils.htmlEncode(inputlname));
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
