package integrals.inlens.GridView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

import integrals.inlens.Activities.AttachSituation;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.R;

public class MainActivity extends AppCompatActivity
        implements RecyclerItemClickListener.OnItemClickListener {
    private  String  DatabaseImageUri="NULLX";
    private  String  DatabaseWeatherDetails="NULLX";
    private  String  DatabaseLocationDetails="NULLX";
    private  String  DatabaseAudioCaptionUri="NULLX";
    private  String  DatabaseTimeTaken="NULLX";
    private  String  DatabaseUploadStatus="NULLX";
    private  String  DatabaseTextCaption="NULLX";
    private  String  DatabaseUploaderID="NULLX";
    private  String  DatabaseUploaderName="NULLX";
    private  String  DatabaseUploaderProfilePic="NULLX";
    private  String  DatabaseCurrentTimeMilliSecond="NULLX";
    private Button floatingActionButton;
    private CardView cardView;
    GalleryAdapter mAdapter;
    GalleryAdapter.MyItemHolder myItemHolder;
    RecyclerView mRecyclerView;
    private int SelectedNumber=0,Index=0,Total;
    private int[] PositionArray;
    ArrayList<ImageModel> data = new ArrayList<>();
    private TextView SelectedText;
    public static String IMGS[];
    ArrayList<String> ImageList = new ArrayList<>();
    String TextValue;
    private Boolean FirstImageClicked=false;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_main);

        IMGS=         new String[10000];
        PositionArray=new int[10000];
        SelectedText=(TextView)findViewById(R.id.SelectText);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);
        floatingActionButton=(Button)findViewById(R.id.idFloatButton);
        floatingActionButton.setVisibility(View.INVISIBLE);
        cardView=(CardView)findViewById(R.id.iDCard);
        }



        @Override
        protected void onStart() {
        super.onStart();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(getIntent()!=null) {
                    AddRecyclerViewForRecentImage();
                    for (int i = 0; IMGS[i] != null; i++) {
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName("Image " + i);
                        imageModel.setUrl(IMGS[i]);
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
       protected void onResume() {
         super.onResume();
         runOnUiThread(new Runnable() {
                @Override
                public void run() {
                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            new AddingDatabaseOperation().execute("");
                           }
                    });

                }
         });
         }

       private boolean isImageSelected(int[] positionArray, int position) {
                    Boolean result=false;
                    for(int i=0;i<Total;i++){
                        if(positionArray[i]==position){
                                            result=true;
                                                      }

                    }

                return result;
      }

    private void ResetText(int selectedNumber) {
    if(selectedNumber==0){
        SelectedText.setText("Tap item to select , long Tap to attach");
        floatingActionButton.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
    }else {
        SelectedText.setText("Selected( "+selectedNumber+" ).");
        }


    }

    public void AddRecyclerViewForRecentImage() {
        RecentImageDatabase recentImageDatabase= new RecentImageDatabase(MainActivity.this,"",null,1);
        int Record=recentImageDatabase.GetNumberOfRows();
        for (int i = 1; i <=Record; i++) {
            ImageList.add(recentImageDatabase.GetPhotoUri(i));
        }
        for (int j=0;j<Record;j++){
            IMGS[j]=ImageList.get(j);
        }

    }

    public void AddingToDatabase(int Index){
        UploadDatabaseHelper  uploadDatabaseHelper= new UploadDatabaseHelper(getApplicationContext(),"",null,1);
        CreateDatabaseValues(Index);
        uploadDatabaseHelper.InsertUploadValues(
                DatabaseImageUri ,
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
        String Default="SS";
        DatabaseImageUri=IMGS[Index];
        DatabaseCurrentTimeMilliSecond= String.valueOf(System.currentTimeMillis());
        DatabaseUploadStatus="NOT_UPLOADED";
        Calendar calendar=Calendar.getInstance();
        DatabaseTimeTaken =calendar.get(Calendar.YEAR)+ "-"
                +calendar.get(Calendar.MONTH)+"-"
                +calendar.get(Calendar.DAY_OF_MONTH)+"T"
                +calendar.get(Calendar.HOUR_OF_DAY)+"-"
                +calendar.get(Calendar.MINUTE)+"-"
                +calendar.get(Calendar.SECOND);

        CurrentDatabase currentDatabase= new CurrentDatabase(getApplicationContext(),"",null,1);
        int Value=currentDatabase.GetUploadingTotal();
        currentDatabase.ResetUploadTotal((Value+1));
        currentDatabase.close();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        }






    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            if(FirstImageClicked==false){
                SelectedNumber += 1;
                PositionArray[position] = 9999;
                ResetText(SelectedNumber);
                view.findViewById(R.id.item_img).setAlpha((float) 0.3);
                FirstImageClicked=true;
                floatingActionButton.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
            } else if(FirstImageClicked==true) {
                SelectedNumber -= 1;
                PositionArray[position] = 0;
                ResetText(SelectedNumber);
                view.findViewById(R.id.item_img).setAlpha((float) 1.0);
                FirstImageClicked=false;

            }



        }
        else
        {


            if (isImageSelected(PositionArray, position) == true) {
                SelectedNumber -= 1;
                PositionArray[position] = 0;
                ResetText(SelectedNumber);
                view.findViewById(R.id.item_img).setAlpha((float) 1.0);

            } else if (isImageSelected(PositionArray, position) == false) {
                SelectedNumber += 1;
                PositionArray[position] = position;
                ResetText(SelectedNumber);
                view.findViewById(R.id.item_img).setAlpha((float) 0.3);
                floatingActionButton.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);

            }


        }

    }

    @Override
    public void onItemLongPress(View childView, int position) {
    SharedPreferences sharedPreferences=getSharedPreferences("PhotoUpdate.pref",MODE_PRIVATE);
    SharedPreferences.Editor editor=sharedPreferences.edit();
    editor.putString("CurrentImage::",IMGS[position]);
    editor.commit();
    startActivity(new Intent(MainActivity.this, AttachSituation.class));
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
            return "Executed";
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

   }



