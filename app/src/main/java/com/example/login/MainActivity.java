package com.example.login;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button Iniciar, Regis;
    EditText user, contra;
    LottieDialog2 lottieDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.main);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        Regis = findViewById(R.id.btnRegister);
        Iniciar = findViewById(R.id.button);
        user = findViewById(R.id.etname);
        contra = findViewById(R.id.etpass);

        Iniciar.setOnClickListener(this);
        Regis.setOnClickListener(this);
        lottieDialog2 = new LottieDialog2(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRegister) {
            Intent intent = new Intent(this, RegistroSesion.class);
            startActivity(intent);
        } else if (v.getId() == R.id.button) {

            String usuario = user.getText().toString().trim();
            String contraseñaTxt = contra.getText().toString().trim();

            if (usuario.isEmpty() || contraseñaTxt.isEmpty()) {
                lottieDialog2.mostrarExito("¡Cuenta creada exitosamente!", () ->
                {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
                return;
            }

            String URL = "https://braylen-unpracticable-alise.ngrok-free.dev/loginApp/login.php";

            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        if (response.contains("exitoso")) {
                            lottieDialog2.mostrarExito("¡Bienvenido!", () -> {
                                Intent intent = new Intent(this, ClienteOrAdmin.class);
                                startActivity(intent);
                                finish();
                            });
                        }  else {
                            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("nombre", usuario);
                    params.put("password", contraseñaTxt);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }
}
