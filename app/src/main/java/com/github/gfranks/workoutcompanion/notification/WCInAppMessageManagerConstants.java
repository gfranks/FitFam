package com.github.gfranks.workoutcompanion.notification;

import com.urbanairship.json.JsonMap;
import com.urbanairship.json.JsonValue;
import com.urbanairship.push.iam.InAppMessage;

import java.util.HashMap;
import java.util.Map;

public class WCInAppMessageManagerConstants {

    public static final String TYPE = "type";
    private static final long DURATION = 5000;

    public static InAppMessage.Builder getDefaultBuilder() {
        return new InAppMessage.Builder()
                .setDuration(DURATION)
                .setPosition(InAppMessage.POSITION_TOP)
                .setExtras(getJsonMapForMessageType(MessageType.DEFAULT));
    }

    public static InAppMessage.Builder getErrorBuilder() {
        return new InAppMessage.Builder()
                .setDuration(DURATION)
                .setPosition(InAppMessage.POSITION_TOP)
                .setExtras(getJsonMapForMessageType(MessageType.ERROR));
    }

    public static InAppMessage.Builder getSuccessBuilder() {
        return new InAppMessage.Builder()
                .setDuration(DURATION)
                .setPosition(InAppMessage.POSITION_TOP)
                .setExtras(getJsonMapForMessageType(MessageType.SUCCESS));
    }

    public static InAppMessage.Builder getInfoBuilder() {
        return new InAppMessage.Builder()
                .setDuration(DURATION)
                .setPosition(InAppMessage.POSITION_TOP)
                .setExtras(getJsonMapForMessageType(MessageType.INFO));
    }

    public static InAppMessage.Builder getWarningBuilder() {
        return new InAppMessage.Builder()
                .setDuration(DURATION)
                .setPosition(InAppMessage.POSITION_TOP)
                .setExtras(getJsonMapForMessageType(MessageType.WARNING));
    }

    private static JsonMap getJsonMapForMessageType(MessageType type) {
        Map<String, JsonValue> map = new HashMap<String, JsonValue>();
        try {
            map.put(TYPE, JsonValue.parseString(type.toString()));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new JsonMap(map);
    }

    public enum MessageType {
        DEFAULT,
        ERROR,
        SUCCESS,
        INFO,
        WARNING;

        public static MessageType parse(String type) {
            type = type.replaceAll("\"", "");
            if (type.equals(String.valueOf(ERROR))) {
                return ERROR;
            } else if (type.equals(String.valueOf(SUCCESS))) {
                return SUCCESS;
            } else if (type.equals(String.valueOf(INFO))) {
                return INFO;
            } else if (type.equals(String.valueOf(WARNING))) {
                return WARNING;
            } else {
                return DEFAULT;
            }
        }
    }
}
