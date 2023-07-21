package first.learn.createdatabase;

import static first.learn.createdatabase.Common.commonDir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;

public class SQLControllerDarsul {


    private DBhelperDarsul dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;
    private String dbName;

    public SQLControllerDarsul(Context c, String DbName) {
        ourcontext = c;
        dbName = DbName;
    }

    public SQLControllerDarsul open() throws SQLException {

        File sdcard = commonDir();
        String dbfile = sdcard.toString() + "/Darsul_Quran/" + dbName.trim();

        String fileName = dbName.trim();

        System.out.println("Check s: dbfile : " +dbfile);

        dbhelper = new DBhelperDarsul(ourcontext, dbfile);
        database = dbhelper.getWritableDatabase();
        filePath = dbfile.toString();
        return this;

    }



    private  String filePath = null ;
    public  File getFilePath (){

        if(filePath!=null){
            return  new File(filePath);
        }else {
            return  null ;
        }

    }



    public SQLiteDatabase getOpenedDatabase() {
        if (database != null) {
            return database;
        } else {
            return null;
        }
    }

    public void close() {
        dbhelper.close();
    }

//        public void insertData(String name) {
//            ContentValues cv = new ContentValues();
//            cv.put(DBhelperDarsul.MEMBER_NAME, name);
//            database.insert(DBhelperDarsul.TABLE_MEMBER, null, cv);
//        }

    public boolean insertDataWithPhoto(String name, String imgString) {
        ContentValues cv = new ContentValues();
        cv.put(DBhelperDarsul.MEMBER_NAME, name);
        cv.put(DBhelperDarsul.MEMBER_PHOTO, imgString);
        try {
            database.insert(DBhelperDarsul.TABLE_MEMBER, null, cv);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //FileReadModel

    public ArrayList<FileReadModel> fileReadList() {

        ArrayList<FileReadModel> fileReadList = new ArrayList<>();
        Cursor cursor = null;


        try {
            cursor = database.rawQuery("SELECT * FROM member", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            cursor.moveToFirst();
        }


        boolean isPhotoColumnExist = false;

        try {
            isPhotoColumnExist = Common.isColumnExist(database, DBhelperDarsul.TABLE_MEMBER, DBhelperDarsul.MEMBER_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (cursor != null && !cursor.isAfterLast()) {
            do {

                FileReadModel model = new FileReadModel();

                String id = cursor.getString(cursor.getColumnIndexOrThrow(DBhelperDarsul.MEMBER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBhelperDarsul.MEMBER_NAME));
                model.setName(name);

                String img = null;
                if (isPhotoColumnExist) {
                    try {
                        img = cursor.getString(cursor.getColumnIndexOrThrow(DBhelperDarsul.MEMBER_PHOTO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        model.setImgString(img);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                fileReadList.add(model);

            } while (cursor.moveToNext());
        }


        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return fileReadList;


    }

    public Cursor readData() {
        String[] allColumns = new String[]{DBhelperDarsul.MEMBER_ID,
                DBhelperDarsul.MEMBER_NAME};
        Cursor c = database.query(DBhelperDarsul.TABLE_MEMBER, allColumns, null,
                null, null, null, null);

        c = database.rawQuery("SELECT * FROM member", null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }



    public  ArrayList<FileContentModel> getFileContent (){

        ArrayList<FileContentModel> modelArrayList = new ArrayList<>();

        Cursor   cursor = database.rawQuery("SELECT * FROM member", null);




        if(cursor !=null ){

            cursor.moveToFirst() ;

            if (!cursor.isAfterLast()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(DBhelperDarsul.MEMBER_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DBhelperDarsul.MEMBER_NAME));
                    String  img = cursor.getString(cursor.getColumnIndexOrThrow(DBhelperDarsul.MEMBER_PHOTO));

                    FileContentModel model = new FileContentModel();
                    model.setId(id);
                    model.setName(name);
                    model.setImage(img);

                    modelArrayList.add(model);

                } while (cursor.moveToNext());
            }

            cursor.close();


        }


        return  modelArrayList;

    }



    public int updateData(long memberID, String memberName) {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(DBhelperDarsul.MEMBER_NAME, memberName);
        int i = database.update(DBhelperDarsul.TABLE_MEMBER, cvUpdate,
                DBhelperDarsul.MEMBER_ID + " = " + memberID, null);
        return i;
    }

    public int updateDataWithImage(long memberID, String memberName, String imgString) {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(DBhelperDarsul.MEMBER_PHOTO, imgString);
        cvUpdate.put(DBhelperDarsul.MEMBER_NAME, memberName);
        int i = database.update(DBhelperDarsul.TABLE_MEMBER, cvUpdate,
                DBhelperDarsul.MEMBER_ID + " = " + memberID, null);
        return i;
    }

    public void deleteData(long memberID) {
        database.delete(DBhelperDarsul.TABLE_MEMBER, DBhelperDarsul.MEMBER_ID + "="
                + memberID, null);
    }


}
