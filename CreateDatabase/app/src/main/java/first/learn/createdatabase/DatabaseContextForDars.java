package first.learn.createdatabase;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.Objects;

public class DatabaseContextForDars extends ContextWrapper {



    private  static  final String DEBUG_CONTEXT = "DatabaseContext";
    public DatabaseContextForDars(Context base) {
        super(base);
    }

    String TAG ="DatabaseContextWrapper";

    @Override
    public File getDatabasePath(String name) {

        String dbfile = name.trim();
        if(!dbfile.endsWith(".db")){
            dbfile += ".db";
        }
        File result = new File(dbfile);
        Log.i(TAG, "Report S_Controller: called_5 contextWrapper : dbname "+dbfile+"  getDatabasePath("+name+")="+result.getAbsolutePath());

        if(!Objects.requireNonNull(result.getParentFile()).exists()){
            result.getParentFile().mkdirs();
        }

        if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)){
            Log.w(DEBUG_CONTEXT, "getDatabasePath("+name+")="+result.getAbsolutePath());
        }
        Log.i(TAG, "dbNamecontains: DatabaseContextWrapper 5 "+name);

        Log.i(TAG, "Report S_Controller: called_5 contextWrapper : dbname "+dbfile+"  getDatabasePath("+name+")="+result.getAbsolutePath());

        //return super.getDatabasePath(name);
        return  result;
    }


    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {

        try {
            return super.openOrCreateDatabase(name, mode, factory);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {

        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);

        if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)){
            Log.w(DEBUG_CONTEXT, "openOrCreateDatabse :: ("+name +", ,)="+result.getPath());
        }
        return result;
    }
}
