package br.rnp.futebol.vocoliseu.util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.rnp.futebol.vocoliseu.visual.fragment.ExperimentGeneralFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.ExperimentMetricsFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.ExperimentScriptsFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.ScriptGeneralFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.ScriptMetricsFragment;

public class ScriptFragmentAdapter extends FragmentStatePagerAdapter {


    public ScriptFragmentAdapter(FragmentManager fm) {
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
                return new ScriptGeneralFragment();
            case 1:
                return new ScriptMetricsFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
