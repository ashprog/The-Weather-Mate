package in.ashprog.theweathermate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements SlidingUpPanelLayout.PanelSlideListener, LocationListener {

    LocationManager locationManager;
    LocationListener locationListener;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private LineChart lineChart;
    private TextView tempTV, timeTV, cityTV;
    private LinearLayout layout1, layout2, layout3;
    private int[] tempTV_loc;
    private int[] layout1_loc;
    private int[] layout2_loc;
    private int[] layout3_loc;
    private String cityName;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();

        setSlidingUpPanelLayout();

        timeTV.setText(new SimpleDateFormat("EEE, h:mm a").format(Calendar.getInstance().getTime()));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

        graph();
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
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);

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

    void graph() {
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(10, 20));
        entries.add(new Entry(15, 17));
        entries.add(new Entry(23, 42));
        entries.add(new Entry(40, 50));
        entries.add(new Entry(45, 60));

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.WHITE);
        dataSet.setValueTextColor(Color.WHITE); // styling, ...
        dataSet.setValueTextSize(8f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.WHITE);
        dataSet.setFillAlpha(20);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setCircleRadius(5f);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
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
        lineChart.animateXY(5000, 2000, Easing.EaseInOutBack, Easing.EaseInOutBack);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        tempTV.setX(tempTV_loc[0] + slideOffset * (getScreenWidth() / 2 - tempTV.getWidth() / 2));
        tempTV.setY(tempTV_loc[1] - slideOffset * 100);

        layout1.setX(layout1_loc[0] - slideOffset * (getScreenWidth() / 2 - layout1.getWidth() / 2));
        layout2.setX(layout2_loc[0] - slideOffset * (getScreenWidth() / 2 - layout2.getWidth() / 2));
        layout3.setX(layout3_loc[0] - slideOffset * (getScreenWidth() / 2 - layout3.getWidth() / 2));
        layout3.setY(layout3_loc[1] - slideOffset * 250);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }

    int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            cityName = addresses.get(0).getLocality();
            cityTV.setText(cityName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}