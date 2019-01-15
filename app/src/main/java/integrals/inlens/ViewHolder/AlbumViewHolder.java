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
import android.widget.LinearLayout;
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
    public Dialog UserDialog;
    public ImageButton StarAlbum,ParticipantsAlbum,AlbumCoverEditBtn,DetailsAlbumn,ShareAlbum;
    public LinearLayout AlbumContainer;

    public AlbumViewHolder(View ItemView) {
        super(ItemView);
        view=ItemView;
        AlbumCoverEditBtn = view.findViewById(R.id.album_changecover_btn);
        StarAlbum = view.findViewById(R.id.album_like_btn);
        ParticipantsAlbum = view.findViewById(R.id.album_participants_btn);
        DetailsAlbumn = view.findViewById(R.id.album_details_btn);
        ShareAlbum = view.findViewById(R.id.album_share_btn);
        AlbumContainer = view.findViewById(R.id.album_card_button_container);
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




  }