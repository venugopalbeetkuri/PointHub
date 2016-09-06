package com.pointhub;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pointhub.EarnBillAmountActivity;
import com.pointhub.R;

/**
 * Created by Venu on 03-05-2016.
 */
public class EarnRedeem extends Activity {

    Button earnButton,redeemButton;
    String storeName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earn_redeem);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            storeName = extras.getString("storeName");
        }

        addListenerOnEarnButton();
        addListenerOnRedeemButton();

    }

    /**
     * Earn Button click listener.
     *
     */
    public void addListenerOnEarnButton() {

        earnButton = (Button) findViewById(R.id.earnButton);

        earnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(EarnRedeem.this, EarnBillAmountActivity.class);
                i.putExtra("storeName",storeName);
                startActivity(i);
            }

        });

    }

    /**
     * Earn Button click listener.
     *
     */
    public void addListenerOnRedeemButton() {

        redeemButton = (Button) findViewById(R.id.redeemButton);

        redeemButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(EarnRedeem.this, RedeemBillAmountActivity.class);
                i.putExtra("storeName",storeName);
                startActivity(i);
            }

        });
    }
}
