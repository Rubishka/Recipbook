package com.rubi.recipbook;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TimePicker;


interface MyOnTimeSetListener{
    void onTimeSet(int hour, int min);
}

public class MyTimePicker extends EditText implements MyOnTimeSetListener {
    int hour = 12;
    int min = 0;

    public MyTimePicker(Context context) {
        super(context);
        setInputType(0);
    }

    public MyTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(0);
    }

    public MyTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInputType(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            Log.d("TAG","event.getAction() == MotionEvent.ACTION_DOWN");
            MyTimePickerDialog tpd =  MyTimePickerDialog.newInstance(getId());
            //tpd.listener = this;
            tpd.show(((Activity)getContext()).getFragmentManager(),"TAG");
            return true;
        }
        return true;
    }

    @Override
    public void onTimeSet(int hour, int min) {
        setText("" + hour + ":" + min);
    }


    public static class MyTimePickerDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private static final String ARG_CONTAINER_EDIT_TEXT_VIEW = "edit_text_container";
        MyOnTimeSetListener listener;

        public static MyTimePickerDialog newInstance(int tag) {
            MyTimePickerDialog timePickerDialog = new MyTimePickerDialog();
            Bundle args = new Bundle();
            args.putInt(ARG_CONTAINER_EDIT_TEXT_VIEW, tag);
            timePickerDialog.setArguments(args);
            return timePickerDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            Dialog timePicker = new TimePickerDialog(getActivity(),this,22,44,false);

            if (getArguments() != null) {
                int tag = getArguments().getInt(ARG_CONTAINER_EDIT_TEXT_VIEW);
                listener = (MyOnTimeSetListener) getActivity().findViewById(tag);
            }

            return timePicker;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d("TAG","onTimeSet " + hourOfDay +":" + minute);
            listener.onTimeSet(hourOfDay,minute);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("TAG", "dialog destroyed");
        }
    }
}



