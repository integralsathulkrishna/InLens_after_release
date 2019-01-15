package integrals.inlens.Helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import de.hdodenhof.circleimageview.CircleImageView;
import integrals.inlens.R;

public class ProfileDilaogHelper extends Dialog {

    private String UserName;
    private String UserImage;
    private String UserEmail;
    private String UserAlbumCount;
    private String UserRating;
    private Boolean ImageChangeBtnVisibility;



    public Boolean getImageChangeBtnVisibility() {
        return ImageChangeBtnVisibility;
    }

    public void setImageChangeBtnVisibility(Boolean imageChangeBtnVisibility) {
        ImageChangeBtnVisibility = imageChangeBtnVisibility;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserAlbumCount() {
        return UserAlbumCount;
    }

    public void setUserAlbumCount(String userAlbumCount) {
        UserAlbumCount = userAlbumCount;
    }

    public String getUserRating() {
        return UserRating;
    }

    public void setUserRating(String userRating) {
        UserRating = userRating;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_profile_dialog);

        Window DialogHelper = getWindow();
        DialogHelper.setGravity(Gravity.BOTTOM);
        DialogHelper.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        DialogHelper.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        DialogHelper.setDimAmount(0.75f);

        final View view =findViewById(android.R.id.content);
        view.setBackgroundColor(Color.WHITE);
        CircleImageView CustomDialogUserImage = findViewById(R.id.custom_profile_dialog_userprofilepic);
        TextView CustomDialogUserName = findViewById(R.id.custom_profile_dialog_username);
        TextView CustomDialogUserEmail = findViewById(R.id.custom_profile_dialog_useremail);
        final ProgressBar CustomDialogProgressbar = findViewById(R.id.custom_profile_dialog_progressbar);
        CustomDialogProgressbar.setVisibility(View.VISIBLE);
        ImageButton UserProfileImageChangeBtn = findViewById(R.id.custom_profile_dialog_profilechangebtn);
        ImageButton CustomUserProfileCloseBtn = findViewById(R.id.custom_profile_dialog_closebtn);

        if(getImageChangeBtnVisibility())
            UserProfileImageChangeBtn.setVisibility(View.VISIBLE);
        else
            UserProfileImageChangeBtn.setVisibility(View.GONE);

        CustomUserProfileCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           dismiss();

            }
        });



        if(!TextUtils.isEmpty(getUserImage()))
        {
            RequestOptions requestOptions=new RequestOptions()
                    .fitCenter();

            Glide.with(view)
                    .load(getUserImage())
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            CustomDialogProgressbar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            CustomDialogProgressbar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(CustomDialogUserImage);
        }
        else
        {
            Glide.with(view).load(R.drawable.ic_account_200dp).into(CustomDialogUserImage);
            CustomDialogProgressbar.setVisibility(View.GONE);
        }

        CustomDialogUserName.setText(getUserName());
        CustomDialogUserEmail.setText(getUserEmail());
    }

    public ProfileDilaogHelper(@NonNull Context context) {
        super(context);
    }

    public ProfileDilaogHelper(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ProfileDilaogHelper(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
