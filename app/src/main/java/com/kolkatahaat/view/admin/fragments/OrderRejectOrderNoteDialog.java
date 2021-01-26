package com.kolkatahaat.view.admin.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.AdminOrderRejectDialogListener;

public class OrderRejectOrderNoteDialog extends DialogFragment {

    private static final String TAG = "AddQuantityDialog";

    private TextInputLayout textInputRejection;
    private TextInputEditText editTextRejection;


    private Button btnOk;
    private Button btnCancel;

    private AdminOrderRejectDialogListener mListener;

    public OrderRejectOrderNoteDialog() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customView= inflater.inflate(R.layout.dialog_admin_reject_order,null);
        textInputRejection = customView.findViewById(R.id.textInputRejection);
        editTextRejection= customView.findViewById(R.id.editTextRejection);


        btnOk =  customView.findViewById(R.id.btnOk);
        btnCancel=  customView.findViewById(R.id.btnCancel);

        builder.setView(customView);

        // Add action buttons
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // sign in the user ...
                if(mListener!=null){

                    if (!validateReject()) {
                        return;
                    }
                    else if(validateReject()) {
                        dismiss();
                        String strQuantityName = editTextRejection.getText().toString().trim();
                        Log.v(TAG, strQuantityName);
                        mListener.onDialogPositive(strQuantityName);
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                if(mListener!=null){
                    mListener.onDialogNegative(null);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdminOrderRejectDialogListener) {
            mListener = (AdminOrderRejectDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CustomDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private boolean validateReject() {
        if (TextUtils.isEmpty(editTextRejection.getText().toString().trim())) {
            textInputRejection.setError("Please Enter Valid Quantity Name!");
            textInputRejection.requestFocus();
            return false;
        } else {
            textInputRejection.setErrorEnabled(false);
        }
        return true;
    }
}
