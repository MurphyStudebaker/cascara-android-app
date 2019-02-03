package com.example.murphy.cascara;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/** Activity to display user information including total number of checkins
 *  and a RecyclerView of checkin history.
 * */
public class UserProfileActivity extends AppCompatActivity {
    //Views
    TextView name, checkins;
    ImageView profilepic;
    RecyclerView recycler;

    //Data
    CascaraApplication app;
    User activeUser;
    ArrayList<CheckIn> checkinList;
    CheckinListAdapter adapter;
    API api;


    /**Specifices actions for menu items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //case android.R.id.export:
                //exportCheckins();
                //return true;
            case R.id.delete_account:
                //app.getRepo().deleteUser();
                return true;
            case R.id.logout:
                startActivity(new Intent(this, LogInActivity.class));
                app.getRepo().setActiveUser(null);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_profile);
        //repo = new CascaraRepository(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.userTool);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        app = (CascaraApplication) getApplication();
        activeUser = app.getRepo().getActiveUser();

        name = findViewById(R.id.user_name);
        checkins = findViewById(R.id.user_checkins);
        profilepic = findViewById(R.id.user_img);
        profilepic.setImageResource(R.drawable.latte_small);

        name.setText(activeUser.getFirstName() + " " + activeUser.getLastName());
        checkins.setText(Integer.toString(activeUser.getTotalCheckins()) + " TOTAL CHECKINS");

        // handle data
        checkinList = new ArrayList<>();
        loadCheckIns();
        recycler = findViewById(R.id.user_recycler);
        adapter = new CheckinListAdapter(checkinList);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    /** Adapter specfic to CheckIn cards */
    public class CheckinListAdapter extends RecyclerView.Adapter<CheckinViewHolder> {
        private List<CheckIn> checkins = new ArrayList<>();

        public CheckinListAdapter(List<CheckIn> myDataset) {
            checkins = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public CheckinViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            return new CheckinViewHolder(parent);
        }

        // Replace the contents of a view (invoked by the layout manager)
        public void onBindViewHolder(CheckinViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.bind(this.checkins.get(position));
/*
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.getRepo().setActiveCoffeeShop();
                    Intent nextScreen = new Intent(getApplicationContext(), CoffeeProfileActivity.class);
                    startActivity(nextScreen);
                }
            });
            */
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return checkins.size();
        }
    }

    /** Defines the structure of a CheckIn card view */
    class CheckinViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        CardView cardView;
        LinearLayout ll1;
        ImageView houseImg;
        LinearLayout ll2;
        TextView coffeeName;
        LinearLayout ll3;
        ImageView coffeeImg;
        TextView coffeeOrder;

        public CheckinViewHolder(ViewGroup container) {
            super(LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.checkin_card, container, false));
            parent = itemView.findViewById(R.id.checkinParent);
            cardView = itemView.findViewById(R.id.checkinCard);
            ll1 = itemView.findViewById(R.id.checkinLL1);
            houseImg = itemView.findViewById(R.id.checkinHouseImg);
            ll2 = itemView.findViewById(R.id.checkinLL2);
            coffeeName = itemView.findViewById(R.id.checkinCardTitle);
            ll3 = itemView.findViewById(R.id.checkinLL3);
            coffeeImg = itemView.findViewById(R.id.checkinCoffeeIcon);
            coffeeOrder = itemView.findViewById(R.id.checkinOrderTitle);
        }

        public void bind(CheckIn curr) {
            houseImg.setImageResource(curr.getCoffeeImg());
            coffeeName.setText(curr.getCoffeeName());
            coffeeImg.setImageResource(R.drawable.coffee_icon);
            coffeeOrder.setText(curr.getOrder());
        }
    }

    /** Starts the Volley network service that loads the user's recent checkins */
    public void loadCheckIns() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api.URL_GET_CHECKINS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("tagconvertstr", "["+ response +"]");
                            JSONArray array = new JSONArray(response);
                            checkinList.clear();
                            //traversing through all the objects
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject shop = array.getJSONObject(i);
                                String generated = "img" + Integer.toString(new Random().nextInt(12));
                                int imgId = getApplicationContext().getResources().getIdentifier(generated, "drawable", getApplicationContext().getPackageName());

                                //adding the product to product list
                                checkinList.add(new CheckIn(
                                        shop.getString("houseName"),
                                        shop.getString("orderItem"),
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
                params.put("userID", Integer.toString(activeUser.getUserID()));
                return params;
            }
        };

        //adding our stringrequest to queue
        VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
/**
    public void exportCheckins() {
        private String checkInData;
        private final String checkInFile = "checkIn.csv";
        FileOutputStream myFile;
        File mFile;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.exportusercheckin);

            exportButton = findViewById(R.id.exportButton);

            exportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exportData();
                }
            });

        }

        private void exportData()
        {

        }
    }
        */

}
