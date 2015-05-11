package ru.gleb.manyagin.weathermap.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.gleb.manyagin.weathermap.R;

/**
 * Created by gleb.manyagin on 08.05.2015.
 */
public class EditTimePreference extends DialogPreference {
    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;
    private int stateToSave;
    private static final String androidns="http://schemas.android.com/apk/res/android";

    public EditTimePreference(Context context, AttributeSet attrs) {
        super(context,attrs);
        setPositiveButtonText(context.getString(R.string.caption_set_time_pref));
        setNegativeButtonText(context.getString(R.string.caption_cancel_time_pref));
    }

    public static int getHour(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.getTime().getHours();
    }

    public static int getMinute(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return calendar.getTime().getMinutes();
       // return 0;
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());
        picker.setIs24HourView(true);
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        lastHour = picker.getCurrentHour();
        lastMinute = picker.getCurrentMinute();

        if (positiveResult) {


            Calendar calendar = Calendar.getInstance();
            calendar.set(2015, 1, 1, lastHour, lastMinute, 0);
            Long time = calendar.getTimeInMillis();

            if (callChangeListener(time)) {
                persistString(time.toString());
            }

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);

        Long time = new Long(-1);

     if (restoreValue) {
            if (defaultValue==null) {
                time = new Long(getPersistedString(new Long(0).toString()));
            }
            else {
                time = new Long(getPersistedString(defaultValue.toString()));
            }
        }
        else {
            time = new Long(defaultValue.toString());
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("lastHour", this.picker.getCurrentHour().intValue());
        bundle.putInt("lastMinute", this.picker.getCurrentMinute().intValue());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            lastHour = bundle.getInt("lastHour");
            lastMinute = bundle.getInt("lastMinute");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

}