package com.example.login;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class LottieDialog {

    private Dialog dialog;
    private Context context;
    private LottieAnimationView lottieAnimation;
    private TextView tvMessage;

    public LottieDialog(Context context) {
        this.context = context;
        inicializarDialog();
    }

    private void inicializarDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        lottieAnimation = dialog.findViewById(R.id.validado);
        tvMessage = dialog.findViewById(R.id.tvMessageCreandoCuenta);
    }



    // Mostrar animación de éxito
    public void mostrarExito(String mensaje, OnAnimacionCompletaListener listener)
    {
        tvMessage.setText(mensaje);
        lottieAnimation.setAnimation(R.raw.sucess);
        lottieAnimation.playAnimation();
        dialog.show();

        new Handler().postDelayed(() ->
        {
            dismiss();
            if (listener != null)
            {
                listener.onCompleta();
            }
        }, 2500);
    }


    // Cerrar el diálogo
    public void dismiss()
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    // Interface para callback
    public interface OnAnimacionCompletaListener
    {
        void onCompleta();
    }
}