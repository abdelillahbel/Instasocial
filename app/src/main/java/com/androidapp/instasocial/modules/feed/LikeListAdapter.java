/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.utils.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.FeedLikeHolder> {
    ArrayList<FeedLike> feedlikelistArray;
    int positions;
    Context context;

    //params initialization
    public LikeListAdapter(ArrayList<FeedLike> feedLikeListsBeen, int position, Context context) {
        this.feedlikelistArray = feedLikeListsBeen;
        this.positions = position;
        this.context = context;
    }

    @Override
    public LikeListAdapter.FeedLikeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feed_like_list_item,  null);

        return new LikeListAdapter.FeedLikeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LikeListAdapter.FeedLikeHolder mholder, int position) {

        //set liked username
        mholder.userName.setText(feedlikelistArray.get(position).user_name);
        try {
            //set liked user image
            Picasso.with(context).load(Config.ApiUrls.PROFILE_URL+feedlikelistArray.get(position).author_pic).fit().into(mholder.userImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return feedlikelistArray.size();
    }

    public class FeedLikeHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;

        public FeedLikeHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userImage =  itemView.findViewById(R.id.userImage);
        }
    }
}

