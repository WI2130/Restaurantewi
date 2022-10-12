package com.example.restaurantebr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    FirebaseFirestore db = FirebaseFirestore.getInstance(); //Instancia De Firestore

    String idCustomer; //Variable Que Contendra El Id De Cada Cliente (customer)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciar Y Referenciar Los ID's Del Archivo xml
        EditText ident = findViewById(R.id.etident);
        EditText fullname = findViewById(R.id.etfullname);
        EditText email = findViewById(R.id.etemail);
        EditText password = findViewById(R.id.etpassword);
        Button btnsave = findViewById(R.id.save);
        Button btnsearch = findViewById(R.id.search);
        Button btnedit = findViewById(R.id.edit);
        Button btndelete = findViewById(R.id.delete);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomer(ident.getText().toString(),fullname.getText().toString(),email.getText().toString(),password.getText().toString());

            }
        });

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("customer")
                        .whereEqualTo("ident", ident.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {    //Si Encontró El Documento
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            idCustomer = document.getId();
                                            fullname.setText(document.getString("fullname"));
                                            email.setText(document.getString("email"));
                                            ident.setText(document.getString("ident"));
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext()," El ID' Cliente No Existe ",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }


                        });

            }
        });

    }

    private void saveCustomer(String sident, String sfullname, String semail, String spassword) {

        db.collection("customer")
                .whereEqualTo("ident", sident)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {    //No Encontró El Documento, Se Puede Guardar

                                //Guardar Los Datos Del Cliente (customer)
                                Map<String, Object> customer = new HashMap<>(); //Tabla Cursor //Tabla Que Creamos En Firestore
                                customer.put("ident", sident);
                                customer.put("fullname", sfullname);
                                customer.put("email", semail);
                                customer.put("password", spassword);

                                db.collection("customer")
                                        .add(customer)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                Toast.makeText(getApplicationContext(), "Cliente Agregado Correctamente....",Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Log.w(TAG, "Error adding document", e);
                                                Toast.makeText(getApplicationContext(), "Error! Cliente No Se Guardo....",Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext()," El ID Del Cliente Existente... ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                });


    }
}