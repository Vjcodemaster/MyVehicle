package com.autochip.myvehicle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import dialogs.DialogMultiple;

import static android.app.Activity.RESULT_OK;
import static com.autochip.myvehicle.MainActivity.mBitmapCompressListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.autochip.myvehicle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsuranceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsuranceFragment extends Fragment implements OnFragmentInteractionListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int PICTURE_REQUEST_CODE = 1414;
    private Uri outputFileUri;
    private ImageView ivPreview;

    private String mParam1;
    private String mParam2;

    private int viewHeight = 0;
    File sdImageMainDirectory;

    Dialog dialog;
    TextView tvStartDateValue, tvExpiryDateValue, tvRemainderDateValue;
    LinearLayout llDate, llDateValue;
    final Calendar myCalendar = Calendar.getInstance();

    FloatingActionButton fab;
    TableLayout tlPolicy;
    TableRow row;
    LayoutInflater layoutInflater;

    public static OnFragmentInteractionListener mListener;
    DialogMultiple dialogMultiple;

    private CircularProgressBar circularProgressBar;
    //public static OnImageUtilsListener mBitmapCompressListener;

    public InsuranceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InsuranceFragment.
     */
    public static InsuranceFragment newInstance(String param1, String param2) {
        InsuranceFragment fragment = new InsuranceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mBitmapCompressListener = this;
        mListener= this;
        circularProgressBar = new CircularProgressBar(getActivity(), false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            viewHeight = Integer.valueOf(mParam1);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            dialogMultiple = new DialogMultiple(getActivity(),1, mBitmapCompressListener);
        }/* else {
            checkAndHide();
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_insurance, container, false);
        tlPolicy = view.findViewById(R.id.tl_policy);
        fab = view.findViewById(R.id.fab);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
        params.bottomMargin = viewHeight + 6;
        fab.setLayoutParams(params);

        //this statement is written in setUserVisible Hint because this dialog should be created only when fragment is visible to user
        //dialogMultiple = new DialogMultiple(getActivity(),1, mBitmapCompressListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialog.show();
                dialogMultiple.dialog.show();
            }
        });

        /*
        inflating views dynamically for table layout where we always add the data dynamically from odoo not from xml
        here we are inflating table rows first using for loop depending on the data we receive and then add table row header
        by inflating it and setting it to 0th index of Table Layout tlPolicy.addView(trHeading, 0);. to set text we can use,
        TextView tv = (TextView) row.childAt(0);| tv.setText("Text"); or row.findViewById()
         */
        TableRow trHeading = (TableRow) inflater.inflate(R.layout.table_row_heading, null);

        for (int i = 0; i < 5; i++) {
            //LayoutInflater trInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = (TableRow) inflater.inflate(R.layout.table_row, null);
            row.setTag(i);
            tlPolicy.addView(row, i);
        }
        tlPolicy.addView(trHeading, 0);
        // Inflate the layout for this fragment
        return view;
    }

    public void initAddDialog() {
        dialog = new Dialog(getActivity(), R.style.CustomDialogTheme90);
        dialog.setContentView(R.layout.dialog_add_insurance_policy);
        dialog.setCancelable(true);

        llDate = dialog.findViewById(R.id.ll_date);
        llDateValue = dialog.findViewById(R.id.ll_date_value);

        TextView tvStartDate = dialog.findViewById(R.id.tv_start_date);
        TextView tvExpiryDate = dialog.findViewById(R.id.tv_expiry_date);
        TextView tvRemainderDate = dialog.findViewById(R.id.tv_remainder_date);
        TextView tvCamera = dialog.findViewById(R.id.tv_camera);

        tvStartDateValue = dialog.findViewById(R.id.tv_sd_value);
        tvExpiryDateValue = dialog.findViewById(R.id.tv_ed_value);
        tvRemainderDateValue = dialog.findViewById(R.id.tv_rd_value);

        ivPreview = dialog.findViewById(R.id.iv_preview);

        View.OnClickListener l = new View.OnClickListener() {
            TextView textView;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_start_date:
                        textView = tvStartDateValue;
                        break;
                    case R.id.tv_expiry_date:
                        textView = tvExpiryDateValue;
                        break;
                    case R.id.tv_remainder_date:
                        textView = tvRemainderDateValue;
                        break;
                    case R.id.tv_sd_value:
                        textView = tvStartDateValue;
                        break;
                    case R.id.tv_ed_value:
                        textView = tvExpiryDateValue;
                        break;
                    case R.id.tv_rd_value:
                        textView = tvRemainderDateValue;
                        break;
                }

                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(textView);
                    }

                };
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        };

        tvStartDate.setOnClickListener(l);
        tvExpiryDate.setOnClickListener(l);
        tvRemainderDate.setOnClickListener(l);
        tvStartDateValue.setOnClickListener(l);
        tvExpiryDateValue.setOnClickListener(l);
        tvRemainderDateValue.setOnClickListener(l);


        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PickImageDialog.build(new PickSetup().setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent))).show(getActivity());
                openImageIntent();
            }
        });
        /*TextView tvHeading = (TextView) DialogMultiple.findViewById(R.id.tv_readmore_heading);
        TextView tvSubHeading = (TextView) DialogMultiple.findViewById(R.id.tv_readmore_sub_heading);
        TextView tvDescription = (TextView) DialogMultiple.findViewById(R.id.tv_readmore_description);
        Typeface lightFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_light.ttf");
        Typeface regularFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_regular.ttf");
        tvHeading.setTypeface(regularFace);
        tvSubHeading.setTypeface(lightFace);
        tvDescription.setTypeface(lightFace);*/
    }

    private void openImageIntent() {
        showProgressBar();
// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + File.separator + getActivity().getPackageName() + File.separator);
        root.mkdirs();
        final String fname = System.currentTimeMillis() + "insurance";
        sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Choose");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
    }

    private void updateLabel(TextView textView) {
        llDate.setGravity(Gravity.END);
        llDateValue.setVisibility(View.VISIBLE);
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textView.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST_CODE) { //PICTURE_REQUEST_CODE

                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }
                final File root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + File.separator + getActivity().getPackageName() + File.separator);
                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    //InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    //Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1920) {
                        ImageUtils imageUtils = new ImageUtils(root, selectedImageUri);
                    } else {
                        DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                        //ivPreview.setImageBitmap(bitmap);
                    }
                    //Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap("Your_Image_Path_Here");
                    //ivPreview.setImageBitmap(bitmap);
                    //uriImageToCompressedBitmap(selectedImageUri);
                } else {
                    selectedImageUri = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1920) {
                        ImageUtils imageUtils = new ImageUtils(root, selectedImageUri);
                    } else {
                        DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                        //ivPreview.setImageBitmap(bitmap);
                    }
                    //Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap("Your_Image_Path_Here");
                    //ivPreview.setImageBitmap(bitmap);
                    //uriImageToCompressedBitmap(selectedImageUri);
                }
            }
        } //else {
            stopProgressBar();
        //}
    }

    /*@Override
    public void onBitmapCompressed(String sMessage, int nCase, Bitmap bitmap, Intent intent, Uri outputFileUri) {
        switch (sMessage) {
            *//*case "BITMAP_COMPRESSED": //this is triggered from ImageUtils, which gets compressed bitmap and the same is sent back to Dialog Multiple
                DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                //ivPreview.setImageBitmap(bitmap);
                stopProgressBar();
                break;
            case "START_ACTIVITY_FOR_RESULT":  //this is triggered from DialogMultiple class to start Activity for result instead of doing there
                this.outputFileUri = outputFileUri;
                startActivityForResult(intent, PICTURE_REQUEST_CODE);
                break;
            case "SHOW_PROGRESS_BAR": //triggered from DialogMultiple class to start progress
                showProgressBar();
                break;*//*
        }
    }*/

    private void showProgressBar() {
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }

    private void stopProgressBar() {
        if (circularProgressBar != null && circularProgressBar.isShowing())
            circularProgressBar.dismiss();
    }

    @Override
    public void onInteraction(String sMessage, int nCase, String sActivityName) {
        switch (sMessage){
            case "UPDATE_TABLE_ROW":
                String[] saData = sActivityName.split(",");
                int count = tlPolicy.getChildCount();
                row = (TableRow) layoutInflater.inflate(R.layout.table_row, null);
                TextView tvSlNo = row.getChildAt(0).findViewById(R.id.tv_table_row_1);
                TextView tvInsuranceNo = row.getChildAt(1).findViewById(R.id.tv_table_row_2);
                TextView tvInsuranceProvider = row.getChildAt(2).findViewById(R.id.tv_table_row_3);
                TextView tvStartDate = row.getChildAt(3).findViewById(R.id.tv_table_row_4);
                TextView tvExpiryDate = row.getChildAt(4).findViewById(R.id.tv_table_row_5);
                TextView tvRemainderDate = row.getChildAt(5).findViewById(R.id.tv_table_row_6);
                tvSlNo.setText(String.valueOf(count));
                tvInsuranceNo.setText(saData[0]);
                tvInsuranceProvider.setText(saData[1]);
                tvStartDate.setText(saData[2]);
                tvExpiryDate.setText(saData[3]);
                tvRemainderDate.setText(saData[4]);
                tlPolicy.addView(row);
                break;
        }
    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>> lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {

    }
    /*private void uriImageToCompressedBitmap(Uri selectedImageUri) {
        InputStream imageStream = null;
        try {
            imageStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
        Log.i("Before", bmp.getWidth() + "-" + bmp.getHeight());
        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap(sdImageMainDirectory.getPath());
        ivPreview.setImageBitmap(bitmap);
        FileOutputStream fileOutputStream = null;
        Log.i("After", bitmap.getWidth() + "-" + bitmap.getHeight());

        final File root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + File.separator + getActivity().getPackageName() + File.separator);
        root.mkdirs();
        final String fname = System.currentTimeMillis() + "insurance";
        sdImageMainDirectory = new File(root, fname);

        try {
            fileOutputStream = new FileOutputStream(sdImageMainDirectory);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream); // bmp is your Bitmap instance
            // PNG is a loss less format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //byte[] byteArray = stream.toByteArray();
        *//*try {
            stream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }*//*
    }*/
}
