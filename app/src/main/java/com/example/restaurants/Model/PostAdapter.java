package com.example.restaurants.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.segev.restaurants.R;

import java.io.Serializable;
import java.util.List;

/**
 * This PostAdapter creates and binds ViewHolders, that hold the posts,
 * to a RecyclerView to efficiently display data.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements Serializable{
    private static final String LOG_TAG = PostAdapter.class.getSimpleName();

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;

    // Class variables for the List that holds task data and the Context
    private List<Post> mPostEntries;
    private Context mContext;


    public PostAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.post_layout, parent, false);

        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        // Determine the values of the wanted data
        Post postEntry = mPostEntries.get(position);


        String title = postEntry.getTitle();
        String location = postEntry.getLocation();
        String imageURL = postEntry.getImage();
        String email = postEntry.getPostingUserEmail();

        Model.getInstance().getImage(imageURL, new Model.GetImageListener() {
            @Override
            public void onDone(Bitmap imageBitmap) {
                holder.mPostImageView.setImageBitmap(imageBitmap);
            }
        });

        //Set values
        holder.mPostTitleField.setText(title);
        holder.mPostLocationField.setText(location);
        holder.mPostEmail.setText(email);

    }


    @Override
    public int getItemCount() {
        if (mPostEntries == null) {
            return 0;
        }
        return mPostEntries.size();
    }


    public List<Post> getPosts(){
        return mPostEntries;
    }

    public void setPosts(List<Post> postEntries) {
        mPostEntries = postEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView mPostTitleField;
        TextView mPostLocationField;
        ImageView mPostImageView;
        TextView mPostEmail;


        public PostViewHolder(View itemView) {
            super(itemView);

            mPostTitleField = itemView.findViewById(R.id.post_layout_title);
            mPostLocationField = itemView.findViewById(R.id.post_layout_location);
            mPostImageView = itemView.findViewById(R.id.post_layout_imageView);
            mPostEmail = itemView.findViewById(R.id.post_layout_user_email);

            itemView.setOnClickListener(this);
        }

        public String getViewHolderPostIdByPos(int pos){
            if(pos > mPostEntries.size())
                return "";

            Post post = mPostEntries.get(pos);

            if(post != null){
                return post.getId();
            }
            return "";
        }

        @Override
        public void onClick(View view) {
            int elementId = Integer.parseInt(mPostEntries.get(getAdapterPosition()).getId());
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}