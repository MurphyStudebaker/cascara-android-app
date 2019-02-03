package com.example.murphy.cascara;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/** The Repository class holds all global objects of data received from the API
 */
public class CascaraRepository {
    private CoffeeShop[] coffeeShops;
    private CoffeeShop activeCoffeeShop;
    private User activeUser;
    private String filter;
    private int intFilterValue;
    private String stringFilterValue;
    private boolean isOffline;

    API api;
    Context appContext;

    public CascaraRepository(Context context) {
        activeCoffeeShop = null;
        appContext = context;
        activeUser = null;
        filter = null;
    }

    public CoffeeShop[] getCoffeeShops() {
        return coffeeShops;
    }

    public void setCoffeeShops(CoffeeShop[] coffeeShops) {
        this.coffeeShops = coffeeShops;
    }

    public CoffeeShop getActiveCoffeeShop() {
        return activeCoffeeShop;
    }

    public void setActiveCoffeeShop(CoffeeShop activeCoffeeShop) {
        this.activeCoffeeShop = activeCoffeeShop;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getIntFilterValue() {
        return intFilterValue;
    }

    public void setIntFilterValue(int intFilterValue) {
        this.intFilterValue = intFilterValue;
    }

    public String getStringFilterValue() {
        return stringFilterValue;
    }

    public void setStringFilterValue(String stringFilterValue) {
        this.stringFilterValue = stringFilterValue;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }
}
