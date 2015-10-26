package com.omeletlab.powagri.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.omeletlab.powagri.R;
import com.omeletlab.powagri.fragment.CommonCropsCardListFragment;
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

public class ExpensesActivity extends AppCompatActivity {

    private String expensesType;
    private String stateName;
    private TextView analysisTypeTextView;

    public static final List<Crop> mCropList = new ArrayList<>();
    public static Map<String,Long> uniqueMapValue = new HashMap<String,Long>();
    private JSONArray cropsJsonArray;
    private static PlaceholderFragment placeHolderFragment;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        analysisTypeTextView = (TextView) findViewById(R.id.analysisType);

        Intent in = getIntent();
        expensesType = in.getStringExtra(GlobalConstant.TAG_EXPENSES_TYPE);
        stateName = in.getStringExtra(GlobalConstant.TAG_state_name);
        uniqueMapValue = new HashMap<String,Long>();

        if(expensesType.equals(GlobalConstant.TAG_FERTILIZER_EXPENSES_TYPE)) {
            analysisTypeTextView.setText(R.string.nav_sub_item_fertilizer_analysis);
        }
        else if(expensesType.equals(GlobalConstant.TAG_CHEMICAL_EXPENSES_TYPE)) {
            analysisTypeTextView.setText(R.string.nav_sub_item_chemical_analysis);
        }
        else if(expensesType.equals(GlobalConstant.TAG_LABOR_EXPENSES_TYPE)) {
            analysisTypeTextView.setText(R.string.nav_sub_item_labor_analysis);
        }
        else if(expensesType.equals(GlobalConstant.TAG_TOTAL_OPERATION_EXPENSES_TYPE)) {
            analysisTypeTextView.setText(R.string.nav_sub_item_total_operation_analysis);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading, Please wait...");
        pDialog.setCancelable(false);

        loadCropsList();

        if (savedInstanceState == null) {
            placeHolderFragment = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.graphContainer, placeHolderFragment).commit();
        }
    }

    public void loadCropsList(){
        showpDialog();

        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair(GlobalConstant.TAG_commodity_desc, expensesType));
        params.add(new NameValuePair(GlobalConstant.TAG_state_name, stateName));
        params.add(new NameValuePair("sector_desc", "ECONOMICS"));
        params.add(new NameValuePair("group_desc", "EXPENSES"));
        params.add(new NameValuePair("agg_level_desc", "STATE"));
        params.add(new NameValuePair("unit_desc", "$"));
        params.add(new NameValuePair(GlobalConstant.TAG_freq_desc, "ANNUAL"));
        params.add(new NameValuePair("statisticcat_desc", "EXPENSE"));
        params.add(new NameValuePair("domain_desc", "TOTAL"));

        if(expensesType.equals(GlobalConstant.TAG_FERTILIZER_EXPENSES_TYPE)) {
            params.add(new NameValuePair("class_desc", "INCL LIME & SOIL CONDITIONERS"));
        }
        else if(expensesType.equals(GlobalConstant.TAG_CHEMICAL_EXPENSES_TYPE)) {
            params.add(new NameValuePair("class_desc", "ALL CLASSES"));
        }
        else if(expensesType.equals(GlobalConstant.TAG_LABOR_EXPENSES_TYPE)) {
            params.add(new NameValuePair("class_desc", "HIRED & CONTRACT, INCL NON-CASH BENEFITS"));
        }
        else if(expensesType.equals(GlobalConstant.TAG_TOTAL_OPERATION_EXPENSES_TYPE)) {
            params.add(new NameValuePair("class_desc", "OPERATING"));
        }


        for(int i=2015;i>=1995;i--){
            params.add(new NameValuePair(GlobalConstant.TAG_year+ "__or", ""+i));
        }

        String fullUrl = GlobalConstant.urlBuilder(GlobalConstant.API_URL, params);
        Log.d("expenses fullurl", fullUrl);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                fullUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(TextUtils.isEmpty(response.toString()) || response.toString().length()<200){
                    hidepDialog();
                    Toast.makeText(ExpensesActivity.this, "No data is available for the choosen state: " + stateName, Toast.LENGTH_LONG).show();
                }
                try {

                    cropsJsonArray = response.getJSONArray("data");
                    ExpensesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mCropList.clear();
                                for (int i = 0; i < cropsJsonArray.length(); i++) {
                                    JSONObject item = cropsJsonArray.getJSONObject(i);

                                    String cropName = item.getString("commodity_desc");
                                    String stateName = item.getString("state_name");
                                    String year = item.getString("year");
                                    String value = item.getString("value").replaceAll(",", "");
                                    String statisticCategory = item.getString(GlobalConstant.TAG_statisticcat_desc);
                                    String units = item.getString(GlobalConstant.TAG_unit_desc);

                                    if(TextUtils.isDigitsOnly(value)) {
                                        if(!uniqueMapValue.containsKey(year)){
                                            uniqueMapValue.put(year, Long.parseLong(value));
                                            mCropList.add(new Crop(cropName, stateName, year, value, statisticCategory, units));
                                        }
                                        else{
                                            Long tempIn = uniqueMapValue.get(year);
                                            uniqueMapValue.put(year, Math.max(Long.parseLong(value), tempIn));
                                        }
                                    }
                                }
                                Collections.sort(mCropList, new CropComparator());
                                placeHolderFragment.generateData();

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
                hidepDialog();
                Toast.makeText(ExpensesActivity.this, "No data is available for the choosen state: " + stateName, Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public static class PlaceholderFragment extends Fragment {

        private ColumnChartView chart;
        private ColumnChartData data;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_single_crop_column_chart, container, false);

            chart = (ColumnChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            return rootView;
        }

        private void generateData() {

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            int i=0;
            for (Crop crop: mCropList) {

                values = new ArrayList<SubcolumnValue>();
                String valueFormatted = ""+uniqueMapValue.get(crop.getYear());

                values.add(new SubcolumnValue(Float.parseFloat(valueFormatted), Color.parseColor("#388E3C")));

                Column column = new Column(values);
                column.setHasLabels(false);
                column.setHasLabelsOnlyForSelected(true);
                columns.add(column);

                axisValues.add(new AxisValue(i).setLabel(crop.getYear()));
                i++;
            }

            data = new ColumnChartData(columns);
            data.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            data.setAxisYLeft(new Axis().setHasLines(true));

            chart.setColumnChartData(data);

        }

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                Crop crop = mCropList.get(columnIndex);
                String message = crop.getStateName()+":"+ crop.getValue() + " " + crop.getUnits()+ "("+ crop.getYear() +")";
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
            return (value1<value2)?1:(value1>value2?-1:0);
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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
