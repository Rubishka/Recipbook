package com.rubi.recipbook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.EditText;


interface MyOnDateSetListener{
    void onDateSet(int day, int month, int year);
}

public class MyDatePicker extends EditText implements MyOnDateSetListener {
    int day = 1;
    int month = 1;
    int year=2017;

    public MyDatePicker(Context context) {
        super(context);
        setInputType(0);
    }

    public MyDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(0);
    }

    public MyDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInputType(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            Log.d("TAG","event.getAction() == MotionEvent.ACTION_DOWN");
            MyDatePickerDialog tpd =  MyDatePickerDialog.newInstance(getId());
            //tpd.listener = this;
            tpd.show(((Activity)getContext()).getFragmentManager(),"TAG");
            return true;
        }
        return true;
    }

    @Override
    public void onDateSet(int day, int month,int year) {
        setText("" + day + "/" + month + "/" + year );
    }




    public static class MyDatePickerDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private static final String ARG_CONTAINER_EDIT_TEXT_VIEW = "edit_text_container";
        MyOnDateSetListener listener;

        public static MyDatePicker.MyDatePickerDialog newInstance(int tag) {
            MyDatePickerDialog datePickerDialog = new MyDatePickerDialog();
            Bundle args = new Bundle();
            args.putInt(ARG_CONTAINER_EDIT_TEXT_VIEW, tag);
            datePickerDialog.setArguments(args);
            return datePickerDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            Dialog DatePicker = new DatePickerDialog(getActivity(),this,2017,12,1);

            if (getArguments() != null) {
                int tag = getArguments().getInt(ARG_CONTAINER_EDIT_TEXT_VIEW);
                listener = (MyOnDateSetListener) getActivity().findViewById(tag);
            }

            return DatePicker;
        }

        @Override
        public void onDateSet(DatePicker view, int day, int month,int year) {
            Log.d("TAG","onTimeSet " + day +"/" + month + "/" + year);
            listener.onDateSet(day,month,year);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("TAG", "dialog destroyed");
        }

    }
}



