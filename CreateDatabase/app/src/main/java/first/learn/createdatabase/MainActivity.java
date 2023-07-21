package first.learn.createdatabase;

import static first.learn.createdatabase.Common.checkDbFileName;
import static first.learn.createdatabase.Common.commonDir;
import static first.learn.createdatabase.Common.darFilesStaticList;
import static first.learn.createdatabase.Common.hideKeyboard;
import static first.learn.createdatabase.Common.sortFilesAscendingOrder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    Button btnCreateNewDarsDb;
    Context mContext ;

    TextView empty_text;
    ListView fileListView ;
    EditText editText ;
    ImageView imgClearEdtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext = this;

        btnCreateNewDarsDb = (Button) findViewById(R.id.btnCreateNewDarsDb);
        empty_text = (TextView)findViewById(R.id.empty_text);
        fileListView = (ListView)findViewById(R.id.fileListView);
        editText = (EditText)findViewById(R.id.inputSearch1);

        btnCreateNewDarsDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewDb ();
            }
        });



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mainAdapter != null) {
                    mainAdapter.getFilter().filter(charSequence, new Filter.FilterListener() {
                        public void onFilterComplete(int count) {

                            if (fileListView != null) {
                                fileListView.setSelection(0);
                            }
                        }
                    });
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mainAdapter != null) {
                    mainAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {

                if (mainAdapter != null) {
                    mainAdapter.getFilter().filter(charSequence, new Filter.FilterListener() {
                        public void onFilterComplete(int count) {

                            if (fileListView != null) {
                                fileListView.setSelection(0);
                            }
                        }
                    });
                }
            }
        });

        imgClearEdtText = (ImageView) findViewById(R.id.imgClearEdtText);
        imgClearEdtText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText(null);
            }
        });




        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

//                TextView txtview = (TextView) view.findViewById(R.id.textview_item);
//                String s = txtview.getText().toString();


                if (filesNamesListPure != null && filesNamesListPure.size() > 0) {

                    int positionAfterFilter = (int) mainAdapter.getItemId(i);

                    String darsFileName = filesNamesListPure.get(positionAfterFilter);

                    String filePath = filePathList.get(positionAfterFilter);

                    CharSequence [] items = {"Open File", "Add Writing", "Delete File"};

                    new AlertDialog.Builder(mContext).setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(which==0){
                                Intent intent = new Intent(MainActivity.this, DarsFileRead.class);
                                intent.putExtra("DarsFileName", darsFileName);
                                startActivity(intent);
                            }

                            if(which == 1){

                                Intent intent = new Intent(MainActivity.this, InsertOrModifyFile.class);
                                intent.putExtra("DarsFileName", darsFileName);
                                intent.putExtra("ForInsertData", "ForInsertData");
                                startActivity(intent);

                            }

                            if(which == 2){

                                new File(filePath).delete();
                                makeFileListInListView();
                            }


                        }
                    }).create().show();


