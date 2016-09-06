package com.pointhub.db;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pointhub.R;

import java.util.List;


/**
 * Created by Raj on 28/04/2016.
 */
public class PointsAdapter extends BaseAdapter {


    private Activity activity;
    private List<Points> places;
    private static LayoutInflater inflater=null;

    public PointsAdapter(Activity activity, List<Points> placesList)

    {
        this.activity=activity;
        this.places=placesList;
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }




    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        final Points point=places.get(position);


        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.point_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtName = (TextView) vi.findViewById(R.id.storeName);
            holder.txtPoints = (TextView) vi.findViewById(R.id.points);
            holder.btnDelete= (Button) vi.findViewById(R.id.btnDelete);



            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();

        holder.txtName.setText(point.getStoreName());

        holder.txtPoints.setText(point.getPoints());

        holder.btnDelete.setTag(point.getId());



        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id= (int) v.getTag();

                Points temp= getPoints(id);

                DatabaseHelper.getInstance(activity).deletePoint(id);
                places=DatabaseHelper.getInstance(activity).getAllPoints();
                PointsAdapter.this.notifyDataSetChanged();


            }
        });

        return vi;
    }

    private Points getPoints(int id)
    {
        for(Points place:places)
        {
            if(place.getId()==id)
                return place;
        }
        return null;
    }

    public static class ViewHolder{

        public TextView txtName;
        public TextView txtPoints;
        public Button btnDelete;


    }
}
