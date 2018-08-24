package integrals.inlens.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import integrals.inlens.Models.Blog;
import integrals.inlens.R;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.GridImageViewHolder> {
        private Context      context;
        private List<Blog>   BlogList;
        private List<String> BlogIDList;

    public GridImageAdapter(Context context,
                            List<Blog> blogList,
                            List<String> blogIDList) {
        this.context = context;
        BlogList = blogList;
        BlogIDList = blogIDList;
    }

    @NonNull
    @Override
    public GridImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_grid_card,parent,false);
        return new GridImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridImageViewHolder holder, int position) {
        RequestOptions requestOptions=new RequestOptions()
                .centerCrop()
                .override(176,176);
        Glide.with(context)
                .load(BlogList.get(position).getImage())
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return BlogList.size();
    }

    public class GridImageViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public GridImageViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.PhotoViewPhotoViewCard);
            }
    }
}
