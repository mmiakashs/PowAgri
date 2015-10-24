package com.omeletlab.powagri.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.omeletlab.powagri.R;
import com.omeletlab.powagri.model.Crop;
import com.omeletlab.powagri.util.AppController;
import com.omeletlab.powagri.util.GlobalConstant;
import com.omeletlab.powagri.util.NameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class CompareActivity extends AppCompatActivity {

    private static String firstCompareItemName;
    private static String secondCompareItemName;
    private static String stateName;
    private static String statisticCategory;
    private static String isReload="NO";

    private PlaceholderFragment placeHolderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        firstCompareItemName = intent.getStringExtra(GlobalConstant.TAG_FIRST_COMPARE_ITEM);
        secondCompareItemName = intent.getStringExtra(GlobalConstant.TAG_SECOND_COMPARE_ITEM);
        stateName = intent.getStringExtra(GlobalConstant.TAG_state_name);
        statisticCategory = intent.getStringExtra(GlobalConstant.TAG_statisticcat_desc);
        isReload = intent.getStringExtra(GlobalConstant.TAG_RELOAD);

        if (savedInstanceState == null) {
            placeHolderFragment = new PlaceholderFragment();
            placeHolderFragment.setRetainInstance(true);
            getSupportFragmentManager().beginTransaction().add(R.id.graphContainer, placeHolderFragment).commit();
        }

    }

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private ColumnChartView chartTop;
        private ColumnChartView chartBottom;

        private ColumnChartData columnDataTop;
        private ColumnChartData columnDataBottom;



        private Spinner stateChooser;
        private Spinner statisticCategoryChooser;
        private Spinner firstCompareItemChooser;
        private Spinner secondCompareItemChooser;
        private ImageButton reloadButton;
        private ArrayAdapter<CharSequence> stateDataAdapter;
        private ArrayAdapter<CharSequence> statisticDataAdapter;
        private ArrayAdapter<CharSequence> firstCorpDataAdapter;
        private ArrayAdapter<CharSequence> secondCorpDataAdapter;

        public List<Crop> mFirstCompareItemList = new ArrayList<>();
        public Map<String,Long> uniqueFirstCompareValue = new HashMap<String,Long>();

        public List<Crop> mSecondCompareItemList = new ArrayList<>();
        public Map<String,Long> uniqueSecondCompareValue = new HashMap<String,Long>();

        private JSONArray cropsJsonArray;

        private ProgressDialog pDialog;

        public PlaceholderFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if(isReload.equals(GlobalConstant.TAG_YES)){
                reloadCompare();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_compare, container, false);

            chartTop = (ColumnChartView) rootView.findViewById(R.id.chart_top);
            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);
            chartTop.setOnValueTouchListener(new ValueTouchListenerTopChart());
            chartBottom.setOnValueTouchListener(new ValueTouchListenerBottomChart());

            stateChooser = (Spinner) rootView.findViewById(R.id.stateChooser);
            statisticCategoryChooser = (Spinner) rootView.findViewById(R.id.statisticCategoryChooser);
            firstCompareItemChooser = (Spinner) rootView.findViewById(R.id.firstCompareItemChooser);
            secondCompareItemChooser = (Spinner) rootView.findViewById(R.id.secondCompareItemChooser);
            reloadButton = (ImageButton) rootView.findViewById(R.id.reloadButton);
            reloadButton.setOnClickListener(this);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading, Please wait...");
            pDialog.setCancelable(false);

            sprinnerInitilizer();

            return rootView;
        }

        private void generateTopGraphData() {

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            int i=0;
            for (Crop crop: mFirstCompareItemList) {

                values = new ArrayList<SubcolumnValue>();
                String valueFormatted = ""+uniqueFirstCompareValue.get(crop.getYear());

                values.add(new SubcolumnValue(Float.parseFloat(valueFormatted), Color.parseColor("#388E3C")));

                Column column = new Column(values);
                column.setHasLabels(false);
                column.setHasLabelsOnlyForSelected(true);
                columns.add(column);

                axisValues.add(new AxisValue(i).setLabel(crop.getYear()));
                i++;
            }

            columnDataTop = new ColumnChartData(columns);
            columnDataTop.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnDataTop.setAxisYLeft(new Axis().setHasLines(true));

            chartTop.setColumnChartData(columnDataTop);
        }

        private void generateBottomGraphData() {

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            int i=0;
            for (Crop crop: mSecondCompareItemList) {

                values = new ArrayList<SubcolumnValue>();
                String valueFormatted = ""+uniqueSecondCompareValue.get(crop.getYear());

                values.add(new SubcolumnValue(Float.parseFloat(valueFormatted), Color.parseColor("#303F9F")));

                Column column = new Column(values);
                column.setHasLabels(false);
                column.setHasLabelsOnlyForSelected(true);
                columns.add(column);

                axisValues.add(new AxisValue(i).setLabel(crop.getYear()));
                i++;
            }

            columnDataBottom = new ColumnChartData(columns);
            columnDataBottom.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnDataBottom.setAxisYLeft(new Axis().setHasLines(true));

            chartBottom.setColumnChartData(columnDataBottom);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.reloadButton) {
                Log.d("clickItemCompare", "yes");
                firstCompareItemName = String.valueOf(firstCompareItemChooser.getSelectedItem());
                secondCompareItemName = String.valueOf(secondCompareItemChooser.getSelectedItem());
                stateName = String.valueOf(stateChooser.getSelectedItem());
                statisticCategory = String.valueOf(statisticCategoryChooser.getSelectedItem());
                reloadCompare();
            }
        }

        private class ValueTouchListenerTopChart implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                Crop crop = mFirstCompareItemList.get(columnIndex);
                String message = crop.getCropName()+":"+ crop.getValue() + " " + crop.getUnits()+ "("+ crop.getYear() +")";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onValueDeselected() {
            }
        }

        private class ValueTouchListenerBottomChart implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                Crop crop = mSecondCompareItemList.get(columnIndex);
                String message = crop.getCropName()+":"+ crop.getValue() + " " + crop.getUnits()+ "("+ crop.getYear() +")";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onValueDeselected() {
            }
        }

        private void reloadCompare(){
            chartTop.setColumnChartData(new ColumnChartData());
            chartBottom.setColumnChartData(new ColumnChartData());
            loadFirstCompareItemList(getFullUrl(firstCompareItemName));
        }

        private String getFullUrl(String cropName){
            List<NameValuePair> params = new ArrayList<>();
            params.add(new NameValuePair(GlobalConstant.TAG_commodity_desc, cropName));
            params.add(new NameValuePair(GlobalConstant.TAG_state_name, stateName));
            params.add(new NameValuePair(GlobalConstant.TAG_statisticcat_desc, statisticCategory));
            params.add(new NameValuePair(GlobalConstant.TAG_agg_level_desc, "STATE"));
            params.add(new NameValuePair("prodn_practice_desc", "ALL PRODUCTION PRACTICES"));
            params.add(new NameValuePair("class_desc", "ALL CLASSES"));
            params.add(new NameValuePair(GlobalConstant.TAG_source_desc, "SURVEY"));
            params.add(new NameValuePair(GlobalConstant.TAG_sector_desc, "CROPS"));
            params.add(new NameValuePair(GlobalConstant.TAG_freq_desc, "ANNUAL"));

            for(int i=2015;i>=1995;i--){
                params.add(new NameValuePair(GlobalConstant.TAG_year+ "__or", ""+i));
            }

            String fullUrl = GlobalConstant.urlBuilder(GlobalConstant.API_URL, params);
            return fullUrl;
        }

        private void loadFirstCompareItemList(String fullUrl){
            showpDialog();
            Log.d("first compare url", fullUrl);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    fullUrl, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("CropStateYear response", response.toString());
                    if(TextUtils.isEmpty(response.toString()) || response.toString().length()<200){
                        hidepDialog();
                        Toast.makeText(getActivity(),"No data is available for the choosen crops: "+firstCompareItemName, Toast.LENGTH_LONG).show();
                    }

                    try {
                        cropsJsonArray = response.getJSONArray("data");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mFirstCompareItemList.clear();
                                    uniqueFirstCompareValue.clear();
                                    for (int i = 0; i < cropsJsonArray.length(); i++) {
                                        JSONObject item = cropsJsonArray.getJSONObject(i);

                                        String cropName = item.getString("commodity_desc");
                                        String stateName = item.getString("state_name");
                                        String year = item.getString("year");
                                        String value = item.getString("value").replaceAll(",", "");
                                        String statisticCategory = item.getString(GlobalConstant.TAG_statisticcat_desc);
                                        String units = item.getString(GlobalConstant.TAG_unit_desc);

                                        if (TextUtils.isDigitsOnly(value)) {
                                            if (!uniqueFirstCompareValue.containsKey(year)) {
                                                uniqueFirstCompareValue.put(year, Long.parseLong(value));
                                                mFirstCompareItemList.add(new Crop(cropName, stateName, year, value, statisticCategory, units));
                                            } else {
                                                Long tempIn = uniqueFirstCompareValue.get(year);
                                                uniqueFirstCompareValue.put(year, Math.max(Long.parseLong(value), tempIn));
                                            }
                                        }
                                    }
                                    Collections.sort(mFirstCompareItemList, new CropComparator());
                                    generateTopGraphData();

                                } catch (JSONException e) {
                                    hidepDialog();
                                    e.printStackTrace();
                                }
                                hidepDialog();
                                loadSecondCompareItemList(getFullUrl(secondCompareItemName));
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Volley library error in login activity", "Error: " + error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }

        private void loadSecondCompareItemList(String fullUrl){
            showpDialog();

            Log.d("second compare url", fullUrl);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    fullUrl, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("CropStateYear response", response.toString());
                    if(TextUtils.isEmpty(response.toString()) || response.toString().length()<200){
                        hidepDialog();
                        Toast.makeText(getActivity(),"No data is available for the choosen crops: "+secondCompareItemName, Toast.LENGTH_LONG).show();
                    }

                    try {

                        cropsJsonArray = response.getJSONArray("data");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mSecondCompareItemList.clear();
                                    uniqueSecondCompareValue.clear();
                                    for (int i = 0; i < cropsJsonArray.length(); i++) {
                                        JSONObject item = cropsJsonArray.getJSONObject(i);

                                        String cropName = item.getString("commodity_desc");
                                        String stateName = item.getString("state_name");
                                        String year = item.getString("year");
                                        String value = item.getString("value").replaceAll(",", "");
                                        String statisticCategory = item.getString(GlobalConstant.TAG_statisticcat_desc);
                                        String units = item.getString(GlobalConstant.TAG_unit_desc);

                                        if (TextUtils.isDigitsOnly(value)) {
                                            if (!uniqueSecondCompareValue.containsKey(year)) {
                                                uniqueSecondCompareValue.put(year, Long.parseLong(value));
                                                mSecondCompareItemList.add(new Crop(cropName, stateName, year, value, statisticCategory, units));
                                            } else {
                                                Long tempIn = uniqueSecondCompareValue.get(year);
                                                uniqueSecondCompareValue.put(year, Math.max(Long.parseLong(value), tempIn));
                                            }
                                        }
                                    }
                                    Collections.sort(mSecondCompareItemList, new CropComparator());
                                    generateBottomGraphData();

                                } catch (JSONException e) {
                                    hidepDialog();
                                    e.printStackTrace();
                                }
                                hidepDialog();
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Volley library error in login activity", "Error: " + error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }

        private void sprinnerInitilizer(){
            //stateDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.state_name));
            stateDataAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.state_name, android.R.layout.simple_spinner_item);
            stateDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stateChooser.setAdapter(stateDataAdapter);
            stateChooser.setSelection(stateDataAdapter.getPosition(stateName));

            statisticDataAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.statistic_category, android.R.layout.simple_spinner_item);
            statisticDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statisticCategoryChooser.setAdapter(statisticDataAdapter);
            statisticCategoryChooser.setSelection(stateDataAdapter.getPosition(statisticCategory));

            firstCorpDataAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.crop_name, android.R.layout.simple_spinner_item);
            firstCorpDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            firstCompareItemChooser.setAdapter(firstCorpDataAdapter);
            firstCompareItemChooser.setSelection(firstCorpDataAdapter.getPosition(firstCompareItemName));

            secondCorpDataAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.crop_name, android.R.layout.simple_spinner_item);
            secondCorpDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            secondCompareItemChooser.setAdapter(secondCorpDataAdapter);
            secondCompareItemChooser.setSelection(secondCorpDataAdapter.getPosition(secondCompareItemName));
        }

        private class CropComparator implements Comparator<Crop> {

            @Override
            public int compare(Crop s1, Crop s2) {
                int value1 = Integer.parseInt(s1.getYear());
                int value2 = Integer.parseInt(s2.getYear());
                return (value1<value2)?1:(value1>value2?-1:0);
            }
        }

        private void showpDialog() {
            if (!pDialog.isShowing())
                pDialog.show();
        }

        private void hidepDialog() {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
