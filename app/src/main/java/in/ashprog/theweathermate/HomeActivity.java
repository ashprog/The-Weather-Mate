package in.ashprog.theweathermate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ashprog.theweathermate.ForecastModel.ForecastData;
import in.ashprog.theweathermate.ForecastModel.Hour;

public class HomeActivity extends AppCompatActivity implements SlidingUpPanelLayout.PanelSlideListener, LocationListener {

    private static String latLong = "25.4358,81.8463";
    private static String API = "http://api.weatherapi.com/v1/forecast.json?key=81fe6ba1f71e48dabc9120751212201&q=" + latLong + "&days=1";

    LocationManager locationManager;
    LocationListener locationListener;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private LineChart lineChart;
    List<Hour> hourList;
    private LinearLayout layout1, layout2, layout3;
    ForecastAdapter forecastAdapter;
    private int[] tempTV_loc;
    private int[] layout1_loc;
    private int[] layout2_loc;
    private int[] layout3_loc;
    private TextView tempTV, timeTV, cityTV, weatherTV, latLngTV;
    private RecyclerView recyclerView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            updateLocation();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();

        setSlidingUpPanelLayout();

        updateTime();

        checkLocationPermission();

        updateGraph(new ArrayList<Entry>());

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onBackPressed() {

        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else
            super.onBackPressed();
    }

    void initialize() {
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        lineChart = findViewById(R.id.chart);

        tempTV = findViewById(R.id.tempTV);
        timeTV = findViewById(R.id.timeTV);
        cityTV = findViewById(R.id.cityTV);
        weatherTV = findViewById(R.id.weatherTV);
        latLngTV = findViewById(R.id.latLngTV);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        recyclerView = findViewById(R.id.recyclerView);

        tempTV_loc = new int[2];
        layout1_loc = new int[2];
        layout2_loc = new int[2];
        layout3_loc = new int[2];

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = this;
    }

    void setSlidingUpPanelLayout() {
        // set a global layout listener which will be called when the layout pass is completed and the view is drawn
        slidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        //Remove the listener before proceeding
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            slidingUpPanelLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            slidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        // measure your views here
                        tempTV.getLocationOnScreen(tempTV_loc);
                        layout1.getLocationOnScreen(layout1_loc);
                        layout2.getLocationOnScreen(layout2_loc);
                        layout3.getLocationOnScreen(layout3_loc);
                    }
                }
        );
        slidingUpPanelLayout.addPanelSlideListener(this);
    }

    void updateGraph(List<Entry> entries) {

        LineDataSet dataSet = new LineDataSet(entries, "Weather"); // add entries to dataSet
        dataSet.setColor(Color.WHITE); //styling dataSet
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(8f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.WHITE);
        dataSet.setFillAlpha(20);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setCircleRadius(5f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        //styling chart
        lineChart.setTouchEnabled(false);
        lineChart.setClickable(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawBorders(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.animateXY(3000, 3000, Easing.EaseInOutBack, Easing.EaseInOutBack);
    }

    void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            updateLocation();
        }
    }

    @SuppressLint("MissingPermission")
    void updateLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Please enable the location services.", Toast.LENGTH_SHORT).show();
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        }
    }

    void updateTime() {
        final Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                timeTV.setText(new SimpleDateFormat("EEE, h:mm a").format(Calendar.getInstance().getTime()));
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    void fetchForecastData(Location location) {
        latLong = location.getLatitude() + "," + location.getLongitude();
        StringRequest request = new StringRequest(Request.Method.GET, API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                ForecastData forecastDataList = gson.fromJson(response, ForecastData.class);

                cityTV.setText(forecastDataList.getLocation().getName());
                tempTV.setText(forecastDataList.getCurrent().getTempC() + "°");
                weatherTV.setText(forecastDataList.getCurrent().getCondition().getText());
                latLngTV.setText(forecastDataList.getLocation().getLat().toString() + "°," + forecastDataList.getLocation().getLon().toString() + "°");

                hourList = forecastDataList.getForecast().getForecastday().get(0).getHour();

                //update Graph
                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < hourList.size() / 2; i++)
                    entries.add(new Entry(hourList.get(i).getTimeEpoch(), hourList.get(i).getTempC().floatValue()));
                updateGraph(entries);

                //update recyclerView
                forecastAdapter = new ForecastAdapter(HomeActivity.this, hourList);
                recyclerView.setAdapter(forecastAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("TAG", "onErrorResponse: TimeoutError");
                } else if (error instanceof NoConnectionError) {
                    Log.d("TAG", "onErrorResponse: NoConnectionError");
                } else if (error instanceof NetworkError) {
                    Log.d("TAG", "onErrorResponse: NetworkError");
                } else if (error instanceof ParseError) {
                    Log.d("TAG", "onErrorResponse: ParseError");
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        tempTV.setX(tempTV_loc[0] + slideOffset * (getScreenWidth() / 2f - tempTV.getWidth()));
        tempTV.setY(tempTV_loc[1] - slideOffset * 100);
        layout1.setX(layout1_loc[0] - slideOffset * (getScreenWidth() / 2f - layout1.getWidth() / 2f));
        layout2.setX(layout2_loc[0] - slideOffset * (getScreenWidth() / 2f - layout2.getWidth() / 2f));
        layout3.setX(layout3_loc[0] - slideOffset * (getScreenWidth() / 2f - layout3.getWidth() / 2f));
        layout3.setY(layout3_loc[1] - slideOffset * 150);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }

    @Override
    public void onLocationChanged(Location location) {
        fetchForecastData(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        checkLocationPermission();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}