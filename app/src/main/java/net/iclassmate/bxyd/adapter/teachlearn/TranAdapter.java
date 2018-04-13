package net.iclassmate.bxyd.adapter.teachlearn;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.iclassmate.bxyd.ui.fragment.tran.LazyFragment;

import java.util.List;

/**
 * Created by xydbj on 2016.7.17.
 */
public class TranAdapter extends FragmentPagerAdapter {

    private List<LazyFragment> fragments;

    public TranAdapter(FragmentManager fm, List<LazyFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (fragments != null){
            count = fragments.size();
        }
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String ret = null;
        LazyFragment lazyFragment = fragments.get(position);
        ret = lazyFragment.getFragmentTitle();
        return ret;
    }
}
