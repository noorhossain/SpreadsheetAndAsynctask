package first.learn.createdatabase;

import static first.learn.createdatabase.Common.CAMERA_PERMISSION_CODE;
import static first.learn.createdatabase.Common.commonDir;
import static first.learn.createdatabase.Common.datePicker;
import static first.learn.createdatabase.Common.getDateAndTime;
import static first.learn.createdatabase.Common.getDateTimeAndData;
import static first.learn.createdatabase.Common.getResizedBitmap;
import static first.learn.createdatabase.Common.imageViewToBitmap;
import static first.learn.createdatabase.Common.resumingAfterModify;
import static first.learn.createdatabase.Common.resumingFromInsertData;
import static first.learn.createdatabase.Common.resumingListPositionAfterModify;
import static first.learn.createdatabase.Common.showTimePicker;
import static first.learn.createdatabase.Common.bitmapToString;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertOrModifyFile extends AppCompatActivity {


    EditText mainEditText,
    mainEditId;

    Context mContext ;

    Button btnSaveToFile, btnDeleteData,
    btnUpdateData ;

    String fileName ;

    Button  btnAddPhoto, btnDeletePhoto, btnAddDate, btnAddTime;

    String id, mainText;
    int listPosition = -1 ;

    long member_id = -1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_or_modify_file);

        mContext = this ;

        mainEditText = (EditText)findViewById(R.id.mainEditText);
        mainEditId = (EditText) findViewById(R.id.mainEditId);

        btnSaveToFile = (Button) findViewById(R.id.btnSaveToFile);
        btnDeleteData = (Button)findViewById(R.id.btnDeleteData);
        btnUpdateData = (Button)findViewById(R.id.btnUpdateData);

        imageViewUserImage = (ImageView) findViewById(R.id.imageViewUserImage);
        imageViewUserImage.setVisibility(View.GONE);
        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
        btnAddDate = (Button) findViewById(R.id.btnAddDate);
        btnAddTime = (Button) findViewById(R.id.btnAddTime);
        btnDeletePhoto = (Button) findViewById(R.id.btnDeletePhoto);

        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);



        if (getIntent() != null) {


            fileName = getIntent().getStringExtra("DarsFileName");

            try {
                dbcon = new SQLControllerDarsul(mContext, fileName);
                dbcon.open();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            if(getIntent().getStringExtra("ForInsertData")!= null){
                btnDeleteData.setVisibility(View.GONE);
                btnUpdateData.setVisibility(View.GONE);
            }else {
                btnSaveToFile.setVisibility(View.GONE);
            }


            if(getIntent().getStringExtra("forModifyData")!= null){

                id = getIntent().getStringExtra("ID");
                mainText = getIntent().getStringExtra("mainText");
                listPosition = getIntent().getIntExtra("listPosition", -1);

                mainEditText.setText(mainText);
                mainEditId.setText(id);
                try {
                    member_id = Long.parseLong(id);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }


                if( getIntent().getStringExtra("imgString")!=null && Common.DARS_FILE_PHOTO_BITMAP!=null){
                    imageViewUserImage.setImageBitmap(Common.DARS_FILE_PHOTO_BITMAP);
                    imageViewUserImage.setVisibility(View.VISIBLE);
                }else {
                    imageViewUserImage.setVisibility(View.GONE);
                }


            }



        }



        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewUserImage.setVisibility(View.GONE);
                imageViewUserImage.setImageBitmap(null);
            }
        });


        btnAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker(mContext, btnAddDate, false);


            }
        });


        btnAddDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s != null && !TextUtils.isEmpty(s)) {
                    DATE = s.toString();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePicker(mContext, btnAddTime, new Interface_On_OK_Pressed() {
                    @Override
                    public void onTextEntered(String text) {

                        TIME = text;

                    }
                });

            }
        });




        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preparePhoto();

            }
        });






        btnSaveToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = mainEditText.getText().toString();



                String filePath = dbcon.getFilePath().toString();

                if (dbcon.insertDataWithPhoto(getDateTimeAndData(DATE, TIME, data), RECEIPT_PHOTO)) {

                    Toast.makeText(mContext, "Alhamdulillah, Saved Success.", Toast.LENGTH_SHORT).show();


                    try {
                        dbcon.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    if(getIntent().getStringExtra("FromOpenedFile")!=null){

                        resumingFromInsertData = true ;
                        finish();

                    }else {

                        Intent home_intent = new Intent(getApplicationContext(),
                                DarsFileRead.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        home_intent.putExtra("DarsFileName", fileName);

                        overridePendingTransition(0, 0);
                        startActivity(home_intent);
                        overridePendingTransition(0, 0);

                        finish();
                    }




                }
            }
        });



        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = null;
                if (imageViewUserImage.getVisibility() == View.VISIBLE) {

                    try {
                        bitmap = imageViewToBitmap(imageViewUserImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String imgString = "";

                if (bitmap != null) {
                    try {
                        imgString = bitmapToString(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                String updatedMainText = mainEditText.getText().toString();

                if (!TextUtils.isEmpty(DATE)) {
                    if (updatedMainText.startsWith("তারিখ:")) {
                        //  updatedMainText = updatedMainText.replaceAll("^তারিখ.*?(\\d+:\\d+|am|pm)", "").trim();
                        updatedMainText = updatedMainText.replaceAll("^তারিখ.*?(\\n)", "").trim();

                    }
                    updatedMainText = getDateAndTime(DATE, TIME) + updatedMainText;
                }

                dbcon.updateDataWithImage(member_id, updatedMainText, imgString);

                Toast.makeText(mContext, "Alhamdulillah, Updating Success.", Toast.LENGTH_SHORT).show();
                resumingFromModify ();

            }
        });

        btnDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });



    }

    void  resumingFromModify (){
        resumingAfterModify = true ;
        resumingListPositionAfterModify = listPosition ;
        finish();

    }



    private void deleteData() {
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
        adb.setTitle("Confirm Delete");
        adb.setMessage("Do you really delete the data? This will not be undone.");
        adb.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                AlertDialog.Builder innerAdb = new AlertDialog.Builder(mContext);
                innerAdb.setTitle("Re-Confirm Delete");
                innerAdb.setMessage("Do you really delete the data anyway? Re-confirm delete.");
                innerAdb.setNeutralButton("Delete Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbcon.deleteData(member_id);

                        try {
                            dbcon.close();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        Toast.makeText(InsertOrModifyFile.this, "Delete One Data!", Toast.LENGTH_SHORT).show();

                        resumingFromModify ();

                    }
                });

                innerAdb.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });
                AlertDialog innerAlert = innerAdb.create();
                innerAlert.show();
            }
        });

        adb.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = adb.create();
        alertDialog.show();
    }


    private void preparePhoto() {
        new android.app.AlertDialog.Builder(mContext).setItems(new CharSequence[]{"From Camera", "From Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 1) {
                    showFileChooser();
                }
                if (i == 0) {
                    if (ContextCompat.checkSelfPermission(InsertOrModifyFile.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(InsertOrModifyFile.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    } else {
                        captureFromCamera();

                    }
                }
            }
        }).create().show();
    }


    String DATE = "";
    String TIME = "";
    SQLControllerDarsul dbcon;
    String RECEIPT_PHOTO = "" ;

    private final int PICK_IMAGE_REQUEST = 1;
    Bitmap rbitmap;
    private ImageView imageViewUserImage;


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private int CAMEARA_IMAGE_REQUEST = 2;
    private void captureFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, CAMEARA_IMAGE_REQUEST);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String cameraFilePath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(commonDir(), "Camera");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, /* prefix */ ".jpg", /* suffix */ storageDir /* directory */);
        image.deleteOnExit();
        cameraFilePath = "file://" + image.getAbsolutePath();

        return image;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == CAMEARA_IMAGE_REQUEST) {


            rbitmap = getResizedBitmap(mContext, Uri.parse(cameraFilePath));
            RECEIPT_PHOTO = bitmapToString(rbitmap);
            imageViewUserImage.setImageBitmap(rbitmap);
            imageViewUserImage.setVisibility(View.VISIBLE);


            System.out.println("Camera Photo Added :  Null");
            if (data != null)
                System.out.println("Camera Photo Added :  data not null");

            if (data != null && data.getData() != null)
                System.out.println("Camera Photo Added :  get data not null");
            else
                System.out.println("Camera Photo Added :  get data  null");

        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                //Getting the Bitmap from Gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //  rbitmap = getResizedBitmap(bitmap,250);//Setting the Bitmap to ImageView
                rbitmap = getResizedBitmap(mContext, uri);
                RECEIPT_PHOTO = bitmapToString(rbitmap);
                imageViewUserImage.setImageBitmap(rbitmap);
                imageViewUserImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }







}