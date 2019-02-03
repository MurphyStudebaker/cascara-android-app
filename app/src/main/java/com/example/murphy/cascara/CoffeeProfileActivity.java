package com.example.murphy.cascara;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
/** Displays detailed information for a specific coffee shop and allows user
 * to submit a check in for that specific coffee shop */
public class CoffeeProfileActivity extends AppCompatActivity {
    //Views
    TextView name, wifiScore, coffeeScore, bestDrink;
    ImageView photo, wifiIcon, coffeeIcon, drinkIcon, call, web, visit;
    Button checkin;
    ChipGroup atmospheres, amenities;

    //Data Source
    CascaraApplication app;
    CoffeeShop activeShop;
    API api;

    /** Controls menu selections */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coffee_profile);
        app = (CascaraApplication) getApplication();
        activeShop = app.getRepo().getActiveCoffeeShop();
        //get detailed information by coffeeID
        int coffeeID = activeShop.getHouseID();
        if (!app.getRepo().isOffline()) {
            loadCoffeeDetails(coffeeID);
        }


        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.coffeeTool);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        checkin = findViewById(R.id.checkIn);
        coffeeIcon = findViewById(R.id.coffeeScoreImg);
        wifiIcon = findViewById(R.id.wifiScoreImg);
        drinkIcon = findViewById(R.id.bestDrinkImg);
        coffeeIcon.setImageResource(R.drawable.bean_icon);
        wifiIcon.setImageResource(R.drawable.wifi_icon);
        drinkIcon.setImageResource(R.drawable.coffee_icon);
        call = findViewById(R.id.callBtn);
        web = findViewById(R.id.webBtn);
        visit = findViewById(R.id.visitBtn);

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(getApplicationContext(), CheckInActivity.class);
                startActivity(nextScreen);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneURI = "tel:" + app.getRepo().getActiveCoffeeShop().getPhone();
                Intent toCall = new Intent(Intent.ACTION_DIAL);
                toCall.setData(Uri.parse(phoneURI));
                startActivity(toCall);
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri url = Uri.parse(app.getRepo().getActiveCoffeeShop().getWebsite());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, url);
                startActivity(launchBrowser);
            }
        });

        visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoffeeShop active = app.getRepo().getActiveCoffeeShop();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + active.getAddress() +","+ active.getCity() + "," + active.getState());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    /** Takes information from the active coffee shop and updates the appropriate views*/
    public void bindCoffeeInfoToUI(CoffeeShop activeShop) {
        name = findViewById(R.id.coffeeName);
        wifiScore = findViewById(R.id.wifiScore);
        coffeeScore = findViewById(R.id.coffeeScore);
        photo = findViewById(R.id.coffeeImg);
        atmospheres = findViewById(R.id.atmosphereGroup);
        amenities = findViewById(R.id.amenitiesGroup);
        bestDrink = findViewById(R.id.bestDrink);

        // things we already know from database
        name.setText(activeShop.getHouseName());
        wifiScore.setText(Double.toString(activeShop.getWifiScore()));
        coffeeScore.setText(Double.toString(activeShop.getCoffeeScore()));
        photo.setImageResource(activeShop.getImgID());
        bestDrink.setText(activeShop.getBestOrder());

        // load atmosphere chips
        ArrayList<String> atmosphereTexts = activeShop.getAtmosphere();
        if (atmosphereTexts.size() <= 0) {
            Chip newChip = new Chip(this);
            newChip.setText("No atmosphere data");
            atmospheres.addView(newChip);
        } else {
            for (int i = 0; i < atmosphereTexts.size(); ++i) {
                Chip newChip = new Chip(this);
                newChip.setText(atmosphereTexts.get(i));
                atmospheres.addView(newChip);
            }
        }

        // load amenities chips
        ArrayList<String> amenityTexts = activeShop.getAmenities();
        if (amenityTexts.size() <= 0) {
            Chip newChip = new Chip(this);
            newChip.setText("No amenities");
            amenities.addView(newChip);
        } else {
            for (int i = 0; i < amenityTexts.size(); ++i) {
                Chip newChip = new Chip(this);
                newChip.setText(amenityTexts.get(i));
                amenities.addView(newChip);
            }
        }
    }

    /** Executes Volley Network Service to load all of the coffee shop information for this ID */
    public void loadCoffeeDetails(int coffeeID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_GET_SHOP_BY_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            //Log.e("LOGGING IN","in the try catch method");
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                //Add new information to the shop Object
                                Log.e("LOGGING IN", "not getting an error from server");

                                //pull out the objects and arrays
                                JSONObject shop = obj.getJSONObject("shop");
                                JSONArray atmospheres = obj.getJSONArray("atmospheres");
                                Log.e("tagconvertstr", "["+ atmospheres +"]");
                                JSONArray amenities = obj.getJSONArray("amenities");
                                Log.e("tagconvertstr", "["+ amenities +"]");
                                JSONArray menu = obj.getJSONArray("menu");
                                Log.e("tagconvertstr", "["+ menu +"]");

                                //populate the single variables
                                activeShop.setAddress(shop.getString("address"));
                                activeShop.setState(shop.getString("state"));
                                activeShop.setCity(shop.getString("city"));
                                activeShop.setBestOrder(shop.getString("popularItem"));
                                activeShop.setWebsite(shop.getString("website"));
                                activeShop.setPhone(shop.getString("phone"));
                                Log.e("PARSE","set single variables");

                                ArrayList<String> amen = new ArrayList<>();
                                for (int i = 0; i < amenities.length(); ++i) {
                                    String amenity = amenities.getString(i);
                                    amen.add(amenity);
                                    Log.e("ADDED",amenity);
                                }
                                Log.e("PARSE","set amenities of size " + amen.size());

                                ArrayList<String> atmsp = new ArrayList<>();
                                for (int i = 0; i < atmospheres.length(); ++i) {
                                    String atmosphere = atmospheres.getString(i);
                                    atmsp.add(atmosphere);
                                    Log.e("ADDED",atmosphere);
                                }
                                Log.e("PARSE","set atmosphere of size " + atmsp.size());

                                ArrayList<String> men = new ArrayList<>();
                                for (int i = 0; i < menu.length(); ++i) {
                                    JSONObject entry = menu.getJSONObject(i);
                                    men.add(entry.getString("item"));
                                }
                                Log.e("PARSE","loaded menu");

                                activeShop.setAtmosphere(atmsp);
                                activeShop.setAmenities(amen);
                                activeShop.setMenuItems(men);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("LOGGING IN","couldn't process JSON " + e.getMessage() );
                        }
                        bindCoffeeInfoToUI(activeShop);
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
                Log.e("LOGGING IN","attached paramaters ID " + coffeeID);
                params.put("coffeeID", Integer.toString(coffeeID));
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Log.e("LOGGING IN","should have sent out request");
    }

}
