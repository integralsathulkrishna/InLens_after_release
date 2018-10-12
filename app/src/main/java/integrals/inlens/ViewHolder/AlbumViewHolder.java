package integrals.inlens.ViewHolder;

/**
 * Created by Athul Krishna on 08/02/2018.
 */
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;

import integrals.inlens.Activities.DisplayPhotographer;
import integrals.inlens.Models.Participants;
import integrals.inlens.R;


/**
 * Created by Athul Krishna on 27/08/2017.
 */

   public class AlbumViewHolder extends RecyclerView.ViewHolder {

    private View view;
    public Button ShareButton;
    public ImageButton AlbuymCoverEditBtn;

    public AlbumViewHolder(View ItemView) {
        super(ItemView);
        view=ItemView;
        ShareButton=(Button)view.findViewById(R.id.ShareAlbum);
        AlbuymCoverEditBtn = view.findViewById(R.id.changecover);

        }


    public void SetAlbumCover(Context context,String Uri){
         ImageView imageView=(ImageView)view.findViewById(R.id.CloudAlbumCover);
        RequestOptions requestOptions=new RequestOptions()
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
     public void SetParticipants(final Context context, DatabaseReference  participantReference){
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
                         Intent i= new Intent(context,DisplayPhotographer.class);
                         i.putExtra("EMAIL_ID", model.getEmail_ID());
                         i.putExtra("PROFILE_PIC", model.getProfile_picture());
                         i.putExtra("USERNAME", model.getName());
                         i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         context.startActivity(i);
                     }
                 });

             }

         };
         ParticipantsRecyclerView.setAdapter(firebaseRecyclerAdapter1);



     }




  }