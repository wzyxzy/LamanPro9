package com.wzy.lamanpro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SPUtility {

    public static String getUserId(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString("account",
                "");
    }

    public static void setUserId(Context c, String id) {
        putSPString(c, "account", id);
    }


    public static boolean getSPBoolean(Context c, int keyId) {
        return getSPBoolean(c, keyId, false);
    }

    public static boolean getSPBoolean(Context c, int keyId,
                                             boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
                c.getResources().getString(keyId), defaultValue);
    }

    public static boolean getSPBoolean(Context c, String key) {
        return getSPBoolean(c, key, false);
    }

    public static boolean getSPBoolean(Context c, String key,
                                             boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(key,
                defaultValue);
    }

    public static void putSPBoolean(Context c, int keyId, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String key = c.getString(keyId);

        if (!sp.contains(key)) {
            sp.edit().putBoolean(key, value).commit();
        } else {
            boolean t = getSPBoolean(c, keyId);
            if (t != value) {
                sp.edit().putBoolean(key, value).commit();
            }
        }
    }

    public static void putSPBoolean(Context c, String key, boolean value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        if (!sp.contains(key)) {
            sp.edit().putBoolean(key, value).commit();
        } else {
            boolean t = getSPBoolean(c, key);
            if (t != value) {
                sp.edit().putBoolean(key, value).commit();
            }
        }
    }

    public static int getSPInteger(Context c, int keyId) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(
                c.getResources().getString(keyId), -1);
    }

    public static int getSPInteger(Context c, String key) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(key, 0);
    }

    public static String getSPString(Context c, int keyId) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(
                c.getResources().getString(keyId), "");
    }

    public static String getSPString(Context c, String key) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(key,
                "");
    }

    public static int getSPStringInteger(Context c, int keyId) {
        return Integer.parseInt(getSPString(c, keyId));
    }

    public static void putSPStringInteger(Context c, int keyId, int value) {
        putSPString(c, keyId, value + "");
    }

    public static long getSPStringLong(Context c, int keyId) {
        String t = getSPString(c, keyId);
        return Long.parseLong(t);
    }

    public static void putSPStringLong(Context c, int keyId, long value) {
        putSPString(c, keyId, value + "");
    }

    public static void putSPString(Context c, int keyId, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String key = c.getString(keyId);
        if (!sp.contains(key)) {
            sp.edit().putString(key, value).commit();
        } else {
            String t = getSPString(c, keyId);
            if (!t.equals(value)) {
                sp.edit().putString(key, value).commit();
            }
        }
    }

    public static void putSPString(Context c, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        if (!sp.contains(key)) {
            sp.edit().putString(key, value).commit();
        } else {
            String t = getSPString(c, key);
            if (!t.equals(value)) {
                sp.edit().putString(key, value).commit();
            }
        }
    }

    public static void putSPInteger(Context c, int keyId, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String key = c.getString(keyId);
        if (!sp.contains(key)) {
            sp.edit().putInt(key, value).commit();
        } else {
            int t = getSPInteger(c, keyId);
            if (t != value) {
                sp.edit().putInt(key, value).commit();
            }
        }
    }

    public static void putSPInteger(Context c, String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        if (!sp.contains(key)) {
            sp.edit().putInt(key, value).commit();
        } else {
            int t = getSPInteger(c, key);
            if (t != value) {
                sp.edit().putInt(key, value).commit();
            }
        }
    }

    public static void clear(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        if (sp != null)
            sp.edit().clear().commit();
    }

}
