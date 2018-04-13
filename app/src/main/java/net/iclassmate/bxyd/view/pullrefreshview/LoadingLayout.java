package net.iclassmate.bxyd.view.pullrefreshview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public abstract class LoadingLayout extends FrameLayout implements
        ILoadingLayout {
    protected View mContainer;
    private State mCurState = State.NONE;
    private State mPreState = State.NONE;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        mContainer = createLoadingView(context, attrs);
        if (null == mContainer) {
            throw new NullPointerException("Loading view can not be null.");
        }

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mContainer, params);
    }

    public void show(boolean show) {
        // If is showing, do nothing.
        if (show == (View.VISIBLE == getVisibility())) {
            return;
        }

        ViewGroup.LayoutParams params = mContainer.getLayoutParams();
        if (null != params) {
            if (show) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = 0;
            }
            setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setLastUpdatedLabel(CharSequence label) {

    }

    protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

    public void setPullLabel(CharSequence pullLabel) {

    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    public void setReleaseLabel(CharSequence releaseLabel) {

    }

    @Override
    public void setState(State state) {
        if (mCurState != state) {
            mPreState = mCurState;
            mCurState = state;
            onStateChanged(state, mPreState);
        }
    }

    @Override
    public State getState() {
        return mCurState;
    }

    @Override
    public void onPull(float scale) {

    }

    protected State getPreState() {
        return mPreState;
    }

    protected void onStateChanged(State curState, State oldState) {
        switch (curState) {
            case RESET:
                onReset();
                break;

            case RELEASE_TO_REFRESH:
                onReleaseToRefresh();
                break;

            case PULL_TO_REFRESH:
                onPullToRefresh();
                break;

            case REFRESHING:
                onRefreshing();
                break;


            case NO_MORE_DATA:
                onNoMoreData();
                break;

            case SUCCEED:
                onSucceed();
                break;

            case FAIL:
                onFail();

            default:
                break;
        }
    }

    protected abstract void onFail();

    protected abstract void onReset();

    protected abstract void onSucceed();

    protected abstract void onPullToRefresh();

    protected abstract void onReleaseToRefresh();

    protected abstract void onRefreshing();

    protected abstract void onNoMoreData();

    public abstract int getContentSize();

    protected abstract View createLoadingView(Context context,
                                              AttributeSet attrs);
}