package com.minlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private EditText searchBox;
    private LinearLayout resultsContainer;
    private List<AppInfo> allApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBox = findViewById(R.id.search_box);
        resultsContainer = findViewById(R.id.results_container);

        loadApps();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo ri : apps) {
            String label = ri.loadLabel(pm).toString();
            String pkg = ri.activityInfo.packageName;
            Intent launchIntent = new Intent();
            launchIntent.setClassName(pkg, ri.activityInfo.name);
            allApps.add(new AppInfo(label, launchIntent));
        }
    }

    private void filterApps(String query) {
        resultsContainer.removeAllViews();
        if (query.isEmpty()) return;

        for (AppInfo app : allApps) {
            if (app.label.toLowerCase().contains(query.toLowerCase())) {
                TextView tv = new TextView(this);
                tv.setText(app.label);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(18);
                tv.setPadding(20, 20, 20, 20);
                tv.setOnClickListener(v -> startActivity(app.launchIntent));
                resultsContainer.addView(tv, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
            }
        }
    }

    private static class AppInfo {
        String label;
        Intent launchIntent;

        AppInfo(String label, Intent launchIntent) {
            this.label = label;
            this.launchIntent = launchIntent;
        }
    }
}
