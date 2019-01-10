package integrals.inlens.ViewHolder;

/**
 * Created by Athul Krishna on 08/02/2018.
 */
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import integrals.inlens.Models.Participants;
import integrals.inlens.R;


/**
 * Created by Athul Krishna on 27/08/2017.
 */

   public class AlbumViewHolder extends RecyclerView.ViewHolder {

    private View view;
    public Button ShareButton;
    public ImageButton AlbuymCoverEditBtn;
    public ImageButton ParticipantsMoreBtn;
    public Dialog UserDialog;

    public AlbumViewHolder(View ItemView) {
        super(ItemView);
        view=ItemView;
        ShareButton=(Button)view.findViewById(R.id.ShareAlbum);
        AlbuymCoverEditBtn = view.findViewById(R.id.changecover);
        ParticipantsMoreBtn = view.findViewById(R.id.ParticipantsRecyclerView_more_btn);

        }

    public void SetAlbumEventType(String EventType)
    {
        TextView EvenTypetText=(TextView)view.findViewById(R.id.AlbumEventType);
        EvenTypetText.setText(EventType);
    }

    public void SetAlbumEndDate(String EventEnd)
    {
        TextView EventEndText=(TextView)view.findViewById(R.id.EventEndTime);
        EventEndText.setText(EventEnd);
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


         UserDialog = new Dialog(context,android.R.style.Theme_Light_NoTitleBar);
         UserDialog.setCancelable(true);
         UserDialog.setCanceledOnTouchOutside(true);
         UserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         UserDialog.setContentView(R.layout.custom_profile_dialog);

         final ProgressBar progressBar = UserDialog.findViewById(R.id.custom_profile_dialog_progressbar);
         final CircleImageView UserImage = UserDialog.findViewById(R.id.custom_profile_dialog_userprofilepic);
         ImageButton ChangeuserImage = UserDialog.findViewById(R.id.custom_profile_dialog_profilechangebtn);
         ChangeuserImage.setVisibility(View.GONE);
         final TextView ProfileUserEmail = UserDialog.findViewById(R.id.custom_profile_dialog_useremail);
         final TextView ProfileuserName = UserDialog.findViewById(R.id.custom_profile_dialog_username);
         ImageButton CloseProfileDialog = UserDialog.findViewById(R.id.custom_profile_dialog_closebtn);

         CloseProfileDialog.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 UserDialog.dismiss();
             }
         });

         if(UserDialog.getWindow()!=null)
         {
             UserDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

             Window UserDialogWindow = UserDialog.getWindow();
             UserDialogWindow.setGravity(Gravity.BOTTOM);
             UserDialogWindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
             UserDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
             UserDialogWindow.setDimAmount(0.75f);

         }



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

                         UserRef.child(model.getPhotographer_UID()).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                 if(dataSnapshot.hasChild("Communities"))
                                 {
                                     for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                     {
                                     }
                                 }

                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });

                         ProfileUserEmail.setText("Email : "+model.getEmail_ID());

                         if(!TextUtils.isEmpty(model.getProfile_picture()))
                         {
                             RequestOptions requestOptions=new RequestOptions()
                                     .fitCenter();

                             Glide.with(view)
                                     .load(model.getProfile_picture())
                                     .apply(requestOptions)
                                     .listener(new RequestListener<Drawable>() {
                                         @Override
                                         public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                             progressBar.setVisibility(View.GONE);
                                             return false;
                                         }

                                         @Override
                                         public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                             progressBar.setVisibility(View.GONE);
                                             return false;
                                         }
                                     })
                                     .into(UserImage);
                         }
                         else
                         {
                             Glide.with(view).load(R.drawable.ic_account_200dp).into(UserImage);
                             progressBar.setVisibility(View.GONE);
                         }
                         ProfileuserName.setText(model.getName());
                         UserDialog.show();

                     }
                 });

             }

         };
         ParticipantsRecyclerView.setAdapter(firebaseRecyclerAdapter1);



     }




  }