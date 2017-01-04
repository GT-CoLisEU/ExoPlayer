package br.rnp.futebol.vocoliseu.util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.rnp.futebol.vocoliseu.visual.fragment.InstructionsFragment1;
import br.rnp.futebol.vocoliseu.visual.fragment.InstructionsFragment2;
import br.rnp.futebol.vocoliseu.visual.fragment.InstructionsFragment3;
import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentGeneralFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentMetricsFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentScriptsFragment;

public class InstructionsFragmentAdapter extends FragmentStatePagerAdapter {


    public InstructionsFragmentAdapter(FragmentManager fm) {
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
                return new InstructionsFragment1();
            case 1:
                return new InstructionsFragment2();
            case 2:
                return new InstructionsFragment3();
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
