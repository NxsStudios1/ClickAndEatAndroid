package com.example.login;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegistroSesion extends AppCompatActivity implements View.OnClickListener {

    Button Crear, Regresar;
    EditText nombre, email, contraseña, fechaNac;
    LottieDialog lottieDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_sesion);

        ScrollView scrollView = findViewById(R.id.registro);

        // Fondo animado
        AnimationDrawable animationDrawable = (AnimationDrawable) scrollView.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        // Enlazar vistas
        Crear = findViewById(R.id.btnRegistrar);
        Regresar = findViewById(R.id.btnRegresar);

        nombre = findViewById(R.id.editNombre);
        email = findViewById(R.id.editEmail);
        contraseña = findViewById(R.id.editContraseña);
        fechaNac = findViewById(R.id.editFechaNac);

        Crear.setOnClickListener(this);
        Regresar.setOnClickListener(this);

        // Inicializar el diálogo de Lottie
        lottieDialog = new LottieDialog(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRegistrar) {
            String nombreTxt = nombre.getText().toString().trim();
            String emailTxt = email.getText().toString().trim();
            String contraTxt = contraseña.getText().toString().trim();
            String fechaTxt = fechaNac.getText().toString().trim();

            if (nombreTxt.isEmpty() || emailTxt.isEmpty() || contraTxt.isEmpty() || fechaTxt.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (contraTxt.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
            String URL = "https://braylen-unpracticable-alise.ngrok-free.dev/loginApp/registro.php";

            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    response ->
                    {
                        if (response.contains("exitoso"))
                        {
                            lottieDialog.mostrarExito("¡Cuenta creada exitosamente!", () ->
                            {
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        } else
                        {
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error ->
                    {
                            Toast.makeText(this, "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("nombre", nombreTxt);
                    params.put("email", emailTxt);
                    params.put("password", contraTxt);
                    params.put("fecha", fechaTxt);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } else if (v.getId() == R.id.btnRegresar) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}