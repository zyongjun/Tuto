package com.windhike.annotation.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.enrique.stackblur.StackBlurManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BlurManager {
    private final int MAX_SIZE;
    private Context _context;
    private HashMap<Integer, Blur> _hashMap;
    private Bitmap _mainBitmap;
    private StackBlurManager _stackBlur;

    public class Blur {
        public Date _addingTime;
        public Bitmap _bitmap;

        public Blur(Bitmap bitmap) {
            this._addingTime = Calendar.getInstance().getTime();
            this._bitmap = bitmap;
        }
    }

    public BlurManager(Context context) {
        this.MAX_SIZE = 3;
        this._context = context;
        this._hashMap = new HashMap();
    }

    public void setMainBitmap(Bitmap bitmap) {
        cleanData();
        this._mainBitmap = bitmap;
        this._stackBlur = new StackBlurManager(bitmap);
    }

    public Bitmap getMainBitmap() {
        return _mainBitmap;
    }

    public boolean hasBlurWithOpacity(int opacity) {
        return this._hashMap.containsKey(Integer.valueOf(opacity));
    }

    public Bitmap getBlurBitmapWithOpacity(int opacity) {
        if (hasBlurWithOpacity(opacity)) {
            return this._hashMap.get(Integer.valueOf(opacity))._bitmap;
        }
        addNewBitmap(opacity, this._stackBlur.processRenderScript(this._context, (float) opacity));
        return getBlurBitmapWithOpacity(opacity);
    }

    private void addNewBitmap(int opacity, Bitmap bitmap) {
        if (!this._hashMap.containsKey(Integer.valueOf(opacity))) {
            if (this._hashMap.size() > 3) {
                removeOldestBitmap();
            }
            this._hashMap.put(Integer.valueOf(opacity), new Blur(bitmap));
        }
    }

    private void removeOldestBitmap() {
        if (this._hashMap.size() != 0) {
            Integer[] listKey = (Integer[]) this._hashMap.keySet().toArray();
            Integer oldestKey = listKey[0];
            for (int i = 1; i < listKey.length; i++) {
                Integer tmpKey = listKey[i];
                if (this._hashMap.get(oldestKey)._addingTime.after(this._hashMap.get(tmpKey)._addingTime)) {
                    oldestKey = tmpKey;
                }
            }
            this._hashMap.remove(oldestKey);
        }
    }

    public void cleanData() {
        cleanBitmap(this._mainBitmap);
        this._hashMap.clear();
        System.gc();
    }

    private void cleanBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
        }
    }
}
