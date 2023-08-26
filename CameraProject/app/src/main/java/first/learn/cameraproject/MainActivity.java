package first.learn.cameraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    Context mContext ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;



        File f = Common.commonDir(mContext);

        File myCameraFile = new File(f, "MY_CAMERA_FILE");

        if(!myCameraFile.exists()){
            myCameraFile.mkdirs();
        }

        System.out.println("CameraFilePath : "+ myCameraFile.toString());


        File thirdDir = new File(myCameraFile, "Third_Dir");

        if(!thirdDir.exists()){
            thirdDir.mkdirs();
        }

        System.out.println("thirdDir Path : "+ thirdDir.toString());







    }
}