package com.example.restaurants.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restaurants.Fragments.PostDetailsFragment;
import com.example.restaurants.MyApplication;
import com.example.segev.restaurants.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter{
    private static final String LOG_TAG = ViewPagerAdapter.class.getSimpleName();
    AppCompatActivity activity;
   List<Post> postsOnViewPager;

    public ViewPagerAdapter(AppCompatActivity activity) {
        this.activity = activity;
        postsOnViewPager = new LinkedList<>();
        }


    public void setPosts(List<Post> postsToPresent){
        if(postsToPresent == null)
            return;

        if(postsToPresent.size() > 5){
            postsOnViewPager = postsToPresent.subList(0,5);
        }else{
            postsOnViewPager = postsToPresent;
        }

        Collections.sort(postsOnViewPager, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                 return (int)(o2.getUpdatedAt()- o1.getUpdatedAt());}
        });
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return postsOnViewPager.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.view_pager_post_layout,container,false);

        Post currentPostToPresent = postsOnViewPager.get(position);

        final TextView title = itemView.findViewById(R.id.viewpager_post_title);
        final View view = itemView.findViewById(R.id.view);
        final TextView location = itemView.findViewById(R.id.viewpager_post_location);

        title.setText(currentPostToPresent.getTitle());
        location.setText(currentPostToPresent.getLocation());

        Model.getInstance().getImage(currentPostToPresent.getImage(), new Model.GetImageListener() {
                @Override
                public void onDone(Bitmap imageBitmap) {
//                    relativeLayout.setBackground(new BitmapDrawable(MyApplication.context.getResources(),imageBitmap));
                    view.setBackground(new BitmapDrawable(MyApplication.context.getResources(),imageBitmap));
                }
            });

        container.addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
            onItemClicked(position);
            }
        });

        return itemView;
    }

    private void onItemClicked(int position) {
        Fragment fragment = new PostDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Post",postsOnViewPager.get(position));
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
