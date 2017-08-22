package com.austinabell8.cardswipe2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.austinabell8.cardswipe.SwipeDeck;
import com.austinabell8.cardswipe.layout.SwipeFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeDeck cardStack;
    private SwipeDeckAdapter adapter;
    private SwipeFrameLayout swipeFrameLayout;
    private int counter;

    private final ArrayList<String> mTestData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counter = 0;

        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        swipeFrameLayout = (SwipeFrameLayout) findViewById(R.id.swipeLayout);

        mTestData.add(counter + "");
        mTestData.add(counter + "");
        mTestData.add(counter + "");
        mTestData.add(counter + "");
        mTestData.add(counter + "");

        adapter = new SwipeDeckAdapter(mTestData, this);
        if(cardStack != null){
            cardStack.setAdapter(adapter);
        }
        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                addNext();
            }

            @Override
            public void cardSwipedRight(long stableId) {
                addNext();
            }

            @Override
            public void cardSwipedTop(long itemId) {
                addNext();
            }

            @Override
            public void cardSwipedBottom(long itemId) {
                addNext();
            }

            @Override
            public boolean isDragEnabled(long itemId) {
                return true;
            }

        });

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);

        //example of buttons triggering events on the deck
        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardLeft(400);
            }
        });
        ImageButton likeButton = (ImageButton) findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardRight(400);
            }
        });
        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardTop(600);
            }
        });
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardBottom(600);
            }
        });
    }

    private void addNext(){
        mTestData.add(counter + "");
        adapter.notifyDataSetChanged();
    }

    private class SwipeDeckAdapter extends BaseAdapter {

        private List<String> data;
        private Context context;

        public SwipeDeckAdapter(List<String> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                // normally use a viewholder
                v = inflater.inflate(R.layout.picture_view, parent, false);
            }

            ImageView imageView = (ImageView) v.findViewById(R.id.picture_holder);
            Picasso.with(context).load(R.drawable.galaxy).fit().centerCrop().into(imageView);
            TextView textView = (TextView) v.findViewById(R.id.sample_text);
            String item = (String)getItem(position);
            textView.setText(item);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
                }
            });
            return v;
        }
    }
}
