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

import com.example.intelligrape.arsampeapp.adapter.AssistantBaseAdapter;

public class VirtualAssistantHome extends ListActivity {

    int[] brandsLogo = new int[]{
            R.drawable.sun_killer_512,
            R.drawable.sun_killer_black_256,
            R.drawable.sun_killer_black_512,
            R.drawable.sun_killer_blue_256,
            R.drawable.sun_killer_blue_512,
            R.drawable.sun_killer_orange_256,
            R.drawable.sun_killer_orange_512
    };
    String[] values = new String[]{
            "Grey (L)",
            "Black (S)",
            "Black (L)",
            "Blue (S)",
            "Blue (L)",
            "Orange (S)",
            "Orange (L)"
    };
    int[] brandsLogoRaw = new int[]{
            R.raw.sun_killer_512,
            R.raw.sun_killer_black_256,
            R.raw.sun_killer_black_512,
            R.raw.sun_killer_blue_256,
            R.raw.sun_killer_blue_512,
            R.raw.sun_killer_orange_256,
            R.raw.sun_killer_orange_512
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AssistantBaseAdapter assistantBaseAdapter = new AssistantBaseAdapter(this, values, brandsLogo);


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, values);
        setListAdapter(assistantBaseAdapter);
        getListView().setBackgroundColor(Color.WHITE);


    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent demoIntent = new Intent(VirtualAssistantHome.this, VirtualAssistant.class);

        demoIntent.putExtra("resource_id", brandsLogo[position]);
        demoIntent.putExtra("demo_id", 1);
        startActivity(demoIntent);
    }

    @Override
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
            View rowView = inflater.inflate(R.layout.row_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values[position]);
            // Change the icon for Windows and iPhone
            imageView.setImageResource(brandsLogo[position]);
//
//            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.list);
//            convertView.startAnimation(animation1);

            return rowView;
        }
    }
}