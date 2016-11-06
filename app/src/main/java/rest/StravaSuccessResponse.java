package rest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TommyD on 12/11/2015.
 * used to convert Retrofit Response to JSONObject that we can parse to extract information we want
 */
public class StravaSuccessResponse {
    public static final String EXTERNAL_ID = "external_id";
    public static final String ERROR = "error";
    public static final String STATUS = "status";
    public static final String ACTIVITY_ID = "activity_id";
    public static final String ID = "id";
    public int id = -1;
    public String filename;
    public String error = "";
    public String status;
    public int activityID = -1;
// Example response
// {"id": 16486788,"external_id": "test.fit","error": null,"status": "Your activity is still being processed.","activity_id": null}

    /**
     *
     * @param s is derived from the Retrofit response string from a successful attempt to upload a file
     */
    public StravaSuccessResponse(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            String temp1 = obj.getString(ID);
            if (temp1 != null && !("").equals(temp1)) try {
                id = Integer.parseInt(temp1);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
            filename = obj.getString(EXTERNAL_ID);
            error = obj.getString(ERROR);
            status = obj.getString(STATUS);
            String temp = obj.getString(ACTIVITY_ID);
            if (temp != null && !("").equals(temp)) try {
                activityID = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
               // e.printStackTrace();
            }
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

}
