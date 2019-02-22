package com.edot.hotelmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.edot.hotelmanagement.common.RoomBookingJSONHelper;
import com.edot.models.LinearViewModel;

import java.util.HashMap;

public final class UserHistoryLinearViewModel extends LinearViewModel {

    public UserHistoryLinearViewModel(Context context) {
        super(context);
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
        ((TextView) innerView).setText(context.getResources().getString(R.string.bookingOn));
        innerView = view.findViewById(R.id.roomDescription);
        ((TextView) innerView).setText(hashMap.get(RoomBookingJSONHelper.RoomInfo.ROOM_BOOKED_ON));
        return view;
    }

}
