package android.widget;

import android.content.Context;
import android.util.Log;
import android.view.FView;
import android.view.FViewGroup;

/**
 * Created by prize on 2017/9/30.
 */

public class FTableLayout extends FViewGroup {

    FPassThroughHierarchyChangeListener mPassThroughListener;

    public FTableLayout() {
        mPassThroughListener = new FPassThroughHierarchyChangeListener();
        // make sure to call the parent class method to avoid potential
        // infinite 无限 loops
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    @Override
    public void setOnHierarchyChangeListener(FViewGroup.FOnHierarchyChangeListener listener) {

        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    void trackCollapsedColumns(FView child){
        Log.i("view --->" ,child.toString());
    }

    class FPassThroughHierarchyChangeListener implements FOnHierarchyChangeListener {

        FOnHierarchyChangeListener mOnHierarchyChangeListener;

        @Override
        public void onChildViewAdded(FView parent, FView child) {
            trackCollapsedColumns(child);

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        @Override
        public void onChildViewRemoved(FView parent, FView child) {
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }
}
