package com.example.murphy.cascara;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/** The main activity once a user is logged in. Displays a RecyclerView of coffee houses from the
 * database, which can be filtered through the filter button in the menu */
public class MainActivity extends AppCompatActivity {
    //Views
    RecyclerView recycler;
    FloatingActionButton actionButton;
    DrawerLayout mDrawerLayout;

    //Data
    List<CoffeeShop> coffeeShops;
    CoffeeListAdapter adapter;
    CascaraApplication app;
    API api;

    /**
     * Set up menu and handle menu click events
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Specifices actions for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                //bring up the settings UI and refresh list
                startActivity(new Intent(getApplicationContext(),FilterActivity.class));
                return true;
            case R.id.profile:
                Intent nextScreen = new Intent(this, UserProfileActivity.class);
                startActivity(nextScreen);
                return true;
            case R.id.logout:
                startActivity(new Intent(this, LogInActivity.class));
                app.getRepo().setActiveUser(null);
                return true;
            case R.id.delete:
                deleteAccount(app.getRepo().getActiveUser().getUserID());
                return true;
            case R.id.export:
                exportCheckins(app.getRepo().getActiveUser().getUserID());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Activity Life Cycle Methods
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        app = (CascaraApplication) getApplication();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();

        // instantiate views
        recycler = findViewById(R.id.recycler);

        // handle data
        coffeeShops = new ArrayList<>();
        adapter = new CoffeeListAdapter(coffeeShops);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        if (app.getRepo().isOffline()) {
            //no coffee shops to load, just set up one to test
            CoffeeShop offlineShop = new CoffeeShop(1000, "Honeydukes Cafe",4.6,3.5,getApplicationContext().getResources().getIdentifier("img4", "drawable", getApplicationContext().getPackageName()));
            offlineShop.setPhone("8175006678");
            offlineShop.setCity("Orange");
            offlineShop.setAddress("1 University Drive");
            offlineShop.setState("California");
            ArrayList<String> menu = new ArrayList<>();
            menu.add("Drip Coffee");
            ArrayList<String> amen = new ArrayList<>();
            amen.add("Vegan Milks");
            ArrayList<String> atmos = new ArrayList<>();
            atmos.add("Magical");
            offlineShop.setMenuItems(menu);
            offlineShop.setAmenities(amen);
            offlineShop.setAtmosphere(atmos);
            coffeeShops.add(offlineShop);
        }
        else if(app.getRepo().getFilter() == null) {
            loadCoffeeShops();
            adapter.notifyDataSetChanged();
        } else {
            Log.e("MAIN","filtering coffee shops");
            filterCoffeeShops();
        }
    }

    /** List Adapter specific to updating Coffeehouse CardViews */
    public class CoffeeListAdapter extends RecyclerView.Adapter<CoffeeViewHolder> {
        private List<CoffeeShop> coffeeShops = new ArrayList<>();

        public CoffeeListAdapter(List<CoffeeShop> myDataset) {
            coffeeShops = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public CoffeeViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
            return new CoffeeViewHolder(parent);
        }

        // Replace the contents of a view (invoked by the layout manager)
        public void onBindViewHolder(CoffeeViewHolder holder, final int position) {
            holder.bind(this.coffeeShops.get(position));

            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.getRepo().setActiveCoffeeShop(coffeeShops.get(position));
                    Intent nextScreen = new Intent(getApplicationContext(), CoffeeProfileActivity.class);
                    startActivity(nextScreen);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return coffeeShops.size();
        }
    }

    /** Sets the structure for the CardView of the coffeehouse */
    class CoffeeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        CardView cardView;
        LinearLayout ll1;
        ImageView houseImg;
        LinearLayout ll2;
        TextView coffeeName;
        LinearLayout ll3;
        ImageView wifiImg;
        TextView wifiScore;
        ImageView coffeeImg;
        TextView coffeeScore;

        public CoffeeViewHolder(ViewGroup container) {
            super(LayoutInflater.from(MainActivity.this).inflate(R.layout.recycler_view_item, container, false));
            parent = itemView.findViewById(R.id.parent);
            cardView = itemView.findViewById(R.id.cardView);
            ll1 = itemView.findViewById(R.id.ll1);
            houseImg = itemView.findViewById(R.id.houseImg);
            ll2 = itemView.findViewById(R.id.ll2);
            coffeeName = itemView.findViewById(R.id.coffeeName);
            ll3 = itemView.findViewById(R.id.ll3);
            wifiImg = itemView.findViewById(R.id.wifiImg);
            wifiScore = itemView.findViewById(R.id.wifiScore);
            coffeeImg = itemView.findViewById(R.id.coffeeImg);
            coffeeScore = itemView.findViewById(R.id.coffeeScore);
        }

