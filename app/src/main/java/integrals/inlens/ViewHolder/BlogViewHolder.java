package integrals.inlens.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import integrals.inlens.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Athul Krishna on 27/08/2017.
 */

public class BlogViewHolder extends RecyclerView.ViewHolder {

    private PhotoViewAttacher photoViewAttacher;
    public View view;
    public Button OriginalImageButton;


    public BlogViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        OriginalImageButton=(Button)view.findViewById(R.id.OriginalImageButton);
    }


    /*public void SetPhoto(final Context context,
                         String ImageUri,
                         final String getImage
            , final String getBlogTitle
            , final String getBlogDescription,
                          final  String UserName,
                          final  String TimeS,
                         final  String PostKey) {
        ImageView imageView = (ImageView) view.findViewById(R.id.PhotoViewPhotoViewCard);
        RequestOptions requestOptions=new RequestOptions()
                .centerCrop()

                .override(176,176);
        Glide.with(context)
                .load(ImageUri)
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(imageView);


        TextView textView=(TextView)view.findViewById(R.id.DateTextPhotoCard);
        textView.setText(TimeS);
        TextView textView1=(TextView)view.findViewById(R.id.CreatedByUserNamePhotoCard);
        textView1.setText(UserName);


    }*/
    public void LoadOriginalPhoto(final Context context,
                                  final String OriginalImageUri
                                  ){
        ImageView imageView=(ImageView)view.findViewById(R.id.PhotoCardImageView);
        final ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.ImageProgress);

        Glide.with(context)
                .load(OriginalImageUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context,"Load Failed. Please check your internet connection. ",Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }



                })
                .into(imageView);
         photoViewAttacher=new PhotoViewAttacher(imageView);

    }


}


