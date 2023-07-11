package com.example.hackathon_mobile;

//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ListView listView;
//    private ArrayAdapter<String> adapter;
//    private OkHttpClient client;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        listView = findViewById(R.id.listView);
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
//        listView.setAdapter(adapter);
//
//        Button refreshButton = findViewById(R.id.refreshButton);
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fetchAndDisplayData();
//            }
//        });
//
//        client = new OkHttpClient();
//
//        fetchAndDisplayData();
//    }
//
//    private void fetchAndDisplayData() {
//        Request request = new Request.Builder()
////                .url("https://hackathon-backend-lsv3.onrender.com/equipments")
//                .url("https://hackathon-backend-lsv3.onrender.com/equipments")
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                call.cancel();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//                    String responseData = response.body().string();
//                    Gson gson = new Gson();
//                    JsonArray jsonArray = gson.fromJson(responseData, JsonArray.class);
//
//                    // Process the JSON array and extract the required data
//                    List<String> list = new ArrayList<>();
//                    for (int i = 0; i < jsonArray.size(); i++) {
//                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
//                        if (jsonObject.has("name") && jsonObject.has("temperature")) {
//                            String name = jsonObject.get("name").getAsString();
//                            double temperature = jsonObject.get("temperature").getAsDouble();
//                            String item = name + " - " + temperature + "°C";
//                            list.add(item);
//                        }
//                    }
//
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.clear();
//                            adapter.addAll(list);
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                } finally {
//                    response.close();
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        client.dispatcher().cancelAll();
//    }
//}

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private OkHttpClient client;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAndDisplayData();
            }
        });

        client = new OkHttpClient();
        notificationManager = NotificationManagerCompat.from(this);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchAndDisplayData();
                handler.postDelayed(this, 5000); // Refresh every 5 seconds
            }
        };

        fetchAndDisplayData();
        handler.postDelayed(refreshRunnable, 5000);
    }

    private void fetchAndDisplayData() {
        Request request = new Request.Builder()
                .url("https://hackathon-backend-lsv3.onrender.com/equipments")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    JsonArray jsonArray = gson.fromJson(responseData, JsonArray.class);

                    // Process the JSON array and extract the required data
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                        if (jsonObject.has("name") && jsonObject.has("temperature")) {
                            String name = jsonObject.get("name").getAsString();
                            double temperature = jsonObject.get("temperature").getAsDouble();
                            String item = name + " - " + temperature + "°C";
                            list.add(item);

                            // Check if temperature is 38 and show notification
                            if (temperature >= 38) {
                                showNotification(name, temperature);
                            }
                        }
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } finally {
                    response.close();
                }
            }
        });
    }

    private void showNotification(String name, double temperature) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("temperature_channel", "Temperature Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "temperature_channel")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Temperature Alert")
                .setContentText(name + " temperature reached 38°C")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(6, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.dispatcher().cancelAll();
    }
}