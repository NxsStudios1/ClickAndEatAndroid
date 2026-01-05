package com.example.login.administrador.recursosa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.example.login.R;

public class ThemeUtils {

    public static final String PREFS_CONFIG = "config_visual";
    private static final String KEY_COLOR = "colorMenu";
    private static final String KEY_LOGO = "logoMenu";
    public static final String SPRING_GREEN = "spring_green";
    public static final String SPRING_PINK = "spring_pink";
    public static final String SPRING_YELLOW = "spring_yellow";

    public static final String SUMMER_ORANGE = "summer_orange";
    public static final String SUMMER_BLUE = "summer_blue";
    public static final String SUMMER_GREEN = "summer_green";

    public static final String AUTUMN_RED = "autumn_red";
    public static final String AUTUMN_ORANGE = "autumn_orange";
    public static final String AUTUMN_BROWN = "autumn_brown";

    public static final String WINTER_BLUE = "winter_blue";
    public static final String WINTER_GRAY = "winter_gray";
    public static final String WINTER_WHITE = "winter_white";

    public static final String HALLOWEEN_ORANGE = "halloween_orange";
    public static final String HALLOWEEN_GRAY = "halloween_gray";
    public static final String HALLOWEEN_PURPLE = "halloween_purple";

    public static final String CHRISTMAS_RED = "christmas_red";
    public static final String CHRISTMAS_GREEN = "christmas_green";
    public static final String CHRISTMAS_GOLD = "christmas_gold";

    public static final String COLOR_NARANJA = SUMMER_ORANGE;
    public static final String COLOR_VERDE = SPRING_GREEN;
    public static final String COLOR_ROJO = AUTUMN_RED;

    public static final String LOGO_CLASICO = "logo_clasico";
    public static final String LOGO_HALLOWEEN = "logo_halloween";
    public static final String LOGO_NAVIDAD = "logo_navidad";
    public static final String LOGO_SAN_VALENTIN = "logo_san_valentin";
    public static final String LOGO_VACACIONES = "logo_vacaciones";

    public static void guardarConfigVisual(Context ctx, String colorKey, String logoKey) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_CONFIG, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_COLOR, colorKey)
                .putString(KEY_LOGO, logoKey)
                .apply();
    }

    public static String obtenerColorMenu(Context ctx) {
        return ctx.getSharedPreferences(PREFS_CONFIG, Context.MODE_PRIVATE)
                .getString(KEY_COLOR, COLOR_NARANJA);
    }

    public static String obtenerLogoMenu(Context ctx) {
        return ctx.getSharedPreferences(PREFS_CONFIG, Context.MODE_PRIVATE)
                .getString(KEY_LOGO, LOGO_CLASICO);
    }

    public static int getColorInt(Context ctx, String colorKey) {
        switch (colorKey) {
            case SPRING_GREEN: return Color.parseColor("#7BC8A4");
            case SPRING_PINK: return Color.parseColor("#F8BBD0");
            case SPRING_YELLOW: return Color.parseColor("#FFF59D");

            case SUMMER_ORANGE: return Color.parseColor("#FFB74D");
            case SUMMER_BLUE: return Color.parseColor("#4FC3F7");
            case SUMMER_GREEN: return Color.parseColor("#81C784");

            case AUTUMN_RED: return Color.parseColor("#E57373");
            case AUTUMN_ORANGE: return Color.parseColor("#FF8A65");
            case AUTUMN_BROWN: return Color.parseColor("#8D6E63");

            case WINTER_BLUE: return Color.parseColor("#90CAF9");
            case WINTER_GRAY: return Color.parseColor("#CFD8DC");
            case WINTER_WHITE: return Color.parseColor("#FFFFFF");

            case HALLOWEEN_ORANGE: return Color.parseColor("#FF9800");
            case HALLOWEEN_GRAY: return Color.parseColor("#616161");
            case HALLOWEEN_PURPLE: return Color.parseColor("#6A1B9A");

            case CHRISTMAS_RED: return Color.parseColor("#E53935");
            case CHRISTMAS_GREEN: return Color.parseColor("#43A047");
            case CHRISTMAS_GOLD: return Color.parseColor("#FFD700");

            default:
                return Color.parseColor("#FFB74D");
        }
    }

    public static int getLogoResId(String logoKey) {
        switch (logoKey) {
            case LOGO_HALLOWEEN:
                return R.drawable.logo_andy_burger_halloween;
            case LOGO_NAVIDAD:
                return R.drawable.logo_andy_burger_navidad;
            case LOGO_SAN_VALENTIN:
                return R.drawable.logo_andy_burger_sanvalentin;
            case LOGO_VACACIONES:
                return R.drawable.logo_andy_burger_vacaciones;
            case LOGO_CLASICO:
            default:
                return R.drawable.logo_andy_burger;
        }
    }

    public static void aplicarConfigBarra(Activity activity) {
        if (activity == null) return;

        View barra = activity.findViewById(R.id.topBar);
        ImageView ivLogo = activity.findViewById(R.id.imgLogo);

        if (barra == null || ivLogo == null) return;

        barra.setBackgroundColor(
                getColorInt(activity, obtenerColorMenu(activity))
        );
        ivLogo.setImageResource(
                getLogoResId(obtenerLogoMenu(activity))
        );
    }
}
