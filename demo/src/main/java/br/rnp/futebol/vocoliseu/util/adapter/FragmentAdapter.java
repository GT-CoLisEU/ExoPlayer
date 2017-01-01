package br.rnp.futebol.vocoliseu.util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentConfigurationGeneralFragment;
import br.rnp.futebol.vocoliseu.visual.fragment.unused.ExperimentConfigurationMetricsFragment;

public class FragmentAdapter extends FragmentStatePagerAdapter {


    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return new ExperimentConfigurationGeneralFragment();
            case 1:
                return new ExperimentConfigurationMetricsFragment();
//            case 2:
//                return new ExperimentConfiguration3();
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
