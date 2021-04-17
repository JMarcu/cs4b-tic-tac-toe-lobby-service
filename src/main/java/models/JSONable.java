package models;

import org.apache.tomcat.util.json.ParseException;

public interface JSONable {
    public abstract String toJSONString();
    public abstract void fromJSONString(String jsonString) throws ParseException;
}
