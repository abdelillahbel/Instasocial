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
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.utils.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * You tab - adapter class
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificaitonHolder> {
    Activity context;
    ArrayList<NotificationBean> notificationBeanArrayList;

    ItemClickListener<NotificationBean> itemClickListener = null;

    //constructor
    public NotificationAdapter(Activity context, ArrayList<NotificationBean> notificaitonArrayList) {
        this.context = context;
        this.notificationBeanArrayList = notificaitonArrayList;
    }

    //onclick registration
    public void setItemClickListener(ItemClickListener<NotificationBean> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public NotificaitonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout._notification_repeat_item_, null);
        return new NotificaitonHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificaitonHolder holder, final int position) {
        final NotificationBean item = notificationBeanArrayList.get(position);
        Picasso.with(context).load(item.getProfile_pic())
                .placeholder(R.color.colorPrimaryLightDark)
                .error(R.color.colorPrimaryLightDark)
                .fit().into(holder.userImage);

        final StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        Spannable span = Spannable.Factory.getInstance().newSpannable(item.message + " " + item.getCreatedAgo());
        int userStart = item.message.indexOf(item.author_name);
        int userEnd = userStart + item.author_name.length();
        // username span
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {
                context.startActivity(ProfileActivity.getArgIntent(context,item.sender_id,""));
            }
        }, userStart, userEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new UnderlineSpan() {
            public void updateDrawState(TextPaint tp) {
                tp.setUnderlineText(false);
            }
        }, userStart, userEnd, 0);
        span.setSpan(bold, userStart, userEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.BLACK), userStart, userEnd, 0);

        //createdAgo
        int createdAgoStart = item.message.length() + 1;
        int createdAgoEnd = createdAgoStart + item.getCreatedAgo().length();
        span.setSpan(new ForegroundColorSpan(Color.GRAY), createdAgoStart, createdAgoEnd, 0);

        holder.txtContent.setTextColor(Color.BLACK);
        holder.txtContent.setText(span);
        holder.txtContent.setMovementMethod(LinkMovementMethod.getInstance());

        //pass data to onitem click methos
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener!=null){
                    itemClickListener.onItemClick(notificationBeanArrayList.get(position),position);
                }
            }
        });

        //redirect to user's profile screen
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(ProfileActivity.getArgIntent(context,item.sender_id,""));
            }
        });

        holder.itemView.setBackgroundColor(notificationBeanArrayList.get(position).status.equalsIgnoreCase("0") ? Color.parseColor("#f6f6f6") : Color.parseColor("#ffffff"));
    }

    @Override
    public int getItemCount() {
        return notificationBeanArrayList.size();
    }

    class NotificaitonHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        CompatTextView txtContent;
        RelativeLayout notification_rela;

        public NotificaitonHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            txtContent = itemView.findViewById(R.id.txtContent);
            notification_rela = itemView.findViewById(R.id.notification_rela);
        }
    }
}
