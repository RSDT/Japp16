package nl.rsdt.japp.application.misc

import android.content.Context
import android.database.AbstractCursor
import android.database.Cursor
import android.text.TextUtils
import androidx.cursoradapter.widget.SimpleCursorAdapter
import nl.rsdt.japp.jotial.maps.searching.Searchable
import java.util.*


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
class SearchSuggestionsAdapter(context: Context, private val searchable: Searchable) : SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, mVisible, mViewIds, 0) {

    override fun runQueryOnBackgroundThread(constraint: CharSequence): Cursor {
        return SuggestionsCursor(constraint, searchable)
    }

    class SuggestionsCursor(constraint: CharSequence, searchable: Searchable) : AbstractCursor() {

        private val entries: MutableList<String>

        fun getEntries(): List<String> {
            return entries
        }

        init {
            entries = searchable.provide()

            if (!TextUtils.isEmpty(constraint)) {
                val constraintString = constraint.toString().toLowerCase(Locale.ROOT)

                val iter = entries.iterator()
                while (iter.hasNext()) {
                    val value = iter.next()
                    if (!value.toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                        iter.remove()
                    }
                }
            }
        }

        override fun getCount(): Int {
            return entries.size
        }

        override fun getColumnNames(): Array<String> {
            return mFields
        }

        override fun getLong(column: Int): Long {
            if (column == 0) {
                return mPos.toLong()
            }
            throw UnsupportedOperationException("unimplemented")
        }

        override fun getString(column: Int): String {
            if (column == 1) {
                return entries[mPos]
            }
            throw UnsupportedOperationException("unimplemented")
        }

        override fun getShort(column: Int): Short {
            throw UnsupportedOperationException("unimplemented")
        }

        override fun getInt(column: Int): Int {
            throw UnsupportedOperationException("unimplemented")
        }

        override fun getFloat(column: Int): Float {
            throw UnsupportedOperationException("unimplemented")
        }

        override fun getDouble(column: Int): Double {
            throw UnsupportedOperationException("unimplemented")
        }

        override fun isNull(column: Int): Boolean {
            return false
        }
    }

    companion object {

        private val mFields = arrayOf("_id", "result")
        private val mVisible = arrayOf("result")
        private val mViewIds = intArrayOf(android.R.id.text1)
    }
}
