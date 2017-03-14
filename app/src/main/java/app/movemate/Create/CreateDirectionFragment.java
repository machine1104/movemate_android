package app.movemate.Create;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import app.movemate.CreateActivity;
import app.movemate.MainActivity;
import app.movemate.R;


public class CreateDirectionFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_direction, container, false);

        Button btn_to = (Button)view.findViewById(R.id.to);
        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                JSONObject route = new JSONObject();
                try {
                    route.put("ToFrom",true);
                    route.put("StudentId",MainActivity.user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                b.putString("route",route.toString());
                CreateTransportFragment frag = new CreateTransportFragment();
                frag.setArguments(b);
                ((CreateActivity)getActivity()).nextFrag(frag);
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
                    route.put("StudentId",MainActivity.user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                b.putString("route",route.toString());
                CreateTransportFragment frag = new CreateTransportFragment();
                frag.setArguments(b);
                ((CreateActivity)getActivity()).nextFrag(frag);
            }
        });

        return view;
    }

}
