package com.wzy.lamanpro.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.wzy.lamanpro.R;

public class SettingTest extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private Preference time;
    private Preference once;
    private Preference power;
    private CheckBoxPreference cbp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setmode);
        time = findPreference("time");
        once = findPreference("once");
        power = findPreference("power");
//        bindPreferenceSummaryToValue(findPreference("use_mode"));
//        time.setDefaultValue(500);
//        once.setDefaultValue(10);
//        power.setDefaultValue(66);
        bindPreferenceSummaryToValue(time);
        bindPreferenceSummaryToValue(once);
        bindPreferenceSummaryToValue(power);
        cbp = (CheckBoxPreference) findPreference("use_mode");
        cbp.setOnPreferenceClickListener(this);
        if (cbp.isChecked()) {
            time.setEnabled(true);
            once.setEnabled(true);
            cbp.setSummary("当前为精检模式");

        } else {
            time.setEnabled(false);
            once.setEnabled(false);
            cbp.setSummary("当前为快检模式");
        }
    }

    /*
     * 自定义方法，设置监听器，查看设置中的选项是否有变更
     * */
    private void bindPreferenceSummaryToValue(Preference preference) {
        //设置监听器，查看设置中的选项是否有变更
        preference.setOnPreferenceChangeListener(this);

        //有选项变更时立即将preference文件中的value进行相应的变更
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if (preference instanceof CheckBoxPreference) {

        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (cbp.isChecked()) {
            time.setEnabled(true);
            once.setEnabled(true);
            cbp.setSummary("当前为精检模式");
        } else {
            time.setEnabled(false);
            once.setEnabled(false);
            cbp.setSummary("当前为快检模式");
        }
        return true;
    }
}
