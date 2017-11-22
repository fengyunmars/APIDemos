package com.android.deskclock.prize;

import com.android.deskclock.provider.Alarm;

import android.app.AlertDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
//import android.widget.TimePicker.ValidationCallback;

import com.android.deskclock.R;

public class PrizeTimerPickerDialog extends AlertDialog implements OnClickListener,
OnTimeChangedListener {
	
	

	public PrizeTimerPickerDialog(Context context, OnTimeSetListener callBack) {
		this(context, 0, callBack);
	}

	public PrizeTimerPickerDialog(Context context, int theme, OnTimeSetListener callBack) {
		super(context, resolveDialogTheme(context, theme));
		mTimeSetCallback = callBack;
		final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.prize_timerpicker_layout, null, false);
        setView(view);
	}

    private final OnTimeSetListener mTimeSetCallback;

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Done' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param alarm selected alarm.
         */
        void onTimeSet(Alarm alarm);
    }


    static int resolveDialogTheme(Context context, int resid) {
        if (resid == 0) {
            final TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(com.android.internal.R.attr.timePickerDialogTheme, outValue, true);
            return outValue.resourceId;
        } else {
            return resid;
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        /* do nothing */
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mTimeSetCallback != null) {
                    mTimeSetCallback.onTimeSet(new Alarm());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

//    private final ValidationCallback mValidationCallback = new ValidationCallback() {
//        @Override
//        public void onValidationChanged(boolean valid) {
//            final Button positive = getButton(BUTTON_POSITIVE);
//            if (positive != null) {
//                positive.setEnabled(valid);
//            }
//        }
//    };

}
