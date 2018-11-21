package integrals.inlens.Activities;
// To be implemented in next update
import android.app.Activity;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.Models.Blog;
import integrals.inlens.R;
import integrals.inlens.ViewHolder.QueueImageAdapter;

public class Uploading_view extends AppCompatActivity {
    private List<Blog>           BlogList;
    private List<String>         BlogListID;
    private int                  TotalData;
    private UploadDatabaseHelper uploadDatabaseHelper;
    private Blog                 blog;
    private String               uploadingImageUri;
    private ImageView            uploadingImageView;
    private RecyclerView         recyclerView;
    private QueueImageAdapter    queueImageAdapter;
    private Activity             activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_view);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setSubtitle("     Currently uploading  ");

        activity=this;
        recyclerView=(RecyclerView)findViewById(R.id.Uploading_queue_recyclerView);
        uploadingImageView=(ImageView)findViewById(R.id.Uploading_image_view);
        uploadDatabaseHelper=new UploadDatabaseHelper(getApplicationContext(),"",null,1);
        TotalData=uploadDatabaseHelper.GetNumberOfRows();
        BlogList=new ArrayList<>();
        BlogListID=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);


    }


    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        CreateBlogListForUploadQueue();
        uploadingImageUri=GetUploadingImageUri();
        ViewImage(uploadingImageUri);
        SetRecyclerView(recyclerView,BlogList);

    }

    private void SetRecyclerView(RecyclerView recyclerView, List<Blog> blogList) {
        recyclerView.removeAllViews();
        queueImageAdapter=new QueueImageAdapter(blogList,getApplicationContext(),
                activity,uploadDatabaseHelper,recyclerView);
        recyclerView.setAdapter(queueImageAdapter);



    }


    private void ViewImage(String uploadingImageUri) {
        RequestOptions requestOptions=new RequestOptions()
                .centerCrop()
                .override(200,200);

        Glide.with(getApplicationContext())
                .load(uploadingImageUri)
                .apply(requestOptions)
                .into(uploadingImageView);


    }

    private String GetUploadingImageUri() {
    String Result=null;

    for(int i=1;i<=TotalData;i++) {
        if (uploadDatabaseHelper.GetUploadStatus(i).contentEquals("UPLOADING")) {
            Result = uploadDatabaseHelper.GetPhotoUri(i);
        }
    }
        return Result;
    }

    private void CreateBlogListForUploadQueue() {
             for(int i=1;i<=TotalData;i++){
                if(uploadDatabaseHelper.GetUploadStatus(i).contentEquals("NOT_UPLOADED")) {
                    blog = new Blog(uploadDatabaseHelper.GetAudioCaption(i), uploadDatabaseHelper.GetPhotoUri(i), uploadDatabaseHelper.GetPhotoUri(i),
                            "NULL", uploadDatabaseHelper.GetTextCaption(i), uploadDatabaseHelper.GetLocationDetails(i),
                            uploadDatabaseHelper.GetTimeTaken(i), uploadDatabaseHelper.GetUsername(i), uploadDatabaseHelper.GetUploaderID(i),
                            uploadDatabaseHelper.GetWeatherDetails(i),
                            uploadDatabaseHelper.GetProfilePicUri(i),
                            uploadDatabaseHelper.GetPhotoUri(i));
                    BlogList.add(blog);
                }
                }
           }

           //Algorithm
    /*
    1.Start

    TimerTask repeat at every 10 sec
   2.Create BlogListForNotUploaded{
         Read UploadDatabaseHelper{
             if(UPLOAD_STATUS=='NOT_UPLOADED'){
                    AddToBlogList;
                    }
              }


    }
    3.Set Recycler View
    4.Read UploadDatabaseHelper
                    if(UPLOAD_STATUS=='UPLOADING'){
                    DisplayImageOnImageView();
                    }

     5.If Image clicked the view should be occured on the dialoge window


     6.Delete Button Clicked{
            SET_UPLOAD_STATUS="DO_NOT_UPLOAD"

      }
     7.Refresh Button Clicked{
            StopServive;
            UPLOAD_STATUS at UPLOAD_TARGET_COLUMN="NOT_UPLOADED"
            StartService;
     }

   }
     */
}
