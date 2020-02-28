package com.example.zreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.zreader.adapter.MyAdapter;
import com.example.zreader.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<Book> books;
    int num_columns;
    static  int columns;

    //declare an instance of LocalBroadcastReceiver
    private BroadcastReceiver localBroadcastReceiver;

    SharedPreferences sp;


    public static ThumbnailDownloader<MyAdapter.MyViewHolder> mThumbnailDownloader;

    private final String mURL = "http://www.lukaspetrik.cz/filemanager/tmp/reader/data.xml";
    private static final String MY_SETTINGS = "my_settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiating the SharedPreferences for saving last selected value of the numbers  columns
        sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        columns = sp.getInt("columns",2);

        //instantiating the BroadcastReceiver
        localBroadcastReceiver = new LocalBroadcastReceiver();

        //floating button which create dialog  and show hint at snackbar
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getResources().getString(R.string.dialog_title), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                FragmentManager manager = getSupportFragmentManager();
                CollDialogFragment myDialogFragment = new CollDialogFragment(books);
                myDialogFragment.show(manager, "myDialog");
            }
        });

        //calling downloading xml from url
        DownloadData downloadData = new DownloadData();
        downloadData.execute(mURL);

        //downloading cover images
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<MyAdapter.MyViewHolder>() {
                    @Override
                    public void onThumbnailDownloaded(MyAdapter.MyViewHolder myViewHolder,
                                                      Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        myViewHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i("Background", "Background thread started");

        initView();

    }

    //setts numbers columns in layout manager with checking devise orientation
    private void setData(int col) {

        recyclerView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            num_columns=col;
            addEmptyItems(col);
        }else {
            num_columns=col+1;
            addEmptyItems(num_columns);
        }
        if(adapter==null)adapter = new MyAdapter(books,this);
        GridLayoutManager layoutManager = new GridLayoutManager(this,num_columns);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    //add empty items to fill the rows
    private void addEmptyItems(int col) {
        for (int i = 0; i < books.size(); i++) {
            if(books.get(i).getID()==0)books.remove(i);
        }
        while (books.size()%col != 0) books.add(new Book());
    }

    private void initView() {
        recyclerView = findViewById(R.id.my_recycler);
    }

    //inner asyncTask class for downloading xml file
    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "xml document: " + s);
            BookXmlParser parser = new BookXmlParser();
            if (s != null && parser.parse(s)) {
                books = parser.getBooks();
                setData(columns);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String content = null;
            try {

                content = downloadXML(strings[0]);
            } catch (IOException ex) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + ex.getMessage());
            }
            return content;
        }

        private String downloadXML(String urlPath) throws IOException {
            StringBuilder xmlResult = new StringBuilder();
            BufferedReader reader = null;
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    xmlResult.append(line);
                }
                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception.  Needs permisson? " + e.getMessage());
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
            return null;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //destroying background thread and clearing queue
        mThumbnailDownloader.quit();
        mThumbnailDownloader.clearQueue();

        //remember in sharedPreferences value of the number of columns
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("columns", columns);
        edit.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //registering for the activity to listen out for the local broadcasts
        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastReceiver,
                new IntentFilter("send"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregistering the broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                localBroadcastReceiver);
    }

    //defining a BroadcastReceiver
    class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals("send")) {
                columns = intent.getIntExtra("RES",2);

                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    if(columns<3||columns>books.size()){
                        columns = 2;
                    }else
                        columns = columns-1;

                setData(columns);
            }
        }
    }
}