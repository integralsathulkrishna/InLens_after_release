package integrals.inlens.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import integrals.inlens.MainActivity;
import integrals.inlens.Models.Blog;
import integrals.inlens.Models.Participants;
import integrals.inlens.Models.SituationModel;
import integrals.inlens.R;
import integrals.inlens.ViewHolder.BlogViewHolder;
import integrals.inlens.ViewHolder.GridImageAdapter;
import integrals.inlens.ViewHolder.ParticipantsViewHolder;

public class PhotoListHelper {


    private List<Blog>          BlogList;
    private List<String>        BlogListID;
    private String              PhotoThumb;
    private String              BlogTitle,ImageThumb,BlogDescription,Location;
    private String              TimeTaken,UserName,User_ID,WeatherDetails,PostedByProfilePic;
    private String              OriginalImageName;
    private String              TimeEnd,TimeStart,GlobalID;
    private Boolean             LastPost;
    private String              CommunityID;
    private DatabaseReference   databaseReferencePhotoList;
    private Context             context;
    private Activity            activity;
    private GridImageAdapter    gridImageAdapter;

    public PhotoListHelper( Context context,
                            Activity activity,
                            DatabaseReference db) {
        this.context=context;
        this.activity=activity;
        this.databaseReferencePhotoList=db;
        TimeEnd="2100-8-6T13-22-45";
        TimeStart="2000-8-6T13-22-45";

    }

