package first.learn.createdatabase;

/**
 * Created by bismillah on 11/1/2017.
 */



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class AdapterFilesCreate extends ArrayAdapter<String> {
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final int mResource;
    private List<String> mObjects;
    private int mFieldId = 0;
    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;
    private final Object mLock = new Object();
    private String mSearchText; // this var for highlight



    private String newCreatedFile = null;

    private ArrayList<String> audioVideoUrlList = null;
    ArrayList<String> fileNameListPure;
    private boolean isAudioActivity = false;


    public AdapterFilesCreate(Context context, int resource, int textViewResourceId, ArrayList<String> objects, String newCreateFile) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        mObjects = objects;
        mFieldId = textViewResourceId;
        this.newCreatedFile = newCreateFile;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    public AdapterFilesCreate(Context context, int resource, int textViewResourceId, String[] objects,
                              ArrayList<String> fileNameListPure,
                              ArrayList<String> audioVideoUrlList, String newCreateFile, boolean isAudioActivity) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        mObjects = Arrays.asList(objects);
        mFieldId = textViewResourceId;
        this.newCreatedFile = newCreateFile;
        this.audioVideoUrlList = audioVideoUrlList;
        this.isAudioActivity = isAudioActivity;
        this.fileNameListPure = fileNameListPure;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    @NonNull
    @Override
    public Context getContext() {

        return mContext;
    }

    @Override
    public int getCount() {

        return mObjects.size();

    }

    @Override
    public String getItem(int position) {
        return mObjects.get(position);
    }


    @Override
    public int getPosition(String item) {
        return mObjects.indexOf(item);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    String TAG = "adapterWorldWide";

    @Override
    public long getItemId(int position) {

        int itemID;

        Log.i(TAG, "getItemId: 1 position: " + position);

        // mOriginalValues will be null only if we haven't filtered yet:
        if (mOriginalValues == null) {
            itemID = position;
            Log.i(TAG, "getItemId: 2 itemID: " + itemID + " text: " + mObjects.get(position));

        } else {
            itemID = mOriginalValues.indexOf(mObjects.get(position));
            Log.i(TAG, "getItemId: 3 itemID: " + itemID + " text: " + mObjects.get(position));

        }
        return itemID;
        // return super.getItemId(position);
    }

    @Override
    public void notifyDataSetChanged() {
        //  Note: if you ever need to use notifyDatasetChanged() it might be a good idea to override this method as well:
        // either this or: orig = dataSet;
        //  mOriginalValues = null;
        super.notifyDataSetChanged();
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            int j = 0;

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                mSearchText = "";
                ArrayList<String> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);

                }
                results.values = list;
                results.count = list.size();
            } else {
                //   String prefixString = prefix.toString().toLowerCase();
                String prefixString = String.valueOf(prefix); // prefix.toString();

                mSearchText = prefixString.toLowerCase(); // For Case Sensitive

                ArrayList<String> values;

                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                    //  replace(values);  // this is the system replace character to an array list elements

                }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i);
                    //  final String valueText = value.toLowerCase();
                    final String valueText = value.toLowerCase(); // For Case Sensitive


                    // First match against the whole, non-splitted value
                    if (valueText.toLowerCase().startsWith(prefixString.toLowerCase()) || valueText.toLowerCase().contains(prefixString.toLowerCase())) {
                        newValues.add(value);
                        j++;
                    } else {
                        final String[] word = valueText.split(" ");
                        final int wordCount = word.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {

                            if (word[k].toLowerCase().startsWith(prefixString.toLowerCase())) {
                                newValues.add(value);

                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
//                if (filterListeners != null)
//                    filterListeners.filteringFinished(j);

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

//            if (filterListeners != null)
//                filterListeners.filteringFinished(mObjects.size());

            //  setFilterListeners(filterListeners);
        }
    }


    public static CharSequence highlight(String search, String originalText) {

        int start = originalText.toLowerCase().indexOf(search.toLowerCase()); // For Case Sensitive
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());

                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                highlighted.setSpan(highlightSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = originalText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView text;


        if (convertView == null) {
            view = mInflater.inflate(R.layout.sahitto_dropdown_layout, null);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        if (convertView != null) {
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                    }
                    return false;
                }
            });
        }


        // HIGHLIGHT...

        String fullText = getItem(position);
        if (fullText != null) {
            if (mSearchText != null && !mSearchText.isEmpty()) {

                int startPos = fullText.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                // int startPos = fullText.indexOf(mSearchText);
                int endPos = startPos + mSearchText.length();
                if (startPos != -1) {

                    text.setText(highlight(mSearchText, fullText));

                } else {
                    text.setText(fullText);
                }
            } else {
                text.setText(fullText);

            }

            if (newCreatedFile != null && fullText.contains(".      " + newCreatedFile)) {
                text.setTextColor(Color.BLUE);
            } else if (fullText.equalsIgnoreCase(newCreatedFile)) {
                text.setTextColor(Color.BLUE);
            }

            System.out.println("newCreatedFile: " + newCreatedFile);
        }





        return view;
    }




}
