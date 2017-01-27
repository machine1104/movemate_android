package app.movemate;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CreateFragment extends Fragment {
    private SeekBar avaible_bar,price_bar;
    private TextView avaible,price;
    private RadioGroup vehicles;
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_create, container, false);

        //Barra posti disponibili
        avaible_bar = (SeekBar) v.findViewById(R.id.seekBar);
        avaible = (TextView) v.findViewById(R.id.text2);
        avaible.setText(avaible_bar.getProgress()+1+"");
        avaible_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                avaible.setText(progress+1+"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                avaible.setText(progress+1+"");

            }
        });

        //Barra prezzo
        price_bar = (SeekBar)v.findViewById(R.id.seekBar2);
        price = (TextView)v.findViewById(R.id.price);
        price.setText("FREE");
        price_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (progress == 0){
                    price.setText("FREE");
                }
                else{
                    price.setText(progress+"€");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progress == 0){
                    price.setText("FREE");
                }
                else{
                    price.setText(progress+"€");
                }
            }
        });

        //Visualizzazione prezzo
        vehicles = (RadioGroup)v.findViewById(R.id.vehicles);
        vehicles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.car || i == R.id.moto ){
                    RelativeLayout ly = (RelativeLayout) v.findViewById(R.id.price_layout);
                    ly.setVisibility(View.VISIBLE);
                }else{
                    RelativeLayout ly = (RelativeLayout) v.findViewById(R.id.price_layout);
                    ly.setVisibility(View.GONE);
                }
            }
        });

        //Autocomplete

        String[] locations = new String[] {"paperina","paperone","paperino","paperoga"};
        AutoCompleteTextView from = (AutoCompleteTextView) v.findViewById(R.id.from);
        AutoCompleteTextView to = (AutoCompleteTextView) v.findViewById(R.id.to);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_dropdown_item_1line,locations
                );
        from.setAdapter(adapter);
        to.setAdapter(adapter);






        //IMPLEMENTARE .SHOW() ONBACKPRESSED
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return v;
    }


}
