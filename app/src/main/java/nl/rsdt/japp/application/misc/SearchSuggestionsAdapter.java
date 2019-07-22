package nl.rsdt.japp.application.misc;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.jotial.maps.searching.Searchable;


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
public class SearchSuggestionsAdapter extends SimpleCursorAdapter {

    private static final String[] mFields  = { "_id", "result" };
    private static final String[] mVisible = { "result" };
    private static final int[]    mViewIds = { android.R.id.text1 };

    private Searchable searchable;

    public SearchSuggestionsAdapter(Context context, Searchable searchable)
    {
        super(context, android.R.layout.simple_list_item_1, null, mVisible, mViewIds, 0);
        this.searchable = searchable;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint)
    {
        return new SuggestionsCursor(constraint, searchable);
    }

    public static class SuggestionsCursor extends AbstractCursor {

        private List<String> entries;

        public List<String> getEntries() {
            return entries;
        }

        public SuggestionsCursor(CharSequence constraint, Searchable searchable) {
            entries = searchable.provide();

            if (!TextUtils.isEmpty(constraint)) {
                String constraintString = constraint.toString().toLowerCase(Locale.ROOT);

                Iterator<String> iter = entries.iterator();
                while(iter.hasNext())
                {
                    String value = iter.next();
                    if (!value.toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                        iter.remove();
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public String[] getColumnNames() {
            return mFields;
        }

        @Override
        public long getLong(int column) {
            if (column == 0) {
                return mPos;
            }
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public String getString(int column) {
            if (column == 1) {
                return entries.get(mPos);
            }
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public short getShort(int column) {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public int getInt(int column) {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public float getFloat(int column) {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public double getDouble(int column) {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public boolean isNull(int column) {
            return false;
        }
    }
}
