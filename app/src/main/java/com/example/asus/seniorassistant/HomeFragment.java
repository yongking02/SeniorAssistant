package com.example.asus.seniorassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    GridLayout mainGrid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mainGrid = (GridLayout)view.findViewById(R.id.mainGrid);

        setSingleEvent(mainGrid);


        return view;
    }

    private void setSingleEvent(GridLayout mainGrid) {
        for(int i=0;i<mainGrid.getChildCount();i++){
            CardView cardView = (CardView)mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalI==0){
                        Intent intent = new Intent(getActivity(), ActivityOne.class);
                        startActivity(intent);
                    }else if (finalI == 2){
                        Intent intent = new Intent(getActivity(), ActivityHeartRate.class);
                        startActivity(intent);
                    }else if (finalI == 3){
                        Intent intent = new Intent(getActivity(), ActivityThree.class);
                        startActivity(intent);
                    }else if (finalI == 4){
                        Intent intent = new Intent(getActivity(), ActivityFour.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), "Please Select One!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
