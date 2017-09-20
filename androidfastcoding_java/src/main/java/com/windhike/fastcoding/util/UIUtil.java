package com.windhike.fastcoding.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.Image;
import android.os.Build.VERSION;
import android.renderscript.RenderScript;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.ByteBuffer;

public class UIUtil {
//    public static final RenderScript rs = RenderScript.create(TutoApplication.getInstance());

    public static class ColorUtil {
        public static int setColorAlpha(int i, float f) {
            return (16777215 & i) | (((int) (255.0f * f)) << 24);
        }

        public static int ratioOfRGB(int i, int i2, float f) {
            float max = Math.max(Math.min(f, 1.0f), 0.0f);
            int alpha = Color.alpha(i);
            alpha += (int) (((float) (Color.alpha(i2) - alpha)) * max);
            int red = Color.red(i);
            red += (int) (((float) (Color.red(i2) - red)) * max);
            int green = Color.green(i);
            green += (int) (((float) (Color.green(i2) - green)) * max);
            int blue = Color.blue(i);
            return Color.argb(alpha, red, green, ((int) (max * ((float) (Color.blue(i2) - blue)))) + blue);
        }

        public static String colorToARGBString(int i) {
            return String.format("#%08X", new Object[]{Integer.valueOf(i)});
        }

        public static String colorToRGBString(int i) {
            return String.format("#%06X", new Object[]{Integer.valueOf(16777215 & i)});
        }
    }

    public static class DeviceInfo {
        public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
        private static int mDeviceHeight = -1;
        private static int mDeviceWidth = -1;

        public enum DeviceScreenType {
            DEVICE_SCREEN_TYPE_NORMAL,
            DEVICE_SCREEN_TYPE_WIDE
        }

        public static int getDeviceScreenWidth() {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }

        public static int getDeviceScreenHeight() {
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        }

        public static int getDeviceWidth() {
            if (mDeviceWidth < 0) {
                int deviceScreenWidth = getDeviceScreenWidth();
                int deviceScreenHeight = getDeviceScreenHeight();
                if (deviceScreenWidth >= deviceScreenHeight) {
                    deviceScreenWidth = deviceScreenHeight;
                }
                mDeviceWidth = deviceScreenWidth;
            }
            return mDeviceWidth;
        }

        public static int getDeviceHeight() {
            if (mDeviceHeight < 0) {
                int deviceScreenWidth = getDeviceScreenWidth();
                int deviceScreenHeight = getDeviceScreenHeight();
                if (deviceScreenWidth >= deviceScreenHeight) {
                    deviceScreenHeight = deviceScreenWidth;
                }
                mDeviceHeight = deviceScreenHeight;
            }
            return mDeviceHeight;
        }

        public static DeviceScreenType getDeviceScreenType() {
            if (((float) getDeviceScreenWidth()) / DENSITY >= 360.0f) {
                return DeviceScreenType.DEVICE_SCREEN_TYPE_WIDE;
            }
            return DeviceScreenType.DEVICE_SCREEN_TYPE_NORMAL;
        }
    }

    public static class DrawableTools {
        private static ColorMatrix mSaturationMatrix;

