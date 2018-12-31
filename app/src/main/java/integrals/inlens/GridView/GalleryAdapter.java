package integrals.inlens.GridView;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import integrals.inlens.R;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int Positions[];
    public Context context;
    public List<ImageModel> data = new ArrayList<>();
    private View v;
    private RecyclerView.ViewHolder viewHolder;
    private int Total;
    private Boolean FirstImageClicked=false;
    public GalleryAdapter(Context context, List<ImageModel> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item, parent, false);
            v.setVisibility(View.VISIBLE);
            viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        RequestOptions requestOptions=new RequestOptions()
                .override(360,240)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_deleted);

        File file = new File(data.get(position).getUrl());
        if(file.exists() && !TextUtils.isEmpty(data.get(position).getUrl()))
        {
            Glide.with(context).load(data.get(position).getUrl())
                    .thumbnail(0.5f)
                    .apply(requestOptions)
                    .into(((MyItemHolder) holder).mImg);
        }
        else
        {
            holder.itemView.setVisibility(View.GONE);
        }


    }




    @Override
    public int getItemCount() {
        return data.size();
    }



    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;


        public MyItemHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.item_img);

            }


    }


}
