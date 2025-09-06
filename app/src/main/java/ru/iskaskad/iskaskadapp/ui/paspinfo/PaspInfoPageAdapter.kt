package ru.iskaskad.iskaskadapp.ui.paspinfo


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.iskaskad.iskaskadapp.R


class PaspInfoPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    val TAB_TITLES = arrayOf(
        R.string.pasp_tab_mk,
        R.string.pasp_tab_info,
        R.string.pasp_tab_history
    )

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> PaspInfoMKFragment.newInstance("1")
            1 -> PaspInfoMainFragment.newInstance("2")
            else -> PaspInfoHistiryFragment.newInstance("3")
        }
    }

    override fun getItemCount(): Int { return TAB_TITLES.size }

}