package com.example.sistemaventasfb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Se genera un objeto para conectarse a la BD de Firebase-Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //referenciar los IDs del archivo activity_main.xml.
        EditText idseller = findViewById(R.id.etidseller);
        EditText fullname = findViewById(R.id.etfullname);
        EditText email = findViewById(R.id.etmail);
        EditText password = findViewById(R.id.etpassword);
        TextView totalcomision = findViewById(R.id.tvtotalcomision);
        ImageButton btnsave = findViewById(R.id.btnsave);
        ImageButton btnsearch = findViewById(R.id.btnsearch);
        ImageButton btnedit = findViewById(R.id.btnedit);
        ImageButton btndelete = findViewById(R.id.btndelete);
        ImageButton btnsales = findViewById(R.id.btnsales);
        ImageButton btnlist = findViewById(R.id.btnlist);
        // Eventos
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buscar vendedor por el idseller, y traer todos los datos
                db.collection("seller")
                        .whereEqualTo("idseller",idseller.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                   if (!task.getResult().isEmpty()){
                                       //la instantanea tiene informacion del documento
                                       for (QueryDocumentSnapshot document: task.getResult()){
                                           //Mostrar la informacion en cada uno de los objetos referenciados
                                           fullname.setText(document.getString("fullname"));
                                           email.setText(document.getString("email"));
                                           totalcomision.setText(String.valueOf(document.getDouble("totalcomision")));
                                       }
                                   }
                                   else {
                                       //Sino encuentra el usuario
                                       Toast.makeText(getApplicationContext(),"Id vendedor no existe...",Toast.LENGTH_SHORT).show();
                                   }
                                }
                            }
                        });
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validar que los datos esten diligenciados
                String mIdseller = idseller.getText().toString();
                String mFullname = fullname.getText().toString();
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();
                if (!mIdseller.isEmpty() && !mFullname.isEmpty() && !mEmail.isEmpty() && !mPassword.isEmpty()) {
                    //crear una tabla temporal con los mismos campos de la coleccion seller
                    Map<String, Object> mSeller = new HashMap<>();
                    mSeller.put("idseller",mIdseller);
                    mSeller.put("fullname",mFullname);
                    mSeller.put("email",mEmail);
                    mSeller.put("password",mPassword);
                    mSeller.put("totalcomision",0);
                    //agregar el documento a la coleccion seller a traves de la tabla temporal mSeller
                    db.collection("seller")
                            .add(mSeller)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getApplicationContext(),"Vendedor agreado con exito...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Error no agrego al vendedor...", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Debe ingresar todos los datos...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}