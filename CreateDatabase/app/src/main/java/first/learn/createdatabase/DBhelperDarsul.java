package first.learn.createdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelperDarsul extends SQLiteOpenHelper {

    static final int DB_VERSION = 3;


    public  static final  String TABLE_STUDENT="Student_Table";
    public static  final  String STUDENT_NAME = "Student_Name";
    public  static  final  String STUDENT_AGE = "Student_Age";
    public  static  final  String STUDENT_ADDRESS = "Student_Address";
    public  static  final  String STUDENT_ID = "student_id";

    private static final String CREATE_STUDENT_TABLE = "create table "
            + TABLE_STUDENT + "("
            + STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STUDENT_AGE + " TEXT NOT NULL, "
            + STUDENT_NAME + " TEXT NOT NULL, "
            + STUDENT_ADDRESS + " TEXT NOT NULL);";


    // TABLE INFORMATTION
    public static final String TABLE_MEMBER = "member";
    public static final String MEMBER_ID = "_id";
    public static final String MEMBER_NAME = "name";
    public static final String MEMBER_PHOTO = "MEMBER_PHOTO";

    // TABLE CREATION STATEMENT
    private static final String CREATE_TABLE = "create table "
            + TABLE_MEMBER + "("
            + MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MEMBER_NAME + " TEXT NOT NULL, "
            + MEMBER_PHOTO + " TEXT NOT NULL);";


    public DBhelperDarsul(Context context, String dbName) {

        super(new DatabaseContextForDars(context), dbName, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        // new Version 3 :

        String sql = "ALTER TABLE " + TABLE_MEMBER + " ADD COLUMN " +
                "MEMBER_PHOTO" + " TEXT NOT NULL DEFAULT '' ";
        db.execSQL(sql);



    }
}
