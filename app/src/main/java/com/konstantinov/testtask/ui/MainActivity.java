package com.konstantinov.testtask.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.konstantinov.testtask.NetworkService;
import com.konstantinov.testtask.POJO.Datum;
import com.konstantinov.testtask.POJO.ResponseIosPixli;
import com.konstantinov.testtask.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private String call = "get_data";
    private ResponseIosPixli responseIosPixli;
    private ProgressBar progressBar;
    private Button buttonReconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

         progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        buttonReconnect = (Button) findViewById(R.id.buttonReconnect);
        buttonReconnect.setVisibility ( Button.INVISIBLE );
// запускаем длительную операцию
        loadInfo ();

    }

    private void setRecycler(List<Datum> data) {
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvResponse );
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MyRecyclerViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    public void loadInfo (){

        NetworkService.getInstance()// выполняем запрос через retrofit
                .getJSONApi()
                .getPostWithID(call)
                .enqueue(new Callback<ResponseIosPixli> () {
                    @Override
                    public void onResponse( Call<ResponseIosPixli> call,
                                            Response<ResponseIosPixli> response) {
                        responseIosPixli = response.body();
                        if (response.isSuccessful())
                        {
                            setRecycler (responseIosPixli.getData ());
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            buttonReconnect.setVisibility ( Button.INVISIBLE );
                        }
                        else {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            buttonReconnect.setVisibility ( Button.VISIBLE );
                        }

                        Toast toast = Toast.makeText(getApplicationContext(),
                                response.body ().getStatus (), Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onFailure(@NonNull Call <ResponseIosPixli> call, @NonNull Throwable t) {

                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            buttonReconnect.setVisibility ( Button.VISIBLE );

                        t.printStackTrace();
                    }
                });



    }


    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent (this, OpencartsActivity.class);
// передача объекта с ключом "id" и значением "id"
        intent.putExtra("id", adapter.getItem (position).getId ());
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