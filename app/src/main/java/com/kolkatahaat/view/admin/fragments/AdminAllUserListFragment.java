package com.kolkatahaat.view.admin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.UsersListAdapter;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.Users;

import java.util.ArrayList;
import java.util.List;

public class AdminAllUserListFragment extends Fragment {

    private static final String TAG = AdminAllUserListFragment.class.getSimpleName();

    private FirebaseFirestore fireStore;
    private CollectionReference collectReference;

    private RecyclerView mRecyclerView;
    private UsersListAdapter mAdapter;
    private List<Users> messages;

    public AdminAllUserListFragment() {
        fireStore = FirebaseFirestore.getInstance();
        collectReference = fireStore.collection("users");
        messages = new ArrayList<>();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        mRecyclerView = view.findViewById(R.id.mRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        collectReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Users note = documentSnapshot.toObject(Users.class);
                    messages.add(note);


                }


                RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(getContext(), "Position " + messages.get(position).getUserName(), Toast.LENGTH_SHORT).show();

                        //  Intent intent = new Intent(getActivity(), AdminAddProductActivity.class);
                        //    startActivity(intent);
                    }
                };
                mAdapter = new UsersListAdapter(getActivity(), messages, listener);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
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

    /*public List<Product> loadNotes() {
        final List<Product> arrayList = new ArrayList<>();

        return arrayList;
    }*/

}
