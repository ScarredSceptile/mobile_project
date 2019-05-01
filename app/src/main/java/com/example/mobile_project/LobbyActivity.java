package com.example.mobile_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class LobbyActivity extends AppCompatActivity{

    EditText room;
    private final SimpleArrayMap<Long, Payload> incomingFilePayload = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, Payload> completedFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> filePayloadWords = new SimpleArrayMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        final Button btn = findViewById(R.id.createBtn);
        room = findViewById(R.id.roomName);

        if (room.getText().toString().length() == 0) {
            btn.setEnabled(false);
        }

        room.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (room.getText().toString().length() == 0) {
                    btn.setEnabled(false);
                }
                else btn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertising();
            }
        });
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build();
        Nearby.getConnectionsClient(LobbyActivity.this).startAdvertising(room.getText().toString(),
                "@string/service_id", new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                        Nearby.getConnectionsClient(LobbyActivity.this).acceptConnection(s, new PayloadCallback() {
                            @Override
                            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                                System.out.println("Payload received");
                                if (payload.getType() == Payload.Type.BYTES) {
                                    String payloadWord = new String(payload.asBytes(), StandardCharsets.UTF_8);
                                    Long payloadId = addPayloadWord(payloadWord);
                                    processFilePayload(payloadId);
                                } else if (payload.getType() == Payload.Type.FILE) {
                                    incomingFilePayload.put(payload.getId(), payload);
                                }
                            }

                            private long addPayloadWord(String payloadWordMessage) {
                                String[] parts = payloadWordMessage.split(":");
                                long payloadId = Long.parseLong(parts[0]);
                                String filename = parts[1];
                                filePayloadWords.put(payloadId, filename);
                                return payloadId;
                            }


                            private void processFilePayload(long payloadId) {
                                // BYTES and FILE could be received in any order, so we call when either the BYTES or the FILE
                                // payload is completely received. The file payload is considered complete only when both have
                                // been received.
                                Payload filePayload = completedFilePayloads.get(payloadId);
                                String word = filePayloadWords.get(payloadId);

                                System.out.println("Payload processing");

                                if (filePayload != null && word != null) {
                                    completedFilePayloads.remove(payloadId);

                                    System.out.println("Payload has processed");

                                    // Get the received file (which will be in the Downloads folder)
                                    File payloadFile = filePayload.asFile().asJavaFile();

                                    DrawActivity.payloadIsReceived();
                                    DrawActivity.getImage(payloadFile, word);
                                    DrawActivity.startGuess();
                                }
                            }

                            @Override
                            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                                if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                                    long payloadId = payloadTransferUpdate.getPayloadId();
                                    Payload payload = incomingFilePayload.remove(payloadId);
                                    completedFilePayloads.put(payloadId, payload);
                                    System.out.println("Payload updated");
                                    processFilePayload(payloadId);

                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                        System.out.println("Connected");
                        final Intent draw = new Intent(LobbyActivity.this, DrawActivity.class);

                        draw.putExtra(DrawActivity.ENDPOINTID, s);

                        Nearby.getConnectionsClient(LobbyActivity.this).stopAdvertising();

                        startActivity(draw);
                    }

                    @Override
                    public void onDisconnected(@NonNull String s) {
                        System.out.println("Disconnected");
                        finish();
                    }
                },
                advertisingOptions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setContentView(R.layout.activity_wait);
                Toast.makeText(LobbyActivity.this, "Lobby created", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LobbyActivity.this, "Unable to create lobby", Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        });
    }
}
