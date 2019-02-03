package com.example.murphy.cascara;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
/** The activity class for Checking In, where users submit reviews to the database */
public class CheckInActivity extends AppCompatActivity {
    //Views
    Button contribute;
    Spinner menuSpinner;
    TextView title;
    RatingBar coffeeRB, wifiRB;
    ChipGroup atmospheres;

    //Data
    CascaraApplication app;
    API api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_checkin);
        app = (CascaraApplication) getApplication();

        // instantiate views
        contribute = findViewById(R.id.btn_contribute);
        title = findViewById(R.id.checkinCardTitle);
        coffeeRB = findViewById(R.id.coffeeRating);
        wifiRB = findViewById(R.id.wifiRating);
        atmospheres = findViewById(R.id.atmosphereOptions);

        bindDataToViews();

        contribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send data to checkin database in another thread
                double coffeeScore = (double) coffeeRB.getRating();
                double wifiScore = (double) wifiRB.getRating();
                String order = menuSpinner.getSelectedItem().toString();
                Chip checked = findViewById(atmospheres.getCheckedChipId());
                String atmosphereSelection = checked.getText().toString();
                checkIn(coffeeScore, wifiScore, order, atmosphereSelection);
            }
        });
    }

    /** Takes the information from the active coffeeshop to populate the appropriate views */
    public void bindDataToViews() {
        title.setText("Checking in at " + app.getRepo().getActiveCoffeeShop().getHouseName());
        ArrayList<String> spinnerArray = app.getRepo().getActiveCoffeeShop().getMenuItems();
        menuSpinner = findViewById(R.id.menuSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        menuSpinner.setAdapter(spinnerArrayAdapter);
    }

    /** Method to execute the Volley network service to submit the check in */
    public void checkIn(double inputCoffeeScore, double inputWifiScore, String inputOrder, String inputAtmosphere) {
        int userID = app.getRepo().getActiveUser().getUserID();
        int coffeeID = app.getRepo().getActiveCoffeeShop().getHouseID();

        //send it to the database
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_CHECK_IN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        app.getRepo().getActiveUser().setTotalCheckins(app.repo.getActiveUser().getTotalCheckins()+1);
                        Toast.makeText(getApplicationContext(), "Thanks for contributing!", Toast.LENGTH_LONG).show();
                        finish();
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
                params.put("coffeeID", Integer.toString(coffeeID));
                params.put("userID", Integer.toString(userID));
                params.put("coffeeScore", Double.toString(inputCoffeeScore));
                params.put("wifiScore", Double.toString(inputWifiScore));
                params.put("order", inputOrder);
                params.put("atmosphere",inputAtmosphere);
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
