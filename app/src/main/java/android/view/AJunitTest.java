package android.view;

import org.junit.Test;

/**
 * Created by prize on 2017/9/25.
 */

public class AJunitTest {

    @Test
    public void testChoreographer(){

        FChoreographer.FCallbackQueue queue  = new FChoreographer.FCallbackQueue();
        long nanoTime = System.nanoTime();
        Action action_3 = new Action("nanoTime - 3 * 1000000");
        Action action_2 = new Action("nanoTime - 2 * 1000000");
        Action action_1 = new Action("nanoTime - 1 * 1000000");
        Action action = new Action("nanoTime");
        Action action01 = new Action("nanoTime + 1 * 1000000");
        Action action02 = new Action("nanoTime + 2 * 1000000");
        Action action03 = new Action("nanoTime + 3 * 1000000");
        Action action04 = new Action("nanoTime + 4 * 1000000");
        queue.addCallbackLocked(nanoTime, action ,FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime - 1 * 1000000, action_1 ,FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime + 1 * 1000000, action01 ,FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime - 2 * 1000000, action_2 ,FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime + 2 * 1000000, action02, FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime - 3 * 1000000, action_3 ,FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime + 3 * 1000000, action03 ,FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime + 4 * 1000000, action04 ,FChoreographer.FRAME_CALLBACK_TOKEN);
        queue.addCallbackLocked(nanoTime + 2 * 1000000, action02 ,FChoreographer.FRAME_CALLBACK_TOKEN);
        System.out.println(queue);

        boolean bool = queue.hasDueCallbacksLocked(nanoTime);
        System.out.println(bool);

        FChoreographer.FCallbackRecord fCallbackRecord = queue.extractDueCallbacksLocked(nanoTime);
        System.out.println(fCallbackRecord);
        System.out.println(queue);

        queue.removeCallbacksLocked(action02, FChoreographer.FRAME_CALLBACK_TOKEN);
        System.out.println(queue);

        queue.removeCallbacksLocked(null, FChoreographer.FRAME_CALLBACK_TOKEN);
        System.out.println(queue);
    }

    class Action extends Object{
        String action;
        public Action(String action){
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }
}
