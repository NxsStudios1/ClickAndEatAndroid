package com.example.login.sesion;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.login.R;

public class LoginSuccessFragment extends Fragment {

    private static final long DELAY_MS = 4000L;

    private int rol = 0;

    public LoginSuccessFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            rol = args.getInt("rol", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_exitoso, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;

            if (rol == 1) {
                // ADMIN
                Navigation.findNavController(view)
                        .navigate(R.id.administradorMenu);
            } else if (rol == 2) {
                // CLIENTE
                Navigation.findNavController(view)
                        .navigate(R.id.clienteMenu);
            } else {
                Navigation.findNavController(view)
                        .navigate(R.id.loginfragment);
            }
        }, DELAY_MS);
    }
}
