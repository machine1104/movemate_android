package app.movemate;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class CreateFragment extends Fragment {
    private SeekBar seekBar;
    private TextView textView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_create, container, false);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        textView = (TextView) v.findViewById(R.id.text2);
        textView.setText(seekBar.getProgress()+"");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                textView.setText(progress+"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText(progress+"");

            }
        });





        //IMPLEMENTARE .SHOW() ONBACKPRESSED
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return v;
    }


}
