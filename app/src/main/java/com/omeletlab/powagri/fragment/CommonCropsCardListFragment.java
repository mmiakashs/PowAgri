package com.omeletlab.powagri.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.devspark.progressfragment.ProgressFragment;
import com.omeletlab.powagri.R;
import com.omeletlab.powagri.adapter.RVAdapter;
import com.omeletlab.powagri.model.Crop;
import com.omeletlab.powagri.util.AppController;
import com.omeletlab.powagri.util.GlobalConstant;
import com.omeletlab.powagri.util.NameValuePair;
import com.omeletlab.powagri.util.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akashs on 10/21/15.
 */
public class CommonCropsCardListFragment extends ProgressFragment {

    private final List<Crop> mCropList = new ArrayList<>();
    private JSONArray cropsJsonArray;

    public RVAdapter rvAdapter;
    private ProgressDialog pDialog;

    private String stateName = "";
    private String year = "";
    private String cropName = "";
    private String analysisType = "";
    private String isShowLoadingDialog = "YES";

    public CommonCropsCardListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.crop_recycle_view, container, false);

        cropName = getArguments().getString(GlobalConstant.TAG_CROP_NAME);
        stateName = getArguments().getString(GlobalConstant.TAG_STATE_NAME);
        year = getArguments().getString(GlobalConstant.TAG_YEAR);
        analysisType = getArguments().getString(GlobalConstant.TAG_ANALYSIS_TYPE);
        isShowLoadingDialog = getArguments().getString(GlobalConstant.TAG_isShowLoadingDialog);

        Context context = getActivity();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        rvAdapter = new RVAdapter(mCropList, getActivity());
        recyclerView.setAdapter(rvAdapter);

        mCropList.clear();
        rvAdapter.notifyDataSetChanged();

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading, Please wait...");
        pDialog.setCancelable(false);

        Network netwrok = new Network(getActivity());
        if (netwrok.isNetworkConnected()) {
            loadCropsList();
        } else {
            GlobalConstant.showMessage(getActivity(), "Internet Conntection is not available.");
        }

        return view;
    }

    public void loadCropsList() {
        showpDialog();

        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair(GlobalConstant.TAG_agg_level_desc, "STATE"));
        if (!TextUtils.isEmpty(stateName)) {
            params.add(new NameValuePair(GlobalConstant.TAG_state_name + "__or", stateName));
        }
        params.add(new NameValuePair("class_desc", "ALL CLASSES"));
        params.add(new NameValuePair(GlobalConstant.TAG_source_desc, "SURVEY"));
        params.add(new NameValuePair(GlobalConstant.TAG_sector_desc, "CROPS"));
        params.add(new NameValuePair(GlobalConstant.TAG_group_desc, "FIELD%20CROPS"));
        params.add(new NameValuePair(GlobalConstant.TAG_statisticcat_desc, analysisType));
        params.add(new NameValuePair(GlobalConstant.TAG_freq_desc, "ANNUAL"));

        if (!TextUtils.isEmpty(cropName)) {
            params.add(new NameValuePair(GlobalConstant.TAG_commodity_desc + "__or", cropName));
        } else {
            String[] cropNameArray = getResources().getStringArray(R.array.crop_name);
            for (int i = 0; i < cropNameArray.length; i++) {
                params.add(new NameValuePair(GlobalConstant.TAG_commodity_desc + "__or", cropNameArray[i]));
            }
        }
        if (!TextUtils.isEmpty(year)) {
            params.add(new NameValuePair(GlobalConstant.TAG_year + "__or", year));
        } else {
            for (int i = 2015; i >= 1995; i--) {
                params.add(new NameValuePair(GlobalConstant.TAG_year + "__or", "" + i));
            }
        }

        String fullUrl = GlobalConstant.urlBuilder(GlobalConstant.API_URL, params);
        Log.d("Nass api[Home all crop]", fullUrl);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                fullUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("nass response",response.toString());
                hidepDialog();
                if(TextUtils.isEmpty(response.toString()) || response.toString().length()<200){
                    Toast.makeText(getActivity(), "No data is available at this moment.", Toast.LENGTH_LONG).show();
                }

                try {
                    cropsJsonArray = response.getJSONArray("data");
                    CommonCropsCardListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mCropList.clear();
                                Map<String,Long> nap = new HashMap<String, Long>();
                                for (int i = 0; i < cropsJsonArray.length(); i++) {
                                    JSONObject item = cropsJsonArray.getJSONObject(i);

                                    String cropName = item.getString("commodity_desc");
                                    String stateName = item.getString("state_name");
                                    String year = item.getString("year");
                                    String value = item.getString("value");
                                    String statisticCategory = item.getString(GlobalConstant.TAG_statisticcat_desc);
                                    String units = item.getString(GlobalConstant.TAG_unit_desc);

                                    if (TextUtils.isDigitsOnly(value.replaceAll(",", ""))) {
                                        String uniqueKey = cropName+year+stateName;
                                        if(nap.containsKey(uniqueKey)){
                                            nap.put(uniqueKey,Math.max(nap.get(uniqueKey),Long.valueOf(value.replaceAll(",",""))));
                                            continue;
                                        }
                                        nap.put(uniqueKey,Long.valueOf(value.replaceAll(",","")));

                                        mCropList.add(new Crop(cropName, stateName, year, value, statisticCategory, units));
                                    }
                                }
                                for(int i=0;i<mCropList.size();i++){
                                    Crop crop = mCropList.get(i);
                                    String uniqueKey = crop.getCropName()+crop.getYear()+crop.getStateName();
                                    mCropList.get(i).setValue( NumberFormat.getInstance().format(nap.get(uniqueKey)));
                                    rvAdapter.notifyItemInserted(i);
                                }
                                setContentShown(true);
                                Collections.sort(mCropList, new CropComparator());
                                rvAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    hidepDialog();
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

    private class CropComparator implements Comparator<Crop> {

        @Override
        public int compare(Crop s1, Crop s2) {

            Log.d("formate value", s1.getValue());
            long value1 = Long.parseLong(s1.getValue().replaceAll(",", ""));
            long value2 = Long.parseLong(s2.getValue().replaceAll(",", ""));

            return (value1 < value2) ? 1 : (value1 > value2 ? -1 : 0);
        }

    }

    private void showpDialog() {
        if (isShowLoadingDialog.equals(GlobalConstant.TAG_YES) && !pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
