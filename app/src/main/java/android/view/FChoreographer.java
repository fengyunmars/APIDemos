/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.view;


/**
 * Created by fengyun on 2017/9/25.
 */

public final class FChoreographer {

    static FChoreographer.FCallbackRecord mCallbackPool;

    static FCallbackRecord obtainCallbackLocked(long dueTime, Object action, Object token) {
        FCallbackRecord callback = mCallbackPool;
        if (callback == null) {
            callback = new FCallbackRecord();
        } else {
            mCallbackPool = callback.next;
            callback.next = null;
        }
        callback.dueTime = dueTime;
        callback.action = action;
        callback.token = token;
        return callback;
    }

    static void recycleCallbackLocked(FCallbackRecord callback) {
        callback.action = null;
        callback.token = null;
        callback.next = mCallbackPool;
        mCallbackPool = callback;
    }

    // All frame callbacks posted by applications have this token.
    static final Object FRAME_CALLBACK_TOKEN = new Object() {
        public String toString() {
            return "FRAME_CALLBACK_TOKEN";
        }
    };

    public interface FFrameCallback {

        public void doFrame(long frameTimeNanos);
    }

    static final class FCallbackRecord {
        public FCallbackRecord next;
        public long dueTime;
        public Object action; // Runnable or FrameCallback
        public Object token;

        public void run(long frameTimeNanos) {
            if (token == FRAME_CALLBACK_TOKEN) {
                ((FFrameCallback)action).doFrame(frameTimeNanos);
            } else {
                ((Runnable)action).run();
            }
        }

        @Override
        public String toString() {
            return String.valueOf(dueTime) + "--------->" + action;
        }
    }

    static class FCallbackQueue {

        FCallbackRecord mHead;

        public boolean hasDueCallbacksLocked(long now) {    // due 到期
            return mHead != null && mHead.dueTime <= now;
        }

        public FCallbackRecord extractDueCallbacksLocked(long now) {
            FCallbackRecord callbacks = mHead;
            if (callbacks == null || callbacks.dueTime > now) {
                return null;
            }

            FCallbackRecord last = callbacks;
            FCallbackRecord next = last.next;
            while (next != null) {
                if (next.dueTime > now) {
                    last.next = null;
                    break;
                }
                last = next;
                next = next.next;
            }
            mHead = next;
            return callbacks;
        }

        public void addCallbackLocked(long dueTime, Object action, Object token) {
            FCallbackRecord callback = obtainCallbackLocked(dueTime, action, token);
            FCallbackRecord entry = mHead;
            if (entry == null) {
                mHead = callback;
                return;
            }
            if (dueTime < entry.dueTime) {
                callback.next = entry;
                mHead = callback;
                return;
            }
            while (entry.next != null) {
                if (dueTime < entry.next.dueTime) {
                    callback.next = entry.next;
                    break;
                }
                entry = entry.next;
            }
            entry.next = callback;
        }

        public void removeCallbacksLocked(Object action, Object token) {
            FCallbackRecord predecessor = null;
            for (FCallbackRecord callback = mHead; callback != null;) {
                final FCallbackRecord next = callback.next;
                if ((action == null || callback.action == action)
                        && (token == null || callback.token == token)) {
                    if (predecessor != null) {
                        predecessor.next = next;
                    } else {
                        mHead = next;
                    }
                    recycleCallbackLocked(callback);
                } else {
                    predecessor = callback;
                }
                callback = next;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            sb.append("i = " + i + "-->" + "mHead = " + mHead + "\n");
            if(mHead != null) {
                FCallbackRecord next = mHead.next;
                while (next != null) {
                    i++;
                    sb.append("i = " + i + "-->" + " next = " + next + "\n");
                    next = next.next;
                }
                return sb.toString();
            }
            return sb.toString();
        }
    }
}
