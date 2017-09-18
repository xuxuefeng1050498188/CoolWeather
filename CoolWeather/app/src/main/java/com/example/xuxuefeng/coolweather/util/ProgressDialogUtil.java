package com.example.xuxuefeng.coolweather.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by xuxuefeng on 2017/9/12.
 */

public class ProgressDialogUtil {

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public static void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
