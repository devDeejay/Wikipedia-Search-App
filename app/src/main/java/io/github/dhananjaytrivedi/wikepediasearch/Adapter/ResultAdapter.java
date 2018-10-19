package io.github.dhananjaytrivedi.wikepediasearch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

import io.github.dhananjaytrivedi.wikepediasearch.Model.Result;
import io.github.dhananjaytrivedi.wikepediasearch.R;
import io.github.dhananjaytrivedi.wikepediasearch.UI.BrowserActivity;
import io.github.dhananjaytrivedi.wikepediasearch.UI.SearchActivity;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultsViewHolder> {

    String TAG = "DJ";
    ArrayList<Result> resultArraylist;
    Context context;

    // Result Adapter Constructor

    public ResultAdapter(ArrayList<Result> list, final Context context) {

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

        holder.resultTitle.setText(title);
        holder.resultDescription.setText(description);

        // For results where Image URL is not given we have inserted "default" otherwise there is a URL to image
        Picasso.with(context)
                .load(imageURL)
                .placeholder(R.drawable.wiki_logo)
                .error(R.drawable.wiki_logo)
                .into(holder.resultImageView);


    }

    // Size of the list to be displayed

    @Override
    public int getItemCount() {
        return resultArraylist.size();
    }

    // View holder class

    public class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Define The Layouts Here To Which Binding Is To Be Done

        public TextView resultTitle;
        public TextView resultDescription;
        public ImageView resultImageView;
        public LinearLayout fullResultLayout;

        public ResultsViewHolder(View itemView) {
            super(itemView);

            resultTitle = itemView.findViewById(R.id.resultTitle);
            resultDescription = itemView.findViewById(R.id.resultDescription);
            resultImageView = itemView.findViewById(R.id.resultImageView);
            fullResultLayout = itemView.findViewById(R.id.fullResultLayout);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            String pageID = resultArraylist.get(position).getPageID();
            String title = resultArraylist.get(position).getTitle();
            Intent i = new Intent(context, BrowserActivity.class);
            i.putExtra("pageID", pageID);
            i.putExtra("title", title);
            context.startActivity(i);

        }
    }
}
