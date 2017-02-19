package app.movemate.ListAdapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import app.movemate.R;

public class PathsAdapter extends ArrayAdapter {
    List list = new ArrayList();

    public PathsAdapter(Context context, int resource) {
        super(context, resource);
    }
    public void add(Path object) {
        super.add(object);
        list.add(object);
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        PathHolder pathHolder = new PathHolder();

        if (row==null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.path_list_layout,parent,false);


            pathHolder.tx_pathname= (TextView) row.findViewById(R.id.pathname);
            pathHolder.tx_fa= (TextView) row.findViewById(R.id.fa);
            pathHolder.tx_ta= (TextView) row.findViewById(R.id.ta);
            pathHolder.imv= (ImageView) row.findViewById(R.id.i);
            pathHolder.tx_d= (TextView) row.findViewById(R.id.d);
            pathHolder.tx_p= (TextView) row.findViewById(R.id.p);

            row.setTag(pathHolder);

        }else{
            pathHolder = (PathHolder)row.getTag();
        }
        Path path = (Path) this.getItem(position);
        try {

            pathHolder.tx_pathname.setText(path.path.getString("PathName"));
            pathHolder.tx_fa.setText(path.path.getString("StartAddress"));
            pathHolder.tx_ta.setText(path.path.getString("DestinationAddress"));
            pathHolder.tx_d.setText(path.path.getString("Date"));

            int i = path.path.getInt("Vehicle");

            Drawable drawable;
            if (i == 1){
                drawable = ContextCompat.getDrawable(pathHolder.imv.getContext(),R.drawable.ic_motorcycle);
                pathHolder.imv.setBackground(drawable);
                int price = Integer.parseInt(path.path.getString("Price"));
                if(price == 0){
                    pathHolder.tx_p.setText("FREE");
                }else{
                    pathHolder.tx_p.setText(price+"€");
                }
                if (price < 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_green));
                }
                else if (price < 7 && price >= 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_orange));
                }else{
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_red));
                }
                pathHolder.tx_p.setText(price+"€");
            }
            else if (i == 2){
                drawable = ContextCompat.getDrawable(pathHolder.imv.getContext(),R.drawable.ic_bus);
                pathHolder.imv.setBackground(drawable);
                pathHolder.tx_p.setText("FREE");
                pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_green));
            }else{
                drawable = ContextCompat.getDrawable(pathHolder.imv.getContext(),R.drawable.ic_car);
                pathHolder.imv.setBackground(drawable);
                int price = Integer.parseInt(path.path.getString("Price"));
                if(price == 0){
                    pathHolder.tx_p.setText("FREE");
                }else{
                    pathHolder.tx_p.setText(price+"€");
                }
                if (price < 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_green));
                }
                else if (price < 7 && price >= 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_orange));
                }else{
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_red));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    static class PathHolder{

        TextView tx_pathname;
        TextView tx_fa;
        TextView tx_ta;
        TextView tx_p;
        TextView tx_d;
        ImageView imv;

    }
}
