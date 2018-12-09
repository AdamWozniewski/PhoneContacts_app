package com.adam.phonecontacts_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable drawable;
    public ItemDivider(Context context) {
        int[] attrs = {android.R.attr.listDivider};
        this.drawable = context.obtainStyledAttributes(attrs).getDrawable(0);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        // Wyliczenie wspolrzednych początka i konca linii
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        // Rysowanie linii pod każdym elementem po za ostatnim z nich
        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            // odczytanie i-tego elementu z listy
            View item = parent.getChildAt(i);

            // Obliczanie współrzędnych na osi Y
            int top = item.getBottom() + ((RecyclerView.LayoutParams) item.getLayoutParams()).bottomMargin;
            int bottom = top + this.drawable.getIntrinsicHeight();

            // Narysowanie zdefiniowanej linii
            this.drawable.setBounds(left, top, right,bottom);
            this.drawable.draw(c);
        }
    }
}
