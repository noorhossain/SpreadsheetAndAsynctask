package first.learn.cameraproject;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.File;

public class Common {

    public static File commonDir(Context mContext) {
        File dir = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/tilawaat");
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/tilawaat");
        }

        try {
            if (!dir.exists()) {
                // Make it, if it doesn't exit
                boolean success = false;
                try {
                    success = dir.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!success) {
                    dir = null;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dir!=null) {
            System.out.println("Called 13: " + dir.toString());
        }else {
            System.out.println("Called 13: " + "Common Dir is Null ");

        }


        return dir;

    }

}
