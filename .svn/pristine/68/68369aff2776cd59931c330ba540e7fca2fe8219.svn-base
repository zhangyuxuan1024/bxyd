package net.iclassmate.bxyd.view.pullrefreshview;


import android.view.View;

public interface IPullToRefresh<T extends View> {
    public void setPullRefreshEnabled(boolean pullRefreshEnabled);

    public void setPullLoadEnabled(boolean pullLoadEnabled);

    public void setScrollLoadEnabled(boolean scrollLoadEnabled);

    public boolean isPullRefreshEnabled();

    public boolean isPullLoadEnabled();

    public boolean isScrollLoadEnabled();

    public void setOnRefreshListener(PullToRefreshBase.OnRefreshListener<T> refreshListener);

    public void onPullDownRefreshComplete();

    public void onPullUpRefreshComplete();

    public T getRefreshableView();

    public LoadingLayout getHeaderLoadingLayout();

    public LoadingLayout getFooterLoadingLayout();

    public void setLastUpdatedLabel(CharSequence label);
}
