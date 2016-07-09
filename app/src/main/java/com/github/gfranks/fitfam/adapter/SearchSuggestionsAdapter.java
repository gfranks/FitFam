package com.github.gfranks.fitfam.adapter;

import android.content.Context;
import android.database.MatrixCursor;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.data.model.FFLocation;

import java.util.List;

public class SearchSuggestionsAdapter extends SimpleCursorAdapter {

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TEXT = "text";
    private static final String[] COLUMN_NAMES = {COLUMN_ID, COLUMN_TEXT};
    private List<FFLocation> mResults;

    public SearchSuggestionsAdapter(Context context) {
        super(context, R.layout.layout_search_suggestions_list_item, null, new String[]{COLUMN_TEXT},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public FFLocation getResultItem(int position) {
        return mResults.get(position);
    }

    public void updateWithLocationResults(List<FFLocation> results) {
        mResults = results;
        MatrixCursor cursor = new MatrixCursor(COLUMN_NAMES);
        String[] temp = new String[2];
        int id = 0;
        for (FFLocation result : mResults) {
            temp[0] = Integer.toString(id++);
            temp[1] = result.getFormatted_address();
            cursor.addRow(temp);
        }
        changeCursor(cursor);
    }
}
