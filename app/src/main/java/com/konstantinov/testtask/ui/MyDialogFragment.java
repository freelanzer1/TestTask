package com.konstantinov.testtask.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.konstantinov.testtask.R;

public class MyDialogFragment extends DialogFragment {
    private Object contextp;
    private Uri mUri;
    private String mfileName;
    public MyDialogFragment(Object context, Uri uri, String fileName) {
        mfileName = fileName;
        contextp = context;
        mUri = uri;
    }
    private LinearLayout mlayout;
    private TextView textName;
    private TextView textSurname;
    private TextView textPatronymic;
    public ProgressBar progressBar;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Используем класс Builder для удобного построения диалогов
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity ( ) );
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final LinearLayout layout = (LinearLayout) inflater.inflate( R.layout.my_dialog, null, false);
        mlayout = layout;
        TextView textFile = layout.findViewById ( R.id.textViewFile );
        textFile.setText ( textFile.getText () + " " + mfileName );
        progressBar = layout.findViewById(R.id.progressBar2);
        progressBar.setVisibility( ProgressBar.INVISIBLE);



        builder
                .setView(layout)
                .setTitle("Отправить фото на сервер?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Здесь ничего не делаем, потому что мы переопределим эту кнопку позже, чтобы изменить поведение закрытия.
                        //Однако нам это все еще нужно, потому что в старых версиях Android, если мы
                        //передадим обработчик, кнопка не создается

                    }
                })
                .setNeutralButton("Переснять", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((OpencartsActivity)getActivity()).intentsPhoto ();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Пользователь отменил диалог
                        dialog.dismiss();
                    }
                });
        // Создаем объект AlertDialog и возвращаем его
        return builder.create();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            final Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()//переопределяем слушатель для позитивбуттон
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;

                    if (checkForm ( mlayout ) == true){

                        boolean sendphoto = ((OpencartsActivity)getActivity()).sendPhoto (textName.getText ().toString(), textSurname.getText ().toString(),textPatronymic.getText ().toString());
                        if (sendphoto == true) {
                            d.dismiss ( );
                        }

                    }else {
                        Toast toast = Toast.makeText ( getContext ( ),
                                "Заполните все поля", Toast.LENGTH_SHORT );
                        toast.show ( );
                    }
                    if(wantToCloseDialog)
                        d.dismiss();
                }
            });
        }
    }

    private boolean checkForm(LinearLayout layout){ //проверяем поля на заполнение
        textName = layout.findViewById ( R.id.editTextName );
        textSurname = layout.findViewById ( R.id.editTextSurname );
        textPatronymic = layout.findViewById ( R.id.editTextPatronymic );

        if (textName.length() > 0 && textSurname.length() > 0 && textPatronymic.length() > 0){

           return true;
        }
        else {
            return false;
        }
    }
}
