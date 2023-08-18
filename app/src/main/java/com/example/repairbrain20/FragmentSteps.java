package com.example.repairbrain20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Map;

public class FragmentSteps extends Fragment {

    ListView listView;
    Listener listener;
    View view;

    FragmentSteps()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        listView = view.findViewById(R.id.list);
        this.view = view;
        listener =  new Listener(getActivity(),view,"next_steps");

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_effects, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add:
                listener.addEffect();
                break;

            case R.id.common:
                Intent intent = new Intent(getActivity(), ActCommon.class);
                intent.putExtra("effect","next_steps");
                startActivity(intent);
                break;

            case R.id.remove:
                Map<String,String> result = listener.getEffectsMap();
                listView.setAdapter(new AdapterPosNegNext(getActivity(),view,result,"next_steps",true));
                break;

            case R.id.reset:
                listener.reset();
                break;
        }
        return true;
    }
}