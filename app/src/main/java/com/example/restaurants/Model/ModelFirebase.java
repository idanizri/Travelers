package com.example.restaurants.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {
    private static final String POSTS_TABLE_NAME = "posts";
    private static final String SEARCH_TABLE_NAME = "searches";
    private static final String LOG_TAG = ModelFirebase.class.getSimpleName();
    private ValueEventListener eventListener;
    private ValueEventListener searchEventListener;

    private static final Object LOCK = new Object();
    private static ModelFirebase instance;
    private DatabaseReference mDatabase;


    public static ModelFirebase getInstance() {
        if(instance == null)
            synchronized (LOCK){
                instance = new ModelFirebase();
            }
            return instance;
    }

    private ModelFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public void insertPost(Post post){
        mDatabase.child(POSTS_TABLE_NAME).child(post.getId()).setValue(post);
    }

    public void deletePost(Post post){
        mDatabase.child(POSTS_TABLE_NAME).child(post.getId()).removeValue();
    }

    public void cancelGetAllPosts() {
        mDatabase.child(POSTS_TABLE_NAME).removeEventListener(eventListener);
    }

    public void getAllPosts(final GetAllPostsListener listener) {
        DatabaseReference mRef = mDatabase.child(POSTS_TABLE_NAME);



        eventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> postList = new LinkedList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                listener.onSuccess(postList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getPostsByLocation(final String location,final Model.onGetPostByLocation listener){

        DatabaseReference mRef = mDatabase.child(POSTS_TABLE_NAME);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedList<Post> postList = new LinkedList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Post post = data.getValue(Post.class);
                        if(post.getLocation().toLowerCase().contains(location.toLowerCase()))
                            postList.add(post);
                    }
                    listener.onDone(postList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public interface GetAllPostsListener{
        void onSuccess(List<Post> postsList);
    }



/////////////////////////////////   PHOTOS    ///////////////////////////////////////

    
    public void saveImage(Bitmap imageBitmap, final Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://restaurants-a7640.appspot.com/");
        FirebaseStorage temp = FirebaseStorage.getInstance();
        Date d = new Date();
        String name = ""+ d.getTime();
        final StorageReference imagesRef = storageRef.child("images/"+name+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onDone(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //do your stuff- uri.toString() will give you download URL\\
                        listener.onDone(uri.toString());
                    }
                });
            }
        });
    }

    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                listener.onDone(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                Log.d("TAG","get image from firebase Failed");
                listener.onDone(null);
            }
        });
    }


    /////////////////////////////////// Searches ///////////////////////////////////////////////////////


    public void insertSearch(SearchQuery query){
        mDatabase.child(SEARCH_TABLE_NAME).child(query.getQuery()).setValue(query);
    }

    public void deleteSearch(SearchQuery query){
        mDatabase.child(SEARCH_TABLE_NAME).child(query.getQuery()).removeValue();
    }

    public void cancelGetAllSearchQueries() {
        mDatabase.child(SEARCH_TABLE_NAME).removeEventListener(searchEventListener);
    }


    public interface GetAllSearchQueriesListener{
        void onSuccess(List<SearchQuery> searchQueryList);
    }

    public void getAllSearchQueries(final GetAllSearchQueriesListener listener) {
        DatabaseReference mRef = mDatabase.child(SEARCH_TABLE_NAME);

        searchEventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<SearchQuery> searchQueryList = new LinkedList<>();

                for (DataSnapshot querySnapshot: dataSnapshot.getChildren()) {
                    SearchQuery query = querySnapshot.getValue(SearchQuery.class);
                    searchQueryList.add(query);
                }
                listener.onSuccess(searchQueryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

//    public interface onGotSearchByNameListener{
//        void onComplete(SearchQuery search);
//    }
//
//    public void getSearchByQuery(String query,final onGotSearchByNameListener listener){
//        DatabaseReference mRef = mDatabase.child(SEARCH_TABLE_NAME).child(query);
//
//        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                listener.onComplete(dataSnapshot.getValue(SearchQuery.class));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });
//    }

//    public interface onGotSearchTopFive{
//        void onComplete(List<SearchQuery> search);
//    }

//    public void getTopThreeSearches(final onGotSearchTopFive listener){
//        DatabaseReference mRef = mDatabase.child(SEARCH_TABLE_NAME);
//
//        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                LinkedList<SearchQuery> queriesList = new LinkedList<>();
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    SearchQuery currQuery = data.getValue(SearchQuery.class);
//                    queriesList.add(currQuery);
//                }
//
//                Collections.sort(queriesList, new Comparator<SearchQuery>() {
//                    @Override
//                    public int compare(SearchQuery o1, SearchQuery o2) {
//                        return o2.getSearchesAmount() - o1.getSearchesAmount();
//                    }
//                });
//
//                if(queriesList.size() > 3)
//                    listener.onComplete(queriesList.subList(0,3));
//                else{
//                    listener.onComplete(queriesList);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });
//    }
}
