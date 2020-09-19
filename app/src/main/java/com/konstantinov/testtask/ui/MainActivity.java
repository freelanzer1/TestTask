package com.konstantinov.testtask.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.konstantinov.testtask.NetworkService;
import com.konstantinov.testtask.POJO.Datum;
import com.konstantinov.testtask.POJO.ResponseIosPixli;
import com.konstantinov.testtask.R;


import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private static final String TAG = "result";
    MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private Button buttonReconnect;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

         progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        buttonReconnect = findViewById(R.id.buttonReconnect);
        buttonReconnect.setVisibility ( Button.INVISIBLE );
// запускаем длительную операцию
        loadInfo ();

    }

    private void setRecycler(List<Datum> data) {
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvResponse );
        LinearLayoutManager mLayoutManager = new LinearLayoutManager ( this );
        recyclerView.setLayoutManager( mLayoutManager );
        adapter = new MyRecyclerViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }


    public void loadInfo (){
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose ();
        }
        String call = "get_data";
        disposable = NetworkService.getInstance()// выполняем запрос через retrofit
                .getJSONApi()
                .getPostWithID( call )
                .subscribeOn ( Schedulers.io () )//адает Scheduler, на котором выполняется подписка на Observable . Другими словами, код метода Observable. create() выполняется в потоке, заданном subscribeOn()
                .observeOn ( AndroidSchedulers.mainThread () )//Используем чтобы обрабатывать результат в основном потоке и показать результат
                 .subscribeWith ( new ResourceObserver<ResponseIosPixli> () {
                     @Override
                     public void onNext(ResponseIosPixli responseIosPixli) {
                         if ( responseIosPixli.getStatus ().equals("success") == true)
                         {
                             setRecycler (responseIosPixli.getData ());
                             progressBar.setVisibility(ProgressBar.INVISIBLE);
                             buttonReconnect.setVisibility ( Button.INVISIBLE );
                         }
                         else {
                             progressBar.setVisibility(ProgressBar.INVISIBLE);
                             buttonReconnect.setVisibility ( Button.VISIBLE );
                         }
                         Log.d(TAG, responseIosPixli.getStatus ());
                     }

                     @Override
                     public void onError(Throwable e) {
                         Log.d(TAG, "load response onError ");
                         progressBar.setVisibility(ProgressBar.INVISIBLE);
                         buttonReconnect.setVisibility ( Button.VISIBLE );
                     }

                     @Override
                     public void onComplete() {
                         Log.d(TAG, "load response onComplete ");
                     }
                 });
    }


    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent (this, OpencartsActivity.class);
// передача объекта с ключом "id" и значением "id"
        intent.putExtra("id", String.valueOf(adapter.getItem (position).getId ()));
        intent.putExtra("target", adapter.getItem (position).getTarget ());
// запуск SecondActivity
        startActivity(intent);

    }

    public void buttonReconnectClic (View view) {
        loadInfo ();
        buttonReconnect.setVisibility ( Button.INVISIBLE );
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }
}