package app.movemate.Adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.movemate.MainActivity;
import app.movemate.R;


public class PassAdapter extends RecyclerView.Adapter<PassAdapter.MyViewHolder> {

    private JSONArray passList;
    private MainActivity ctx;

    public PassAdapter(Context context,JSONArray passList) {
        this.passList = passList;
        this.ctx = (MainActivity)context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partecipant_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject partecipant = null;
        try {
            partecipant = passList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.name.setText(partecipant.getString("Name"));
            if(partecipant.getString("StudentId").equals(ctx.user_id)){
                //NavigationView navigationView = (NavigationView) ctx.findViewById(R.id.nav_view);
                //View dView =  navigationView.getHeaderView(0);
                //ImageView pic = (ImageView) dView.findViewById(R.id.photo);
                //holder.imv.setBackground(ctx.pic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return passList.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView imv;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            imv = (ImageView)view.findViewById(R.id.pic);

        }
    }
    public JSONObject getItem(int position) throws JSONException {
        return passList.getJSONObject(position);
    }
}


