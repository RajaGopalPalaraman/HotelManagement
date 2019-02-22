package com.edot.hotelmanagement;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edot.hotelmanagement.common.AppConstants;
import com.edot.hotelmanagement.common.RoomBookingJSONHelper;
import com.edot.network.NetworkHelperUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class AdminActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView dateView;
    private ScrollView scrollView;
    private GifImageView gifImageView;

    private AsyncTask<Void,Void,View> asyncTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading);
        updateSpinner();
    }

    private void updateSpinner() {
        new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... voids) {

                String data = NetworkHelperUtil.readData("http://autoiot2019-20.000webhostapp.com/" +
                        "HotelManagement/roomTypeMeta.php",null);
                if (data != null) {
                    Log.d(AppConstants.LOG_TAG,"Data received by server"+data);
                    Gson gson = new Gson();
                    AdminActivity.JSONHelper jsonHelper = gson.fromJson(data, AdminActivity.JSONHelper.class);

                    int[] ints = DateChooserHelper.parseDate(jsonHelper.date);
                    if (ints != null) {
                        DateChooserHelper.refDay = ints[0];
                        DateChooserHelper.refMonth = ints[1];
                        DateChooserHelper.refYear = ints[2];
                    }
                    return true;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean charSequenceList) {
                super.onPostExecute(charSequenceList);
                if (charSequenceList) {
                    setContentView(R.layout.activity_admin);
                    gifImageView = findViewById(R.id.loadingGIFView);
                    dateView = findViewById(R.id.dateTextView);
                    dateView.setText(DateChooserHelper.generateDate(DateChooserHelper.refYear
                            ,DateChooserHelper.refMonth,DateChooserHelper.refDay));
                    scrollView = findViewById(R.id.roomsView);
                    String s = dateView.getText().toString();
                    String[] strings = s.split("-");
                    downloadBookedRoomsList(strings[2]+"-"+strings[1]+"-"+strings[0]);
                }
                else
                {
                    setContentView(new LinearLayout(AdminActivity.this));
                    Toast.makeText(AdminActivity.this,
                            R.string.somethingWentWrongCommon,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void openDatePicker(View view) {
        if (asyncTask != null)
        {
            asyncTask.cancel(true);
        }
        TextView textView = (TextView) view;
        int[] ints = DateChooserHelper.parseDate(textView.getText().toString());
        if (ints == null) {
            new DatePickerDialog(this, this, DateChooserHelper.refYear,
                    DateChooserHelper.refMonth-1, DateChooserHelper.refDay).show();
        }
        else {
            new DatePickerDialog(this, this, ints[2],
                    ints[1]-1, ints[0]).show();
        }
    }

    private void downloadBookedRoomsList(final String date)
    {
        asyncTask = new AsyncTask<Void,Void,View>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                scrollView.removeAllViews();
                gifImageView.setVisibility(View.VISIBLE);
            }

            @Override
            protected View doInBackground(Void... voids) {
                String url = "http://autoiot2019-20.000webhostapp.com/" +
                        "HotelManagement/viewHistory.php?date="+date;

                String data = NetworkHelperUtil.readData(url,null);
                Log.d(AppConstants.LOG_TAG,"Rooms Data received from server"+data);
                if (data != null) {
                    RoomBookingJSONHelper roomBookingJSONHelper =  new Gson().fromJson(data,
                            RoomBookingJSONHelper.class);
                    if (!roomBookingJSONHelper.roomsList.isEmpty()) {
                        return new AdminLinearViewModel(AdminActivity.this)
                                .renderMap(roomBookingJSONHelper.convertToHashMap());
                    }
                    TextView textView = new TextView(AdminActivity.this);
                    textView.setText(R.string.noRoomsBooked);
                    textView.setTextSize(20);
                    return textView;
                }
                return null;
            }

            @Override
            protected void onPostExecute(View view) {
                super.onPostExecute(view);
                asyncTask = null;
                gifImageView.setVisibility(View.GONE);
                if (view != null)
                {
                    scrollView.addView(view);
                }
                else
                {
                    Toast.makeText(AdminActivity.this,R.string.somethingWentWrongCommon
                            ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onCancelled(View view) {
                super.onCancelled(view);
                gifImageView.setVisibility(View.GONE);
            }
        };
        asyncTask.execute();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateView.setText(DateChooserHelper.generateLocalDateString(year,++month,day));
        String s = dateView.getText().toString();
        String[] strings = s.split("-");
        downloadBookedRoomsList(strings[2]+"-"+strings[1]+"-"+strings[0]);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dateView != null) {
            String s = dateView.getText().toString();
            String[] strings = s.split("-");
            if (strings.length == 3) {
                downloadBookedRoomsList(strings[2] + "-" + strings[1] + "-" + strings[0]);
            }
        }
    }

    private final class JSONHelper
    {
        private class TypeInfo
        {
            int no;
            String type;
        }

        private AdminActivity.JSONHelper.TypeInfo newInstance()
        {
            return new AdminActivity.JSONHelper.TypeInfo();
        }

        private ArrayList<AdminActivity.JSONHelper.TypeInfo> roomList;
        private String date;
    }

}
