package com.github.android.lvrn.lvrnproject.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.android.lvrn.lvrnproject.LavernaApplication;
import com.github.android.lvrn.lvrnproject.R;
import com.github.android.lvrn.lvrnproject.persistent.entity.Notebook;
import com.github.android.lvrn.lvrnproject.service.core.NotebookService;
import com.github.android.lvrn.lvrnproject.view.adapters.NotebookRecyclerViewAdapter;
import com.github.android.lvrn.lvrnproject.view.adapters.NotesRecyclerViewAdapter;
import com.github.android.lvrn.lvrnproject.view.util.CurrentState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrii Bei <psihey1@gmail.com>
 */

public class NotebookFragmentImpl extends Fragment implements NotesRecyclerViewAdapter.ItemClickListener {
    @BindView(R.id.recycler_view_all_notes)
    RecyclerView mRecyclerView;
    @Inject
    NotebookService mNotebookService;
    private List<Notebook> mNotebookData = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private NotebookRecyclerViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_notes, container, false);
        ButterKnife.bind(this, rootView);
        initRecyclerView();
        LavernaApplication.getsAppComponent().inject(this);
        //TODO: clean it, use ifPresent method on Optional.
        mNotebookService.openConnection();
        if (mNotebookService.getByProfile(CurrentState.profileId,1,10) != null){
            mNotebookData.addAll(mNotebookService.getByProfile(CurrentState.profileId,1,10));
        }
        mNotebookService.closeConnection();
        return rootView;
    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NotebookRecyclerViewAdapter(mNotebookData);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {

    }
}