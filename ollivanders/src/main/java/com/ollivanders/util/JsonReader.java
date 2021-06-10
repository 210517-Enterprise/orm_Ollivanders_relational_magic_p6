package com.ollivanders.util;

import org.json.*;

public interface JsonReader{
	

String jsonString = ... ; //assign your JSON String here
JSONObject obj = new JSONObject(jsonString);
String pageName = obj.getJSONObject("pageInfo").getString("pageName");

JSONArray arr = obj.getJSONArray("posts"); // notice that `"posts": [...]`
for (int i = 0; i < arr.length(); i++)
{
    String post_id = arr.getJSONObject(i).getString("post_id");
    ......
}
}}