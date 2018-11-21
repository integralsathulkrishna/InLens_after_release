package integrals.inlens.GridView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Calendar;

import integrals.inlens.Activities.AttachSituation;
import integrals.inlens.Activities.Uploading_view;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.R;

public class MainActivity extends AppCompatActivity
        implements RecyclerItemClickListener.OnItemClickListener {


    private String DatabaseImageUri = "NULLX";
    private String DatabaseWeatherDetails = "NULLX";
    private String DatabaseLocationDetails = "NULLX";
    private String DatabaseAudioCaptionUri = "NULLX";
    private String DatabaseTimeTaken = "NULLX";
    private String DatabaseUploadStatus = "NULLX";
    private String DatabaseTextCaption = "NULLX";
    private String DatabaseUploaderID = "NULLX";
    private String DatabaseUploaderName = "NULLX";
    private String DatabaseUploaderProfilePic = "NULLX";
    private String DatabaseCurrentTimeMilliSecond = "NULLX";
    private CardView cardView;
    GalleryAdapter mAdapter;
    GalleryAdapter.MyItemHolder myItemHolder;
    private RecyclerView mRecyclerView;
    private int SelectedNumber = 0, Index = 0, Total;
    private int[] PositionArray;
    ArrayList<ImageModel> data = new ArrayList<>();
    private TextView SelectedText;
    public static String IMGS[];
    public static String TIME[];
    ArrayList<String> ImageList = new ArrayList<>();
    ArrayList<String> TimeList = new ArrayList<>();
    private Boolean FirstImageClicked = false;
    private Boolean SelectIndex=false;
    private String DefaultDialogue="";
    private final int NOTIFICATION_ID = 237;
    private static int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_main);

        IMGS = new String[10000];
        PositionArray = new int[10000];
        TIME = new String[10000];
        SelectedText = (TextView) findViewById(R.id.SelectText);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
        cardView = (CardView) findViewById(R.id.iDCard);
        SelectedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //startActivity(new Intent(MainActivity.this,Uploading_view.class));

           }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getIntent() != null) {
                    AddRecyclerViewForRecentImage();
                    for (int i = 0; IMGS[i] != null; i++) {
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName("Image " + i);
                        imageModel.setUrl(IMGS[i]);
                        imageModel.setTimeTaken(TIME[i]);
                        data.add(imageModel);
                        Total += 1;

                    }
                    mAdapter = new GalleryAdapter(MainActivity.this, data);
                    mRecyclerView.setAdapter(mAdapter);
                    setIntent(null);
                }


            }


        });


    }






    @Override
    public void onItemClick(View view, final int position) {
        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(MainActivity.this);
        ImageDialog.setTitle("Add to Uploading queue");
        final ImageView showImage = new ImageView(MainActivity.this);
        showImage.setMinimumHeight(600);
        showImage.setMinimumWidth(600);
        ImageDialog.setView(showImage);

        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getApplicationContext())
                .load(data.get(position).getUrl())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into((showImage));




        if(position==0){
          if(FirstImageClicked==true){
              DefaultDialogue="Remove";
          }else{
              DefaultDialogue="Upload";
          }
         }
         else {
          if (isImageSelected(PositionArray, position) == true) {
              DefaultDialogue="Remove";
          }else{
              DefaultDialogue="Upload";
          }

      }




              ImageDialog.setPositiveButton(DefaultDialogue, new DialogInterface.OnClickListener() {
                @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                if (position == 0) {
                    if (FirstImageClicked == false) {
                        SelectedNumber += 1;
                        PositionArray[position] = 9999;
                        ResetText(SelectedNumber);
                        FirstImageClicked = true;
                        SelectIndex=true;
                        cardView.setVisibility(View.VISIBLE);
                    } else if (FirstImageClicked == true) {
                        SelectedNumber -= 1;
                        PositionArray[position] = 0;
                        ResetText(SelectedNumber);
                        FirstImageClicked = false;

                    }


                } else {


                    if (isImageSelected(PositionArray, position) == true) {
                        SelectedNumber -= 1;
                        PositionArray[position] = 0;
                        ResetText(SelectedNumber);

                    } else if (isImageSelected(PositionArray, position) == false) {
                        SelectedNumber += 1;
                        PositionArray[position] = position;
                        ResetText(SelectedNumber);
                        SelectIndex=true;
                        cardView.setVisibility(View.VISIBLE);

                    }


                }

                Toast.makeText(getApplicationContext(), "Added to uploading queue", Toast.LENGTH_SHORT);

            }
        });
        ImageDialog.setNegativeButton("Attach", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPreferences sharedPreferences=getSharedPreferences("PhotoUpdate.pref",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("CurrentImage::",IMGS[position]);
                editor.commit();
                startActivity(new Intent(MainActivity.this, AttachSituation.class));
                Toast.makeText(getApplicationContext(), "Added to Attach", Toast.LENGTH_SHORT);


            }
        });

        ImageDialog.show();


    }


    @Override
    public void onItemLongPress(View childView, int position) {

    }

    private class AddingDatabaseOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i <Total; i++) {
                try {
                    if(PositionArray[i]==9999){
                        AddingToDatabase(0);
                    }
                    else{
                        if (PositionArray[i] != 0) {
                            AddingToDatabase(PositionArray[i]);
                        }
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
            return  "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SelectIndex==true){
            new AddingDatabaseOperation().execute("");
        }
        else if(SelectIndex==false){
            Toast.makeText(getApplicationContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }














    private boolean isImageSelected(int[] positionArray, int position) {
        Boolean result = false;
        for (int i = 0; i < Total; i++) {
            if (positionArray[i] == position) {
                result = true;
            }

        }

        return result;
    }

    private void ResetText(int selectedNumber) {
        if (selectedNumber == 0) {
            SelectedText.setText("Tap item to select , long Tap to attach");
            SelectIndex=false;
            cardView.setVisibility(View.INVISIBLE);

        }
        else
            {
            SelectedText.setText("Selected( " + selectedNumber + " ).");
            }


    }

    public void AddRecyclerViewForRecentImage() {
        RecentImageDatabase recentImageDatabase = new RecentImageDatabase(MainActivity.this, "", null, 1);
        int Record = recentImageDatabase.GetNumberOfRows();
        for (int i = 1; i <= Record; i++) {
            ImageList.add(recentImageDatabase.GetPhotoUri(i));
            TimeList.add(recentImageDatabase.GetTimeTaken(i));
        }

        for (int j = 0; j < Record; j++) {

            IMGS[j] = ImageList.get(Record - (j + 1));
            TIME[j] = TimeList.get(Record - (j + 1));


        }
    }

    public void AddingToDatabase(int Index) {
        UploadDatabaseHelper uploadDatabaseHelper = new UploadDatabaseHelper(getApplicationContext(), "", null, 1);
        CreateDatabaseValues(Index);
        uploadDatabaseHelper.InsertUploadValues(
                DatabaseImageUri,
                DatabaseWeatherDetails,
                DatabaseLocationDetails,
                DatabaseAudioCaptionUri,
                DatabaseTimeTaken,
                DatabaseUploadStatus,
                DatabaseTextCaption,
                DatabaseUploaderID,
                DatabaseUploaderName,
                DatabaseUploaderProfilePic,
                DatabaseCurrentTimeMilliSecond

        );


    }

    private void CreateDatabaseValues(int Index) {
        String Default = "SS";
        DatabaseImageUri = IMGS[Index];
        DatabaseCurrentTimeMilliSecond = String.valueOf(System.currentTimeMillis());
        DatabaseUploadStatus = "NOT_UPLOADED";
        Calendar calendar = Calendar.getInstance();
        DatabaseTimeTaken = TIME[Index];
        CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
        int Value = currentDatabase.GetUploadingTotal();
        currentDatabase.ResetUploadTotal((Value + 1));
        currentDatabase.close();

    }
    /*Uploading Details
    To Do
      1. A card view to show the uploading image
                    1.There should be  refresh button to refresh the uploading of the selected image
                    2.if  the uploading is completed the image should be Uploaded Recycler view
                    3.if the image is clicked it should be viewed

      2.A recycler view to show the image that is to be uploaded
                    1. It should have the data List of images that is to be uploaded
                    2. It Should have a button that enables that delete the image
                    3. If the image is clicked it should be viewed in Photo View

      3.A Recycler view to show the images that  uploaded
                    1. If the image is clicked it should be shown in dialgue box
                       // Cannot delete the uploaded image









     */

}