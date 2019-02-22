package com.edot.hotelmanagement;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.edot.hotelmanagement.common.AppConstants;
import com.edot.hotelmanagement.common.RoomBookingJSONHelper;
import com.edot.network.NetworkHelperUtil;
import com.google.gson.Gson;

import java.util.HashMap;

public class UserHistoryActivity extends AppCompatActivity {

    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollView = new ScrollView(this);
        downloadBookedRoomsDetails();
    }

    private void downloadBookedRoomsDetails()
    {
        new AsyncTask<Void,Void,View>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                scrollView.removeAllViews();
                setContentView(R.layout.layout_loading);
            }

            @Override
            protected View doInBackground(Void... voids) {
                String url = "http://autoiot2019-20.000webhostapp.com/" +
                        "HotelManagement/viewHistory.php?";
                HashMap<String,String> paramsMap = new HashMap<>();
                paramsMap.put("user",AppConstants.currentLoggedInUserID);

                String data = NetworkHelperUtil.readData(url,paramsMap);
                Log.d(AppConstants.LOG_TAG,"Rooms Data received from server"+data);
                if (data != null) {
                    RoomBookingJSONHelper roomBookingJSONHelper =  new Gson().fromJson(data,
                            RoomBookingJSONHelper.class);
                    if (!roomBookingJSONHelper.roomsList.isEmpty()) {
                        return new UserHistoryLinearViewModel(UserHistoryActivity.this)
                                .renderMap(roomBookingJSONHelper.convertToHashMap());
                    }
                    TextView textView = new TextView(UserHistoryActivity.this);
                    textView.setText(R.string.noRoomsBookedUserText);
                    textView.setTextSize(20);
                    return textView;
                }
                return null;
            }

            @Override
            protected void onPostExecute(View view) {
                super.onPostExecute(view);
                setContentView(scrollView);
                if (view != null)
                {
                    scrollView.addView(view);
                }
                else
                {
                    Toast.makeText(UserHistoryActivity.this,R.string.somethingWentWrongCommon
                            ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onCancelled(View view) {
                super.onCancelled(view);
                setContentView(scrollView);
            }
        }.execute();
    }

}
