package io.github.dhananjaytrivedi.wikepediasearch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.github.dhananjaytrivedi.wikepediasearch.DAO.WikiResultsStorage;
import io.github.dhananjaytrivedi.wikepediasearch.Model.Result;
import io.github.dhananjaytrivedi.wikepediasearch.R;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultsViewHolder> {

    String TAG = "DJ";
    ArrayList<Result> resultArraylist;
    Context context;

    // Result Adapter Constructor

    public ResultAdapter(ArrayList<Result> list, final Context context) {

        this.context = context;
        this.resultArraylist = list;

        for (Result result : list) {
            Log.d(TAG, result.getTitle());
        }

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

            Result result = resultArraylist.get(position);
            String pageID = result.getPageID();

            /*

            // Custom Browser Activity Intent

            String title = result.getTitle();
            Intent i = new Intent(context, BrowserActivity.class);
            i.putExtra("pageID", pageID);
            i.putExtra("title", title);
            context.startActivity(i);

            */

            // Mobile Browser Intent

            String URL = "https://en.wikipedia.org/wiki?curid=" + pageID;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URL));
            context.startActivity(i);

            // Saving This Item As It is visited
            WikiResultsStorage.addNewResultObjectToStore(result);

        }
    }
}