//                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
//                    builder.setTitle(darsFileName.replace(separatorSixHash, ""));
//                    setBuilderBody(builder, darsFileName, positionAfterFilter);

                }

            }
        });






        if(!checkAndRequestPermissions()){
            checkAndRequestPermissions();
            return;
        }


        makeFileListInListView();


    }

    void  createNewDb (){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setTitle("নতুন দারস/ফাইল তৈরী করুন: ");

        final View cl = getLayoutInflater().inflate(R.layout.create_new_dars_layout, null);

        final EditText edtFileName = (EditText) cl.findViewById(R.id.edtFileName);
        final Button btnCancel = (Button) cl.findViewById(R.id.btnCancel);
        Button btnCreate = (Button) cl.findViewById(R.id.btnCreate);



        final TextView txtError = (TextView) cl.findViewById(R.id.txtError);


        builder.setView(cl);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtError.setText("");

                String fileName = edtFileName.getText().toString().trim();

                File dir = commonDir();
                File subDir = new File(dir, FILE_TOIRY_FOLDER_NAME_STRING);
                File alreadyloaded = new File(subDir + "/" + fileName + ".db");
                File alreadyloaded2 = new File(subDir + "/" + fileName);

                if (TextUtils.isEmpty(fileName)) {

                    edtFileName.setError("Empty");
                } else {
                    edtFileName.setError(null);

                    if (alreadyloaded.exists() || alreadyloaded2.exists()) {

                        txtError.setText("একই নামের একটি ফাইল রয়েছে, অন্য নাম চেষ্টা করুন  - ");

                    } else {

                        hideKeyboard(mContext, view);

                       createNewDarsDatabase(fileName);
                        txtError.setText("আলহামদুলিল্লাহ   " + fileName + "    নামক দারস / ফাইলটি তৈরী হয়েছে।  OK বাটনে ক্লিক করুন। ");
                        btnCancel.setText("OK");

                        dialog.dismiss();
                    }
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        
        dialog.show();


    }



    SQLControllerDarsul dbcon;
    String newCreatedFile = null;

    AdapterFilesCreate mainAdapter;
    ArrayList<String> fileList;

    String[] fileNamesArray;

    private void createNewDarsDatabase(String fileName) {
        dbcon = new SQLControllerDarsul(this, fileName);
        dbcon.open();
        newCreatedFile = fileName;
        refreshListAfterNewCreated(fileName);
    }

    private void refreshListAfterNewCreated(String JUST_CREATED_FILE) {

        newCreatedFile = JUST_CREATED_FILE;

        if (fileListView != null) {
            fileListView.setAdapter(null);
            makeFileListInListView();
        }
        if (fileListView != null && filesNamesListPure != null && filesNamesListPure.size() > 0) {
            int p = 0;

            try {
                p = filesNamesListPure.indexOf(JUST_CREATED_FILE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            fileListView.setSelection(p);
        }

        if (editText != null && !TextUtils.isEmpty(editText.getText().toString())) {

            CharSequence charSequence = editText.getText().toString();

            if (mainAdapter != null && fileListView != null && mainAdapter.getCount() > 0) {
                mainAdapter.getFilter().filter(charSequence, new Filter.FilterListener() {
                    public void onFilterComplete(int count) {

                        fileListView.setSelection(0);
                    }
                });
            }
        }

        //  openFile(JUST_CREATED_FILE);

    }


    String FILE_TOIRY_FOLDER_NAME_STRING = "Darsul_Quran";


    public void makeFileListInListView() {


        File dir = commonDir();
        if (!dir.exists())
            dir.mkdir();
        File subDir = new File(dir + "/" + FILE_TOIRY_FOLDER_NAME_STRING);


        if (!subDir.exists())
            subDir.mkdir();


        fileList = GetFilesList(subDir.toString());

        fileListView.setAdapter(null);
        if (fileList != null && fileList.size() > 0) {
            //   fileAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileListArray);

            fileNamesArray = new String[fileList.size()];
            fileNamesArray = fileList.toArray(fileNamesArray);

            mainAdapter = new AdapterFilesCreate(mContext, R.layout.sahitto_dropdown_layout, R.id.textview_item, fileList, newCreatedFile);


            fileListView.setAdapter(mainAdapter);


            empty_text.setVisibility(View.GONE);
        } else {

            empty_text.setVisibility(View.VISIBLE);
        }
    }

    ArrayList<String> filesNamesListPure;
    ArrayList<String> filePathList;

    public ArrayList<String> GetFilesList(String DirectoryPath) {

        darFilesStaticList = new ArrayList<>();
        filesNamesListPure = new ArrayList<String>();
        filePathList = new ArrayList<>();
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();

        File[] files = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {

                return checkDbFileName(name);

            }
        });

        int fileNumber = 1;

        assert files != null;
        if (files.length == 0)
            return null;

        else {

            Arrays.sort(files, sortFilesAscendingOrder);

            for (int i = 0; i < files.length; i++) {

                String fileName = files[i].getName();

                if (checkDbFileName(fileName)) {
                    String s = files[i].getName();
                    String filePath = files[i].getPath();


                        if (checkDbFileName(s)) {
                            s = s.replace(".db", "");
                            MyFiles.add(fileNumber + ".      " + s.trim()); // for extra security
                            filesNamesListPure.add(s.trim()); //  for extra  security
                            darFilesStaticList.add(s.trim()); // for alertDialog items ;
                            filePathList.add(filePath);
                            fileNumber++;
                        }

                }

            }
        }


        return MyFiles;
    }



////////////////////////////////////////////////////////////////////////////////////////


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    private boolean checkAndRequestPermissions() {


        int permissionReadExternalStorage;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissionReadExternalStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES);
        else
            permissionReadExternalStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);


        int permissionWriteExtarnalStorage;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissionWriteExtarnalStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_AUDIO);
        else
            permissionWriteExtarnalStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();


        if (permissionWriteExtarnalStorage != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_AUDIO);
            else
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }

        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
            else
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            int permissionVideoStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_VIDEO);
            if (permissionVideoStorage != PackageManager.PERMISSION_GRANTED) {

                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_VIDEO);

            }

            int notificationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS);

            if (notificationPermission != PackageManager.PERMISSION_GRANTED) {

                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);

            }

        }

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if(cameraPermission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }



        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    perms.put(Manifest.permission.READ_MEDIA_IMAGES, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.READ_MEDIA_AUDIO, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.READ_MEDIA_VIDEO, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.POST_NOTIFICATIONS, PackageManager.PERMISSION_GRANTED);


                } else {
                    perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {


                        if (perms.get(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

                        ) {
                            // All Permissions Are granted :
                            Toast.makeText(this, "Jajakumullah, For Granting Permission.", Toast.LENGTH_LONG).show();
                            makeFileListInListView();
                            //else any one or both the permissions are not granted
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_VIDEO)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)
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


                    } else {


                        if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

                        ) {
                            Toast.makeText(this, "Jajakumullah, For Granting Permission.", Toast.LENGTH_LONG).show();
                            //else any one or both the permissions are not granted
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
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