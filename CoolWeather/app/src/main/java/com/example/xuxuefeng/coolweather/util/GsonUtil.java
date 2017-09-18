package com.example.xuxuefeng.coolweather.util;/*
22  * 封装的GSON解析工具类，提供泛型参数
23  */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;


public class GsonUtil {
    // 将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    // 将Json数组解析成相应的映射对象列表
    public static <T> List<T> parseJsonArrayWithGson(String jsonData, Class<T> type) {
        List<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(jsonData).getAsJsonArray();
        Gson mGson = new Gson();
        for (final JsonElement elem : array) {
            mList.add(mGson.fromJson(elem, type));
        }
        return mList;
    }
}