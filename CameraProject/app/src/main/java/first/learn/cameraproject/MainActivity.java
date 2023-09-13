package first.learn.cameraproject;

import static first.learn.cameraproject.Common.bitmapToString;
import static first.learn.cameraproject.Common.commonDir;
import static first.learn.cameraproject.Common.getResizedBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

   public static final int CAMERA_PERMISSION_CODE = 11 ;
    Context mContext ;
    Button btnTakePhoto;
    ImageView imageView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        imageView = (ImageView)findViewById(R.id.ImageView);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ////

                CharSequence [] items = {"Take From Camera", "Take From Gallery"};

                AlertDialog.Builder ad = new AlertDialog.Builder(mContext);

                ad.setPositiveButton("Cancel", null);
                ad.setTitle("Choose Content:");

                ad.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if(position==0){
                            // Capture From Camera
                            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                            } else {
                              //  captureFromCamera();
                                System.out.println("Camera Permission Granted, Now Do your work ");
                                captureFromCamera(mContext);

                            }



                        }

                        if(position==1){
                            // Capture From Gallery ;
                        }

                    }
                });

                AlertDialog dialog = ad.create();
                dialog.show();



            }
        });



        if(!checkAndRequestPermissions()){
            checkAndRequestPermissions();
        }else {
            ReadDataBase();
        }



    }

    private int CAMEARA_IMAGE_REQUEST = 2;
    private void captureFromCamera(Context context) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", createImageFile(context)));
            startActivityForResult(intent, CAMEARA_IMAGE_REQUEST);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    Bitmap rbitmap ;
    String imageString ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        System.out.println("Request Code : "+ requestCode);
        System.out.println("resultCode : "+ resultCode);




        if(requestCode==CAMEARA_IMAGE_REQUEST && resultCode == RESULT_OK){


                rbitmap = getResizedBitmap(mContext, Uri.parse(cameraFilePath));
                imageView.setImageBitmap(rbitmap);
                imageString = bitmapToString(rbitmap);
                System.out.println("Image String : "+ imageString);

        }


    }

    String cameraFilePath = null;

    private File createImageFile(Context context) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(commonDir(context), "Camera");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFilePath = File.createTempFile(imageFileName, /* prefix */ ".jpg", /* suffix */ storageDir /* directory */);

        try {
            imageFilePath.deleteOnExit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cameraFilePath = "file://" + imageFilePath.getAbsolutePath();

        return imageFilePath;
    }





    void  ReadDataBase (){

    }


 ///////////////Permissions Code ///////////////



    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    private boolean checkAndRequestPermissions() {


        int permissionReadExternalStorage;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissionReadExternalStorage = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_IMAGES);
        else
            permissionReadExternalStorage = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);


        int permissionWriteExtarnalStorage;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissionWriteExtarnalStorage = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_AUDIO);
        else
            permissionWriteExtarnalStorage = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();


        if (permissionWriteExtarnalStorage != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                listPermissionsNeeded.add(android.Manifest.permission.READ_MEDIA_AUDIO);
            else
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }

        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                listPermissionsNeeded.add(android.Manifest.permission.READ_MEDIA_IMAGES);
            else
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            int permissionVideoStorage = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_VIDEO);

            if (permissionVideoStorage != PackageManager.PERMISSION_GRANTED) {

                listPermissionsNeeded.add(android.Manifest.permission.READ_MEDIA_VIDEO);

            }

            int notificationPermission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS);

            if (notificationPermission != PackageManager.PERMISSION_GRANTED) {

                listPermissionsNeeded.add(android.Manifest.permission.POST_NOTIFICATIONS);

            }

        }



        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        if(cameraPermission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }


        if(listPermissionsNeeded.size()>0){
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

//        if (!listPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
//            return false;
//        }

        return true;
    }




    @SuppressWarnings("ConstantConditions")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            if( requestCode == CAMERA_PERMISSION_CODE){


                    if(grantResults.length>0){
                        if(grantResults[0] == RESULT_OK){
                            System.out.println("Camera Permission Granted, Now Do your work ");
                        }else {
                            Toast.makeText(mContext, "Camera Permission Not Granted ", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(mContext, "Camera Permission Not Granted ", Toast.LENGTH_SHORT).show();
                    }


            }


        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    perms.put(android.Manifest.permission.READ_MEDIA_IMAGES, PackageManager.PERMISSION_GRANTED);
                    perms.put(android.Manifest.permission.READ_MEDIA_AUDIO, PackageManager.PERMISSION_GRANTED);
                    perms.put(android.Manifest.permission.READ_MEDIA_VIDEO, PackageManager.PERMISSION_GRANTED);
                    perms.put(android.Manifest.permission.POST_NOTIFICATIONS, PackageManager.PERMISSION_GRANTED);


                } else {
                    perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                    perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }

                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    if(grantResults[0]==RESULT_OK){
                        System.out.println("grantsults [0] : " + grantResults[0] );
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {


                        if (       perms.get(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                                && perms.get(android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
                                && perms.get(android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                                && perms.get(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                                && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

                        ) {
                            // All Permissions Are granted :
                            Toast.makeText(this, "Jajakumullah, For Granting Permission.", Toast.LENGTH_LONG).show();
                            ReadDataBase();


                            //else any one or all the permissions are not granted

                        } else {
                            if (       ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_VIDEO)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)

                            ) {
                                showDialogOK("Necessary Permissions required for this app",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkAndRequestPermissions();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        // proceed with logic by disabling the related features or quit the app.
                                                        Toast.makeText(MainActivity.this, "Necessary Permissions required for this app", Toast.LENGTH_LONG).show();
                                                        // permissionSettingScreen ( );
                                                        //  finish();
                                                        break;
                                                }
                                            }
                                        });
                            }
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
                            else {
                                permissionSettingScreen();

                            }
                        }


                    } else {


                        if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

                        ) {
                            Toast.makeText(this, "Jajakumullah, For Granting Permission.", Toast.LENGTH_LONG).show();
                            //else any one or both the permissions are not granted
                            ReadDataBase();

                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            ) {
                                showDialogOK("Necessary Permissions required for this app",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkAndRequestPermissions();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        // proceed with logic by disabling the related features or quit the app.
                                                        Toast.makeText(MainActivity.this, "Necessary Permissions required for this app", Toast.LENGTH_LONG).show();
                                                        // permissionSettingScreen ( );
                                                        //  finish();
                                                        break;
                                                }
                                            }
                                        });
                            }
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
                            else {
                                permissionSettingScreen();

                            }
                        }


                    }


                }

            }

            break;


        }

    }

    private void permissionSettingScreen() {
        Toast.makeText(this, "Enable All permissions, Click On Permission", Toast.LENGTH_LONG)
                .show();

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        // finishAffinity();
        finish();

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }







}