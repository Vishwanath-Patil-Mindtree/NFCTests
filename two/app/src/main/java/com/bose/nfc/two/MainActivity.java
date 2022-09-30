package com.bose.nfc.two;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter mAdapter;
    private Boolean writeNfcRecord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("TAG","onCreate " + this.getLocalClassName());
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        setContentView(R.layout.activity_main);
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        findViewById(R.id.button).setOnClickListener(view -> {
            writeNfcRecord = true;
            if(mAdapter!= null) {
                Bundle options = new Bundle();
                // Work around for some broken Nfc firmware implementations that poll the card too fast
                options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

                // Enable ReaderMode for all types of card and disable platform sounds
                mAdapter.enableReaderMode(this,
                        this,
                        NfcAdapter.FLAG_READER_NFC_A |
                                NfcAdapter.FLAG_READER_NFC_B |
                                NfcAdapter.FLAG_READER_NFC_F |
                                NfcAdapter.FLAG_READER_NFC_V |
                                NfcAdapter.FLAG_READER_NFC_BARCODE |
                                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                        options);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(writeNfcRecord) {
            if (mAdapter != null) {
                mAdapter.disableReaderMode(this);
            }
        }
    }

    public void onTagDiscovered(Tag tag) {

        Ndef mNdef = Ndef.get(tag);

        // Check that it is an Ndef capable card
        if (mNdef!= null) {
            NdefRecord appRecord = new NdefRecord( NdefRecord.TNF_MIME_MEDIA ,
                    "application/swift.speaker.setup.500".getBytes(StandardCharsets.US_ASCII),
                    new byte[0], "010000".getBytes(StandardCharsets.US_ASCII));

            NdefRecord rtdUriRecord = NdefRecord.createUri("https://swift.boseconcept.com/?p=010000");

            NdefMessage mMsg = new NdefMessage(new NdefRecord[] { appRecord, rtdUriRecord });

            // Catch errors
            try {
                mNdef.connect();
                mNdef.writeNdefMessage(mMsg);

                // Success if got to here
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        "Write to NFC Success",
                        Toast.LENGTH_SHORT).show());

                Log.d("TAG", "Write Tag success");

            } catch (FormatException e) {
                // if the NDEF Message to write is malformed
            } catch (TagLostException e) {
                // Tag went out of range before operations were complete
            } catch (IOException e){
                // if there is an I/O failure, or the operation is cancelled
            } finally {
                // Be nice and try and close the tag to
                // Disable I/O operations to the tag from this TagTechnology object, and release resources.
                try {
                    mNdef.close();
                } catch (IOException e) {
                    // if there is an I/O failure, or the operation is cancelled
                }
            }
        }
    }
}