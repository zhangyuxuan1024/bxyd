package net.iclassmate.bxyd.ui.fragment.tran;

import android.support.v4.app.Fragment;

/**
 * Created by xydbj on 2016.7.17.
 */
public abstract class LazyFragment extends Fragment {
    private boolean isVisible;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    protected void onVisible(){
        lazyLoad();
    }
    protected abstract void lazyLoad();

    protected void onInvisible(){}
    public String getFragmentTitle(){return null;}
    public void sendDataToFragment(int index,Object o){};
    public void textViewSelectedChanged(int index){};
}