        public void bind(CoffeeShop shop) {
            houseImg.setImageResource(shop.getImgID());
            coffeeName.setText(shop.getHouseName());
            wifiImg.setImageResource(R.drawable.wifi_icon);
            wifiScore.setText(Double.toString(shop.getWifiScore()));
            coffeeImg.setImageResource(R.drawable.bean_icon);
            coffeeScore.setText(Double.toString(shop.getCoffeeScore()));
        }
    }
    /** Starts the Volley network service to load a list of coffeeshops from the database. */
    public void loadCoffeeShops() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_GET_SHOPS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            //Log.e("LOGGING IN","in the try catch method");
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONArray array = new JSONArray(response);

                            //traversing through all the objects
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject shop = array.getJSONObject(i);
                                String generated = "img" + Integer.toString(new Random().nextInt(12));
                                int imgId = getApplicationContext().getResources().getIdentifier(generated, "drawable", getApplicationContext().getPackageName());

                                //adding the product to product list
                                coffeeShops.add(new CoffeeShop(
                                        shop.getInt("coffeeID"),
                                        shop.getString("houseName"),
                                        shop.getDouble("coffeeScore"),
                                        shop.getDouble("wifiScore"),
                                        imgId
                                ));
                            }
                            //Log.e("LOGGING IN",obj.getString("message"));

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
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Log.e("LOGGING IN","should have sent out request");
    }

   /** If a filter is specified, only coffee shops that match that filter are loaded. */
    public void filterCoffeeShops() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_FILTER_SHOPS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONArray array = new JSONArray(response);
                            coffeeShops.clear();
                            //traversing through all the objects
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject shop = array.getJSONObject(i);
                                String generated = "img" + Integer.toString(new Random().nextInt(12));
                                int imgId = getApplicationContext().getResources().getIdentifier(generated, "drawable", getApplicationContext().getPackageName());

                                //adding the product to product list
                                coffeeShops.add(new CoffeeShop(
                                        shop.getInt("coffeeID"),
                                        shop.getString("houseName"),
                                        shop.getDouble("coffeeScore"),
                                        shop.getDouble("wifiScore"),
                                        imgId
                                ));
                            }
                            //Log.e("LOGGING IN",obj.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("LOGGING IN","couldn't process JSON " + e.getMessage() );
                        }
                        adapter.notifyDataSetChanged();
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
                String filter = app.getRepo().getFilter();
                String value = app.getRepo().getStringFilterValue();

                Log.e("LOGGING IN","attached paramaters filter" + filter + " and value " + value);
                params.put(filter, TextUtils.htmlEncode(value));
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void deleteAccount(int userID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_DELETE_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean("error")){
                                //deletion was successful
                                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(),"Unable to delete your account",Toast.LENGTH_LONG).show();
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
                params.put("userID", Integer.toString(userID));
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void exportCheckins(int activeUserID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_GET_CHECKINS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String checkInFile = "checkIn.csv";
                        FileOutputStream myFile;
                        File mFile;

                        try {
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONArray array = new JSONArray(response);

                            try {
                                myFile = openFileOutput(checkInFile, MODE_PRIVATE);
                                mFile = getFileStreamPath(checkInFile);

                                ObjectOutputStream out = new ObjectOutputStream(myFile);

                                //traversing through all the objects
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject shop = array.getJSONObject(i);
                                    String generated = "img" + Integer.toString(new Random().nextInt(12));
                                    int imgId = getApplicationContext().getResources().getIdentifier(generated, "drawable", getApplicationContext().getPackageName());

                                    String name = shop.getString("houseName");
                                    String order = shop.getString("orderItem");
                                    //coffeeScore = CheckInManager.checkInList.get(i).getCoffeeScore();
                                    //wifiScore = CheckInManager.checkInList.get(i).getWifiScore();

                                    String checkInData = name + ", " + order + "\n";
                                    out.writeObject(checkInData);
                                }
                                out.close();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            //Log.e("LOGGING IN",obj.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("LOGGING IN","couldn't process JSON " + e.getMessage() );
                        }
                        adapter.notifyDataSetChanged();
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
                params.put("userID", Integer.toString(activeUserID));
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}