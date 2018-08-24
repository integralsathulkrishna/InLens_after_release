package integrals.inlens.Activities;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import integrals.inlens.Models.Blog;
import integrals.inlens.R;
import integrals.inlens.ViewHolder.BlogViewHolder;

public class PhotoView_Recycler extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String CommunityID;
    private RecyclerView recyclerView;
    private int Position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_photo_view__recycler);
            Position=getIntent().getIntExtra("POSITION",1);
            recyclerView=(RecyclerView)findViewById(R.id.PhotoRecycler);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            CommunityID=getIntent().getStringExtra("GlobalID::");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("BlogPosts");



      }


    @Override
    protected void onStart() {
        super.onStart();
        CreateRecyclerView();
    }

    private void CreateRecyclerView() {
        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager lm, int velocityX, int velocityY) {
                View centerView = findSnapView(lm);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = lm.getPosition(centerView);
                int targetPosition = -1;
                if (lm.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (lm.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = lm.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        try {
            recyclerView.scrollToPosition(Position);
            snapHelper.attachToRecyclerView(recyclerView);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        final FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                        Blog.class,
                        R.layout.photo_card,
                        BlogViewHolder.class,
                        databaseReference

                ) {


                    @Override
                    protected void populateViewHolder(final BlogViewHolder viewHolder,
                                                      final Blog model,
                                                      int position) {
                        final String PostKey = getRef(position).getKey().toString().trim();
                        Position=position;
                        viewHolder.SetThumbPhoto(getApplicationContext(),getParent(),
                                model.getImageThumb(),model.getOriginalImageName(),
                                model.getBlogTitle(),model.getBlogDescription(),PostKey,model.getLocation(),
                                model.getWeatherDetails());

                        viewHolder.OriginalImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewHolder.LoadOriginalPhoto(getApplicationContext(),model.getOriginalImageName());
                            }
                        });

                    }



                    @Override
                    public void onViewDetachedFromWindow(@NonNull BlogViewHolder holder) {
                        super.onViewDetachedFromWindow(holder);
                        holder.CleanUp();
                    }
                };


        recyclerView.setAdapter(firebaseRecyclerAdapter);





    }

}

