package com.konstantinov.testtask.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konstantinov.testtask.POJO.Datum;
import com.konstantinov.testtask.R;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Datum> adapterData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // данные передаются в конструктор
    MyRecyclerViewAdapter(Context context, List<Datum> data) {
        this.mInflater = LayoutInflater.from(context);
        this.adapterData = data;
    }

    // генерим макет строчки из xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate( R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // связываем данные с TextView в каждой строке
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Datum datum = adapterData.get (position);
        holder.myTextView.setText(datum.getId () + " ");
        holder.myTextView2.setText(datum.getTarget ());
    }

    // общее количество строк
    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myTextView2;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.datumID);
            myTextView2 = itemView.findViewById(R.id.datumTarget);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // удобный метод получения данных при клике
    Datum getItem(int id) {

        return adapterData.get (id);
    }


    //позволяет перехватывать события кликов
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // родительская активность будет реализовывать этот метод для ответа на события щелчка
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}