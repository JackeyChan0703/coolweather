package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();


    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//县列表
    private Province selectedProvince;//被选中的省
    private City selectedCity;//被选中的市
    private int currentlevel;//当前选中的级别

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        backButton = view.findViewById(R.id.back_button);
        titleText = view.findViewById(R.id.title_text);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentlevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                //查询城市
                queryCities();
            } else if (currentlevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                //查询县
                queryCounties();
            }else if(currentlevel == LEVEL_COUNTY){
                String weatherId = countyList.get(position).getWeatherId();
                Intent intent = new Intent(getActivity(),WeatherActivity.class);
                intent.putExtra("weatherId",weatherId);
                startActivity(intent);
                getActivity().finish();
            }
        });
        backButton.setOnClickListener(view -> {
            if (currentlevel == LEVEL_COUNTY) {
                //查询城市
                queryCities();
            } else if (currentlevel == LEVEL_CITY) {
                //查询省
                queryProvinces();
            }
        });
        //查询省
        queryProvinces();
    }


    /**
     * 查询全国所有的省，优先查询数据库，再从服务器查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        //provinceList>0即当前数据库有数据，直接获取
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            //从服务器查询省
            queryFromServer(address, "province");
        }
    }

    /**
     * 优先从数据库查询所有的城市，再从服务器查询所有的市
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityNmae());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            //查询当前省内所有的市，从服务器查询
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询县城的天气情况，优先从数据库查询，再从服务器端查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityNmae());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            //查询县城，从服务器查
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的类型查询省或市或县
     *
     * @param address 地址
     * @param type    类型
     */
    private void queryFromServer(String address, String type) {
        //显示加载进度条
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //响应成功执行的回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reponseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    //解析当前reponse的数据
                    result = Utility.handleProvinceResponse(reponseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(reponseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(reponseText, selectedCity.getId());
                }
                if (result) {
                    //去主线程更新UI
                    getActivity().runOnUiThread(() -> {
                        //关闭加载进度条显示
                        closeProgressDialog();
                        if ("province".equals(type)) {
                            queryProvinces();
                        } else if ("city".equals(type)) {
                            queryCities();
                        } else if ("county".equals(type)) {
                            queryCounties();
                        }
                    });
                }
            }

            //响应失败执行的回调
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    //关闭加载进度条的显示
                    closeProgressDialog();
                    Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 显示加载进度条
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度条的显示
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
