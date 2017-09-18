package com.example.xuxuefeng.coolweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuxuefeng.coolweather.R;
import com.example.xuxuefeng.coolweather.db.City;
import com.example.xuxuefeng.coolweather.db.County;
import com.example.xuxuefeng.coolweather.db.Province;
import com.example.xuxuefeng.coolweather.util.Constant;
import com.example.xuxuefeng.coolweather.util.GsonUtil;
import com.example.xuxuefeng.coolweather.util.ProgressDialogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by xuxuefeng on 2017/9/12.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int currentLevel;

    @InjectView(R.id.tittle_text)
    TextView tittleText;
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.listView)
    ListView listView;

    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        ButterKnife.inject(this, view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //第一次进来,先拿省份的数据
        queryProvinces();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });

    }

    /**
     * 查询所有省份的信息,先从数据库查询,如果没有再从服务器查询
     */
    private void queryProvinces() {
        tittleText.setText("中国");
        backBtn.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (int i = 0; i < provinceList.size(); i++) {
                dataList.add(provinceList.get(i).getName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = Constant.BaseUrl;
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询所有城市的信息,先从数据库查询,如果没有再从服务器查询
     */
    private void queryCities() {
        tittleText.setText(selectedProvince.getName());
        backBtn.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (int i = 0; i < cityList.size(); i++) {
                dataList.add(cityList.get(i).getName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = Constant.BaseUrl + "/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询所有县的信息,先从数据库查询,如果没有再从服务器查询
     */
    private void queryCounties() {
        tittleText.setText(selectedCity.getName());
        backBtn.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (int i = 0; i < countyList.size(); i++) {
                dataList.add(countyList.get(i).getName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        } else {
            int cityCode = selectedCity.getCityCode();
            int provinceCode = selectedProvince.getProvinceCode();
            String address = Constant.BaseUrl + "/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String url, final String type) {
        ProgressDialogUtil.showProgressDialog(getActivity());
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ProgressDialogUtil.closeProgressDialog();
                        Toast.makeText(getActivity(), "加载数据失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        responseData(response, type);
                    }
                });
    }

    private void responseData(String response, String type) {
        ProgressDialogUtil.closeProgressDialog();
        switch (type) {
            case "province":
                try {
                    List<Province> provinceList = GsonUtil.parseJsonArrayWithGson(response, Province.class);
                    //保存到数据库
                    for (int i = 0; i < provinceList.size(); i++) {
                        Province province = provinceList.get(i);
                        province.setProvinceCode(province.getId());
                        province.save();
                    }
                    queryProvinces();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "加载数据失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case "city":
                try {
                    List<City> cityList = GsonUtil.parseJsonArrayWithGson(response, City.class);
                    for (int i = 0; i < cityList.size(); i++) {
                        City city = cityList.get(i);
                        city.setProvinceId(selectedProvince.getProvinceCode());
                        city.setCityCode(city.getId());
                        city.save();
                    }
                    queryCities();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "加载数据失败", Toast.LENGTH_SHORT).show();
                }

                break;
            case "county":
                try {
                    List<County> countyList = GsonUtil.parseJsonArrayWithGson(response, County.class);
                    for (int i = 0; i < countyList.size(); i++) {
                        County county = countyList.get(i);
                        county.setCityId(selectedCity.getCityCode());
                        county.save();
                    }
                    queryCounties();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "加载数据失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.back_btn)
    public void onViewClicked() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }
    }
}
