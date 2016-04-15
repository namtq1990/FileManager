package com.quangnam.baseframework.model.message;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;

/**
 * Created by quangnam on 4/15/16.
 * Base Class for Request message
 */
public abstract class BaseRequest extends BaseMessage {

    //----------------------------------------------------------------------------------------------
    //Function to parser

    /**
     * Convert message request to get String.
     * All field of this method will be put in parameter
     *
     * @return Data part of method GET
     */
    public String toGetRequest() throws IllegalAccessException {
        return convertGetRequest(getClass().getFields(), false);
    }

    /**
     * Option of {@link #toGetRequest()} except that it can ignore all field is not a serializedName
     *
     * @param isSerialOnly  ignore all field is not a serializedName if true.
     * @throws IllegalAccessException
     */
    public String toGetRequest(boolean isSerialOnly) throws IllegalAccessException {
        return convertGetRequest(getClass().getFields(), isSerialOnly);
    }

    public abstract String toJson();

    private String convertGetRequest(Field[] fields, boolean isSerialOnly) throws IllegalAccessException {
        StringBuilder s = new StringBuilder();
        for (Field field : fields) {
            if (field != null) {
                String tag;
                SerializedName name = field.getAnnotation(SerializedName.class);

                if (isSerialOnly) {
                    if (name != null) {
                        tag = name.value();
                    } else {
                        continue;
                    }
                } else {
                    if (name != null) {
                        tag = name.value();
                    } else {
                        tag = field.getName();
                    }
                }

                Object value = field.get(this);

                if (value instanceof GetParameter) {
                    s.append(((GetParameter) value).toGetParameter());
                } else {
                    s.append(tag).append("=").append(value);
                }
            }
        }

        return s.toString();
    }

    //----------------------------------------------------------------------------------------------

    public interface GetParameter {
        String toGetParameter();
    }
}
