package com.evg.simsend.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Properties;

public class ObjectToPropertiesConverter {

    public static Properties convert(Gson gson, String prefix, Object value) {
        Properties p = new Properties();
        JsonElement je = gson.toJsonTree( value );
        JsonObject jo = je.getAsJsonObject();
        create( jo, prefix, p );
        return p;
    }

    private static void create(JsonObject jo, String str, Properties p) {
        for ( Map.Entry<String, JsonElement> x : jo.entrySet() ) {
            JsonElement y = x.getValue();
            if ( y.isJsonArray() ) {

            } else if ( y.isJsonNull() ) {

            } else if ( y.isJsonObject() ) {
                create( y.getAsJsonObject(), str == null ? x.getKey() : str + "." + x.getKey(), p );
            } else if ( y.isJsonPrimitive() ) {
                p.setProperty( str == null ? x.getKey() : str + "." + x.getKey(), y.getAsString() );
            }
        }
    }
}