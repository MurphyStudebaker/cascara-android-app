package com.example.murphy.cascara;

import java.io.StringWriter;
/** The API class defines all API access points as Strings.
 * This class is used every time a network request needs to be executed
 * to specify which API endpoint needs to be accessed. */
public class API {
    private static final String ROOT_URL = "http://192.168.56.1/CascaraAPI/v1/"; //set to 192.168.56.1
    public static final String URL_GET_SHOPS = ROOT_URL + "getcoffeeshopslite.php";
    public static final String URL_CHECK_IN = ROOT_URL + "checkin.php";
    public static final String URL_LOG_IN = ROOT_URL + "login.php";
    public static final String URL_DELETE_USER = ROOT_URL + "deleteuser.php";
    public static final String URL_REGISTER = ROOT_URL + "register.php";
    public static final String URL_GET_SHOP_BY_ID = ROOT_URL + "getcoffeeshopsfull.php";
    public static final String URL_FILTER_SHOPS = ROOT_URL + "getfilteredcoffeeshops.php";
    public static final String URL_GET_CHECKINS = ROOT_URL + "getcheckins.php";
}
