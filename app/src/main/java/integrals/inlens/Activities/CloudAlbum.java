package integrals.inlens.Activities;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecyclerItemClickListener;
import integrals.inlens.Models.Blog;
import integrals.inlens.Models.SituationModel;
import integrals.inlens.R;
import integrals.inlens.ViewHolder.GridImageAdapter;
import integrals.inlens.ViewHolder.SituationAdapter;

public class CloudAlbum extends AppCompatActivity {
    private RecyclerView            recyclerView;
    private DatabaseReference       databaseReference;
    private String                  CommunityID;
    private Calendar                calendar;
    private SituationAdapter        adapter;
    private String                  sowner,stime,stitle,sKey,sTime;
    private List<SituationModel>    SituationList;
    private List<String>            SituationIDList;
    private DatabaseReference       db,ComNotyRef;
    private String                  Album;
    private Button                  NewSituation;
    private String                  ReturnName="Oops";
    private String                  TimeEnd,TimeStart,GlobalID;
    private Boolean                 LastPost;
    private DatabaseReference   databaseReferencePhotoList=null;
    private List<Blog>          BlogList;
    private List<String>        BlogListID;
    private GridImageAdapter gridImageAdapter;
    private RecyclerView        recyclerViewPhotoList,recyclerViewGrid;
    private String              PhotoThumb;
    private String BlogTitle,ImageThumb,BlogDescription,Location;
    private String TimeTaken,UserName,User_ID,WeatherDetails,PostedByProfilePic;
    private String OriginalImageName;
    private Dialog createNewSituation , Renamesituation;
    private TextView SituationName;
    private String   Name;
    private Button SwipeControl;
    private Boolean SwipeUp=false;
    private String TestCommunityID=null;
    private BottomSheetBehavior bottomSheetBehavior;
    private Activity activity;
    private BottomSheet.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_cloud_album);

        activity=this;
        SwipeControl=(Button)findViewById(R.id.SwipeControl);
        String AlbumName = getIntent().getStringExtra("AlbumName");
        CommunityID = getIntent().getStringExtra("GlobalID::");
        recyclerView = (RecyclerView)findViewById(R.id.SituationRecyclerView);
        SituationName=(TextView)findViewById(R.id.SituationNametxt);
        recyclerViewPhotoList=(RecyclerView)findViewById(R.id.SituationPhotos);
        recyclerViewGrid=(RecyclerView)findViewById(R.id.SituationPhotosGrid);
        recyclerViewGrid.setEnabled(false);
        recyclerViewGrid.setVisibility(View.INVISIBLE);
        recyclerViewPhotoList.setEnabled(false);
        recyclerViewPhotoList.setVisibility(View.INVISIBLE);
        databaseReferencePhotoList = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("BlogPosts");

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        Album = AlbumName;
        SituationList = new ArrayList<>();
        SituationIDList = new ArrayList<>();
        TimeEnd="2100-8-6T13-22-45";
        TimeStart="2000-8-6T13-22-45";
        Name="Album Photos";
        LastPost=false;
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        SharedPreferences sPreferences = getSharedPreferences("ComIDPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreferences.edit();
        if(!TextUtils.isEmpty(CommunityID) && !TextUtils.isEmpty(Album))
        {
            editor.putString("GlobalID::",CommunityID);
            editor.putString("Album",AlbumName);
            editor.commit();
        }

        else
        {
            CommunityID = sPreferences.getString("GlobalID::","");
            AlbumName = sPreferences.getString("Album","");
        }

        getSupportActionBar().setTitle(AlbumName);

        //////////////////////////////////////////////////////////////////////////////////////////
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("Situations");
        ComNotyRef = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("CommunityPhotographer");
        ///////////////////////////////////////////////////////////////////////////////////////////

        // new situation layout
        createNewSituation = new Dialog(CloudAlbum.this);
        createNewSituation.setContentView(R.layout.create_new_situation_layout);
        createNewSituation.setCancelable(false);
        final EditText SituationName = createNewSituation.findViewById(R.id.situation_name);
        SituationName.requestFocus();
        Button Done ,Cancel;
        Done =   createNewSituation.findViewById(R.id.done_btn);
        Cancel = createNewSituation.findViewById(R.id.cancel_btn);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(SituationName.getText().toString()))
                {
                    calendar=Calendar.getInstance();
                    String SituationTimeIntervel=calendar.get(Calendar.YEAR)+ "-"
                            +calendar.get(Calendar.MONTH)+"-"
                            +calendar.get(Calendar.DAY_OF_MONTH)+"T"
                            +calendar.get(Calendar.HOUR_OF_DAY)+"-"
                            +calendar.get(Calendar.MINUTE)+"-"
                            +calendar.get(Calendar.SECOND);

                    Map situationmap = new HashMap();
                    situationmap.put("name",SituationName.getText().toString().trim());
                    situationmap.put("time", ServerValue.TIMESTAMP);
                    situationmap.put("owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final String push_id =databaseReference.push().getKey();



                    // Added by  For Implementation of Situation Upload
                    situationmap.put("SituationKey",push_id);
                    situationmap.put("SituationTime",SituationTimeIntervel);
                    final Map member = new HashMap();
                    member.put("memid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("ComNoty");
                    ComNotyRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // Situation Notification function by elson jose

                            Map notymap = new HashMap();
                            notymap.put("name",SituationName.getText().toString().trim());
                            notymap.put("ownername",GetUserName(FirebaseAuth.getInstance().getCurrentUser().getUid()));


                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                String id = snapshot.child("Photographer_UID").getValue().toString();
                                if(!id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    dref.child(id).push().setValue(notymap);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    databaseReference.child(push_id).setValue(situationmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                databaseReference.child(push_id).child("members").push().setValue(member);
                                Toast.makeText(CloudAlbum.this,"New Situation Created : "+SituationName.getText().toString(),Toast.LENGTH_SHORT).show();
                                SituationName.setText("");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(e.toString().contains("FirebaseNetworkException"))
                                Toast.makeText(CloudAlbum.this,"Not Connected to Internet.",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(CloudAlbum.this,"Unable to create new Situation.", Toast.LENGTH_SHORT).show();

                            SituationName.setText("");
                        }
                    });
                    createNewSituation.dismiss();
                }
                else
                {
                    Toast.makeText(CloudAlbum.this,"No name given",Toast.LENGTH_LONG).show();
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewSituation.dismiss();
            }
        });

      View bottomSheet=findViewById(R.id.design_bottom_sheet);
      bottomSheetBehavior=BottomSheetBehavior.from(bottomSheet);
      bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
              switch (newState){
                  case BottomSheetBehavior.STATE_EXPANDED:
                      recyclerViewGrid.setEnabled(false);
                      recyclerViewGrid.setVisibility(View.INVISIBLE);
                      recyclerViewPhotoList.setEnabled(false);
                      recyclerViewPhotoList.setVisibility(View.INVISIBLE);
                      SwipeUp=true;
                      SetRecyclerView(TimeStart,TimeEnd
                              ,GlobalID,
                              LastPost,Name,
                              false,
                              recyclerViewGrid);
                      SwipeControl.setBackgroundResource(R.drawable.ic_down);

                      break;
                  case BottomSheetBehavior.STATE_DRAGGING:
                      try {

                      }catch (NullPointerException e){
                          e.printStackTrace();
                      }
                      break;
                   case BottomSheetBehavior.STATE_COLLAPSED:
                       recyclerViewGrid.setEnabled(false);
                       recyclerViewGrid.setVisibility(View.INVISIBLE);
                       recyclerViewPhotoList.setEnabled(false);
                       recyclerViewPhotoList.setVisibility(View.INVISIBLE);
                       SwipeUp=false;
                       SetRecyclerView(TimeStart,
                              TimeEnd,GlobalID,
                              LastPost,Name,true,
                              recyclerViewPhotoList);
                       SwipeControl.setBackgroundResource(R.drawable.ic_up);

                      break;

                  case BottomSheetBehavior.STATE_HIDDEN:
                      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                      break;


              }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {

          }
      });



      SwipeControl.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(SwipeUp==false){
                  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
              }else {
                  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

              }
          }
      });




     CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),"",null,1);
     TestCommunityID=currentDatabase.GetLiveCommunityID();
     currentDatabase.close();


        SetRecyclerView(TimeStart,
                TimeEnd,GlobalID,
                LastPost,Name,true,
                recyclerViewPhotoList);


    }


    private String GetUserName(String uid) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue().toString();
                ReturnName = name;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return ReturnName;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean Default = false;
        SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        SharedPreferences sharedPreferences1=getSharedPreferences("Owner.pref",MODE_PRIVATE);

        if (sharedPreferences.getBoolean("UsingCommunity::", Default) == true) {
        if((TestCommunityID).contentEquals(getIntent().getExtras().getString("GlobalID::"))){
            menu.add(0, 0, 0, "Add Participant")
                    .setIcon(R.drawable.ic_add_participant)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if(sharedPreferences1.getBoolean("ThisOwner::",false) ==true) {
            menu.add(0, 1, 0, "Add Situation")
                    .setIcon(R.drawable.ic_add)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        }


    }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            startActivity(new Intent(CloudAlbum.this, QRCodeGenerator.class));
        }
        if(item.getItemId()==1){
            createNewSituation.show();
        }
        return true;
    }







    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SituationIDList.clear();
                SituationList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {



                    if(snapshot.hasChildren())
                    {
                        String SituationId = snapshot.getKey();


                        if(snapshot.hasChild("owner"))
                        {
                            String ownerid = snapshot.child("owner").getValue().toString();
                            sowner = ownerid;
                        }

                        if(snapshot.hasChild("time"))
                        {
                            String time = snapshot.child("time").getValue().toString();
                            stime = time;
                        }

                        if(snapshot.hasChild("name"))
                        {
                            String title = snapshot.child("name").getValue().toString();
                            stitle = title;

                            if(snapshot.hasChild("SituationKey"))
                            {
                                String SituationKey = snapshot.child("SituationKey").getValue().toString();
                                sKey = SituationKey;

                            }
                            if(snapshot.hasChild("SituationTime"))
                            {
                                String SituationTime = snapshot.child("SituationTime").getValue().toString();
                                sTime = SituationTime;

                            }

                        }

                        if(!SituationIDList.contains(SituationId))
                        {
                            SituationIDList.add(SituationId);
                            SituationModel model = new SituationModel(sowner,stime,stitle,sKey,sTime);
                            SituationList.add(model);
                        }

                    }


                }

                adapter = new SituationAdapter(getApplicationContext(),
                        SituationList,
                        SituationIDList,db,
                        CommunityID,databaseReference);
                recyclerView.setAdapter(adapter);


                //Done for Implementation of Recycler View OnClick


                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(CloudAlbum.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {

                                try {
                                    if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED) {
                                        TimeStart = SituationList.get(position).getSituationTime();
                                        TimeEnd = SituationList.get(position + 1).getSituationTime();
                                        GlobalID = CommunityID;
                                        LastPost = false;
                                        Name = SituationList.get(position).getTitle();
                                        SetRecyclerView(TimeStart, TimeEnd, GlobalID, LastPost, Name, true, recyclerViewPhotoList);
                                    }

                                }catch (IndexOutOfBoundsException e) {
                                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                                        TimeStart = SituationList.get(position).getSituationTime();
                                        TimeEnd = SituationList.get(position).getSituationTime();
                                        GlobalID = CommunityID;
                                        LastPost = true;
                                        Name = SituationList.get(position).getTitle();
                                        SetRecyclerView(TimeStart, TimeEnd, GlobalID, LastPost, Name, true, recyclerViewPhotoList);
                                    }
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, final int position) {


                              BottomSheet.Builder BottomS =  new BottomSheet.Builder(CloudAlbum.this);
                              BottomS.title("Edit Situation : "+SituationList.get(position).getTitle())
                                        .sheet(R.menu.situationmenu).listener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        switch (i)
                                        {
                                            case R.id.renamesituation :
                                                RenameSituation(SituationIDList.get(position));
                                                Renamesituation.show();
                                                break;
                                            case R.id.deletesituation:
                                            {
                                                if(SituationIDList.size()<=1)
                                                {
                                                    Toast.makeText(getApplicationContext(),"Unable to perform deletion. Album should have at least one situation.",Toast.LENGTH_LONG).show();
                                                }
                                                else
                                                {

                                                    final android.app.AlertDialog.Builder alertbuilder = new android.app.AlertDialog.Builder(CloudAlbum.this);
                                                    alertbuilder.setTitle("Delete Situation "+SituationList.get(position).getTitle())
                                                            .setMessage("You are about to delete the situation. Are you sure you want to continue ?")
                                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                                    dialogInterface.dismiss();
                                                                }
                                                            })
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(final DialogInterface dialogInterface, int i) {

                                                                    databaseReference.child(SituationIDList.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            Toast.makeText(CloudAlbum.this,"Successfully deleted the situation",Toast.LENGTH_LONG).show();
                                                                            dialogInterface.dismiss();
                                                                        }
                                                                    });

                                                                }
                                                            })
                                                            .create()
                                                            .show();

                                                }

                                            }

                                            break;
                                        }

                                    }
                                    }).show();
                            }
                        })
                );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onBackPressed() {

        if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void SetRecyclerView(String timeStart,
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

        SituationName.setText(situationName);

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
                                    } else
                                    {
                                    }
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }

                            gridImageAdapter = new GridImageAdapter(
                                    getApplicationContext(),
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

            /*recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(CloudAlbum.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            String PostKey1 = BlogListID.get(position).toString().trim();
                            Intent i = new Intent(getApplicationContext(), PhotoView.class);
                            i.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) BlogList);
                            i.putExtra("position",position);
                            startActivity(i);


                        }

                        @Override
                        public void onLongItemClick(View view, int position) {


                        }
                    })
            );
*/


        if(Local==true) {
            recyclerView.setEnabled(true);
            recyclerView.setVisibility(View.VISIBLE);

            try {
                recyclerView.setLayoutManager(new CardSliderLayoutManager(this));
                new CardSnapHelper().attachToRecyclerView(recyclerView);

            } catch (IllegalStateException e) {
                e.printStackTrace();
            }



        }else if(Local==false){
            recyclerView.setEnabled(true);
            recyclerView.setVisibility(View.VISIBLE);
            try {
                final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),
                        2,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(gridLayoutManager);
               /* recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(CloudAlbum.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        })
                );
*/
            }catch (IllegalStateException e){
                e.printStackTrace();
            }


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
            Toast.makeText(getApplicationContext(),"Parse Exception",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return Result;
        }



    private void RenameSituation(final String s) {

        Renamesituation = new Dialog(CloudAlbum.this);
        Renamesituation.setContentView(R.layout.create_new_situation_layout);
        Renamesituation.setCancelable(false);
        final EditText SituationName = Renamesituation.findViewById(R.id.situation_name);
        SituationName.requestFocus();
        Button Done ,Cancel;
        Done =   Renamesituation.findViewById(R.id.done_btn);
        Cancel = Renamesituation.findViewById(R.id.cancel_btn);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(SituationName.getText().toString()))
                {
                    databaseReference.child(s).child("name").setValue(SituationName.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                Toast.makeText(CloudAlbum.this,"Situation renamed as : "+SituationName.getText().toString(),Toast.LENGTH_SHORT).show();
                                SituationName.setText("");
                                Renamesituation.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(e.toString().contains("FirebaseNetworkException"))
                                Toast.makeText(CloudAlbum.this,"Not Connected to Internet.",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(CloudAlbum.this,"Unable to rename new Situation.", Toast.LENGTH_SHORT).show();

                            SituationName.setText("");
                        }
                    });
                    Renamesituation.dismiss();
                }
                else
                {
                    Toast.makeText(CloudAlbum.this,"No name given",Toast.LENGTH_LONG).show();
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Renamesituation.dismiss();
            }
        });

    }



}


