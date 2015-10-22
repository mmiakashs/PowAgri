package com.omeletlab.argora.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

/**
 * Created by akashs on 9/23/15.
 */
public class GlobalConstant {

    public static Context mContext;

    public static final String API_URL = "http://nass-api.azurewebsites.net/api/api_get";

    public static final String TAG_USER_NAME = "username";
    public static final String TAG_PASSWORD = "password";

    public static final String TAG_agg_level_desc = "agg_level_desc";
    public static final String TAG_year = "year" ;
    public static final String TAG_source_desc = "source_desc" ;
    public static final String TAG_sector_desc = "sector_desc" ;
    public static final String TAG_group_desc = "group_desc" ;
    public static final String TAG_statisticcat_desc = "statisticcat_desc" ;
    public static final String TAG_reference_period_desc = "reference_period_desc";
    public static final String TAG_commodity_desc = "commodity_desc";
    public static final String TAG_state_name = "state_name";
    public static final String TAG_freq_desc = "freq_desc";

    public static final String TAG_CROP_NAME = "CROP_NAME";
    public static final String TAG_STATE_NAME = "STATE_NAME";
    public static final String TAG_ANALYSIS_TYPE = "ANALYSIS_TYPE";
    public static final String TAG_YEAR = "YEAR";
    public static final String TAG_isShowLoadingDialog="isShowLoadingDialog";

    public static final String TAG_FIRST_COMPARE_ITEM = "FIRST_COMPARE_ITEM";
    public static final String TAG_SECOND_COMPARE_ITEM = "SECOND_COMPARE_ITEM";

    public static final String UNITS_YEILD = "BU/ACRE";

    public static final String TAG_YES = "YES";
    public static final String TAG_NO = "NO";
    public static final String LOGIN_USER_NAME = "login_username";
    public static final String NOT_LOGIN = "not_login";


    public static String urlBuilder(String url, List<NameValuePair> params){
        String fullUrl = url+"?";
        boolean firstFlag = false;
        for(NameValuePair item : params){
            String key = item.getName();
            String value = item.getValue();
            value = value.replaceAll(" ","%20");
            value = value.replaceAll("&","%26");

            key = key.replaceAll(" ","%20");
            key = key.replaceAll("&","%26");

            if(value!=null){
                if(firstFlag){
                    fullUrl+="&"+key+"="+value;
                }
                else{
                    firstFlag=true;
                    fullUrl+=key+"="+value;
                }
            }
        }
        return fullUrl;
    }

    public static void showMessage(final Context context, final String message){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String getUnitsName(String statisticType){
        if(statisticType.equals("AREA HARVESTED")){
            return "ACRES";
        }else if(statisticType.equals("AREA PLANTED")){
            return "ACRES";
        }
        else if(statisticType.equals("YIELD")){
            return "BU / ACRE";
        }
        else if(statisticType.equals("PRICE RECEIVED")){
            return "$ / BU";
        }
        else{
            return "";
        }
    }

    public static int getPreviousYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -1);
        return prevYear.get(Calendar.YEAR);
    }
}
