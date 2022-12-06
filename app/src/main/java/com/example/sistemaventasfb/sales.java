package com.example.sistemaventasfb;

import static java.lang.Double.parseDouble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class sales extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idAutomatic;
    double mTotalcomision = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        EditText idsellers = findViewById(R.id.etidsellers);
        EditText idsale = findViewById(R.id.etidsale);
        EditText datesale = findViewById(R.id.etdatesale);
        EditText salevalue = findViewById(R.id.etsalevalue);
        ImageButton btnsavesale = findViewById(R.id.btnsavesale);
        ImageButton btnback = findViewById(R.id.btnback);
        // Recibir la identificación enviada desde la actividad MainActivity
        idsellers.setText(getIntent().getStringExtra("eidseller"));
        idAutomatic = getIntent().getStringExtra("eidautomatic");
        mTotalcomision = parseDouble(getIntent().getStringExtra("etotalcomision"));
        //Toast.makeText(getApplicationContext()," IdAutomatic: "+getIntent().getStringExtra("eidautomatic"),Toast.LENGTH_SHORT).show();
        //Eventos
        btnsavesale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verificar que todos los datos esten diligenciados
                String xidsale = idsale.getText().toString();
                String xdatesale = datesale.getText().toString();
                String xsalevalue = salevalue.getText().toString();
                if (!xidsale.isEmpty() && !xdatesale.isEmpty() && !xsalevalue.isEmpty()) {
                    db.collection("sales")
                            .whereEqualTo("idsale",idsale.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()){
                                    //Guardar la venta
                                    Map<String, Object> cSales = new HashMap<>();
                                    cSales.put("idsale", idsale.getText().toString());
                                    cSales.put("idseller", idsellers.getText().toString());
                                    cSales.put("datesale", datesale.getText().toString());
                                    cSales.put("salevalue", parseDouble(salevalue.getText().toString()));
                                    db.collection("sales")
                                            .add(cSales)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    // buscar el idseller para que retorne el total de la comision actual para acumular el total
                                                    //  de la comision con base en la comision de la venta
                                                    db.collection("seller").document(idAutomatic)
                                                            .update("totalcomision", mTotalcomision + (parseDouble(salevalue.getText().toString()) * 0.02))
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getApplicationContext(), "Venta guardada exitosamente...", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), "Error al guardar la venta....", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "Id de venta ya existe...", Toast.LENGTH_SHORT).show();
                                        }
                            }
                        }
                    });

                };
            }
        });
    }
}