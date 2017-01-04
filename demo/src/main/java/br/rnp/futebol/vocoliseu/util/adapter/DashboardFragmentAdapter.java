package br.rnp.futebol.vocoliseu.util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.rnp.futebol.vocoliseu.visual.fragment.ExpListFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.InstructionsFragment1;
import br.rnp.futebol.vocoliseu.visual.fragment.InstructionsFragment2;
import br.rnp.futebol.vocoliseu.visual.fragment.InstructionsFragment3;
import br.rnp.futebol.vocoliseu.visual.fragment.NoExpsFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.VideoListFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.unused.DashboardLastVideoFragment;

public class DashboardFragmentAdapter extends FragmentStatePagerAdapter {


    private int size;

    public DashboardFragmentAdapter(FragmentManager fm, int size) {
        super(fm);
        this.size = size;
    }


    /*
    * Observation:
    *   When adding (or deleting) tabs, increase or decrease getCount() value
    * */
    @Override
    public Fragment getItem(int pos) {
        if (size == 1) {
            switch (pos) {
                case 0:
                    return new NoExpsFragment();
                default:
                    break;
            }
        } else if (size == 3)
            switch (pos) {
                case 0:
                    return new DashboardLastVideoFragment();
                case 1:
                    return new ExpListFragment();
                case 2:
                    return new VideoListFragment();
                default:
                    break;
            }
        else
            switch (pos) {
                case 0:
                    return new ExpListFragment();
                case 1:
                    return new VideoListFragment();
                default:
                    break;
            }
        return null;
    }

    @Override
    public int getCount() {
        return size;
    }
}
