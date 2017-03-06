package app.movemate;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;


public class ChooseDirectionFragment extends Fragment {

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
                JSONObject route = new JSONObject();
                try {
                    route.put("ToFrom",true);
                    route.put("StudentId",((MainActivity)getActivity()).user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                b.putString("route",route.toString());
                CreateTransportFragment frag = new CreateTransportFragment();
                frag.setArguments(b);
                ((MainActivity)getActivity()).nextFrag(frag);
            }
        });

        Button btn_from = (Button)view.findViewById(R.id.from);
        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                JSONObject route = new JSONObject();
                try {
                    route.put("ToFrom",false);
                    route.put("StudentId",((MainActivity)getActivity()).user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                b.putString("route",route.toString());
                CreateTransportFragment frag = new CreateTransportFragment();
                frag.setArguments(b);
                ((MainActivity)getActivity()).nextFrag(frag);
            }
        });

        return view;
    }

}
