package com.dsi11.grapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dsi11.grapp.dummy.TutorialContent;

/**
 * A fragment representing a single TutorialEntry detail screen.
 * This fragment is either contained in a {@link TutorialEntryListActivity}
 * in two-pane mode (on tablets) or a {@link TutorialEntryDetailActivity}
 * on handsets.
 */
public class TutorialEntryDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private TutorialContent.TutorialItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TutorialEntryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = TutorialContent.getItemMap().get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorialentry_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {

            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.tutorialentry_detail_fragment_linearlayout);
            if (mItem.simple) {
                TextView textView = new TextView(linearLayout.getContext());
                textView.setText(mItem.text);
                linearLayout.addView(textView);
                ImageView imageView1 = new ImageView(textView.getContext());
                imageView1.setImageBitmap(mItem.picture);
//                imageView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(imageView1);
            } else {
                for (TutorialContent.TutorialEntrySection o : mItem.content) {
                    if (o.getType() == TutorialContent.TutorialEntrySection.TutorialEntrySectionType.TEXT) {
                        String newText = (String) o.getContent();
                        TextView textView = new TextView(linearLayout.getContext());
                        textView.setText(newText);
                        linearLayout.addView(textView);
                    } else {
                        if (o.getType() == TutorialContent.TutorialEntrySection.TutorialEntrySectionType.PICTURE) {
                            Bitmap picture = (Bitmap) o.getContent();
                            ImageView imageView = new ImageView(linearLayout.getContext());
                            imageView.setImageBitmap(picture);
                            linearLayout.addView(imageView);
                        }
                    }

                }
            }
        }

        return rootView;
    }
}
