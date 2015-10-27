package com.omeletlab.powagri.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class CropStateYearPieAnalysisActivity extends AppCompatActivity {

    private String stateName;
    private String cropName;

    public static final List<Crop> mCropList = new ArrayList<>();
    private JSONArray cropsJsonArray;
    private static PlaceholderFragment placeHolderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_state_year_analysis);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent in = getIntent();
        cropName = in.getStringExtra(GlobalConstant.TAG_commodity_desc);
        stateName = in.getStringExtra(GlobalConstant.TAG_state_name);

        loadCropsList();

        if (savedInstanceState == null) {
            placeHolderFragment = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.graphContainer, placeHolderFragment).commit();
        }
    }

    public void loadCropsList() {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair(GlobalConstant.TAG_commodity_desc, cropName));
        params.add(new NameValuePair(GlobalConstant.TAG_state_name, stateName));
        params.add(new NameValuePair(GlobalConstant.TAG_agg_level_desc, "STATE"));
        params.add(new NameValuePair(GlobalConstant.TAG_year, "2014"));
        params.add(new NameValuePair("class_desc", "ALL CLASSES"));
        params.add(new NameValuePair(GlobalConstant.TAG_source_desc, "SURVEY"));
        params.add(new NameValuePair(GlobalConstant.TAG_sector_desc, "CROPS"));
        params.add(new NameValuePair(GlobalConstant.TAG_group_desc, "FIELD%20CROPS"));
        params.add(new NameValuePair(GlobalConstant.TAG_statisticcat_desc, "AREA%20HARVESTED"));
        params.add(new NameValuePair(GlobalConstant.TAG_reference_period_desc, "YEAR"));

        String fullUrl = GlobalConstant.urlBuilder(GlobalConstant.API_URL, params);
        Log.d("CropStateYear", fullUrl);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                fullUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("CropStateYear response", response.toString());

                try {

                    cropsJsonArray = response.getJSONArray("data");
                    CropStateYearPieAnalysisActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mCropList.clear();
                                for (int i = 0; i < cropsJsonArray.length(); i++) {
                                    JSONObject item = cropsJsonArray.getJSONObject(i);

                                    String cropName = item.getString("commodity_desc");
                                    String stateName = item.getString("state_name");
                                    String year = item.getString("year");
                                    String value = item.getString("value");
                                    String statisticCategory = item.getString(GlobalConstant.TAG_statisticcat_desc);
                                    String units = item.getString(GlobalConstant.TAG_unit_desc);

                                    if (TextUtils.isDigitsOnly(value.replaceAll(",", ""))) {
                                        mCropList.add(new Crop(cropName, stateName, year, value, statisticCategory, units));
                                    }
                                }
                                Collections.sort(mCropList, new CropComparator());
                                placeHolderFragment.generateData();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    public static class PlaceholderFragment extends Fragment {

        private PieChartView chart;
        private PieChartData data;

        private boolean hasLabels = false;
        private boolean hasLabelsOutside = false;
        private boolean hasCenterCircle = false;
        private boolean hasCenterText1 = false;
        private boolean hasCenterText2 = false;
        private boolean isExploded = false;
        private boolean hasLabelForSelected = false;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);

            chart = (PieChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            return rootView;
        }

        private void reset() {
            chart.setCircleFillRatio(1.0f);
            hasLabels = false;
            hasLabelsOutside = false;
            hasCenterCircle = false;
            hasCenterText1 = false;
            hasCenterText2 = false;
            isExploded = false;
            hasLabelForSelected = false;
        }

        private void generateData() {
            int numValues = 6;

            List<SliceValue> values = new ArrayList<SliceValue>();

            for (int i = 0; i < 20; i++) {
                Crop crop = mCropList.get(i);
                String valueFormatted = crop.getValue().replaceAll(",", "");
                Log.d("graphValue", valueFormatted);
                SliceValue sliceValue = new SliceValue(Float.parseFloat(valueFormatted), ChartUtils.pickColor());
                values.add(sliceValue);
            }

            data = new PieChartData(values);
            data.setHasLabels(hasLabels);
            data.setHasLabelsOnlyForSelected(hasLabelForSelected);
            data.setHasLabelsOutside(hasLabelsOutside);
            data.setHasCenterCircle(hasCenterCircle);

            if (isExploded) {
                data.setSlicesSpacing(24);
            }

            if (hasCenterText1) {
                data.setCenterText1("Hello!");

                // Get roboto-italic font.
                Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
                data.setCenterText1Typeface(tf);

                // Get font size from dimens.xml and convert it to sp(library uses sp values).
                data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                        (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
            }

            if (hasCenterText2) {
                data.setCenterText2("Charts (Roboto Italic)");

                Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

                data.setCenterText2Typeface(tf);
                data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                        (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
            }

            chart.setPieChartData(data);
        }

        private void explodeChart() {
            isExploded = !isExploded;
            generateData();

        }

        private void toggleLabelsOutside() {
            // has labels have to be true:P
            hasLabelsOutside = !hasLabelsOutside;
            if (hasLabelsOutside) {
                hasLabels = true;
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }

            if (hasLabelsOutside) {
                chart.setCircleFillRatio(0.7f);
            } else {
                chart.setCircleFillRatio(1.0f);
            }

            generateData();

        }

        private void toggleLabels() {
            hasLabels = !hasLabels;

            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);

                if (hasLabelsOutside) {
                    chart.setCircleFillRatio(0.7f);
                } else {
                    chart.setCircleFillRatio(1.0f);
                }
            }

            generateData();
        }

        private void toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected;

            chart.setValueSelectionEnabled(hasLabelForSelected);

            if (hasLabelForSelected) {
                hasLabels = false;
                hasLabelsOutside = false;

                if (hasLabelsOutside) {
                    chart.setCircleFillRatio(0.7f);
                } else {
                    chart.setCircleFillRatio(1.0f);
                }
            }

            generateData();
        }


        private void prepareDataAnimation() {
            for (SliceValue value : data.getValues()) {
                value.setTarget((float) Math.random() * 30 + 15);
            }
        }

        private class ValueTouchListener implements PieChartOnValueSelectListener {

            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                Toast.makeText(getActivity(), mCropList.get(arcIndex).getCropName() + ":" + mCropList.get(arcIndex).getValue(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

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
}
