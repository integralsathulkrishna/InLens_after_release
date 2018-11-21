package integrals.inlens.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.Models.Blog;
import integrals.inlens.R;

public class QueueImageAdapter extends RecyclerView.Adapter<QueueImageAdapter.QueueViewHolder>{
    private List<Blog> blogList;
    private Context    context;
    private Activity   activity;
    private UploadDatabaseHelper uploadDatabaseHelper;
    private RecyclerView recyclerView;
    public QueueImageAdapter(List<Blog> blogList,
                             Context context, Activity activity,
                             UploadDatabaseHelper uploadDatabaseHelper,
                             RecyclerView recyclerView) {
        this.blogList = blogList;
        this.context = context;
        this.activity = activity;
        this.uploadDatabaseHelper=uploadDatabaseHelper;
        this.recyclerView=recyclerView;

    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upload_queue_card,parent,false);
        return new QueueImageAdapter.QueueViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, final int position) {
        RequestOptions requestOptions=new RequestOptions()
                .centerCrop()
                .override(176,176);
        Glide.with(context)
                .load(blogList.get(position).getImage())
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(holder.imageView);

          holder.deleteButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
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
                          DeleteImage((position+1),blogList.get(position).getImageThumb());
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






              }
          });


    }

    private void DeleteImage(int position,String ImageThumb) {
        position=GetPostionFromDatabase(ImageThumb);
        uploadDatabaseHelper.UpdateUploadStatus(position,"DO_NOT_UPLOAD");
        Toast.makeText(context,"Image Deleted",Toast.LENGTH_SHORT).show();
        recyclerView.removeAllViews();
    }

    private int GetPostionFromDatabase(String imageThumb) {
    int Position=0;
    for(int i=1;i<=uploadDatabaseHelper.GetNumberOfRows();i++){
        if(uploadDatabaseHelper.GetPhotoUri(i).contentEquals(imageThumb)){
            Position=i;
        }
    }

    return Position;

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class QueueViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public Button    deleteButton;
        public QueueViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.uploadingQueueImageView_card);
            deleteButton=(Button)itemView.findViewById(R.id.delete_Btn_uploading_queue);
            }
    }
}
