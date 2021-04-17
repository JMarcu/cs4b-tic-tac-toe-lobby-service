package models.ServerMessage;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.LinkedHashMap;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

import models.JSONable;

public class Message implements JSONable {

    private JSONable body;
    private MessageType type;

    public Message(){
        this(null, null);
    }
    
    public Message(JSONable body, MessageType type){
        this.body = body;
        this.type = type;
    }

    public JSONable getBody(){ return body; }
    public MessageType getType(){ return type; }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("body", body.toJSONString());
        json.put("type", type);

        StringWriter stringify = new StringWriter();
        json.write(stringify);
        return stringify.toString();
    }

    @Override
    public void fromJSONString(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser(jsonString);
        LinkedHashMap<String, Object> json = parser.parseObject();

        this.type = (MessageType) json.get("type");

        final String bodyString = (String) json.get("body");
        
        JSONable body = null;

        switch(type){
            case AUTHENTICATION_SUCCESS:
                body = new AuthenticationResultMessageBody();    
                break;
            default:
                break;
        }
        
        body.fromJSONString(bodyString);
        this.body = body;
    }
}
