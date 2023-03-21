/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui.country;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CompatTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CountryPicker extends DialogFragment {

  private EditText searchEditText;
  private ListView countryListView;
  private CountryListAdapter adapter;
  private List<Country> countriesList = new ArrayList<>();
  private List<Country> selectedCountriesList = new ArrayList<>();
  private CountryPickerListener listener;
  private Context context;
  private CompatTextView txtImgBack,noData;
  InputMethodManager imm;

  /**
   * To support show as dialog
   */
  public static CountryPicker newInstance(String dialogTitle) {
    CountryPicker picker = new CountryPicker();
    Bundle bundle = new Bundle();
    bundle.putString("dialogTitle", dialogTitle);
    picker.setArguments(bundle);
    return picker;
  }

  public CountryPicker() {
    setCountriesList(Country.getAllCountries());

  }
  @Override
  public void onStart()
  {
    super.onStart();
    if (getDialog() != null)
    {
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setLayout(width, height);
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AppThemeNoActionBar);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.country_picker, null);
    Bundle args = getArguments();
    if (args != null) {
    //  String dialogTitle = args.getString("dialogTitle");
      getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
      getDialog().setCancelable(false);
    }
    searchEditText = (EditText) view.findViewById(R.id.country_code_picker_search);
    countryListView = (ListView) view.findViewById(R.id.country_code_picker_listview);
    searchEditText.setText("");
    selectedCountriesList = new ArrayList<>(countriesList.size());
    selectedCountriesList.addAll(countriesList);

    txtImgBack = view.findViewById(R.id.txtImgBack);
  //  txtImgBack.setTypeface(Common_Fonts.getInstance(getActivity()).getFontTypeFace());
    noData = view.findViewById(R.id.noData);

    adapter = new CountryListAdapter(getActivity(), selectedCountriesList);
    countryListView.setAdapter(adapter);

    countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
          Country country = selectedCountriesList.get(position);
          listener.onSelectCountry(country.getName(), country.getCode(), country.getDialCode());
        }
        searchEditText.setText("");
      }
    });

    searchEditText.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        search(s.toString());
      }
    });

    searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
       /* if(!searchEditText.getText().toString().isEmpty())
        {*/
         // AppLog.e("inside","search option working");
          //search(searchEditText.getText().toString());
          // code to hide the soft keyboard
          imm = (InputMethodManager) getActivity().getSystemService(
                  Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), 0);

       // }

        return true;
      }
    });

    txtImgBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), 0);
        searchEditText.setText("");
        dismiss();
      }
    });

    return view;
  }

  public void setListener(CountryPickerListener listener) {
    this.listener = listener;
  }

  @SuppressLint("DefaultLocale")
  private void search(String text) {
    selectedCountriesList.clear();
    for (Country country : countriesList) {
      if (country.getName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase())) {
        selectedCountriesList.add(country);
      }
    }

    if(selectedCountriesList.size()==0)
    {
      noData.setVisibility(View.VISIBLE);
    }
    else
    {
      noData.setVisibility(View.GONE);
    }
    adapter.notifyDataSetChanged();
  }

  public void setCountriesList(List<Country> newCountries) {
    this.countriesList.clear();
    this.countriesList.addAll(newCountries);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    searchEditText.setText("");
  }
}
