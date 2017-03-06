package app.movemate.Adapters;


import org.json.JSONObject;

public class Path {
    public JSONObject path;

    public Path(JSONObject j){
        setJson(j);
    }

    public void setJson(JSONObject j){
        this.path = j;
    }
}
