package com.example.intelligrape.arsampeapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class VirtualSponsorHome extends ListActivity {

    int[] brandsLogo = new int[]{
            R.drawable.intelligrape,
            R.drawable.coca_cola,
            R.drawable.google,
            R.drawable.play_store,
            R.drawable.logo_tv
    };

    String[] values = new String[]{
            "Intelligrape Softwares",
            "Cocacola Inc",
            "Google Inc",
            "Play Store",
            "Logo TV"
    };

    String[] urls = new String[]{
            "http://www.intelligrape.com",
            "http://www.coca-colaindia.com",
            "https://www.google.co.in",
            "https://play.google.com/store",
            "http://www.logotv.com"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, values, brandsLogo);
        setListAdapter(adapter);
        getListView().setBackgroundColor(Color.WHITE);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent demoIntent = new Intent(VirtualSponsorHome.this, VirtualSponser.class);
        demoIntent.putExtra("resource_id", brandsLogo[position]);
        demoIntent.putExtra("demo_id", 1);
        demoIntent.putExtra("url", urls[position]);
        startActivity(demoIntent);
    }

    protected void onDestroy() {

        super.onDestroy();

    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;
        private final int[] logos;

        public MySimpleArrayAdapter(Context context, String[] values, int[] logos) {
            super(context, R.layout.row_layout, values);
            this.context = context;
            this.logos = logos;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.layout_virtual_assistant, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.tv_glass_name);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_glass);
            textView.setText(values[position]);
            // Change the icon for Windows and iPhone
            imageView.setImageResource(brandsLogo[position]);


            return rowView;
        }
    }


}