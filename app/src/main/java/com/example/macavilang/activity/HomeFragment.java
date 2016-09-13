package com.example.macavilang.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.macavilang.model.TradeRecordModel;
import com.example.macavilang.jaguarfund_android.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private SharedPreferences preferences;
    private RequestQueue homeQueue;
    private String urlToken;
    private ArrayList<Object> mainList= new ArrayList<>();
    private List<String> groupTitle = new ArrayList<String>();
    private List<TradeRecordModel> tradeRecords = new ArrayList<>();
    private HomeAdapter home_adapter = new HomeAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home,container,false);
        ListView homelistView = (ListView) rootView.findViewById(R.id.homeListView);
        preferences = getActivity().getSharedPreferences(getResources().getString(R.string.loginSharedPreferences), Context.MODE_PRIVATE);
        urlToken = preferences.getString("token",null);
        homeQueue = Volley.newRequestQueue(getContext());
        getRecentTradeRecordData();
        initListsData();
        homelistView.setAdapter(home_adapter);
        return rootView;
    }

    public void initListsData(){
        groupTitle.add("最新交易");
        groupTitle.add("净值");
        groupTitle.add("事件提醒");


    }

    public void getRecentTradeRecordData(){
        String tradeUrl = getResources().getString(R.string.baseURL) + "api/fund/trades";
        StringRequest tradeRecordRequest = new StringRequest(Request.Method.GET, tradeUrl,
                new Response.Listener<String>() {
                    Gson gson = new Gson();
            @Override
            public void onResponse(String response) {

                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(response);
                JsonElement tradeRecordJson = jsonElement.getAsJsonObject().get("list");
                Type tradeRecordListType = new TypeToken<List<TradeRecordModel>>(){}.getType();
                tradeRecords = (List<TradeRecordModel>) gson.fromJson(tradeRecordJson,tradeRecordListType);
                Log.e("tradeRecords", String.valueOf(tradeRecords));
                mainList.add("最新交易");
                mainList.addAll(tradeRecords);
                Log.e("tradeRecords", String.valueOf(tradeRecords));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("tradeRecordError",error.getMessage(),error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String, String>();
                map.put("keyWords","");
                map.put("page","1");
                map.put("pageSize","10");
                map.put("sort","-tradeDate");
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<String, String>();
                header.put("X-Auth-Token",urlToken);
                return header;
            }
        };
        homeQueue.add(tradeRecordRequest);
    }


    private class HomeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mainList.size();
        }

        @Override
        public Object getItem(int i) {
            return mainList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = view;
            if (groupTitle.contains(getItem(i))){
                view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_customer_trade_record_list_item_tag,null);
                TextView text = (TextView)view1.findViewById(R.id.groupName);
                text.setText((CharSequence) getItem(i));
            }else {
                view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_customer_trade_record_list_item,null);
                TextView tradeInfo = (TextView)view1.findViewById(R.id.tradeInfo);
                TradeRecordModel tradeRecordModel = (TradeRecordModel)mainList.get(i);
                Log.e("___________", tradeRecordModel.getTradeType());

            }

            return view1;
        }
    }

}
