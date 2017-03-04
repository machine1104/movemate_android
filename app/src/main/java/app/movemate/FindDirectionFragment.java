package app.movemate;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FindDirectionFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_choose_direction, container, false);

        Button btn_to = (Button)view.findViewById(R.id.to);
        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putBoolean("ToFrom",true);
                FilterFragment frag = new FilterFragment();
                frag.setArguments(b);
                ((MainActivity)getActivity()).nextFrag(frag);
            }
        });

        Button btn_from = (Button)view.findViewById(R.id.from);
        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putBoolean("ToFrom",false);
                FilterFragment frag = new FilterFragment();
                frag.setArguments(b);
                ((MainActivity)getActivity()).nextFrag(frag);
            }
        });

        return view;
    }

}
