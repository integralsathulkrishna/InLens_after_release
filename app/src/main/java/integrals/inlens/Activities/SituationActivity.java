package integrals.inlens.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import integrals.inlens.Helper.RecyclerItemClickListener;
import integrals.inlens.Models.Blog;
import integrals.inlens.R;
import integrals.inlens.ViewHolder.GridImageAdapter;

public class SituationActivity extends AppCompatActivity {
    private String              TimeStart=null,TimeEnd=null;
    private DatabaseReference   databaseReference=null;
    private String              CommunityID=null;
    private List<Blog>          BlogList;
    private List<String>        BlogListID;
    private GridImageAdapter    gridImageAdapter;
    private RecyclerView        recyclerView;
    private String              PhotoThumb;
    private String BlogTitle,ImageThumb,BlogDescription,Location;
    private String TimeTaken,UserName,User_ID,WeatherDetails,PostedByProfilePic;
    private String OriginalImageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_situation);
         BlogList=new ArrayList<>();
         BlogListID=new ArrayList<>();
         TimeStart=getIntent().getExtras().getString("TimeStart::");
         TimeEnd=getIntent().getExtras().getString("TimeEnd::");
         CommunityID=getIntent().getExtras().getString("GlobalID::");
         databaseReference = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("BlogPosts");
         recyclerView=(RecyclerView)findViewById(R.id.PhotoListRecyclerView);
         GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2, LinearLayoutManager.VERTICAL,false);
         recyclerView.setLayoutManager(gridLayoutManager);
         }


    @Override
    protected void onStart() {
        super.onStart();

        try {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    recyclerView.removeAllViews();
                    BlogList.clear();
                    BlogListID.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        if (snapshot.hasChildren()) {

                            if (CheckIntervel(snapshot.child("TimeTaken").getValue().toString(),TimeStart,TimeEnd)) {
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
                                { }
                                }
                    }

                    gridImageAdapter=new GridImageAdapter(
                            getApplicationContext(),
                            BlogList,
                            BlogListID
                    );
                    recyclerView.setAdapter(gridImageAdapter);



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            }catch (NullPointerException e){
            e.printStackTrace();
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(SituationActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String PostKey1 = BlogListID.get(position).toString().trim();

                        Intent i = new Intent(getApplicationContext(), PhotoView_Recycler.class);
                        i.putExtra("PHOTO_URI", BlogList.get(position).getImageThumb());

                        i.putExtra("PHOTO_TITLE", BlogList.get(position).getBlogTitle());
                        i.putExtra("PHOTO_DESCRIPTION", BlogList.get(position).getBlogDescription());
                        i.putExtra("POST_KEY", PostKey1);
                        i.putExtra("POSITION", position);
                        i.putExtra("GlobalID::", CommunityID);
                        startActivity(i);


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );




    }

    private boolean CheckIntervel(String timeTaken, String timeStart, String timeEnd) {
    Boolean Result=false;
        try{
            SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
            Date dt_1 = objSDF.parse(timeTaken);
            Date dt_2 = objSDF.parse(timeStart);
            Date dt_3=  objSDF.parse(timeEnd);
            if(dt_1.after(dt_2)&&(dt_1.before(dt_3))){
                Result=true;
            }
            else if(dt_1.after(dt_2)&&(dt_1).equals(dt_3)){
                Result=true;
            }
        }
        catch (ParseException e){
            Toast.makeText(getApplicationContext(),"Parse Exception",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return Result;
    }






}





                //Done for Implementation of Recycler View OnClick
                /*
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(CloudAlbum.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent= new Intent(CloudAlbum.this,SituationActivity.class);
                                intent.putExtra("TimeIntervel::",SituationList.get(position).getSituationTime());
                                intent.putExtra("GlobalID::",CommunityID);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                            }
                        })
                );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








    }
}


/*        final FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                        Blog.class,
                        R.layout.photo_grid_card,
                        BlogViewHolder.class,
                        databaseReference

                ) {
                    @Override
                    protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, final int position) {

                        String PostKey = getRef(position).getKey().toString().trim();
                        viewHolder.SetPhoto(
                                getApplicationContext(), model.getImage(),
                                model.getImage(),
                                model.getBlogTitle(),
                                model.getBlogDescription(),
                                model.getUserName(),
                                model.getTimeTaken(),
                                PostKey);
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String PostKey1 = getRef(position).getKey().toString().trim();

                                Intent i = new Intent(getApplicationContext(), PhotoView_Recycler.class);
                                i.putExtra("PHOTO_URI", model.getOriginalImageName());
                                i.putExtra("PHOTO_TITLE", model.getBlogTitle());
                                i.putExtra("PHOTO_DESCRIPTION", model.getBlogDescription());
                                i.putExtra("POST_KEY", PostKey1);
                                i.putExtra("POSITION", position);
                                i.putExtra("GlobalID::", CommunityID);
                                startActivity(i);

                            }
                        });
                    }


                 };
                recyclerView.setAdapter(firebaseRecyclerAdapter);
                */
