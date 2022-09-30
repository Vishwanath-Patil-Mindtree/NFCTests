package com.bose.nfc.two;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class DeepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        setContentView(R.layout.activity_deep);

        Log.d("TAG","onCreate " + this.getLocalClassName());
        processIntent(getIntent());
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        try {
            if (rawMsgs != null) {
                NdefMessage msg = (NdefMessage) rawMsgs[0];

                StringBuilder sb = new StringBuilder();
                sb.append("tag: ");
                sb.append(new String(msg.getRecords()[0].getType()));
                sb.append("\r\n");
                sb.append("payload: ");
                sb.append(new String(msg.getRecords()[0].getPayload()));

                Log.d("TAG", sb.toString());

                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(sb.toString());
            }
        } catch(Exception e) {
            Log.d("TAG", "Exception " + e.getLocalizedMessage());
        }
    }
}