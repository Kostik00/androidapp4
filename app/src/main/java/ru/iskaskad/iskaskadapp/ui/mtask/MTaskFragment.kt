package ru.iskaskad.iskaskadapp.ui.mtask

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.ui.mtask.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class MTaskFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mtask_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                adapter = MTaskRVAdapter(PlaceholderContent.ITEMS)
            }
        }
        return view
    }


}