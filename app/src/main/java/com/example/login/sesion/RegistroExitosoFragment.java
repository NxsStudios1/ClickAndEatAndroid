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

public class RegistroExitosoFragment extends Fragment {

    private static final long DELAY_MS = 4000L; // 2 segundos

    public RegistroExitosoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registro_exitoso, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_registroExitosoFragment_to_loginfragment);
            }
        }, DELAY_MS);
    }
}