        public static Bitmap createCircleBitmap(int i, int i2, int i3) {
            Bitmap createBitmap = Bitmap.createBitmap(i, i, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(i2);
            paint.setStyle(Style.FILL);
            int i4 = i / 2;
            canvas.drawCircle((float) i4, (float) i4, (float) i4, paint);
            if (i3 != 0) {
                paint.setColor(i3);
                paint.setStyle(Style.STROKE);
                int dpToPx = UIUtil.dpToPx(1);
                paint.setStrokeWidth((float) dpToPx);
                canvas.drawCircle((float) i4, (float) i4, (float) (i4 - ((int) (((double) (dpToPx / 2)) + 0.5d))), paint);
            }
            return createBitmap;
        }

        @Deprecated
        public static Bitmap createBitmapWithMaskColor(Bitmap bitmap, int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            int i7;
            int i8;
            if (mSaturationMatrix == null) {
                mSaturationMatrix = new ColorMatrix();
            } else {
                mSaturationMatrix.reset();
            }
            mSaturationMatrix.setSaturation(0.0f);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float f = ((float) i3) / ((float) i4);
            if (f > ((float) width) / ((float) height)) {
                i5 = (int) ((((float) i4) / ((float) i3)) * ((float) width));
                i6 = width;
            } else {
                i5 = height;
                i6 = (int) (f * ((float) height));
            }
            Bitmap createBitmap = Bitmap.createBitmap(i6, i5, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColorFilter(new ColorMatrixColorFilter(mSaturationMatrix));
            paint.setAlpha(i);
            if (i6 < width) {
                i7 = (width - i6) / 2;
                i8 = i7;
                i7 += i6;
            } else {
                i8 = 0;
                i7 = width;
            }
            if (i5 < height) {
                width = (height - i5) / 2;
                height = width + i5;
            } else {
                width = 0;
            }
            canvas.drawBitmap(bitmap, new Rect(i8, width, i7, height), new Rect(0, 0, i6, i5), paint);
            paint.setColorFilter(null);
            paint.setColor(i2);
            canvas.drawRect(0.0f, 0.0f, (float) i6, (float) i5, paint);
            return createBitmap;
        }

        public static BitmapDrawable createDrawableWithColor(Resources resources, int i) {
            return createDrawableWithSize(resources, UIUtil.dpToPx(4), UIUtil.dpToPx(4), 0, i);
        }

        public static BitmapDrawable createDrawableWithSize(Resources resources, int i, int i2, int i3, int i4) {
            Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            if (i4 == 0) {
                i4 = 0;
            }
            if (i3 > 0) {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Style.FILL);
                paint.setColor(i4);
                canvas.drawRoundRect(new RectF(0.0f, 0.0f, (float) i, (float) i2), (float) i3, (float) i3, paint);
            } else {
                canvas.drawColor(i4);
            }
            return new BitmapDrawable(resources, createBitmap);
        }

        public static BitmapDrawable createDrawableWithStroke(Resources resources, int i, int i2, int i3, int i4, int i5) {
            Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            if (i5 == 0) {
                i5 = 0;
            }
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Style.STROKE);
            paint.setColor(i5);
            paint.setStrokeWidth((float) i4);
            if (i3 > 0) {
                canvas.drawRoundRect(new RectF(0.0f, 0.0f, (float) i, (float) i2), (float) i3, (float) i3, paint);
            } else {
                canvas.drawRect(new RectF(0.0f, 0.0f, (float) i, (float) i2), paint);
            }
            return new BitmapDrawable(resources, createBitmap);
        }

        public static BitmapDrawable createMergedDrawable(Resources resources, Drawable drawable, Drawable drawable2, Point point) {
            Bitmap createBitmap = Bitmap.createBitmap(drawable.getBounds().width(), drawable.getBounds().height(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            canvas.translate((float) point.x, (float) point.y);
            drawable2.draw(canvas);
            return new BitmapDrawable(resources, createBitmap);
        }

    }

    public static int getNumberDigits(int i) {
        if (i <= 0) {
            return 0;
        }
        return (int) (Math.log10((double) i) + 1.0d);
    }

    public static Bitmap image2Bitmap(Image image){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            return bitmap;
        }
        return null;
    }

    public static int dpToPx(int i) {
        return (int) ((((float) i) * DeviceInfo.DENSITY) + 0.5f);
    }

    public static int pxToDp(float f) {
        return (int) ((f / DeviceInfo.DENSITY) + 0.5f);
    }

    @TargetApi(16)
    public static void setBackgroundKeepingPadding(View view, Drawable drawable) {
        int[] iArr = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        if (VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
        view.setPadding(iArr[0], iArr[1], iArr[2], iArr[3]);
    }

    public static void setBackgroundKeepingPadding(View view, int i) {
        setBackgroundKeepingPadding(view, view.getResources().getDrawable(i));
    }

    public static void setBackgroundColorKeepPadding(View view, int i) {
        int[] iArr = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackgroundColor(i);
        view.setPadding(iArr[0], iArr[1], iArr[2], iArr[3]);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter != null) {
            int count = adapter.getCount();
            int i = 0;
            for (int i2 = 0; i2 < count; i2++) {
                View view = adapter.getView(i2, null, listView);
                view.measure(0, 0);
                i += view.getMeasuredHeight();
            }
            LayoutParams layoutParams = listView.getLayoutParams();
            layoutParams.height = (listView.getDividerHeight() * (adapter.getCount() - 1)) + i;
            listView.setLayoutParams(layoutParams);
        }
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        ListAdapter adapter = gridView.getAdapter();
        if (adapter != null) {
            int count = adapter.getCount();
            int i = 0;
            for (int i2 = 0; i2 < count; i2++) {
                View view = adapter.getView(i2, null, gridView);
                view.measure(0, 0);
                i += view.getMeasuredHeight();
            }
            LayoutParams layoutParams = gridView.getLayoutParams();
            layoutParams.height = i / gridView.getNumColumns();
            gridView.setLayoutParams(layoutParams);
        }
    }

