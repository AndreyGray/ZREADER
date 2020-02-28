package com.example.zreader.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zreader.MainActivity;
import com.example.zreader.R;
import com.example.zreader.ThumbnailDownloader;
import com.example.zreader.model.Book;

import java.util.List;
/*

This class is an adapter with inner class holder for recyclerView.

 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Book> books;
    private ThumbnailDownloader<MyViewHolder> mThumbnailDownloader;
    Context context;

    public MyAdapter(List<Book> books,Context context) {
        this.context=context;
        this.books = books;
        this.mThumbnailDownloader = MainActivity.mThumbnailDownloader;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        
        holder.bookTitle.setText(books.get(position).getTitle());
        if(books.get(position).isNew())holder.bookNew.setVisibility(View.VISIBLE);
        mThumbnailDownloader.queueThumbnail(holder, books.get(position).getThumbnail());
        if (books.get(position).getID()!=0) holder.bookCover.setBackgroundResource(R.drawable.book_shadow);
        if(books.get(position).getTitle() !=null)holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,
                        context.getResources().getString(R.string.selected_item) +books.get(position).getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView bookCover,bookNew;
        TextView bookTitle;
        LinearLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.card_cover);
            bookNew = itemView.findViewById(R.id.card_new);
            bookTitle = itemView.findViewById(R.id.card_text);
            container = itemView.findViewById(R.id.item);
        }

        public void bindDrawable(Drawable drawable){
            bookCover.setImageDrawable(drawable);
        }
    }


}
