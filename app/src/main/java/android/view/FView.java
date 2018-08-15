package android.view;


/**
 * Created by fengyun on 2017/9/30.
 */

public class FView {

    int mId = 5;

    FListenerInfo mListenerInfo;

    @Override
    public String toString() {
        return "" + mId;
    }

    public void requestLayout(){

    }


    public interface OnClickListener {

        void onClick(FView v);
    }

    public boolean performClick() {
        final boolean result;
        final FListenerInfo li = mListenerInfo;
        if (li != null && li.mOnClickListener != null) {
            li.mOnClickListener.onClick(this);
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public void setOnClickListener( OnClickListener l) {
        getListenerInfo().mOnClickListener = l;
    }

    FListenerInfo getListenerInfo() {
        if (mListenerInfo != null) {
            return mListenerInfo;
        }
        mListenerInfo = new FListenerInfo();
        return mListenerInfo;
    }

    static class FListenerInfo {
        public OnClickListener mOnClickListener;
    }

}
