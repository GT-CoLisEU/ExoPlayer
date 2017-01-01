package br.rnp.futebol.vocoliseu.util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentGeneralFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentMetricsFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentScriptsFragment;

public class ExpFragmentAdapter extends FragmentStatePagerAdapter {


    public ExpFragmentAdapter(FragmentManager fm) {
        super(fm);
    }


    /*
    * Observation:
    *   When adding (or deleting) tabs, increase or decrease getCount() value
    * */
    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return new ExperimentGeneralFragment();
            case 1:
                return new ExperimentMetricsFragment();
            case 2:
                return new ExperimentScriptsFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