    public void SetRecyclerView(String timeStart,
                                 String timeEnd,
                                 String globalID,
                                 Boolean lastPost,
                                 String situationName,
                                 Boolean Local,
                                 final RecyclerView recyclerView) {
        BlogList=new ArrayList<>();
        BlogListID=new ArrayList<>();
        TimeStart=timeStart;
        TimeEnd=timeEnd;
        CommunityID=globalID;
        LastPost=lastPost;



        try {

            databaseReferencePhotoList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    BlogList.clear();
                    BlogListID.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        if (snapshot.hasChildren()) {
                            try {


                                if (CheckIntervel(snapshot.child("TimeTaken").getValue().toString(), TimeStart, TimeEnd)) {
                                    String BlogListIDString = snapshot.getKey();
                                    if (snapshot.hasChild("Image")) {
                                        String photoThumb = snapshot.child("Image").getValue().toString();
                                        PhotoThumb = photoThumb;
                                    }

                                    if (snapshot.hasChild("BlogTitle")) {
                                        String blogTitle = snapshot.child("BlogTitle").getValue().toString();
                                        BlogTitle = blogTitle;
                                    }

                                    if (snapshot.hasChild("Location")) {
                                        String location = snapshot.child("Location").getValue().toString();
                                        Location = location;
                                    }

                                    if (snapshot.hasChild("TimeTaken")) {
                                        String timeTaken = snapshot.child("TimeTaken").getValue().toString();
                                        TimeTaken = timeTaken;
                                    }

                                    if (snapshot.hasChild("OriginalImageName")) {
                                        String originalImageName = snapshot.child("OriginalImageName").getValue().toString();
                                        OriginalImageName = originalImageName;
                                    }
                                    if (snapshot.hasChild("ImageThumb")) {
                                        String imageThumb = snapshot.child("ImageThumb").getValue().toString();
                                        ImageThumb = imageThumb;
                                    }


                                    if (snapshot.hasChild("WeatherDetails")) {
                                        String weatherDetails = snapshot.child("WeatherDetails").getValue().toString();
                                        WeatherDetails = weatherDetails;
                                    }


                                    if (snapshot.hasChild("UserName")) {
                                        String userName = snapshot.child("UserName").getValue().toString();
                                        UserName = userName;
                                    }


                                    if (snapshot.hasChild("User_ID")) {
                                        String user_id = snapshot.child("User_ID").getValue().toString();
                                        User_ID = user_id;
                                    }

                                    if (snapshot.hasChild("PostedByProfilePic")) {
                                        String postedByProfilePic = snapshot.child("PostedByProfilePic").getValue().toString();
                                        PostedByProfilePic = postedByProfilePic;
                                    }

                                    if (!BlogListID.contains(BlogListIDString)) {
                                        BlogListID.add(BlogListIDString);
                                        Blog model = new Blog("", PhotoThumb, ImageThumb,
                                                "", BlogTitle, Location, TimeTaken,
                                                UserName, User_ID,
                                                WeatherDetails,
                                                PostedByProfilePic,
                                                OriginalImageName);
                                        BlogList.add(model);
                                    }
                                }
                                else
                                {
                                }
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                        gridImageAdapter = new GridImageAdapter(
                                context,
                                BlogList,
                                BlogListID,
                                activity,databaseReferencePhotoList

                        );

                        recyclerView.setAdapter(gridImageAdapter);


                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }catch (NullPointerException e){
            e.printStackTrace();
        }




    }


    private boolean CheckIntervel(String timeTaken, String timeStart, String timeEnd) {
        Boolean Result=false;
        try{
            SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
            Date dt_1 = objSDF.parse(timeTaken);
            Date dt_2 = objSDF.parse(timeStart);
            Date dt_3=  objSDF.parse(timeEnd);
            if(LastPost==false) {
                if (dt_1.after(dt_2) && (dt_1.before(dt_3))) {
                    Result = true;
                }
            }
            else
            {
                if(dt_1.after(dt_2)){
                    Result=true;
                }
            }

        }
        catch (ParseException e){
            Toast.makeText(context,"Parse Exception",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return Result;
    }




    public void DisplayBottomSheet(final Dialog mBottomSheetDialog,
                                   final RecyclerView mBottomSheetDialogRecyclerView,
                                   ImageButton mBottomSheetDialogCloseBtn,
                                   TextView mBottomSheetDialogTitle,
                                   final ProgressBar mBottomSheetDialogProgressbar,
                                   String timeStart, String timeEnd, String globalID,String Name, Boolean lastPost) {

        BlogList=new ArrayList<>();
        BlogListID=new ArrayList<>();
        TimeStart=timeStart;
        TimeEnd=timeEnd;
        CommunityID=globalID;
        LastPost=lastPost;
        mBottomSheetDialogTitle.setText(Name);
        final ProfileDilaogHelper BottomSheetUserDialog = new ProfileDilaogHelper(activity);
        BottomSheetUserDialog.setCancelable(true);
        if (BottomSheetUserDialog.getWindow() != null) {
            BottomSheetUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            BottomSheetUserDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }




        try {

            databaseReferencePhotoList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    BlogList.clear();
                    BlogListID.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        if (snapshot.hasChildren()) {
                            try {


                                if (CheckIntervel(snapshot.child("TimeTaken").getValue().toString(), TimeStart, TimeEnd)) {
                                    String BlogListIDString = snapshot.getKey();
                                    if (snapshot.hasChild("Image")) {
                                        String photoThumb = snapshot.child("Image").getValue().toString();
                                        PhotoThumb = photoThumb;
                                    }

                                    if (snapshot.hasChild("BlogTitle")) {
                                        String blogTitle = snapshot.child("BlogTitle").getValue().toString();
                                        BlogTitle = blogTitle;
                                    }

                                    if (snapshot.hasChild("Location")) {
                                        String location = snapshot.child("Location").getValue().toString();
                                        Location = location;
                                    }

                                    if (snapshot.hasChild("TimeTaken")) {
                                        String timeTaken = snapshot.child("TimeTaken").getValue().toString();
                                        TimeTaken = timeTaken;
                                    }

                                    if (snapshot.hasChild("OriginalImageName")) {
                                        String originalImageName = snapshot.child("OriginalImageName").getValue().toString();
                                        OriginalImageName = originalImageName;
                                    }
                                    if (snapshot.hasChild("ImageThumb")) {
                                        String imageThumb = snapshot.child("ImageThumb").getValue().toString();
                                        ImageThumb = imageThumb;
                                    }


                                    if (snapshot.hasChild("WeatherDetails")) {
                                        String weatherDetails = snapshot.child("WeatherDetails").getValue().toString();
                                        WeatherDetails = weatherDetails;
                                    }


                                    if (snapshot.hasChild("UserName")) {
                                        String userName = snapshot.child("UserName").getValue().toString();
                                        UserName = userName;
                                    }


                                    if (snapshot.hasChild("User_ID")) {
                                        String user_id = snapshot.child("User_ID").getValue().toString();
                                        User_ID = user_id;
                                    }

                                    if (snapshot.hasChild("PostedByProfilePic")) {
                                        String postedByProfilePic = snapshot.child("PostedByProfilePic").getValue().toString();
                                        PostedByProfilePic = postedByProfilePic;
                                    }

                                    if (!BlogListID.contains(BlogListIDString)) {
                                        BlogListID.add(BlogListIDString);
                                        Blog model = new Blog("", PhotoThumb, ImageThumb,
                                                "", BlogTitle, Location, TimeTaken,
                                                UserName, User_ID,
                                                WeatherDetails,
                                                PostedByProfilePic,
                                                OriginalImageName);
                                        BlogList.add(model);
                                    }
                                } else {
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                    gridImageAdapter = new GridImageAdapter(
                            context,
                            BlogList,
                            BlogListID,
                            activity,databaseReferencePhotoList

                    );

                    mBottomSheetDialogRecyclerView.setAdapter(gridImageAdapter);
                    mBottomSheetDialogProgressbar.setVisibility(View.GONE);
                    mBottomSheetDialog.show();





                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }








}
