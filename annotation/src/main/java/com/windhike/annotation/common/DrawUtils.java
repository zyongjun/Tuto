package com.windhike.annotation.common;


import com.windhike.annotation.view.DotObject;

public class DrawUtils {
    public static int ALPHA_RESIZE = 10;

    public static boolean checkTouchPointInSideRectangleShape(float x, float y, float w, float h, float xTouchStart, float yTouchStart) {
        if (x > xTouchStart || xTouchStart > w || y > yTouchStart || yTouchStart > h) {
            return false;
        }
        return true;
    }

    public static boolean checkTouchPointInSideDotPoint(DotObject dot, float xTouchStart, float yTouchStart) {
        return !(dot.getmX_vitural_touch() > xTouchStart || xTouchStart > dot.getmX_vitural_touch() + dot.getmW() || dot.getmY_vitural_touch() > yTouchStart || yTouchStart > dot.getmY_vitural_touch() + dot.getmH());
    }

}
