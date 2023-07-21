package first.learn.createdatabase;

import static first.learn.createdatabase.Common.stringToBitmapImage;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AdapterFileContent extends ArrayAdapter <FileContentModel> {

//////////// This is Test Of File Changes //////////

    Context mContext ;
    ArrayList<FileContentModel> modelArrayList ;
    String databaseName ;


    public AdapterFileContent(@NonNull Context context, int resource, @NonNull ArrayList<FileContentModel> objects, String databaseName) {
        super(context, resource, objects);

        this.mContext = context;
        this.modelArrayList = objects ;
        this.databaseName = databaseName ;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view ;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.file_content_item, null );
        } else  {
            view = convertView ;
        }

        TextView member_id = (TextView) view. findViewById(R.id.member_id);
        TextView member_name = (TextView) view.findViewById(R.id.member_name);
        ImageView imgEdt = (ImageView) view.findViewById(R.id.imgEdt);
        ImageView imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);



        FileContentModel model = modelArrayList.get(position);

        String id = model.getId();
        String mainText = model.getName();


        member_id.setText(id);
        member_name.setText(mainText);



        String imgString = model.getImage();

        Bitmap parcelBitmap = null;
        Bitmap bitmap = null;

        if (imgString != null && !TextUtils.isEmpty(imgString)) {
            try {
                bitmap = stringToBitmapImage(imgString);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean isPhotoSet = false;
        if (bitmap != null) {
            try {
                imgPhoto.setImageBitmap(bitmap);
                isPhotoSet = true;
                parcelBitmap = bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        if (isPhotoSet) {
            imgPhoto.setVisibility(View.VISIBLE);
        } else {
           imgPhoto.setVisibility(View.GONE);
        }

        Bitmap finalParcelBitmap = parcelBitmap;
        imgEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOptions(position, id, mainText, finalParcelBitmap);
            }
        });


        return view ;
    }




    private void saveOptions(final int position, String id, String mainText, Bitmap parcelBitmap) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.ColorTextDialog);
        alertDialog.setTitle("What Action ? ");
        //alertDialog.setMessage("Please select Anyone");
        alertDialog.setItems(new CharSequence[]{"Edit/Delete", "Copy",
                "Share"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                switch (which) {

                    case 0:
                        editText(position, id, mainText, parcelBitmap);
                        break;
                    case 1:
                        copyText(mainText);
                        break;
                    case 2:
                        share(mainText);
                        break;
                    default:
                        break;

                }
            }
        });

        alertDialog.create().show();
    }


    private void editText(int position, String id, String mainText, Bitmap parcelBitmap) {

        Intent modify_intent = new Intent(mContext,
                InsertOrModifyFile.class);
        modify_intent.putExtra("mainText", mainText);
        modify_intent.putExtra("ID", id);
        modify_intent.putExtra("listPosition", position);
        modify_intent.putExtra("DarsFileName", databaseName);
        modify_intent.putExtra("forModifyData", "forModifyData");

        if (parcelBitmap != null) {
            Common.DARS_FILE_PHOTO_BITMAP = parcelBitmap;
            modify_intent.putExtra("imgString", "imgString");
        }else  {
            Common.DARS_FILE_PHOTO_BITMAP = null;
        }

        mContext.startActivity(modify_intent);

    }



    private void share(String memberName_val) {

        copyText(memberName_val);

        // String txtString = memName_tv.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, memberName_val);
        mContext.startActivity(Intent.createChooser(intent, "Share To : "));
    }


    private void copyText(String memberName_val) {

        // String memberName_val = memName_tv.getText().toString();

        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("search.tafheem.noor Text Viewer", memberName_val);
        cm.setPrimaryClip(clip);

        Toast.makeText(mContext, "Copied", Toast.LENGTH_SHORT).show();


    }




}
