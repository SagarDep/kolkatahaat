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
import com.kolkatahaat.interfaces.AdminQuantityDialogListener;

public class AddQuantityDialog extends DialogFragment {

    private static final String TAG = "AddQuantityDialog";

    private TextInputLayout textInputItemQuantity;
    private TextInputEditText editTextItemQuantity;

    private TextInputLayout textInputItemPrice;
    private TextInputEditText editTextItemPrice;

    private Button btnOk;
    private Button btnCancel;

    private AdminQuantityDialogListener mListener;

    public AddQuantityDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }
    //dialog_admin_add_quantity


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customView= inflater.inflate(R.layout.dialog_admin_add_quantity,null);
        textInputItemQuantity = customView.findViewById(R.id.textInputItemQuantity);
        editTextItemQuantity= customView.findViewById(R.id.editTextItemQuantity);

        textInputItemPrice= customView.findViewById(R.id.textInputItemPrice);
        editTextItemPrice= customView.findViewById(R.id.editTextItemPrice);

        btnOk =  customView.findViewById(R.id.btnOk);
        btnCancel=  customView.findViewById(R.id.btnCancel);

        builder.setView(customView);

        // Add action buttons
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // sign in the user ...
                if(mListener!=null){

                    if (!validateQuantity() && !validatePrice()) {
                        return;
                    }
                    else if(validateQuantity() && validatePrice()) {
                        dismiss();
                        String strQuantityName = editTextItemQuantity.getText().toString().trim();
                        float strPrice = Float.valueOf(editTextItemPrice.getText().toString().trim());
                        Log.v(TAG, strQuantityName);
                        Log.v(TAG, ""+strPrice);

                        mListener.onDialogPositive(strQuantityName, strPrice);
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
        if (context instanceof AdminQuantityDialogListener) {
            mListener = (AdminQuantityDialogListener) context;
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

    private boolean validateQuantity() {
        if (TextUtils.isEmpty(editTextItemQuantity.getText().toString().trim())) {
            textInputItemQuantity.setError("Please Enter Valid Quantity Name!");
            textInputItemQuantity.requestFocus();
            return false;
        } else {
            textInputItemQuantity.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatePrice() {
        if (TextUtils.isEmpty(editTextItemPrice.getText().toString().trim())) {
            textInputItemPrice.setError("Please Enter Valid Price!");
            textInputItemPrice.requestFocus();
            return false;
        } else {
            textInputItemPrice.setErrorEnabled(false);
        }
        return true;
    }
}
