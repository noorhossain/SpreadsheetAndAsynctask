package read.data.readspreadsheet;

import android.util.Log;

import androidx.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Common {

    public  static  final String SPREAD_SHEET_URL = "https://script.google.com/macros/s/AKfycbxCj-v_sXwNK5lQi9bEzuEhemNXMqN9OybgNhZsJpwmEr5uk-REE4NufAssD06ndi3PfQ/exec";

    private static Response response;

    public static JSONObject insertAndDataToSpreadsheet(String url, String id, String name, String address, String age) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url+"?action=insert&id="+id+"&name="+name+"&address="+address+"&age="+age)
                    .build();
            response = client.newCall(request).execute();
            //    Log.e(TAG,"response from gs"+response.body().string());
            return new JSONObject(response.body().string());

        } catch (@NonNull IOException | JSONException e) {
            System.out.println("recieving null " + e.getLocalizedMessage());
        }
        return null;
    }




}
