package com.konstantinov.testtask.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.konstantinov.testtask.NetworkService;
import com.konstantinov.testtask.POJO.RespSendImage;
import com.konstantinov.testtask.R;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class OpencartsActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageView imageView;
    TextView textViewTarget;
    TextView textViewSResp;
    String id;

    private Uri outputFileUri;
    private Uri inputFileUri;

    private static final int TAKE_PICTURE_REQUEST = 1;
    private Object context;
    private File file;
    private File fileout;
    //private RespSendImage otvet;
    private String otvet;
    private ConstraintLayout constLayout;
    private String fileName;

    private String nameSend;
    private String surnameSend;
    private String patronymicSend;
    private String contactString;
    private Disposable disposable;
    private static final String TAG = "result";
    boolean flagComplite = false;
    public final int REQUEST_CODE_PERMISSION = 1;
    MyDialogFragment myDialogFragment;








    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_opencarts );
        textViewTarget = findViewById(R.id.textViewTarget);
        textViewSResp = findViewById(R.id.textViewSResp);
        imageView = findViewById(R.id.imageView);
        Bundle arguments = getIntent().getExtras();
        // получаем корневой элемент
        constLayout = (ConstraintLayout) findViewById(R.id.target);


        if (arguments == null) {
        } else {
            id = arguments.getString ("id");
            String target = arguments.getString("target");

            textViewTarget.setText("Id: " + id + " target: " + target);


            String mFileName = "IMG" + id + ".jpg";
            File file = new File(this.getFilesDir()+ "/TestTask/" +mFileName);
            if(file.exists()) {
                Uri mUri= Uri.fromFile(file);
                Picasso
                        .get()
                        .load(mUri)
                        .placeholder(R.drawable.gallery)
                        .error(R.drawable.nophoto)
                        .into(imageView);

            }

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

            } catch (Exception e) {
                e.printStackTrace ( );
                Toast toast = Toast.makeText ( getApplicationContext ( ),
                        "ошибка сохранения файла", Toast.LENGTH_SHORT );
                toast.show ( );
            }

            FragmentManager manager = getSupportFragmentManager();
            context = getContext();
            myDialogFragment = new MyDialogFragment(context, outputFileUri, fileName);//создаем диалог
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

    public  Uri reSavePhoto(Uri uriIn) throws Exception{//сохраняем фото в директорию приложения, т.к. она на прямую недоступна из камеры
        Uri uri = uriIn;
        File myFiledir =new File(this.getFilesDir(),"TestTask");

        if(!myFiledir.exists())
            myFiledir.mkdir();
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
        myDialogFragment.progressBar.setVisibility( ProgressBar.VISIBLE);


        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileout.getName(), RequestBody.create( MediaType.parse("image/jpeg"), fileout));

         nameSend = name;
         surnameSend = surname;
         patronymicSend = patronymic;
         contactString = "send_data";

        RequestBody send_data = RequestBody.create(MediaType.parse("multipart/form-data"), contactString);
        RequestBody send_id = RequestBody.create(MediaType.parse("multipart/form-data"), id);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose ();
        }

        disposable = NetworkService.getInstance()// выполняем запрос через retrofit2 используем RxJava2
                .getJSONApi()
                .sendImage(send_data, nameSend, surnameSend, patronymicSend , send_id ,imagePart)
                .subscribeOn ( Schedulers.io () )//адает Scheduler, на котором выполняется подписка на Observable . Другими словами, код метода Observable. create() выполняется в потоке, заданном subscribeOn()
                .observeOn ( AndroidSchedulers.mainThread () )//Используем чтобы обрабатывать результат в основном потоке и показать результат
                .subscribeWith ( new ResourceObserver<String> () {
                    @Override
                    public void onNext(String response) {
                        otvet = response;
                    if (otvet.startsWith("{\"") == false){//если ответ содержит данные кроме json
                            int indexJson = otvet.indexOf("{\"");
                            otvet = otvet.substring(indexJson);
                            Log.d(TAG, "новая строка " + otvet);
                        }
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        RespSendImage respSendImage = gson.fromJson(otvet, RespSendImage.class);

                        if ( respSendImage.getStatus ().equals("success") == true) {
                            Snackbar.make ( constLayout, "Данные отправлены на сервер", Snackbar.LENGTH_LONG )
                                    .show ( );
                            myDialogFragment.dismiss ();
                        } else {
                            Toast toast = Toast.makeText ( getApplicationContext (),
                                    "Произошла ошибка при отправке!", Toast.LENGTH_SHORT );
                            toast.show ( );
                        }
                        textViewSResp.setText("Ответ от сервера: " + response);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "upload onError ");
                        Toast toast = Toast.makeText ( getApplicationContext (),
                                "Произошла ошибка при отправке!", Toast.LENGTH_SHORT );
                        toast.show ( );
                        myDialogFragment.progressBar.setVisibility( ProgressBar.INVISIBLE);
                        textViewSResp.setText("Ответ от сервера: " + e.toString ());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "upload onComplete ");
                        myDialogFragment.progressBar.setVisibility( ProgressBar.INVISIBLE);

                    }
                });
    }
}


