package com.example.xuxuefeng.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by xuxuefeng on 2017/9/12.
 */

public class Province extends DataSupport {

    private int id;
    private String name;
    private int provinceCode;

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
