package com.pointhub;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pointhub.R;
import com.pointhub.db.DatabaseHelper;
import com.pointhub.db.Points;
import com.pointhub.scanner.QRCode;

/**
 * Created by Venu on 03-05-2016.
 */
public class RedeemBillAmountActivity extends Activity {

    Button button;
    EditText billAmount;
    Spinner pointsRedeemed;
    String storeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.redeem_bill_amount);

        billAmount = (EditText) findViewById(R.id.redeemBillAmountText);

        button = (Button) findViewById(R.id.submitButton);

        pointsRedeemed = (Spinner) findViewById(R.id.spinner1);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean success = true;

                // success = updateInMongoDBServer();

                if (success) {

                    hideSoftKeyboard(billAmount);

                    int amount = Integer.parseInt(billAmount.getText().toString());

                    String points = pointsRedeemed.getSelectedItem().toString();
                    int pointsRedeemed = Integer.parseInt(points);
                    if(pointsRedeemed > amount) {
                        Toast.makeText(getApplicationContext(), "Select proper points. " , Toast.LENGTH_LONG).show();
                        Intent i = new Intent(RedeemBillAmountActivity.this, RedeemBillAmountActivity.class);
                        startActivity(i);
                        return;
                    }

                    int pay = amount - pointsRedeemed;

                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        storeName = extras.getString("storeName");
                    }

                    String lastUpdate = DatabaseHelper.getInstance(getApplicationContext()).getDateTime();
                    Points pnts = DatabaseHelper.getInstance(getApplicationContext()).getPoints(storeName);

                    if(null == pnts) {

                        pnts = new Points(storeName, billAmount.getText().toString(), lastUpdate);
                        DatabaseHelper.getInstance(getApplicationContext()).createPoints(pnts);
                    } else {

                        int lastPoints = Integer.parseInt(pnts.getPoints());
                        Integer presentPoints = lastPoints - pointsRedeemed;
                        pnts.setPoints(presentPoints.toString());
                        pnts.setLastVisited(lastUpdate);

                        DatabaseHelper.getInstance(getApplicationContext()).updatePoints(pnts);
                    }

                    Toast.makeText(getApplicationContext(), "You have redeemed, Just pay amount: " + pay , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to redeem points.", Toast.LENGTH_LONG).show();
                }

                Intent i = new Intent(RedeemBillAmountActivity.this, QRCode.class);
                startActivity(i);
            }
        });
    }

    /**
     * Hide keyboard.
     *
     * @param input
     */
    protected void hideSoftKeyboard(EditText input) {
        input.setInputType(0);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }
}





