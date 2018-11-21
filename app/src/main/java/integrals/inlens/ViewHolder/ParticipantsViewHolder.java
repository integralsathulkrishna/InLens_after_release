package integrals.inlens.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import integrals.inlens.R;

/**
 * Created by Athul Krishna on 27/08/2017.
 */
public  class ParticipantsViewHolder extends RecyclerView.ViewHolder{
    public View InView;

    public ParticipantsViewHolder(View ItemView) {
        super(ItemView);
        InView=ItemView;
    }


    public void setProfile_picture(Context context, String ImageVi){
        ImageView PostImage=(ImageView) InView.findViewById(R.id.participants_profile_pic);
        RequestOptions requestOptions=new RequestOptions()
                .placeholder(R.drawable.ic_account_circle)
                .fitCenter();
        ;
        Glide.with(context)
                .load(ImageVi)
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(PostImage);
    }
     void setUserName(String UserName){
        TextView textView=(TextView)InView.findViewById(R.id.participants_username);
        textView.setText(UserName);
    }



}
