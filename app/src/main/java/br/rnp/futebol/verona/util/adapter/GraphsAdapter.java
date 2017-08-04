package br.rnp.futebol.verona.util.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.rnp.futebol.verona.visual.fragment.results.QoEResultsActivity;
import br.rnp.futebol.verona.visual.fragment.results.QoSResultsActivity;

public class GraphsAdapter extends FragmentStatePagerAdapter {



    public GraphsAdapter(FragmentManager fm) {
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
                return new QoEResultsActivity();
            case 1:
                return new QoSResultsActivity();
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
