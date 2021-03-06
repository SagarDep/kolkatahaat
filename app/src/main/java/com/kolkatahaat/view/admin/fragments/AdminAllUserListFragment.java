package com.kolkatahaat.view.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.UsersListAdapter;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.interfaces.RecyclerViewRemoveClickListener;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;
import com.kolkatahaat.view.admin.AdminEditCustomerProfileActivity;
import com.kolkatahaat.view.admin.AdminUpdateSliderImageActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminAllUserListFragment extends Fragment {

    private static final String TAG = AdminAllUserListFragment.class.getSimpleName();

    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private CollectionReference collectReference;

    private RecyclerView mRecyclerView;
    private UsersListAdapter mAdapter;
    private List<Users> messages;

    private ProgressBar progressBar;

    public AdminAllUserListFragment() {
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        collectReference = fireStore.collection("users");
        messages = new ArrayList<>();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        if (NetUtils.isNetworkAvailable(getActivity())) {
            collectReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("TAG", "New Msg: " + dc.getDocument().toObject(Users.class));
                                Users note = dc.getDocument().toObject(Users.class);
                                messages.add(note);
                                mAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                String key = dc.getDocument().getId();
                                Log.d("TAG", "Modified Msg: " + key);

                                if (messages.size() != 0) {
                                    for (int i = 0; i < messages.size(); i++) {
                                        if (messages.get(i).getUserUId().equals(dc.getDocument().getId())) {

                                            Users billModel = dc.getDocument().toObject(Users.class);
                                            messages.set(i, billModel);
                                            mAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                }
                                break;

                            case REMOVED:
                                Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(Users.class));

                                if (messages.size() != 0) {
                                    for (int i = 0; i < messages.size(); i++) {
                                        if (messages.get(i).getUserUId().equals(dc.getDocument().getId())) {

                                            //BillModel billModel = dc.getDocument().toObject(BillModel.class);
                                            messages.remove(i);
                                            mAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            });

            RecyclerViewRemoveClickListener listener = new RecyclerViewRemoveClickListener() {
                @Override
                public void onClick(View view, int position) {
                    //Toast.makeText(getContext(), "Position " + messages.get(position).getUserUId(), Toast.LENGTH_SHORT).show();
                    if(!TextUtils.isEmpty(messages.get(position).getUserUId()) && messages.get(position).getUserUId() != null && messages.get(position).getUserUId() != "") {
                        Intent intent = new Intent(getActivity(), AdminEditCustomerProfileActivity.class);
                        intent.putExtra("EXTRA_EDIT_USER_ID", messages.get(position).getUserUId());
                        getActivity().startActivity(intent);
                    }
                }

                @Override
                public void onRemoveItem(View view, int position) {
                    //Toast.makeText(getContext(), "Position " + messages.get(position).getUserUId(), Toast.LENGTH_SHORT).show();
                    if (NetUtils.isNetworkAvailable(getActivity())) {
                        if (messages.size() != 0  && messages != null) {
                            userRemove(messages, position);
                        } else {
                            Utility.displayDialog(getActivity(), getString(R.string.common_no_user_available), false);
                        }
                    } else {
                        Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
                    }
                }
            };
            mAdapter = new UsersListAdapter(getActivity(), messages, listener);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }

        /*collectReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Users note = documentSnapshot.toObject(Users.class);
                    messages.add(note);
                }


                RecyclerViewRemoveClickListener listener = new RecyclerViewRemoveClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //Toast.makeText(getContext(), "Position " + messages.get(position).getUserUId(), Toast.LENGTH_SHORT).show();
                        if(!TextUtils.isEmpty(messages.get(position).getUserUId()) && messages.get(position).getUserUId() != null && messages.get(position).getUserUId() != "") {
                            Intent intent = new Intent(getActivity(), AdminEditCustomerProfileActivity.class);
                            intent.putExtra("EXTRA_EDIT_USER_ID", messages.get(position).getUserUId());
                            getActivity().startActivity(intent);
                        }
                    }

                    @Override
                    public void onRemoveItem(View view, int position) {
                        //Toast.makeText(getContext(), "Position " + messages.get(position).getUserUId(), Toast.LENGTH_SHORT).show();
                        if (NetUtils.isNetworkAvailable(getActivity())) {
                            if (messages.size() != 0  && messages != null) {
                                userRemove(messages, position);
                            } else {
                                Utility.displayDialog(getActivity(), getString(R.string.common_no_user_available), false);
                            }
                        } else {
                            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
                        }
                    }
                };
                mAdapter = new UsersListAdapter(getActivity(), messages, listener);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });*/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set title of this fragment
        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.menu_users_list));
        }
    }


    public void userRemove(final List<Users> messages, final int position){
        if (NetUtils.isNetworkAvailable(getActivity())) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(messages.get(position).getUserEmail(), messages.get(position).getUserPassword());

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");

                                DocumentReference fireRefe = fireStore.collection("users").document(messages.get(position).getUserUId());
                                fireRefe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                        messages.remove(position);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error deleting document", e);
                                    }
                                });

                            }
                        }
                    });
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }
}
