package com.edot.hotelmanagement.common;

import java.util.ArrayList;
import java.util.HashMap;

public final class RoomBookingJSONHelper {

    public ArrayList<RoomInfo> roomsList;

    public class RoomInfo
    {
        public static final String ROOM_NUMBER = "roomNumber";
        public static final String ROOM_TYPE = "roomType";
        public static final String ROOM_GROUP = "roomGroup";
        public static final String ROOM_COST = "roomCost";
        public static final String ROOM_DESCRIPTION = "roomDescription";
        public static final String ROOM_BOOKED_ON = "roomBookedOn";
        public static final String ROOM_BOOKED_BY = "roomBookedBy";
        public static final String ROOM_STATUS = "roomStatus";

        private String roomNumber;
        private String roomType;
        private String roomGroup;
        private String roomCost;
        private String roomDescription;
        private String roomBookedOn;
        private String roomBookedBy;
        private String roomStatus;
    }

    public HashMap<String,HashMap<String,String>> convertToHashMap()
    {
        HashMap<String,HashMap<String,String>> map = new HashMap<>();
        for (RoomInfo info : roomsList)
        {
            HashMap<String,String> innerMap = new HashMap<>();
            innerMap.put(RoomInfo.ROOM_NUMBER,info.roomNumber);
            innerMap.put(RoomInfo.ROOM_TYPE,info.roomType);
            innerMap.put(RoomInfo.ROOM_GROUP,info.roomGroup);
            innerMap.put(RoomInfo.ROOM_COST,info.roomCost);
            innerMap.put(RoomInfo.ROOM_DESCRIPTION,info.roomDescription);
            innerMap.put(RoomInfo.ROOM_BOOKED_ON,info.roomBookedOn);
            innerMap.put(RoomInfo.ROOM_BOOKED_BY,info.roomBookedBy);
            innerMap.put(RoomInfo.ROOM_STATUS,info.roomStatus);

            map.put(info.roomNumber,innerMap);
        }
        return map;
    }

}
