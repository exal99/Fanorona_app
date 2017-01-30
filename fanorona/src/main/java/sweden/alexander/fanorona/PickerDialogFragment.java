package sweden.alexander.fanorona;

import android.app.Dialog;
import android.os.Bundle;

import mobi.upod.timedurationpicker.TimeDurationPickerDialog;
import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;
import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationUtil;

public class PickerDialogFragment extends TimeDurationPickerDialogFragment {
    
    private String title;
    private String message;
    private TimeDurationPickerDialog.OnDurationSetListener durationSetListener;

    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimeDurationPickerDialog dialog = (TimeDurationPickerDialog) super.onCreateDialog(savedInstanceState);
        dialog.getDurationInput().setNumPadButtonPadding(0);
        dialog.getDurationInput().setDisplayTextAppearance(R.style.SmallFont);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.hide();
        return dialog;
    }
    
    
    public void setTitle(String title) {
        this.title = title;
    }

    
    public void setMessage(String newMessage) {
        message = newMessage;
    }
    
    @Override
    protected long getInitialDuration() {
        return 3 * 60 * 1000;
    }


    @Override
    protected int setTimeUnits() {
        return TimeDurationPicker.MM_SS;
    }

    public void setDurationSetListener(TimeDurationPickerDialog.OnDurationSetListener listener) {
        durationSetListener = listener;
    }

    @Override
    public void onDurationSet(TimeDurationPicker view, long duration) {
        durationSetListener.onDurationSet(view, duration);
    }
    
    public static int[] getTime(long duration) {
        int minutes = TimeDurationUtil.minutesOf(duration);
        int seconds = TimeDurationUtil.secondsInMinuteOf(duration);
        int[] time = {minutes, seconds, 0};
        return time;
    }
}
