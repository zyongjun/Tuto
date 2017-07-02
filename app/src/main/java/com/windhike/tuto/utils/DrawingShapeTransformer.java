package com.windhike.tuto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Base64;

import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.FlaotPoint;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.annotation.reuse.DisplayUtil;
import com.windhike.annotation.reuse.FileUtils;
import com.windhike.annotation.view.ArrowObject;
import com.windhike.annotation.view.BlurObject;
import com.windhike.annotation.view.CustomerShapeView;
import com.windhike.annotation.view.EllipseObject;
import com.windhike.annotation.view.EraserObject;
import com.windhike.annotation.view.FreeStyleObject;
import com.windhike.annotation.view.RecordObject;
import com.windhike.annotation.view.RectangleObject;
import com.windhike.annotation.view.TextObject;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;

/**
 * author: gzzyj on 2017/3/13.
 * email:zhyongjun@windhike.cn
 */

public class DrawingShapeTransformer {
    public static final int ADD_ANNOTATION = 1;
    public static final int UPDATE_ANNOTATION = 2;
    public static final int DELETE_ANNOTATION = 3;
    @IntDef({ADD_ANNOTATION, UPDATE_ANNOTATION, DELETE_ANNOTATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnnotaionOperateType{}

    public static JSONObject transToSyncTime() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("add",new JSONObject());
            jsonObject.put("update",new JSONArray());
            jsonObject.put("delete",new JSONArray());
            jsonObject.put("synctime","0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject transToSyncJson(ImageDrawObject imageObject, @AnnotaionOperateType int type) {
        JSONObject jsonObject = new JSONObject();
        try {
            if(type == ADD_ANNOTATION){
                imageObject.setRemarkId("");
                jsonObject.put("add",transShapeToJson(Configs.ANNOTATION_IBOS,imageObject));
                jsonObject.put("update",new JSONArray());
                jsonObject.put("delete",new JSONArray());
            } else if (type == UPDATE_ANNOTATION) {
                jsonObject.put("add",new JSONArray());
                jsonObject.put("delete",new JSONArray());
                jsonObject.put("update",transShapeToJson("",imageObject));
            } else if (type == DELETE_ANNOTATION) {
                jsonObject.put("add",new JSONArray());
                jsonObject.put("update",new JSONArray());
                JSONArray sysncChangedArray = new JSONArray();
                JSONObject annotationJson = new JSONObject();
                annotationJson.put("remarkid", imageObject.getRemarkId());
                annotationJson.put("updatetime",new Date().getTime()/1000f);
                sysncChangedArray.put(annotationJson);
                jsonObject.put("delete",sysncChangedArray);
            }

            long synctime = PreferenceConnector.readLong(TutoApplication.getInstance(), Configs.FLAG_SYSNC_UPLOAD_ANNOTATION_TIME, 0);
            jsonObject.put("synctime", String.valueOf(synctime));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray transShapeToJson(String tmpId, ImageDrawObject managerImageObject) {
        JSONArray sysncChangedArray = new JSONArray();
        try {
            JSONObject annotationJson = new JSONObject();
            if(TextUtils.isEmpty(managerImageObject.getRemarkId())) {
                annotationJson.put("time",new Date().getTime()/1000f);
                annotationJson.put("id",tmpId);
                annotationJson.put("isdel",0);
                annotationJson.put("aid",tmpId);
            }else{
                annotationJson.put("remarkid", managerImageObject.getRemarkId());
                annotationJson.put("updatetime",new Date().getTime()/1000f);
            }
            //contentJson
            JSONObject contentJson = new JSONObject();
            contentJson.put("annotationId",managerImageObject.getRemarkId());
            String editPath = managerImageObject.getEditImagePath();
            Bitmap bmp= BitmapFactory.decodeFile(editPath);
            contentJson.put("annotationThumbnailData", BitmapAndStringUtils.convertIconToString(ThumbnailUtils.extractThumbnail(bmp,200,200)));

            contentJson.put("density",TutoApplication.getInstance().getResources().getDisplayMetrics().density);
            contentJson.put("time","");
            contentJson.put("drawImageData",BitmapAndStringUtils.convertIconToString(bmp));
            bmp.recycle();
            String mImageDisplayPath = managerImageObject.getOriginalImagePath();
            float xMargin = 0;
            float yMargin = 0;
            if (new File(mImageDisplayPath).exists()) {
                Bitmap currentLoadingBitmap = Utilities.scaleToActualAspectRatio(BitmapFactory.decodeFile(mImageDisplayPath),
                        TutoApplication.getInstance().getDisplayMetrics().widthPixels,
                        TutoApplication.getInstance().getDisplayMetrics().heightPixels - DisplayUtil.dip2px(TutoApplication.getInstance(), 110));
                contentJson.put("imageData", BitmapAndStringUtils.convertIconToString(currentLoadingBitmap));
                contentJson.put("contentHeight",currentLoadingBitmap.getHeight());
                contentJson.put("contentWidth",currentLoadingBitmap.getWidth());
                int canvasWidth = TutoApplication.getInstance().getDisplayMetrics().widthPixels;
                int canvasHeight = TutoApplication.getInstance().getDisplayMetrics().heightPixels-DisplayUtil.dip2px(TutoApplication.getInstance(),110);
                boolean isWidthEnough = ((float) canvasHeight)/currentLoadingBitmap.getHeight()>=((float)canvasWidth)/currentLoadingBitmap.getWidth();
                boolean isHeightEnough = !isWidthEnough;
//                if (!isWidthEnough) {
                    xMargin = (canvasWidth-currentLoadingBitmap.getWidth())/2f;
//                }
                if (!isHeightEnough) {
                    yMargin = (canvasHeight-currentLoadingBitmap.getHeight())/2f;
                }
            }

            contentJson.put("url",managerImageObject.getUrl());
            //remarks
            JSONArray remarksJson = new JSONArray();
            //voices
            JSONArray voicesJson = new JSONArray();
            //draws json
            JSONArray drawsJson = new JSONArray();
            //annotation draws json
            ArrayList<CustomerShapeView> shapeList = managerImageObject.getListRootShape();
            for (CustomerShapeView customerShapeView : shapeList) {
                if(customerShapeView.isVisible) {
                    if (customerShapeView.mType == 6) {//textObject
                        remarksJson.put(transTextViewJson(customerShapeView,xMargin,yMargin));
                    } else if (customerShapeView.mType == 8) {
                        voicesJson.put(transVoicesViewJson(customerShapeView,xMargin,yMargin));
                    } else {
                        drawsJson.put(transShapeViewJson(customerShapeView,xMargin,yMargin));
                    }
                }
            }
            contentJson.put("remarks",remarksJson);
            contentJson.put("voices",voicesJson);
            contentJson.put("draws",drawsJson);
            annotationJson.put("content",contentJson);
            sysncChangedArray.put(annotationJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sysncChangedArray;
    }

    public static JSONObject transVoicesViewJson(CustomerShapeView shapeView, float xMargin, float yMargin) throws JSONException {
        RecordObject recordObject = (RecordObject) shapeView;
        Uri uri = recordObject.getAudioPath();
        JSONObject voiceJson = new JSONObject();
        voiceJson.put("pointX",getFixedAnnotationPx(recordObject.x,xMargin));
        voiceJson.put("pointY",getFixedAnnotationPx(recordObject.y,yMargin));
        voiceJson.put("voiceData",new VoiceMessageHandler().encodeMessage(uri));
        return voiceJson;
    }

    public static JSONObject transShapeViewJson(CustomerShapeView shapeView, float xMargin, float yMargin) throws JSONException {
        JSONObject annoDrawsJson = new JSONObject();
        JSONArray pointListArray = new JSONArray();
        switch (shapeView.mType) {
            case 1://rectangle
                annoDrawsJson.put("shapeType",4);
                RectangleObject rectangle = (RectangleObject) shapeView;
                JSONObject pointObjectLeft = new JSONObject();
                pointObjectLeft.put("x",getFixedAnnotationPx(rectangle.x,xMargin));
                pointObjectLeft.put("y",getFixedAnnotationPx(rectangle.y,yMargin));
                JSONObject pointObjectRight = new JSONObject();
                pointObjectRight.put("x",getFixedAnnotationPx(rectangle.w,xMargin));
                pointObjectRight.put("y",getFixedAnnotationPx(rectangle.h,yMargin));
                pointListArray.put(pointObjectLeft);
                pointListArray.put(pointObjectRight);
                break;
            case 2://circle
                annoDrawsJson.put("shapeType",3);
                EllipseObject ellipse = (EllipseObject)shapeView;
                JSONObject pointLeft = new JSONObject();
                pointLeft.put("x",getFixedAnnotationPx(ellipse.x,xMargin));
                pointLeft.put("y",getFixedAnnotationPx(ellipse.y,yMargin));
                JSONObject pointRight = new JSONObject();
                pointRight.put("x",getFixedAnnotationPx(ellipse.w,xMargin));
                pointRight.put("y",getFixedAnnotationPx(ellipse.h,yMargin));
                pointListArray.put(pointLeft);
                pointListArray.put(pointRight);
                break;
            case 3://arrow
                annoDrawsJson.put("shapeType",5);
                ArrowObject arrow = (ArrowObject)shapeView;
                JSONObject pointTail = new JSONObject();
                pointTail.put("x",getFixedAnnotationPx(arrow.getTailX(),xMargin));
                pointTail.put("y",getFixedAnnotationPx(arrow.getTailY(),yMargin));
                JSONObject pointTip = new JSONObject();
                pointTip.put("x",getFixedAnnotationPx(arrow.getTipX(),xMargin));
                pointTip.put("y",getFixedAnnotationPx(arrow.getTipY(),yMargin));
                pointListArray.put(pointTail);
                pointListArray.put(pointTip);
                break;
            case 4://blur
                annoDrawsJson.put("shapeType",7);
                BlurObject blur = (BlurObject)shapeView;
                Path path = new Path();
                path.moveTo(blur.x,blur.y);
                path.lineTo(blur.w,blur.y);
                float cx = blur.x;
                for(float i = blur.y+2;i<blur.h;i+=2) {
                    path.lineTo(cx,i);
                    if(cx==blur.x) {
                        cx = blur.w;
                    }else{
                        cx=blur.x;
                    }
                    path.lineTo(cx,i);
                }
                PathMeasure pm = new PathMeasure(path, false);
                float length = pm.getLength();
                FlaotPoint[] pointArray = new FlaotPoint[((int) length)];
                float speed = length / ((float) ((int) length));
                int counter = 0;
                float[] aCoordinates = new float[]{0.0f, 0.0f};
                for (float distance = 0.0f; distance < length && counter < ((int) length); distance += speed) {
                    pm.getPosTan(distance, aCoordinates, null);
                    pointArray[counter] = new FlaotPoint(aCoordinates[0], aCoordinates[1]);
                    counter++;
                }
                for (FlaotPoint point : pointArray) {
                    JSONObject pointObject = new JSONObject();
                    pointObject.put("x",getFixedAnnotationPx(point.getX(),xMargin));
                    pointObject.put("y",getFixedAnnotationPx(point.getY(),yMargin));
                    pointListArray.put(pointObject);
                }
                break;
            case 5://free
                annoDrawsJson.put("shapeType",1);
                FreeStyleObject freeStyle = (FreeStyleObject)shapeView;
                FlaotPoint[] points = freeStyle.getListPoints();
                for (FlaotPoint point : points) {
                    JSONObject pointObject = new JSONObject();
                    pointObject.put("x",getFixedAnnotationPx(point.getX(),xMargin));
                    pointObject.put("y",getFixedAnnotationPx(point.getY(),yMargin));
                    pointListArray.put(pointObject);
                }
                break;
            case 7://eraser
                annoDrawsJson.put("shapeType",6);
                EraserObject eraserObject = (EraserObject)shapeView;
                FlaotPoint[] epoints = eraserObject.getListPoints();
                for (FlaotPoint point : epoints) {
                    JSONObject pointObject = new JSONObject();
                    pointObject.put("x",getFixedAnnotationPx(point.getX(),xMargin));
                    pointObject.put("y",getFixedAnnotationPx(point.getY(),yMargin));
                    pointListArray.put(pointObject);
                }
                break;
            case 6://text

                break;
            case 8://voice
        }

        annoDrawsJson.put("pointList",pointListArray);
        annoDrawsJson.put("hight",TutoApplication.getInstance().getDisplayMetrics().heightPixels-DisplayUtil.dip2px(TutoApplication.getInstance(),110));
        annoDrawsJson.put("width",TutoApplication.getInstance().getDisplayMetrics().widthPixels);
        annoDrawsJson.put("paintColor", Integer.toHexString(shapeView.mPaint.getColor()));
        annoDrawsJson.put("paintSize",4);
        return annoDrawsJson;
    }

//    KBGDrawingShapeNone = 0,//关闭
//    KBGDrawingShapeCurve,//曲线
//    KBGDrawingShapeLine,//直线
//    KBGDrawingShapeEllipse,//椭圆
//    KBGDrawingShapeRect,//矩形
//    KBGDrawingShapeArrow,//箭头
//    KBGDrawingShapeEraser,//橡皮擦
//    KBGDrawingShapeMosaic//马赛克

//    hight=595
//    paintColor=F65252
//    paintSize=4
//    shapeType=1
//    width=335
//    pointList[{x,y}]

    public static JSONObject transTextViewJson(CustomerShapeView shapeView, float xMargin, float yMargin) throws JSONException {
        JSONObject textjson = new JSONObject();
        TextObject textObject = (TextObject)shapeView;
        textjson.put("pointX",getFixedAnnotationPx(textObject.x,xMargin));
        textjson.put("pointY",getFixedAnnotationPx(textObject.y,yMargin));
        textjson.put("text",textObject.getText());
        return textjson;
    }

    public static CustomerShapeView transJsonToTextView(JSONObject json, int contentWidth, int contentHeight, float density) {
        int canvasWidth = TutoApplication.getInstance().getDisplayMetrics().widthPixels;
        int canvasHeight = TutoApplication.getInstance().getDisplayMetrics().heightPixels-DisplayUtil.dip2px(TutoApplication.getInstance(),110);
        float scale = Math.min(((float) canvasHeight)/contentHeight,((float)canvasWidth)/contentWidth);
        boolean isWidthEnough = ((float) canvasHeight)/contentHeight>=((float)canvasWidth)/contentWidth;
        boolean isHeightEnough = !isWidthEnough;


        float xMargin = 0;
        float yMargin = 0;
        if (!isWidthEnough) {
            xMargin = (canvasWidth-contentWidth*scale)/2f;
        }

        if (!isHeightEnough) {
            yMargin = (canvasHeight-contentHeight*scale)/2f;
        }
        String text = json.optString("text");
        float x = getFixedPx(json.optLong("pointX"),scale,xMargin);
        float y = getFixedPx(json.optLong("pointY"),scale,yMargin);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(ResourcesCompat.getColor(TutoApplication.getInstance().getResources(), R.color.white,null));
        mPaint.setTextSize(TutoApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.s_18));
        mPaint.setStyle(Paint.Style.FILL);
        CustomerShapeView shapeView = new TextObject(TutoApplication.getInstance(), 6, text, x, y,0,0, mPaint, false, new FlaotPoint(0,0), contentWidth,contentHeight);
        shapeView.scaleAndChangePositionForTextObject(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
        shapeView._action = Configs.ShapeAction.AddNewShape;
        shapeView.isFocus = false;
        shapeView.isVisible = true;
        return shapeView;
    }

    public static RecordObject transJsonToVoiceView(JSONObject json, int contentWidth, int contentHeight, float density) {
        int canvasWidth = TutoApplication.getInstance().getDisplayMetrics().widthPixels;
        int canvasHeight = TutoApplication.getInstance().getDisplayMetrics().heightPixels-DisplayUtil.dip2px(TutoApplication.getInstance(),110);
        float scale = Math.min(((float) canvasHeight)/contentHeight,((float)canvasWidth)/contentWidth);
        boolean isWidthEnough = ((float) canvasHeight)/contentHeight>=((float)canvasWidth)/contentWidth;
        boolean isHeightEnough = !isWidthEnough;


        float xMargin = 0;
        float yMargin = 0;
        if (!isWidthEnough) {
            xMargin = (canvasWidth-contentWidth*scale)/2f;
        }

        if (!isHeightEnough) {
            yMargin = (canvasHeight-contentHeight*scale)/2f;
        }

        float x = getFixedPx(json.optLong("pointX"),scale,xMargin);
        float y = getFixedPx(json.optLong("pointY"),scale,yMargin);
        Bitmap mBitmapVoicePrepare = BitmapFactory.decodeResource(TutoApplication.getInstance().getResources(), R.mipmap.ic_anno_voice_prepare);
        int width = mBitmapVoicePrepare.getWidth();
        int height = mBitmapVoicePrepare.getHeight();
        Uri audioPath = new VoiceMessageHandler().decodeMessage(json.optString("voiceData"));
//        audiopath:/data/data/cn.ibos/cache/1490551903893temp.voice
        RecordObject recordObj = new RecordObject(TutoApplication.getInstance(), 8, x, y, x + ((float) width), y + ((float) height), new Paint(), false, new FlaotPoint(0,0), contentWidth,contentHeight,audioPath);
        recordObj.scaleAndChangePosition(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
        recordObj._action = Configs.ShapeAction.AddNewShape;
        recordObj.isFocus = false;
        recordObj.isVisible = true;
        return recordObj;
    }

    public static float getFixedPx(long targetValue, float scale, float xMargin) {
        return targetValue*scale+xMargin;
    }

    public static float getFixedAnnotationPx(float targetValue,float margin) {
        return targetValue-margin;
    }


    public static CustomerShapeView transJsonToShapeView(JSONObject json, final int contentWidth, final int contentHeight, float density) {
        try {
            int shapeType = json.getInt("shapeType");
            if (shapeType == 0) {
                return null;
            }
            int canvasWidth = TutoApplication.getInstance().getDisplayMetrics().widthPixels;
            int canvasHeight = TutoApplication.getInstance().getDisplayMetrics().heightPixels- DisplayUtil.dip2px(TutoApplication.getInstance(),110);
            float scale = Math.min(((float) canvasHeight)/contentHeight,((float)canvasWidth)/contentWidth);
            boolean isWidthEnough = ((float) canvasHeight)/contentHeight>=((float)canvasWidth)/contentWidth;
            boolean isHeightEnough = !isWidthEnough;


            float xMargin = 0;
            float yMargin = 0;
            if (!isWidthEnough) {
                xMargin = (canvasWidth-contentWidth*scale)/2f;
            }

            if (!isHeightEnough) {
                yMargin = (canvasHeight-contentHeight*scale)/2f;
            }

//            int width = json.getInt("width");
//            int height = json.getInt("hight");
            String paintColor = json.optString("paintColor","ffff0101");
            CustomerShapeView shapeView = null;
            switch (shapeType) {
                case 1://
                    RectF rectF = new RectF();
                    Path path = new Path();
                    JSONArray pointListArray = json.optJSONArray("pointList");
                    if(pointListArray!=null&&pointListArray.length()>0) {
                        int size = pointListArray.length();
                        for(int i=0;i<size;i++) {
                            JSONObject point = pointListArray.getJSONObject(i);
                            if (i == 0) {
                                path.moveTo(getFixedPx(point.getLong("x"),scale,xMargin),getFixedPx(point.getLong("y"),scale,yMargin));
                            }else{
                                path.lineTo(getFixedPx(point.getLong("x"),scale,xMargin),getFixedPx(point.getLong("y"),scale,yMargin));
                            }
                        }
                        path.computeBounds(rectF, true);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setColor(Color.parseColor(String.format("#%s",paintColor)));
                        paint.setStrokeWidth(10);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        shapeView = new FreeStyleObject(TutoApplication.getInstance(), 5, rectF.left, rectF.top, rectF.right, rectF.bottom,
                                path, paint, new FlaotPoint(0,0),
                                canvasWidth,canvasHeight);


                        shapeView.scaleAndChangePosition(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
                        shapeView._action = Configs.ShapeAction.AddNewShape;
                        shapeView.isFocus = false;
                        shapeView.isVisible = true;
                    }
                    break;
                case 3:
                    JSONArray pointListArray3 = json.optJSONArray("pointList");
                    if(pointListArray3!=null&&pointListArray3.length()>0) {
                        FlaotPoint point1 = new FlaotPoint();
                        FlaotPoint point2 = new FlaotPoint();
                        int size3=pointListArray3.length();
                        for (int i = 0; i < size3; i++) {
                            JSONObject point = pointListArray3.getJSONObject(i);
                            if (i == 0) {
                                point1.setX(getFixedPx(point.getLong("x"),scale,xMargin));
                                point1.setY(getFixedPx(point.getLong("y"),scale,yMargin));
                            } else if (i == 1) {
                                point2.setX(getFixedPx(point.getLong("x"),scale,xMargin));
                                point2.setY(getFixedPx(point.getLong("y"),scale,yMargin));
                            }
                        }
                        RectF r3 = null;
                        if (point1.getX() < point2.getX()) {
                            r3 = new RectF(point1.getX(), point1.getY(), point2.getX(), point2.getY());
                        } else {
                            r3 = new RectF(point2.getX(), point2.getY(), point1.getX(), point1.getY());
                        }
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setColor(Color.parseColor(String.format("#%s",paintColor)));
                        paint.setStrokeWidth(10);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        shapeView = new EllipseObject(TutoApplication.getInstance(), 2, r3.left, r3.top, r3.right, r3.bottom, paint, new FlaotPoint(0,0),
                                canvasWidth,canvasHeight);
                        shapeView.scaleAndChangePosition(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
                        shapeView._action = Configs.ShapeAction.AddNewShape;
                        shapeView.isFocus = false;
                        shapeView.isVisible = true;
                    }
                    break;
                case 4:
                    JSONArray pointListArray4 = json.optJSONArray("pointList");
                    if(pointListArray4!=null&&pointListArray4.length()>0) {
                        FlaotPoint point1 = new FlaotPoint();
                        FlaotPoint point2 = new FlaotPoint();
                        int size4= pointListArray4.length();
                        for (int i = 0; i < size4; i++) {
                            JSONObject point = pointListArray4.getJSONObject(i);
                            if (i == 0) {
                                point1.setX(getFixedPx(point.getLong("x"),scale,xMargin));
                                point1.setY(getFixedPx(point.getLong("y"),scale,yMargin));
                            } else if(i==1){
                                point2.setX(getFixedPx(point.getLong("x"),scale,xMargin));
                                point2.setY(getFixedPx(point.getLong("y"),scale,yMargin));
                            }
                        }
                        RectF r4 = null;
                        if(point1.getX()<point2.getX()) {
                            r4 = new RectF(point1.getX(),point1.getY(),point2.getX(),point2.getY());
                        }else{
                            r4 = new RectF(point2.getX(),point2.getY(),point1.getX(),point1.getY());
                        }
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setColor(Color.parseColor(String.format("#%s",paintColor)));
                        paint.setStrokeWidth(10);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        shapeView = new RectangleObject(TutoApplication.getInstance(), 1, r4.left, r4.top, r4.right, r4.bottom, paint, new FlaotPoint(0,0), (float) canvasWidth, (float) canvasHeight);
                        shapeView.scaleAndChangePosition(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
                        shapeView._action = Configs.ShapeAction.AddNewShape;
                        shapeView.isFocus = false;
                        shapeView.isVisible = true;
                    }
                    break;
                case 5:
                    JSONArray pointListArray5 = json.optJSONArray("pointList");
                    if(pointListArray5!=null&&pointListArray5.length()>0) {
                        FlaotPoint point1 = new FlaotPoint();
                        FlaotPoint point2 = new FlaotPoint();
                        JSONObject pointJsonStart = pointListArray5.getJSONObject(0);
                        point1.setX(getFixedPx(pointJsonStart.getLong("x"),scale,xMargin));
                        point1.setY(getFixedPx(pointJsonStart.getLong("y"),scale,yMargin));
                        JSONObject pointJsonEnd = pointListArray5.getJSONObject(pointListArray5.length()-1);
                        point2.setX(getFixedPx(pointJsonEnd.getLong("x"),scale,xMargin));
                        point2.setY(getFixedPx(pointJsonEnd.getLong("y"),scale,yMargin));
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setColor(Color.parseColor(String.format("#%s",paintColor)));
                        paint.setStrokeWidth(10);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        shapeView = new ArrowObject(TutoApplication.getInstance(), 3, point1.getX(), point1.getY(), point2.getX(), point2.getY(), paint, new FlaotPoint(0,0),
                                canvasWidth,canvasHeight);
                        shapeView.scaleAndChangePosition(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
                        shapeView._action = Configs.ShapeAction.AddNewShape;
                        shapeView.isFocus = false;
                        shapeView.isVisible = true;
                    }
                    break;
                case 6:
                    //erraser
                    RectF erectF = new RectF();
                    Path epath = new Path();
                    JSONArray epointListArray = json.optJSONArray("pointList");
                    if(epointListArray!=null&&epointListArray.length()>0) {
                        int esize = epointListArray.length();
                        for(int i=0;i<esize;i++) {
                            JSONObject point = epointListArray.getJSONObject(i);
                            if (i == 0) {
                                epath.moveTo(getFixedPx(point.getLong("x"),scale,xMargin),getFixedPx(point.getLong("y"),scale,yMargin));
                            }else{
                                epath.lineTo(getFixedPx(point.getLong("x"),scale,xMargin),getFixedPx(point.getLong("y"),scale,yMargin));
                            }
                        }
                       epath.computeBounds(erectF, true);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setStrokeWidth(10);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        shapeView = new EraserObject(TutoApplication.getInstance(), 7, erectF.left, erectF.top, erectF.right,erectF.bottom,
                                epath, paint, new FlaotPoint(0,0),
                                canvasWidth,canvasHeight);
                        shapeView.scaleAndChangePosition(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
                        shapeView._action = Configs.ShapeAction.AddNewShape;
                        shapeView.isFocus = false;
                        shapeView.isVisible = true;
                    }
                    break;
                case 7:
                    JSONArray pointListArray7 = json.optJSONArray("pointList");
                    if(pointListArray7!=null&&pointListArray7.length()>0) {
                        RectF r7 = new RectF();
                        Path p7 = new Path();
                        int size7 = pointListArray7.length();
                        for (int i = 0; i < size7; i++) {
                            JSONObject point = pointListArray7.getJSONObject(i);
                            if (i == 0) {
                                p7.moveTo(getFixedPx(point.getLong("x"),scale,xMargin), getFixedPx(point.getLong("y"),scale,yMargin));
                            } else {
                                p7.lineTo(getFixedPx(point.getLong("x"),scale,xMargin), getFixedPx(point.getLong("y"),scale,yMargin));
                            }
                        }
                        p7.computeBounds(r7, true);
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setColor(TutoApplication.getInstance().getResources().getColor(R.color.red));
                        paint.setStrokeWidth(10);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        p7.computeBounds(r7, true);
                        shapeView = new BlurObject(TutoApplication.getInstance(), null, 4,0,0, r7.left, r7.top, r7.right, r7.bottom, paint, new FlaotPoint(0,0),
                                canvasWidth,canvasHeight);
                        shapeView.scaleAndChangePosition(new FlaotPoint(0,0),new FlaotPoint(0,0),1);
                        shapeView._action = Configs.ShapeAction.AddNewShape;
                        shapeView.isFocus = false;
                        shapeView.isVisible = true;
                    }
                    break;
            }
            return shapeView;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class VoiceMessageHandler {

         public VoiceMessageHandler() {
        }

        public Uri decodeMessage(String data) {
            Uri uri = obtainVoiceUri(TutoApplication.getInstance());
            String name = new Date().getTime() + ".amr";
            File file = new File(uri.toString() + name);
            if(!file.exists()) {
                try {
                    byte[] e = Base64.decode(data, 2);
                    file = saveFile(e, uri.toString() + "/voice/", name);
                } catch (IllegalArgumentException | IOException var7) {
                    var7.printStackTrace();
                }
            }
            return Uri.fromFile(file);
        }

        public String encodeMessage(Uri uri) {
            byte[] voiceData = FileUtils.getByteFromUri(uri);
            try {
                return Base64.encodeToString(voiceData, 2);
            } catch (IllegalArgumentException var8) {
                var8.printStackTrace();
            }
            return null;
        }

        private File saveFile(byte[] data, String path, String fileName) throws IOException {
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }

            file = new File(path + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
            bos.flush();
            bos.close();
            return file;
        }

        private Uri obtainVoiceUri(Context context) {
            File file = context.getFilesDir();
            String path = file.getAbsolutePath();
            String userId = "ibos_anno";
            Uri uri = Uri.parse(path + File.separator + userId);
            return uri;
        }
    }
}
