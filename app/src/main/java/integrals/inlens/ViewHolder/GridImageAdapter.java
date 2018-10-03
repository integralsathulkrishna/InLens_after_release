package integrals.inlens.ViewHolder;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import integrals.inlens.Activities.PhotoView;
import integrals.inlens.Models.Blog;
import integrals.inlens.R;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.GridImageViewHolder> {
        private Context      context;
        private List<Blog>   BlogList;
        private List<String> BlogIDList;
        private Activity activity;
        private FirebaseStorage mFirebaseStorage;
        private DatabaseReference databaseReference;
    public GridImageAdapter(Context context,
                            List<Blog> blogList,
                            List<String> blogIDList,
                            Activity activity,
                            DatabaseReference databaseReference) {
        this.context = context;
        BlogList = blogList;
        BlogIDList = blogIDList;
        this.activity=activity;
        this.databaseReference=databaseReference;
        mFirebaseStorage=FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public GridImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_grid_card,parent,false);
        return new GridImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridImageViewHolder holder, final int position) {
        RequestOptions requestOptions=new RequestOptions()
                .centerCrop()
                .override(176,176);
        Glide.with(context)
                .load(BlogList.get(position).getImage())
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(holder.imageView);
        holder.OptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new BottomSheet.Builder(activity).title("Image Options").sheet(R.menu.photo_menu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.delete_photo:
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

                                // Setting Dialog Title
                                alertDialog.setTitle("Confirm Delete...");

                                // Setting Dialog Message
                                alertDialog.setMessage("Are you sure you want delete this?");

                                // Setting Icon to Dialog
                                alertDialog.setIcon(R.drawable.ic_deleted);

                                // Setting Positive "Yes" Button
                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        DeleteImage(position);
                                    }
                                });

                                // Setting Negative "NO" Button
                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                // Showing Alert Message
                                alertDialog.show();
                                break;

                            case R.id.copy_link:
                                Intent SharingIntent = new Intent(Intent.ACTION_SEND);
                                SharingIntent.setType("text/plain");
                                SharingIntent.putExtra(Intent.EXTRA_TEXT, "InLens" + "\n\t" + BlogList.get(position).getImageThumb());
                                SharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(SharingIntent);
                                break;
                        }
                    }
                }).show();

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context ,PhotoView.class);
                i.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) BlogList);
                i.putExtra("position",position);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return BlogList.size();
    }

    public class GridImageViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public Button    OptionButton;
        public GridImageViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.PhotoViewPhotoViewCard);
            OptionButton=(Button)itemView.findViewById(R.id.OptionsButton);

        }
    }
    private void DeleteImage(final int position){
        Toast.makeText(context,"Deleting Image ..Please wait",Toast.LENGTH_SHORT).show();
        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(BlogList.get(position).getImageThumb().toString());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(BlogIDList.get(position)).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(context,"Image Deleted",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(context,"Image Unable to delete ..Please check the internet connection",Toast.LENGTH_SHORT).show();

            }
        });



    }


}
