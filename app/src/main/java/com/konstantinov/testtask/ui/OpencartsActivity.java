package com.konstantinov.testtask.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.konstantinov.testtask.NetworkService;
import com.konstantinov.testtask.POJO.RespSendImage;
import com.konstantinov.testtask.R;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class OpencartsActivity extends AppCompatActivity {


    ImageView imageView;
    TextView textViewTarget;
    String id;

    private Uri outputFileUri;
    private Uri inputFileUri;

    private static final int TAKE_PICTURE_REQUEST = 1;
    private Object context;
    private File file;
    private File fileout;
    private RespSendImage otvet;
    private ConstraintLayout constLayout;
    private String fileName;

    private String nameSend;
    private String surnameSend;
    private String patronymicSend;
    private String contactString;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_opencarts );
        textViewTarget = findViewById(R.id.textViewTarget);
        imageView = findViewById(R.id.imageView);
        Bundle arguments = getIntent().getExtras();
        // получаем корневой элемент
        constLayout = (ConstraintLayout) findViewById(R.id.target);

        if (arguments == null) {
        } else {
            id = arguments.getString ("id");
            String target = arguments.getString("target");

            textViewTarget.setText("Id: " + id + " target: " + target);
        }



    }
    public void buttonPhotoClick(View view) {
        intentsPhoto();
    }

    public void intentsPhoto() {//интент вызов камеры

        Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
        File dir =new File(this.getExternalFilesDir(null),"TestTask");

        if(!dir.exists())
            dir.mkdir();
        file = new File(dir.getAbsolutePath() + "/IMG" + System.currentTimeMillis() + ".jpg");
        inputFileUri = Uri.fromFile(file);

        intent.putExtra ( MediaStore.EXTRA_OUTPUT, inputFileUri );
        startActivityForResult ( intent, TAKE_PICTURE_REQUEST );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//запускается при ответе интента
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_PICTURE_REQUEST && resultCode==RESULT_OK)
        {
            try {
                outputFileUri = reSavePhoto ( inputFileUri ); //сохраняем фото в памяти приложения, удаляем из кэша
            } catch (IOException e) {
                e.printStackTrace ( );
                Toast toast = Toast.makeText ( getApplicationContext ( ),
                        "ошибка сохранения файла", Toast.LENGTH_SHORT );
                toast.show ( );
            }
            FragmentManager manager = getSupportFragmentManager();
            context = getContext();
            MyDialogFragment myDialogFragment = new MyDialogFragment(context, outputFileUri, fileName);//создаем диалог
            myDialogFragment.show(manager, "myDialog");

            Picasso
                    .get()
                    .load(outputFileUri)
                    .placeholder(R.drawable.gallery)
                    .error(R.drawable.nophoto)
                    .into(imageView);

        }
    }


    public void dialogOpenPhotoClic(View view) {
        fullscreenImage ();
    }
    public void fullscreenImage() {//открывает активити с изображением на весь экран
        Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
        intent.putExtra("image_uri", outputFileUri);
        startActivity(intent);
    }

    public  Uri reSavePhoto(Uri uriIn) throws IOException{//сохраняем фото в директорию приложения, т.к. она на прямую недоступна из камеры
        Uri uri = uriIn;
        File myFiledir =new File(this.getFilesDir(),"TestTask");
        fileName = "IMG" + id + ".jpg";
        fileout = new File(myFiledir.getAbsolutePath() + "/"+ fileName);

        FileInputStream in = new FileInputStream(file);
        try {
            FileOutputStream out = new FileOutputStream(fileout);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }

            uri= Uri.fromFile(fileout);
            file.delete();
        return uri;
    }

    public void sendPhoto(String name, String surname, String patronymic ){//метод для отправки изображения через ретрофит

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("Image", fileout.getName(), RequestBody.create( MediaType.parse("image/*"), fileout));

         nameSend = name;
         surnameSend = surname;
         patronymicSend = patronymic;
         contactString = "send_data";

        RequestBody send_data = RequestBody.create(MediaType.parse("multipart/form-data"), contactString);

        NetworkService.getInstance()// выполняем запрос через retrofit
                .getJSONApi()
                .sendImage(send_data, nameSend, surnameSend, patronymicSend ,imagePart)
                .enqueue(new Callback <RespSendImage>() {
                    @Override
                    public void onResponse( Call <RespSendImage> queryImage,
                                            Response <RespSendImage> response) {
                        otvet = response.body();
                        if (response.isSuccessful())
                        {
                            Snackbar.make(constLayout, "Данные отправлены на сервер", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call <RespSendImage> call, @NonNull Throwable t) {
                        Snackbar.make(constLayout, "Произошла ошибка при отправке!", Snackbar.LENGTH_LONG)
                                .show();
                        t.printStackTrace();
                    }
                });
    }
}


