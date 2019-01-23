package com.example.mymapview;

import android.app.Activity;
import android.widget.Toast;

/**
 * 百度地图定位错误信息
 *
 * @author Admin
 */
public class MapErrorUtil {
    public static void showMapError(Activity activity, int result, int type) {

        String errorText = "";
//        62	4	定位失败，无法获取任何有效定位依据
//        62	5	定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位
//        62	6	定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试
//        62	7	定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试
//        62	9	定位失败，无法获取任何有效定位依据

//        161 1 定位失败，建议您打开GPS
//        161 2 定位失败，建议您打开Wi - Fi
//        67 3 定位失败，请您检查您的网络状态

//        167 8 定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限


        switch (result) {
            case 62:
                if (type == 4) {
                    errorText = "定位失败，无法获取任何有效定位依据";
                } else if (type == 5) {
                    errorText = "定位失败，无法获取有效定位依据，请检查运营商网络或者Wi - Fi网络是否正常开启，尝试重新请求定位";
                } else if (type == 6) {
                    errorText = "定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开WiFi重试";
                } else if (type == 7) {
                    errorText = "定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试";
                } else if (type == 9) {
                    errorText = "定位失败，无法获取任何有效定位依据";
                }
                break;
            case 67:
                if (type == 3) {
                    errorText = "定位失败，请您检查您的网络状态";
                }
                break;
            case 161:
                if (type == 1) {
                    errorText = "定位失败，建议您打开GPS";
                } else if (type == 2) {
                    errorText = "定位失败，建议您打开WiFi";
                }

                break;
            case 167:
                if (type == 8) {
                    errorText = "定位失败，确认定位开关及权限状态";
                }
                break;
            default:
                break;
        }
        Toast.makeText(activity, errorText, Toast.LENGTH_SHORT).show();
    }
}
