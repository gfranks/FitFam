package com.github.gfranks.workoutcompanion.adapter;

import android.content.Context;
import android.database.MatrixCursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCLocation;

import java.util.List;

public class SearchSuggestionsAdapter extends SimpleCursorAdapter {

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TEXT = "text";
    private static final String[] COLUMN_NAMES = {COLUMN_ID, COLUMN_TEXT};
    private List<WCLocation> mResults;

    public SearchSuggestionsAdapter(Context context) {
        super(context, R.layout.layout_search_suggestions_list_item, null, new String[]{COLUMN_TEXT},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public WCLocation getResultItem(int position) {
        return mResults.get(position);
    }

    public void updateWithLocationResults(List<WCLocation> results) {
        mResults = results;
        MatrixCursor cursor = new MatrixCursor(COLUMN_NAMES);
        String[] temp = new String[2];
        int id = 0;
        for (WCLocation result : mResults) {
            temp[0] = Integer.toString(id++);
            temp[1] = result.getFormatted_address();
            cursor.addRow(temp);
        }
        changeCursor(cursor);
    }
}
