package com.edot.hotelmanagement;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edot.hotelmanagement.common.AppConstants;
import com.edot.hotelmanagement.common.RoomBookingJSONHelper;
import com.edot.network.NetworkHelperUtil;
import com.google.gson.Gson;

import java.util.HashMap;

public class RoomConfirmationActivity extends AppCompatActivity {

    public static final String ROOM_ID = "room";
    public static final String DATE = "date";
    public static final String DATA_MAP = "map";
    private static final String USER_ID = "user";

    private HashMap<String,String> formDate;
    private String date;
    private String clientDate;

    private boolean unLock = false;
    private boolean enableBackButton = false;
    private boolean booked = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (!booked) {
            setContentView(R.layout.layout_loading);
            unLock = false;
            formDate = (HashMap<String, String>) getIntent().getSerializableExtra(DATA_MAP);
            date = getIntent().getStringExtra(DATE);
            clientDate = date;
            int[] ints = DateChooserHelper.parseDate(date);
            if (ints != null) {
                date = ints[2] + "-" + ints[1] + "-" + ints[0];
            } else {
                Log.d(AppConstants.LOG_TAG, "Current session : " + date);
            }

            new AsyncTask<Void, Void, RoomBookingJSONHelper>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    enableBackButton = false;
                }

                @Override
                protected RoomBookingJSONHelper doInBackground(Void... voids) {
                    if (AppConstants.currentLoggedInUserID != null) {
                        String url = "http://autoiot2019-20.000webhostapp.com/HotelManagement/roomLocker.php";
                        HashMap<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(ROOM_ID, formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_NUMBER));
                        paramsMap.put(DATE, date);
                        paramsMap.put(USER_ID, AppConstants.currentLoggedInUserID);

                        String data = NetworkHelperUtil.readData(url, paramsMap);
                        if (data != null) {
                            Log.d(AppConstants.LOG_TAG, "Room locking response : " + data);
                            RoomBookingJSONHelper roomBookingJSONHelper = new Gson().fromJson(data,
                                    RoomBookingJSONHelper.class);
                            if (!(roomBookingJSONHelper.roomsList == null || roomBookingJSONHelper.roomsList.isEmpty())) {
                                unLock = true;
                                return roomBookingJSONHelper;
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(RoomBookingJSONHelper roomInfo) {
                    super.onPostExecute(roomInfo);
                    enableBackButton = true;
                    if (roomInfo == null) {
                        setContentView(new LinearLayout(RoomConfirmationActivity.this));
                        Toast.makeText(RoomConfirmationActivity.this,
                                R.string.roomBlockingFailed, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        HashMap<String, String> map = roomInfo.convertToHashMap().get(
                                formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_NUMBER));
                        if (map != null) {
                            setContentView(R.layout.activity_room_confirmation);
                            TextView textView = findViewById(R.id.rommConfirmationNumber);
                            textView.setText(getResources().getString(R.string.roomNumber,
                                    formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_NUMBER)));

                            textView = findViewById(R.id.activity_room_confirmation_type_view);
                            textView.setText(formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_TYPE));

                            textView = findViewById(R.id.activity_room_confirmation_cost_view);
                            textView.setText(map.get(RoomBookingJSONHelper.RoomInfo.ROOM_COST));

                            textView = findViewById(R.id.activity_room_confirmation_floor_view);
                            textView.setText(formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_GROUP));

                            textView = findViewById(R.id.activity_room_confirmation_date_view);
                            textView.setText(clientDate);
                        } else {
                            setContentView(new LinearLayout(RoomConfirmationActivity.this));
                            Toast.makeText(RoomConfirmationActivity.this,
                                    R.string.roomBlockingFailed, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            }.execute();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (unLock)
        {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Toast.makeText(RoomConfirmationActivity.this, R.string.unLockingRoom, Toast.LENGTH_SHORT).show();
                }
                @Override
                protected Void doInBackground(Void... voids) {
                    String url = "http://autoiot2019-20.000webhostapp.com/HotelManagement/roomUnLocker.php";
                    HashMap<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(ROOM_ID, formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_NUMBER));
                    paramsMap.put(DATE, date);
                    paramsMap.put(USER_ID, AppConstants.currentLoggedInUserID);

                    String data = NetworkHelperUtil.readData(url, paramsMap);
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    unLock = false;
                }
            }.execute();
        }
    }

    @Override
    public void onBackPressed() {
        if (enableBackButton) {
            if (unLock) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(RoomConfirmationActivity.this, R.string.unLockingRoom, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        String url = "http://autoiot2019-20.000webhostapp.com/HotelManagement/roomUnLocker.php";
                        HashMap<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(ROOM_ID, formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_NUMBER));
                        paramsMap.put(DATE, date);
                        paramsMap.put(USER_ID, AppConstants.currentLoggedInUserID);

                        String data = NetworkHelperUtil.readData(url, paramsMap);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        unLock = false;
                        RoomConfirmationActivity.super.onBackPressed();
                    }
                }.execute();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void onBook(final View view) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                view.setVisibility(View.GONE);
                findViewById(R.id.activityRooConfirmationLoading).setVisibility(View.VISIBLE);
                unLock = false;
                enableBackButton = false;
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                String url = "http://autoiot2019-20.000webhostapp.com/HotelManagement/roomBooker.php";
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(ROOM_ID, formDate.get(RoomBookingJSONHelper.RoomInfo.ROOM_NUMBER));
                paramsMap.put(DATE, date);
                paramsMap.put(USER_ID, AppConstants.currentLoggedInUserID);

                String data = NetworkHelperUtil.readData(url, paramsMap);
                Log.d(AppConstants.LOG_TAG,"Booking response from server : "+data);
                return Boolean.parseBoolean(data);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean)
                {
                    booked=true;
                    LinearLayout l = findViewById(R.id.activity_room_confirmation_room_info_view);
                    l.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(RoomConfirmationActivity.this);
                    inflater.inflate(R.layout.booking_success,l,true);
                }
                else
                {
                    unLock = true;
                    view.setVisibility(View.VISIBLE);
                    Toast.makeText(RoomConfirmationActivity.this,
                            R.string.roomBookingFailed,Toast.LENGTH_SHORT).show();
                }
                findViewById(R.id.activityRooConfirmationLoading).setVisibility(View.GONE);
                enableBackButton = true;
            }
        }.execute();
    }
}
