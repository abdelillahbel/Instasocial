/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.androidapp.instasocial.R;


public class CommentOptionsDialog extends Dialog {
    //    private Activity activity;
    ButtonClickListener onButtonClickListener = null;
    public TextView txtEdit, txtDelete;
    private View txtCancel;





    public interface ButtonClickListener {
        void onEditAction(CommentOptionsDialog dialog);

        void onDeleteAction(CommentOptionsDialog dialog);
    }

    public CommentOptionsDialog(Context context) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }



    public CommentOptionsDialog setOnButtonClickListener(ButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_comment_options);
        txtEdit = (TextView) findViewById(R.id.txtEdit);
        txtDelete = (TextView) findViewById(R.id.txtDelete);
        txtCancel=findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (onButtonClickListener != null) {
//                    onButtonClickListener.onPositiveAction(KtjAlertDialog.this);
//                }
                if (onButtonClickListener != null) {
                    onButtonClickListener.onEditAction(CommentOptionsDialog.this);
                }
            }
        });
        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (onButtonClickListener != null) {
//                    onButtonClickListener.onNegativeAction(KtjAlertDialog.this);
//                }
                if (onButtonClickListener != null) {
                    onButtonClickListener.onDeleteAction(CommentOptionsDialog.this);
                }
            }
        });
    }
}
