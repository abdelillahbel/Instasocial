/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.notification;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.profile.FeedDetailFragment;
import com.androidapp.instasocial.modules.notification.followingbean.FollowFeed;
import com.androidapp.instasocial.modules.notification.followingbean.Post;
import com.androidapp.instasocial.modules.notification.followingbean.User;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.utils.ExpandableHeightGridView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Following tab - adapter class
 */
public class FollowNotificationAdapter extends RecyclerView.Adapter<FollowNotificationAdapter.CustomViewHolder> {
    private List<FollowFeed> followNotificationFeeds;
    private String replace_text;
    Activity context;
    HomeActivity homeActivity;

    public FollowNotificationAdapter(List<FollowFeed> followNotificationFeeds, Activity context, HomeActivity homeActivity) {
        this.context = context;
        this.followNotificationFeeds = followNotificationFeeds;
        this.homeActivity = homeActivity;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.following_notification_layout_item, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {

        String time = followNotificationFeeds.get(position).getCreatedAtAgo();
        time = time.replace(time, "<font color=#b1b1b1>" + time + "</font>");
        replace_text = followNotificationFeeds.get(position).getText();
        String actualString = replace_text;
        replace_text = replace_text + " " + time;


        Picasso.with(context).load(followNotificationFeeds.get(position).getProfilePic())
                .placeholder(R.color.colorPrimaryLightDark)
                .error(R.color.colorPrimaryLightDark)
                .fit().centerCrop().into(holder.profile_avatar);

        try {

            holder.follower_txt.setText(Html.fromHtml(replace_text));
            holder.follower_txt.setMovementMethod(LinkMovementMethod.getInstance());
            Spannable mySpannable = (Spannable) holder.follower_txt.getText();

            User loginUser = new User(followNotificationFeeds.get(position).getProfileId(), followNotificationFeeds.get(position).getProfileName(), followNotificationFeeds.get(position).getProfilePic());
            List<User> userList = followNotificationFeeds.get(position).getData().getUsers();
            userList.add(loginUser);

            for (int i = 0; i < userList.size(); i++) {
                User user = followNotificationFeeds.get(position).getData().getUsers().get(i);
                String user_names = user.getUserName();
                final String user_ids = user.getUserId().toString();
                int startIndex = actualString.indexOf(user_names);
                int EndIndex = startIndex + user_names.length();

                //setting spannable text
                mySpannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        context.startActivity(ProfileActivity.getArgIntent(context, user_ids, ""));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        ds.setUnderlineText(false);
                    }
                }, startIndex, EndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //set adapter for each row
        if (followNotificationFeeds.get(position).getData().getPosts() != null) {
            holder.grid_circle.setVisibility(View.VISIBLE);
            holder.grid_circle.setAdapter(new ImageAdapter(context, followNotificationFeeds.get(position).getData().getPosts(), position));
        } else {
            holder.grid_circle.setVisibility(View.GONE);
        }

        //Redirect to profile page
        holder.profile_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(ProfileActivity.getArgIntent(context, followNotificationFeeds.get(position).getProfileId().toString(), ""));
            }
        });

    }

    @Override
    public int getItemCount() {
        return followNotificationFeeds.size();
    }

    //View holder class
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFeed;
        TextView follower_txt, time_text;
        ImageView horizontal_image;
        RelativeLayout main_layout;
        ExpandableHeightGridView grid_circle;
        CircleImageView profile_avatar;

        public CustomViewHolder(View view) {
            super(view);
            this.profile_avatar = view.findViewById(R.id.profile_avatar);
            this.follower_txt = (TextView) view.findViewById(R.id.follower_txt);
            this.main_layout = (RelativeLayout) view.findViewById(R.id.main_layout);
            this.imageFeed = (ImageView) view.findViewById(R.id.image_feed);
            this.grid_circle = (ExpandableHeightGridView) view.findViewById(R.id.grid_circle);
            this.grid_circle.setExpanded(true);

        }
    }

    public class ImageAdapter extends BaseAdapter {
        List<Post> following_feeds;
        int feed_location;
        private Context mContext;

        // Constructor
        public ImageAdapter(Context c, List<Post> following_feeds, int feed_location) {
            this.mContext = c;
            this.following_feeds = following_feeds;


            this.feed_location = feed_location;
        }


        public int getCount() {
            return following_feeds.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = new Holder();
            final LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View rowView;
            if (convertView!=null){
                rowView=convertView;
            }else {
                rowView = mInflater.inflate(R.layout.following_feeds_item_layout, null);
            }
            holder.hori_lin = rowView.findViewById(R.id.hori_item);
            holder.play_btn = (ImageView) rowView.findViewById(R.id.play_btn);
            rowView.setTag(holder);
            try {
                if (following_feeds.get(position).getMedia().get(0).getMediaType().equalsIgnoreCase("photo")) {
                    holder.play_btn.setVisibility(View.GONE);
                    Picasso.with(context).load(following_feeds.get(position).getMedia().get(0).getMediaName())
                            .placeholder(R.color.colorPrimaryLightDark)
                            .error(R.color.colorPrimaryLightDark)
                            .fit().centerCrop().into(holder.hori_lin);

                } else {
                    holder.play_btn.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(following_feeds.get(position).getMedia().get(0).getMediaImage())
                            .placeholder(R.color.colorPrimaryLightDark)
                            .error(R.color.colorPrimaryLightDark)
                            .fit().centerCrop().into(holder.hori_lin);

                }

            } catch (Exception e) {

            }

            holder.hori_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeActivity.views.bottomTabs.addFragment(FeedDetailFragment.newInstance(following_feeds.get(position).getPostId().toString(), null), true);
                }
            });

            return rowView;
        }

        // Keep all Images in array
        class Holder {
            ImageView play_btn;
            ImageView hori_lin;
        }

    }
}
