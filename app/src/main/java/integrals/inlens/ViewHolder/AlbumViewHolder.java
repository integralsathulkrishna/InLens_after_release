package integrals.inlens.ViewHolder;

/**
 * Created by Athul Krishna on 08/02/2018.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import integrals.inlens.Activities.DisplayPhotographer;
import integrals.inlens.Helper.ProfileDilaogHelper;
import integrals.inlens.Models.Participants;
import integrals.inlens.R;


/**
 * Created by Athul Krishna on 27/08/2017.
 */

   public class AlbumViewHolder extends RecyclerView.ViewHolder {

    private View view;
    public Button ShareButton;
    public ImageButton AlbuymCoverEditBtn;
    private int dbcount;

    public AlbumViewHolder(View ItemView) {
        super(ItemView);
        view=ItemView;
        ShareButton=(Button)view.findViewById(R.id.ShareAlbum);
        AlbuymCoverEditBtn = view.findViewById(R.id.changecover);

        }


    public void SetAlbumCover(Context context,String Uri){
         ImageView imageView=(ImageView)view.findViewById(R.id.CloudAlbumCover);
        RequestOptions requestOptions=new RequestOptions()
                .placeholder(R.drawable.image_avatar)
                .fitCenter();

        Glide.with(context)
                .load(Uri)
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(imageView);
    }
    public void SetTitle(String Text){
        TextView textView=(TextView)view.findViewById(R.id.AlbumTitle);
        textView.setText(Text);
    }
    public void SetAlbumTime(String Text){
        TextView textView=(TextView)view.findViewById(R.id.EventOccurTime);
        textView.setText(Text);
    }

    public void SetProfilePic(Context context,String ImageUri){
        ImageView imageView=(ImageView)view.findViewById(R.id.CreatedByProfilePic);
         RequestOptions requestOptions=new RequestOptions()
                 .centerCrop()
                 .placeholder(R.drawable.ic_account_circle)
                 .override(176,176);
         ;
         Glide.with(context)
                 .load(ImageUri)
                 .thumbnail(0.1f)
                 .apply(requestOptions)
                 .into(imageView);

     }
     public void SetAlbumDescription(String Desc){
         TextView textView=(TextView)view.findViewById(R.id.AlbumDescription);
         textView.setText(Desc);
     }
     public void SetParticipants(final Context context, DatabaseReference participantReference, final DatabaseReference UserRef){


         final ProfileDilaogHelper UserDialog = new ProfileDilaogHelper(context);
         UserDialog.setCancelable(true);
         if(UserDialog.getWindow()!=null)
             UserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


         RecyclerView ParticipantsRecyclerView=(RecyclerView)view.findViewById(R.id.ParticipantsRecyclerView);
         ParticipantsRecyclerView.setHasFixedSize(true);
         LinearLayoutManager linearLayoutManager= new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
         ParticipantsRecyclerView.setLayoutManager(linearLayoutManager);

         final FirebaseRecyclerAdapter<Participants, ParticipantsViewHolder> firebaseRecyclerAdapter1 =
                 new FirebaseRecyclerAdapter<Participants, ParticipantsViewHolder>(
                 Participants.class,
                 R.layout.member_card,
                 ParticipantsViewHolder.class,
                 participantReference
         ) {
             @Override
             protected void populateViewHolder(ParticipantsViewHolder viewHolder,
                                               final Participants model,
                                               int position) {
                 viewHolder.setProfile_picture(context, model.getProfile_picture());
                 viewHolder.setUserName(model.getName());
                 viewHolder.InView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         dbcount = 0;
                         UserRef.child(model.getPhotographer_UID()).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                 if(dataSnapshot.hasChild("Communities"))
                                 {
                                     for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                     {
                                         dbcount++;
                                     }
                                 }

                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });

                         UserDialog.setUserAlbumCount("Album Count : "+dbcount);
                         UserDialog.setUserEmail("Email : "+model.getEmail_ID());
                         UserDialog.setUserImage(model.getProfile_picture());
                         UserDialog.setUserRating("3.5");
                         UserDialog.setUserName(model.getName());
                         UserDialog.setImageChangeBtnVisibility(false);
                         UserDialog.show();

                     }
                 });

             }

         };
         ParticipantsRecyclerView.setAdapter(firebaseRecyclerAdapter1);



     }




  }