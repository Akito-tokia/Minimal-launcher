package com.minlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private List<AppInfo> allApps;
    private ArrayAdapter<String> adapter;
    private List<String> filteredNames;
    private List<AppInfo> filteredApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchBar = findViewById(R.id.search_bar);
        ListView appList = findViewById(R.id.app_list);

        allApps = loadInstalledApps();
        filteredApps = new ArrayList<>(allApps);
        filteredNames = new ArrayList<>();
        for (AppInfo app : filteredApps) {
            filteredNames.add(app.label);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredNames);
        appList.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        appList.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo app = filteredApps.get(position);
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        });
    }

    private List<AppInfo> loadInstalledApps() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        List<AppInfo> appInfos = new ArrayList<>();
        for (ResolveInfo info : apps) {
            String label = info.loadLabel(pm).toString();
            String packageName = info.activityInfo.packageName;
            appInfos.add(new AppInfo(label, packageName));
        }
        Collections.sort(appInfos, (a, b) -> a.label.compareToIgnoreCase(b.label));
        return appInfos;
    }

    private void filterApps(String query) {
        filteredApps.clear();
        filteredNames.clear();

        if (query.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }

        for (AppInfo app : allApps) {
            if (app.label.toLowerCase().contains(query.toLowerCase())) {
                filteredApps.add(app);
                filteredNames.add(app.label);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private static class AppInfo {
        String label;
        String packageName;

        AppInfo(String label, String packageName) {
            this.label = label;
            this.packageName = packageName;
        }
    }
}
