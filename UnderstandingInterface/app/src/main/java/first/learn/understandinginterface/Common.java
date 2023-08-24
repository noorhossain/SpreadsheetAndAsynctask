package first.learn.understandinginterface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

public class Common {

    public  static void   showColorChangeAlert (Context mContext, MyChoice myChoice){

//        new AlertDialog.Builder(mContext)
//                .setTitle("Change Color")
//                .setMessage("Click To Change Color")
//                .setPositiveButton("Magenta", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        myChoice.onClickMyChoice(Color.MAGENTA);
//                    }
//                })
//                .setNeutralButton("RED", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        myChoice.on_ok_pressed("Null", Color.RED);
//                    }
//                }).create().show();



        CharSequence [] items = new CharSequence[]{ "Red", "Magenta", "Yellow", "Blue"};

        new AlertDialog.Builder(mContext).setTitle("Select Color: ")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which==0){
                           myChoice.onClickMyChoice(Color.RED);
                        }
                        if(which==1){
                            myChoice.onClickMyChoice(Color.MAGENTA);
                        }

                        if (which==2){
                            myChoice.onClickMyChoice(Color.YELLOW);
                        }

                        if(which==3){
                            myChoice.onClickMyChoice(Color.BLUE);
                        }

                    }
                }).create().show();


    }




}
