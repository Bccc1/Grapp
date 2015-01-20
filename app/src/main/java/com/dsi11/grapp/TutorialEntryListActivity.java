package com.dsi11.grapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.MenuItem;


/**
 * An activity representing a list of TutorialEntries. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TutorialEntryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TutorialEntryListFragment} and the item details
 * (if present) is a {@link TutorialEntryDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link TutorialEntryListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class TutorialEntryListActivity extends FragmentActivity
        implements TutorialEntryListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorialentry_list);
        // Show the Up button in the action bar.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); //fixme: auskommentiert, da es nicht funktioniert

        if (findViewById(R.id.tutorialentry_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((TutorialEntryListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.tutorialentry_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            //navigateUpFromSameTask(this); //fixme: auskommentiert, da es nicht funktioniert
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link TutorialEntryListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TutorialEntryDetailFragment.ARG_ITEM_ID, id);
            TutorialEntryDetailFragment fragment = new TutorialEntryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tutorialentry_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, TutorialEntryDetailActivity.class);
            detailIntent.putExtra(TutorialEntryDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
