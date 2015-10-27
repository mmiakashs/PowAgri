package com.omeletlab.powagri.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
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
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class CompareSelectedCropActivity extends AppCompatActivity {

    private static String firstCompareItemName;
    private static String secondCompareItemName;
    private static String stateName;
    private static String statisticCategory;

    public static final List<Crop> mFirstCompareItemList = new ArrayList<>();
    public static Map<String, Long> uniqueFirstCompareValue = new HashMap<String, Long>();

    public static final List<Crop> mSecondCompareItemList = new ArrayList<>();
    public static Map<String, Long> uniqueSecondCompareValue = new HashMap<String, Long>();

    private JSONArray cropsJsonArray;
    private static PlaceholderFragment placeHolderFragment;

    private ProgressDialog pDialog;

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

        Log.d("compare selected", firstCompareItemName + " " + secondCompareItemName + " " + stateName + " " + statisticCategory);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading, Please wait...");
        pDialog.setCancelable(false);

        loadFirstCompareItemList(getFullUrl(firstCompareItemName));

        if (savedInstanceState == null) {
            placeHolderFragment = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.graphContainer, placeHolderFragment).commit();
        }
    }

    public String getFullUrl(String cropName) {
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

        for (int i = 2015; i >= 1995; i--) {
            params.add(new NameValuePair(GlobalConstant.TAG_year + "__or", "" + i));
        }

        String fullUrl = GlobalConstant.urlBuilder(GlobalConstant.API_URL, params);
        return fullUrl;
    }

    public void loadFirstCompareItemList(String fullUrl) {
        showpDialog();

        Log.d("fullurl", fullUrl);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                fullUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("CropStateYear response", response.toString());
                if (TextUtils.isEmpty(response.toString()) || response.toString().length() < 200) {
                    hidepDialog();
                    Toast.makeText(CompareSelectedCropActivity.this, "No data is available for the choosen crops: " + firstCompareItemName, Toast.LENGTH_LONG).show();
                }

                try {

                    cropsJsonArray = response.getJSONArray("data");
                    CompareSelectedCropActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mFirstCompareItemList.clear();
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
                                placeHolderFragment.generateTopGraphData();

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
                VolleyLog.d("Volley library", "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void loadSecondCompareItemList(String fullUrl) {
        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                fullUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (TextUtils.isEmpty(response.toString()) || response.toString().length() < 200) {
                    hidepDialog();
                    Toast.makeText(CompareSelectedCropActivity.this, "No data is available for the choosen crops: " + secondCompareItemName, Toast.LENGTH_LONG).show();
                }

                try {

                    cropsJsonArray = response.getJSONArray("data");
                    CompareSelectedCropActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mSecondCompareItemList.clear();
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
                                placeHolderFragment.generateBottomGraphData();

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
                VolleyLog.d("Volley library", "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static class PlaceholderFragment extends Fragment {

        private ColumnChartView chartTop;
        private ColumnChartView chartBottom;

        private ColumnChartData columnDataTop;
        private ColumnChartData columnDataBottom;

        private TextView compareItemOneNameTextView;
        private TextView compareItemTwoNameTextView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_selected_item_compare, container, false);

            chartTop = (ColumnChartView) rootView.findViewById(R.id.chart_top);
            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);
            chartTop.setOnValueTouchListener(new ValueTouchListenerTopChart());
            chartBottom.setOnValueTouchListener(new ValueTouchListenerBottomChart());

            compareItemOneNameTextView = (TextView) rootView.findViewById(R.id.compareItemNameOne);
            compareItemTwoNameTextView = (TextView) rootView.findViewById(R.id.compareItemNameTwo);
            compareItemOneNameTextView.setText(firstCompareItemName);
            compareItemTwoNameTextView.setText(secondCompareItemName);

            return rootView;
        }

        private void generateTopGraphData() {

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            int i = 0;
            for (Crop crop : mFirstCompareItemList) {

                values = new ArrayList<SubcolumnValue>();
                String valueFormatted = "" + uniqueFirstCompareValue.get(crop.getYear());

                values.add(new SubcolumnValue(Float.parseFloat(valueFormatted), ChartUtils.pickColor()));

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
            int i = 0;
            for (Crop crop : mSecondCompareItemList) {

                values = new ArrayList<SubcolumnValue>();
                String valueFormatted = "" + uniqueSecondCompareValue.get(crop.getYear());

                values.add(new SubcolumnValue(Float.parseFloat(valueFormatted), ChartUtils.pickColor()));

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

        private class ValueTouchListenerTopChart implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                Crop crop = mFirstCompareItemList.get(columnIndex);
                String message = crop.getCropName() + ":" + crop.getValue() + " " + crop.getUnits() + "(" + crop.getYear() + ")";
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
                String message = crop.getCropName() + ":" + crop.getValue() + " " + crop.getUnits() + "(" + crop.getYear() + ")";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
            }
        }
    }

    private class CropComparator implements Comparator<Crop> {

        @Override
        public int compare(Crop s1, Crop s2) {
            int value1 = Integer.parseInt(s1.getYear());
            int value2 = Integer.parseInt(s2.getYear());
            return (value1 < value2) ? 1 : (value1 > value2 ? -1 : 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
