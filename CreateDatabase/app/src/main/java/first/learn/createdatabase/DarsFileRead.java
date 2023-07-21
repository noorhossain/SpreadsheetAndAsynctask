package first.learn.createdatabase;

import static first.learn.createdatabase.Common.resumingAfterModify;
import static first.learn.createdatabase.Common.resumingFromInsertData;
import static first.learn.createdatabase.Common.resumingListPositionAfterModify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DarsFileRead extends AppCompatActivity {

    String fileName ;
    ListView listView2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dars_file_read);
        mContext = this ;
        listView2 = (ListView) findViewById(R.id.listView2);


        if (getIntent() != null) {

            if (getIntent().getStringExtra("DarsFileName") != null) {

                fileName = getIntent().getStringExtra("DarsFileName");
                setTitle(Html.fromHtml("<small>" + fileName + " :</small>"));

            }

        }

        try {
            openDatabase(fileName.trim());
        } catch (Exception e) {

            Toast.makeText(mContext, "Can't Open this Database, Table Not Matching", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }


        FloatingActionButton fabAddText = (FloatingActionButton) findViewById(R.id.fabAddText);
        fabAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DarsFileRead.this, InsertOrModifyFile.class);
                intent.putExtra("DarsFileName", fileName);
                intent.putExtra("ForInsertData", "ForInsertData");
                intent.putExtra("FromOpenedFile", "FromOpenedFile");

                startActivity(intent);
            }
        });


    }

    SQLControllerDarsul dbcon = null ;
    String dbFilePath = null;
    ArrayList<String> imgArray;

    ArrayList<String> idArray, nameArray;

    Cursor cursor;
    ListView mainListView;


    Context mContext;

    TextView txtFileName;
    TextView memID_tv, memName_tv;
    private void openDatabase(String fileName) {

        dbcon = new SQLControllerDarsul(this, fileName.trim());
        dbcon.open();

        dbFilePath = dbcon.getFilePath().toString();

        makeListView(fileName);
    }


    ArrayList<FileContentModel> modelArrayList ;
    AdapterFileContent adapterFileContent ;

    void  makeListView (String fileName){

        modelArrayList = new ArrayList<>();

        modelArrayList = dbcon.getFileContent();


        adapterFileContent = new AdapterFileContent(mContext, R.layout.file_content_item, modelArrayList, fileName);
        listView2.setAdapter(adapterFileContent);

        listView2.setSelection(adapterFileContent.getCount() - 1);




    }


    @Override
    protected void onResume() {
        super.onResume();

        if(resumingAfterModify){

            resumingAfterModify = false ;

            if(listView2!=null&&adapterFileContent!=null){
                listView2.setAdapter(null);
            }

            makeListView(fileName.trim());

            if(resumingListPositionAfterModify!=-1){
                listView2.setSelection(resumingListPositionAfterModify);
                resumingListPositionAfterModify = -1 ;
            }
        }

        if(resumingFromInsertData){
            resumingFromInsertData = false ;
            if(listView2!=null&&adapterFileContent!=null){
                listView2.setAdapter(null);
            }
            makeListView(fileName.trim());
        }

    }
}