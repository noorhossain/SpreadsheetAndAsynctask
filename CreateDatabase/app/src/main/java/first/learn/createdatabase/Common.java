package first.learn.createdatabase;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Common {


    public static File commonDir() {
        File dir = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(_MyApplication.getContext().getExternalFilesDir(null) + "/tilawaat");
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

        System.out.println("Called 13: " + dir.toString());


        return dir;

    }




    public static void hideKeyboard(Context mContext, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

    }



    public static boolean isColumnExist(SQLiteDatabase alreadyOpenedDb, String tableName, String columnName)
    {
        boolean isExist = false;

        if(alreadyOpenedDb==null){
            return false;
        }

        Cursor cursor = null;
        try {
            cursor = alreadyOpenedDb.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (cursor.moveToFirst()) {
                do {
                    String currentColumn = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    if (currentColumn.equals(columnName)) {
                        isExist = true;
                    }
                } while (cursor.moveToNext());

            }
        }catch (Exception ex)
        {
            Log.e("TAG", "isColumnExist: "+ex.getMessage(),ex );
        }
        finally {
            if (cursor != null)
                cursor.close();
            alreadyOpenedDb.close();
        }
        return isExist;
    }


    public static boolean checkDbFileName(String fileName) {

        return !fileName.toLowerCase().endsWith(".db-journal".toLowerCase())
                && !fileName.toLowerCase().endsWith(".db-wal".toLowerCase())
                && !fileName.toLowerCase().endsWith(".db-shm".toLowerCase());
    }

    public  static ArrayList<String> darFilesStaticList ;

    public  static String [] darsFileStaticArray (ArrayList<String> darFilesStaticList ){
        String [] itemsArray = null ;

        if(darFilesStaticList!=null&&darFilesStaticList.size()>0){

            itemsArray = new String[darFilesStaticList.size()];
            itemsArray = darFilesStaticList.toArray(itemsArray);
        }

        return  itemsArray ;

    }

    public static Bitmap DARS_FILE_PHOTO_BITMAP = null;



    public  static Comparator<? super File> sortFilesAscendingOrder = new Comparator<File>(){

        public int compare(File file1, File file2) {

            if(file1.isDirectory()){
                if (file2.isDirectory()){
                    return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                }else{
                    return -1;
                }
            }else {
                if (file2.isDirectory()){
                    return 1;
                }else{
                    return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                }
            }

        }
    };





    public static String bitMaptoString(Bitmap image) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap stringToBitmapImage(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String getDateAndTime(String dateString, String timeString) {
        if (TextUtils.isEmpty(dateString)) {
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy-EEEE", Locale.US);
            dateString = date.format(new Date());
        }
        if (TextUtils.isEmpty(timeString)) {
            SimpleDateFormat time = new SimpleDateFormat("hh.mm a", Locale.US);
            timeString = time.format(new Date());
        }

        String dateAndTime = "তারিখ: " + dateString + "-" + timeString + "\\n" + "\\n";
        final String finalString = dateAndTime.replace("\\n", Objects.requireNonNull(System.getProperty("line.separator")))
                .replaceAll("<.*?>", "");

        return finalString;
    }


    public static String currentDate;

    public static void datePicker(Context mContext, EditText editText, boolean isShortDate) {
        getDateFromCalender(mContext, editText, null, null, isShortDate);
    }

    public static void datePicker(Context mContext, TextView textView, boolean isShortDate) {
        getDateFromCalender(mContext, null, textView, null, isShortDate);
    }

    public static void datePicker(Context mContext, Button button, boolean isShortDate) {
        getDateFromCalender(mContext, null, null, button, isShortDate);
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void getDateFromCalender(Context mContext, EditText editText, TextView textView, Button button, boolean isShortDate) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View cl = inflater.inflate(R.layout.date_picker_layout, null);

        CalendarView calendar;
        TextView date_view;

        calendar = (CalendarView) cl.findViewById(R.id.calendar);
        date_view = (TextView) cl.findViewById(R.id.date_view);

        Button btnThisDate;

        btnThisDate = (Button) cl.findViewById(R.id.btnThisDate);
        btnThisDate.setEnabled(false);

        btnThisDate.setAlpha(0.1f);

        String todaysFullDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.getDate());
        date_view.setText(todaysFullDate);

        builder.setView(cl);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();


        final String[] fullMonthName = {"January"};
        // Add Listener in calendar
        calendar
                .setOnDateChangeListener(
                        new CalendarView
                                .OnDateChangeListener() {
                            @Override

                            // In this Listener have one method
                            // and in this method we will
                            // get the value of DAYS, MONTH, YEARS
                            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE", Locale.US);
                                Date date = new Date(year, month, dayOfMonth - 1);
                                String dayOfWeek = simpledateformat.format(date);


                                // Add 1 in month because month
                                // index is start with 0

                                String thisDate;
                                if (isShortDate) {
                                    String yearString = String.valueOf(year);
                                    try {
                                        yearString = yearString.substring(2, 4);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    thisDate = dayOfMonth + "-"
                                            + (month + 1) + "-" + yearString;
                                } else {
                                    thisDate = dayOfMonth + "-"
                                            + (month + 1) + "-" + year + "-" + dayOfWeek;
                                }


                                // set this date in TextView for Display
                                date_view.setText(thisDate);
                                currentDate = thisDate;


                                btnThisDate.setEnabled(true);


                                btnThisDate.setAlpha(1f);


                            }
                        });

        btnThisDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String date = date_view.getText().toString();

                if (TextUtils.isEmpty(date)) {
                    date_view.setError("Empty");
                } else {

                    date_view.setError("Null");


                    if (button != null) {
                        button.setText(date);
                    }

                    if (textView != null) {
                        textView.setText(date);
                    }

                    if (editText != null) {
                        editText.setText(date);
                    }

                    dialog.dismiss();
                }

            }
        });


        dialog.show();


    }


    public static final int CAMERA_PERMISSION_CODE = 100;


    public static void showTimePicker(Context mContext, Button btnAddTime, final Interface_On_OK_Pressed onOK) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.timepicker_header, null);
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog builder = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {


                        btnAddTime.setText(getFormatedTime(hour, min));
                        onOK.onTextEntered(getFormatedTime(hour, min));


                        // TIME = getFormatedTime(hour, min);


                    }
                }, hour, minute, false);

        //builder.setCustomTitle(view);
        builder.show();

    }


    public static String getFormatedTime(int h, int m) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm a";

        String oldDateString = h + ":" + m;
        String newDateString = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, getCurrentLocale());
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            assert d != null;
            newDateString = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDateString;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return _MyApplication.getContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return _MyApplication.getContext().getResources().getConfiguration().locale;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap  getResizedBitmap(Context mContext, Uri imageUri) {

        String filePath = getPathFromUri(mContext, imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        assert scaledBitmap != null;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            assert filePath != null;
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  scaledBitmap ;



    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + split[1];
                    else
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /////////////////////////////////////////////////////////////



    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }



    public static String bitmapToString(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }


    public static Bitmap imageViewToBitmap(ImageView imageView) {

        Bitmap bm = null;

        try {
            bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }


    public  static boolean resumingAfterModify = false ;
    public static  int resumingListPositionAfterModify = -1 ;

    public  static boolean resumingFromInsertData = false ;

}
