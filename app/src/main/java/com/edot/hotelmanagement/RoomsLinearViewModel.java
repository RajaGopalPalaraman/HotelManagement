package com.edot.hotelmanagement;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.edot.hotelmanagement.common.AppConstants;
import com.edot.hotelmanagement.common.RoomBookingJSONHelper;
import com.edot.models.LinearViewModel;

import java.util.HashMap;

public final class RoomsLinearViewModel extends LinearViewModel {

    private String date;

    RoomsLinearViewModel(Context context,String date) {
        super(context);
        this.date = date;
    }

    @Override
    public View renderChildMap(final HashMap<String, String> hashMap) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_room_list_view,linearLayout,false);
        View innerView = view.findViewById(R.id.roomNumber);
        ((TextView) innerView).setText(context.getResources().getString(R.string.roomNumber
                ,hashMap.get(RoomBookingJSONHelper.RoomInfo.ROOM_NUMBER)));
        innerView = view.findViewById(R.id.roomCost);
        ((TextView) innerView).setText(hashMap.get(RoomBookingJSONHelper.RoomInfo.ROOM_COST));
        innerView = view.findViewById(R.id.roomTypeAndGroup);
        ((TextView) innerView).setText(context.getResources().getString(R.string.roomTypeInfo
                ,hashMap.get(RoomBookingJSONHelper.RoomInfo.ROOM_TYPE)
                ,hashMap.get(RoomBookingJSONHelper.RoomInfo.ROOM_GROUP)));
        innerView = view.findViewById(R.id.roomDescription);
        ((TextView) innerView).setText(hashMap.get(RoomBookingJSONHelper.RoomInfo.ROOM_DESCRIPTION));
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AppConstants.LOG_TAG,((TextView)v.findViewById(R.id.roomNumber))
                        .getText().toString());
                Intent intent = new Intent(context,RoomConfirmationActivity.class);
                intent.putExtra(RoomConfirmationActivity.DATA_MAP,hashMap);
                intent.putExtra(RoomConfirmationActivity.DATE,date);
                context.startActivity(intent);
            }
        });
        return view;
    }

}
