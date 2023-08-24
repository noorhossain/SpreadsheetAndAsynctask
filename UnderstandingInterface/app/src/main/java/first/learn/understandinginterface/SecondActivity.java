package first.learn.understandinginterface;

import static first.learn.understandinginterface.MainActivity.initializeMychoice;
import static first.learn.understandinginterface.MainActivity.myChoice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    Button btnPress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        setTitle("Second Activity");

        btnPress = (Button) findViewById(R.id.btnPress);
        btnPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myChoice!=null){
                    myChoice.onClickMyChoice(Color.BLUE);
                    initializeMychoice(myChoice);
                }

            }
        });


    }





}