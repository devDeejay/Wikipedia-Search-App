package io.github.dhananjaytrivedi.wikepediasearch.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.github.dhananjaytrivedi.wikepediasearch.Model.Result;
import io.github.dhananjaytrivedi.wikepediasearch.R;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultsViewHolder>{

    String TAG = "DJ";
    ArrayList<Result> resultArraylist;
    Context context;

    // Result Adapter Constructor

    public ResultAdapter(ArrayList<Result> list, final Context context, RecyclerView.OnItemTouchListener listener) {

        resultArraylist = list;
        this.context = context;

    }

    // Creating Views, Runs in the beginning

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Here we create a view, and then we return a View Holder holding that view

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.result_layout, parent, false);

        return new ResultsViewHolder(view);
    }

    // Binding above created Views, ie, Filling data in them. Runs repeatedly as we scroll

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {

        // Here we get the view position, now we need to get Data to fill that position

        Result object = resultArraylist.get(position);

        String title = object.getTitle();
        String description = object.getDescription();
        String imageURL = object.getImageURL();

        Log.d(TAG, title + " " + description + " " + imageURL);

        holder.resultTitle.setText(title);
        holder.resultDescription.setText(description);
        Log.d(TAG, imageURL);
        /*
        if (!imageURL.equals("")) {
            Picasso.with(context).load(imageURL).into(holder.resultImageView);
        }
        */
    }

    // Size of the list to be displayed

    @Override
    public int getItemCount() {
        return resultArraylist.size();
    }

    // View holder class

    public class ResultsViewHolder extends RecyclerView.ViewHolder {

        //Define The Layouts Here To Which Binding Is To Be Done

        public TextView resultTitle;
        public TextView resultDescription;
        public ImageView resultImageView;

        public ResultsViewHolder(View itemView) {
            super(itemView);

            Context context = itemView.getContext();
            resultTitle = itemView.findViewById(R.id.resultTitle);
            resultDescription = itemView.findViewById(R.id.resultDescription);

        }
    }
}
