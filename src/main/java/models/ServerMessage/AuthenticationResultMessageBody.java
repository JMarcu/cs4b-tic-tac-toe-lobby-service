package models.ServerMessage;

import java.io.StringWriter;
import java.util.LinkedHashMap;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

import models.JSONable;

public class AuthenticationResultMessageBody implements JSONable {
    private boolean success;

    public AuthenticationResultMessageBody(){
        this(false);
    }

    public AuthenticationResultMessageBody(boolean success){
        this.success = success;
    }

    public boolean getSuccess(){
        return this.success;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("success", success);

        StringWriter stringify = new StringWriter();
        json.write(stringify);
        return stringify.toString();
    }

    @Override
    public void fromJSONString(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser(jsonString);
        LinkedHashMap<String, Object> json = parser.parseObject();
        this.success = (boolean) json.get("success");
    }
}
