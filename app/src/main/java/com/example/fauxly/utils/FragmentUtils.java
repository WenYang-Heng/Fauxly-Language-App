package com.example.fauxly.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import com.example.fauxly.R;

public class FragmentUtils {

    private static final int DEFAULT_MARGIN_BOTTOM_DP = 56; // Default margin in dp

    public static void setFragmentContainerMarginBottom(FragmentActivity activity, boolean removeMargin) {
        if (activity != null) {
            androidx.fragment.app.FragmentContainerView fragmentContainerView = activity.findViewById(R.id.fragment_container_view);
            if (fragmentContainerView != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentContainerView.getLayoutParams();
                int marginInPx = removeMargin ? 0 : dpToPx(activity, DEFAULT_MARGIN_BOTTOM_DP);
                params.bottomMargin = marginInPx;
                fragmentContainerView.setLayoutParams(params);
            }

            // Adjust BottomNavigationView visibility
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);
            if (bottomNav != null) {
                bottomNav.setVisibility(removeMargin ? View.GONE : View.VISIBLE);
            }
        }
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