    public static Rect getRangeTextViewLocation(TextView textView, Point point, int i, int i2) {
        double lineRight;
        Rect rect;
        Rect rect2 = new Rect();
        Layout layout = textView.getLayout();
        double primaryHorizontal = (double) layout.getPrimaryHorizontal(i);
        double primaryHorizontal2 = (double) layout.getPrimaryHorizontal(i2);
        int lineForOffset = layout.getLineForOffset(i);
        int lineForOffset2 = layout.getLineForOffset(i2);
        Object obj = lineForOffset != lineForOffset2 ? 1 : null;
        layout.getLineBounds(lineForOffset, rect2);
        int[] iArr = new int[]{0, 0};
        textView.getLocationOnScreen(iArr);
        double scrollY = (double) ((iArr[1] - textView.getScrollY()) + textView.getCompoundPaddingTop());
        rect2.top = (int) (((double) rect2.top) + scrollY);
        rect2.bottom = (int) (((double) rect2.bottom) + scrollY);
        if (obj != null) {
            if ((rect2.top > point.y - rect2.bottom ? 1 : null) != null) {
                lineRight = (double) layout.getLineRight(lineForOffset);
                primaryHorizontal2 = primaryHorizontal;
                rect = rect2;
            } else {
                rect = new Rect();
                layout.getLineBounds(lineForOffset2, rect);
                rect.top = (int) (((double) rect.top) + scrollY);
                rect.bottom = (int) (((double) rect.bottom) + scrollY);
                double d = primaryHorizontal2;
                primaryHorizontal2 = (double) layout.getLineLeft(lineForOffset2);
                lineRight = d;
            }
        } else {
            lineRight = primaryHorizontal2;
            primaryHorizontal2 = primaryHorizontal;
            rect = rect2;
        }
        rect.left = (int) (((double) rect.left) + (((((double) iArr[0]) + primaryHorizontal2) + ((double) textView.getCompoundPaddingLeft())) - ((double) textView.getScrollX())));
        rect.right = (int) ((lineRight + ((double) rect.left)) - primaryHorizontal2);
        return rect;
    }

    public static void requestApplyInsets(Window window) {
        if (VERSION.SDK_INT >= 19 && VERSION.SDK_INT < 21) {
            window.getDecorView().requestFitSystemWindows();
        } else if (VERSION.SDK_INT >= 21) {
            window.getDecorView().requestApplyInsets();
        }
    }

    public static ColorStateList createColorStateList(int i, int i2, int i3, int i4) {
        int[] iArr = new int[]{i2, i3, i, i3, i4, i};
        int[][] iArr2 = new int[6][];
        iArr2[0] = new int[]{16842919, 16842910};
        iArr2[1] = new int[]{16842910, 16842908};
        iArr2[2] = new int[]{16842910};
        iArr2[3] = new int[]{16842908};
        iArr2[4] = new int[]{16842909};
        iArr2[5] = new int[0];
        return new ColorStateList(iArr2, iArr);
    }

    public static ColorStateList createColorStateList(int i, int i2, int i3) {
        return createColorStateList(i, i2, i2, i3);
    }

    public static ColorStateList createColorStateList(int i, int i2) {
        return createColorStateList(i, i2, i2, i);
    }

    public static StateListDrawable createStateListDrawable(Context context, int i, int i2, int i3, int i4) {
        Drawable drawable = null;
        StateListDrawable stateListDrawable = new StateListDrawable();
        Drawable drawable2 = i == -1 ? null : context.getResources().getDrawable(i);
        Drawable drawable3 = i2 == -1 ? null : context.getResources().getDrawable(i2);
        Drawable drawable4 = i3 == -1 ? null : context.getResources().getDrawable(i3);
        if (i4 != -1) {
            drawable = context.getResources().getDrawable(i4);
        }
        createStateListDrawable(drawable2, drawable3, drawable4, drawable);
        return stateListDrawable;
    }

    public static StateListDrawable createStateListDrawable(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919, 16842910}, drawable2);
        stateListDrawable.addState(new int[]{16842910, 16842908}, drawable3);
        stateListDrawable.addState(new int[]{16842910}, drawable);
        stateListDrawable.addState(new int[]{16842908}, drawable3);
        stateListDrawable.addState(new int[]{16842909}, drawable4);
        stateListDrawable.addState(new int[0], drawable);
        return stateListDrawable;
    }

    public static StateListDrawable createStateListDrawable(Drawable drawable, Drawable drawable2, Drawable drawable3) {
        return createStateListDrawable(drawable, drawable2, drawable2, drawable3);
    }
}