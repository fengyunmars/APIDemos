package android.content;

import android.annotation.NonNull;

/**
 * Created by fengyun on 2017/10/13.
 */

public abstract class FContext {

    public abstract Object getSystemService(@NonNull String name);
}
