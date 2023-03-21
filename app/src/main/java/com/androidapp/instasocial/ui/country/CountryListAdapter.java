/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui.country;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;

import java.util.List;

public class CountryListAdapter extends BaseAdapter {

  private Context mContext;
  List<Country> countries;
  LayoutInflater inflater;

  public CountryListAdapter(Context context, List<Country> countries) {
    super();
    this.mContext = context;
    this.countries = countries;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return countries.size();
  }

  @Override
  public Object getItem(int arg0) {
    return null;
  }

  @Override
  public long getItemId(int arg0) {
    return 0;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    Country country = countries.get(position);

    if (view == null)
      view = inflater.inflate(R.layout.row, null);

    Cell cell = Cell.from(view);
    cell.textView.setText(country.getName());


    return view;
  }

  static class Cell {
    public TextView textView;
    public ImageView imageView;

    static Cell from(View view) {
      if (view == null)
        return null;

      if (view.getTag() == null) {
        Cell cell = new Cell();
        cell.textView = (CompatTextView) view.findViewById(R.id.row_title);
        cell.imageView = (CompatImageView) view.findViewById(R.id.row_icon);
        view.setTag(cell);
        return cell;
      } else {
        return (Cell) view.getTag();
      }
    }
  }
}