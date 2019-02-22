package com.edot.hotelmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class RoomBookingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private List<JSONHelper.TypeInfo> roomsInfoList;
    private int type;

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
        new AsyncTask<Void, Void, List<CharSequence>>()
        {
            @Override
            protected List<CharSequence> doInBackground(Void... voids) {

                String data = NetworkHelperUtil.readData("http://autoiot2019-20.000webhostapp.com/" +
                        "HotelManagement/roomTypeMeta.php",null);
                if (data != null) {
                    Log.d(AppConstants.LOG_TAG,"Data received by server"+data);
                    Gson gson = new Gson();
                    JSONHelper jsonHelper = gson.fromJson(data, JSONHelper.class);
                    roomsInfoList = jsonHelper.roomList;
                    if (roomsInfoList.size() > 1) {
                        JSONHelper.TypeInfo info = jsonHelper.newInstance();
                        info.no = -1;
                        info.type = "ALL";
                        roomsInfoList.add(info);
                    }
                    ArrayList<CharSequence> list = new ArrayList<>();
                    for (JSONHelper.TypeInfo info1 : roomsInfoList) {
                        list.add(info1.type);
                    }
                    int[] ints = DateChooserHelper.parseDate(jsonHelper.date);
                    if (ints != null) {
                        DateChooserHelper.refDay = ints[0];
                        DateChooserHelper.refMonth = ints[1];
                        DateChooserHelper.refYear = ints[2];
                    }
                    return list;
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<CharSequence> charSequenceList) {
                super.onPostExecute(charSequenceList);
                if (charSequenceList != null) {
                    setContentView(R.layout.activity_room_booking);
                    gifImageView = findViewById(R.id.loadingGIFView);
                    dateView = findViewById(R.id.dateTextView);
                    dateView.setText(DateChooserHelper.generateDate(DateChooserHelper.refYear
                            ,DateChooserHelper.refMonth,DateChooserHelper.refDay));
                    scrollView = findViewById(R.id.roomsView);
                    Spinner spinner = findViewById(R.id.roomCategoryChooser);
                    spinner.setOnItemSelectedListener(RoomBookingActivity.this);
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(RoomBookingActivity.this,
                            R.layout.spinner_helper, 0, charSequenceList);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_helper);
                    spinner.setAdapter(adapter);
                }
                else
                {
                    setContentView(new LinearLayout(RoomBookingActivity.this));
                    Toast.makeText(RoomBookingActivity.this,
                            R.string.somethingWentWrongCommon,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (asyncTask != null)
        {
            asyncTask.cancel(true);
        }
        JSONHelper.TypeInfo typeInfo = roomsInfoList.get(position);
        Log.d(AppConstants.LOG_TAG,"Selected Type : Id:"+typeInfo.no+",Name:"+typeInfo.type);
        type = typeInfo.no;
        String s = dateView.getText().toString();
        String[] strings = s.split("-");
        downloadAvailableRoomsList(type,strings[2]+"-"+strings[1]+"-"+strings[0]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateView.setText(DateChooserHelper.generateDate(year,++month,day));
        String s = dateView.getText().toString();
        String[] strings = s.split("-");
        downloadAvailableRoomsList(type,strings[2]+"-"+strings[1]+"-"+strings[0]);
    }

    public void onHistory(View view) {
        Intent intent = new Intent(this,UserHistoryActivity.class);
        startActivity(intent);
    }

    private final class JSONHelper
    {
        private class TypeInfo
        {
            int no;
            String type;
        }

        private TypeInfo newInstance()
        {
            return new TypeInfo();
        }

        private ArrayList<TypeInfo> roomList;
        private String date;
    }

    private void downloadAvailableRoomsList(final int type,final String date)
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
                        "HotelManagement/roomSearcher.php?date="+date;
                if (type > 0)
                {
                    url = url + "&type="+type;
                }
                String data = NetworkHelperUtil.readData(url,null);
                Log.d(AppConstants.LOG_TAG,"Rooms Data received from server"+data);
                if (data != null) {
                    RoomBookingJSONHelper roomBookingJSONHelper =  new Gson().fromJson(data,
                            RoomBookingJSONHelper.class);
                    if (!roomBookingJSONHelper.roomsList.isEmpty()) {
                        return new RoomsLinearViewModel(RoomBookingActivity.this,dateView.getText().toString())
                                .renderMap(roomBookingJSONHelper.convertToHashMap());
                    }
                    TextView textView = new TextView(RoomBookingActivity.this);
                    textView.setText(R.string.noRoomsAvailable);
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
                    Toast.makeText(RoomBookingActivity.this,R.string.somethingWentWrongCommon
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
    protected void onStart() {
        super.onStart();
        if (AppConstants.currentLoggedInUserID == null)
        {
            finish();
        }
        else if (dateView != null) {
            String s = dateView.getText().toString();
            String[] strings = s.split("-");
            if (strings.length == 3) {
                downloadAvailableRoomsList(type, strings[2] + "-" + strings[1] + "-" + strings[0]);
            }
        }
    }

}
