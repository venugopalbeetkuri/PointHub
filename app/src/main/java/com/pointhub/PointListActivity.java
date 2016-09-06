package com.pointhub;

import android.app.Activity;

import android.os.Bundle;
import android.widget.ListView;

import com.pointhub.R;
import com.pointhub.db.DatabaseHelper;
import com.pointhub.db.Points;
import com.pointhub.db.PointsAdapter;

import java.util.List;

/**
 * Created by Venu on 03-05-2016.
 */
public class PointListActivity extends Activity {

    ListView lstPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);

        lstPoints= (ListView) findViewById(R.id.lstPoints);

        List<Points> points= DatabaseHelper.getInstance(this).getAllPoints();
        lstPoints.setAdapter(new PointsAdapter(this,points));

    }
}
