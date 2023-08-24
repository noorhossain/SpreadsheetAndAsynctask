package first.learn.understandinginterface;

import static first.learn.understandinginterface.Common.showColorChangeAlert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity {

    TextView txtOne ;
    Button btnChangeColor ;

    Context mCotext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        setTitle("Third Activity");
        mCotext = ThirdActivity.this ;

        txtOne = (TextView) findViewById(R.id.txtOne);
        btnChangeColor = (Button) findViewById(R.id.btnChangeColor);

        btnChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showColorChangeAlert(mCotext, new MyChoice() {
                    @Override
                    public void on_ok_pressed(String s, int n) {

                        btnChangeColor.setTextColor(n);
                    }

                    @Override
                    public void on_test(String s) {

                    }

                    @Override
                    public void onClickMyChoice(int i) {

                        txtOne.setTextColor(i);

                    }
                });
            }
        });




    }
}