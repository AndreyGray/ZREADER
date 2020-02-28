package com.example.zreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.zreader.model.Book;

import java.util.List;

/*

This class for creating dialogs to choose number of books per row

 */

public class CollDialogFragment extends DialogFragment {

    private List<Book> books;
    private String[] indexes;
    private int result;


    public CollDialogFragment(List<Book> books) {
        this.books = books;
        indexes =new String[books.size()-1];
        loadIndexes();
    }

    private void loadIndexes() {
        int temp;
        for (int i = 0; i < books.size()-1 ; i++) {
            if(books.get(i).getID() !=0 ) {
                temp = i + 2;
                indexes[i] = String.valueOf(temp);

            }
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialog_title))
                .setSingleChoiceItems(indexes, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result =which+2;
                            }
                        }).setPositiveButton(getResources().getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent("send");
                                intent.putExtra("RES",result);
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
                                        intent);

                        }
                        }).setNegativeButton(getResources().getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
            }
        });

        return builder.create();
    }
}
