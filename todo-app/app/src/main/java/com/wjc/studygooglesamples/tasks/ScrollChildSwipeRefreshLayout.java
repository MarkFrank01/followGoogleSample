package com.wjc.studygooglesamples.tasks;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/12/2.
 */

public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout{

    private View mScrollChildUpChild;

    public ScrollChildSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollChildUpChild!=null){
            return ViewCompat.canScrollVertically(mScrollChildUpChild,-1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view){
        mScrollChildUpChild = view;
    }
}
