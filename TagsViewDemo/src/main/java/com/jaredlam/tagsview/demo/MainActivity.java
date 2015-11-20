package com.jaredlam.tagsview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaredlam.tagsview.TagsView;

/**
 * Created by jaredluo on 15/11/5.
 */
public class MainActivity extends Activity {

    private static String[] labels = {"Hello Bubble", "This", "Android", "Github",
            "Bubble with different size", "a", "Yo", "More", "Long long long long tag again", "For test", "https://github.com/", "Great"};
    private static int[] colors = {R.color.dark_orange, R.color.yellow_green, R.color.brown, R.color.pink, R.color.red, R.color.yellow, R.color.green};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout allLayout = (LinearLayout) findViewById(R.id.all_layout);
        for (int i = 0; i < labels.length; i++) {
            TextView tag = new TextView(this);
            tag.setTextColor(getResources().getColor(android.R.color.black));
            tag.setBackgroundResource(colors[i % colors.length]);
            tag.setText(labels[i]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != labels.length - 1) {
                params.setMargins(0, 0, 10, 0);
            }
            allLayout.addView(tag, params);
        }


        TagsView tagsView1 = (TagsView) findViewById(R.id.tags_view_1);
        for (int i = 0; i < labels.length; i++) {
            TextView tag = new TextView(this);
            tag.setTextColor(getResources().getColor(android.R.color.black));
            tag.setBackgroundResource(colors[i % colors.length]);
            tag.setText(labels[i]);
            tagsView1.addView(tag);
        }

        TagsView tagsView2 = (TagsView) findViewById(R.id.tags_view_2);
        tagsView2.setWillShiftAndFillGap(true);
        for (int i = 0; i < labels.length; i++) {
            TextView tag = new TextView(this);
            tag.setTextColor(getResources().getColor(android.R.color.black));
            tag.setBackgroundResource(colors[i % colors.length]);
            tag.setText(labels[i]);
            tagsView2.addView(tag);
        }

        TagsView tagsView3 = (TagsView) findViewById(R.id.tags_view_3);
        for (int i = 0; i < labels.length; i++) {
            TextView tag = new TextView(this);
            tag.setTextColor(getResources().getColor(android.R.color.black));
            tag.setBackgroundResource(colors[i % colors.length]);
            tag.setText(labels[i]);
            tagsView3.addView(tag);
        }

    }
}
