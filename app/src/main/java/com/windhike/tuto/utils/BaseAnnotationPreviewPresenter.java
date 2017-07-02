package com.windhike.tuto.utils;

import android.os.Bundle;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.mvputils.BasePresenter;
import com.windhike.tuto.view.IAnnotationPreivewView;

/**
 * author: zyongjun on 2017/3/15 0015.
 * email: zhyongjun@windhike.cn
 */

public abstract class BaseAnnotationPreviewPresenter<T extends IAnnotationPreivewView> extends BasePresenter<T>{
    private static final String TAG = "BaseAnnotationPreviewPr";
    protected ManagerImageObject mManagerObject;
    protected int mImageDrawObjectIndex;
    protected boolean isReady;
    public BaseAnnotationPreviewPresenter(Bundle bundle) {

    }
    public static BaseAnnotationPreviewPresenter create(Bundle bundle) {
        BaseAnnotationPreviewPresenter presenter=null;
//                = new AnnotationPreviewPresenter(bundle);
        return presenter;
    }

    public String getProjectName() {
        return Configs.ANNOTATION_IBOS;
    }

    public void initProject() {

    }

    public void navigateToDrawing(int drawobjectPosition) {

    }

    public ManagerImageObject getManagerObject() {
        return mManagerObject;
    }

    public int getDrawObjectIndex() {
        return mImageDrawObjectIndex;
    }

    protected void downloadJsonFile(final ImageDrawObject imageDrawObj) {

//        if (!TextUtils.isEmpty(imageDrawObj.getUrl())) {
//            mView.showWaitingDialog(mView.getViewContext());
//            RxApiClient.getInstance().getAnnotationApi()
//                    .downloadFile(imageDrawObj.getUrl())
//                    .flatMap(new Func1<Response<ResponseBody>, Observable<File>>() {
//                        @Override
//                        public Observable<File> call(final Response<ResponseBody> responseBodyResponse) {
//                            return Observable.create(new Observable.OnSubscribe<File>() {
//                                @Override
//                                public void call(Subscriber<? super File> subscriber) {
//                                    try {
//                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), imageDrawObj.getRemarkId()+".json");
//                                        BufferedSink sink = Okio.buffer(Okio.sink(file));
//                                        sink.writeAll(responseBodyResponse.body().source());
//                                        sink.close();
//                                        subscriber.onNext(file);
//                                        subscriber.onCompleted();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                        subscriber.onError(e);
//                                    }
//                                }
//                            });
//                        }
//                    }).compose(SchedulersTransFormer.<File>applyExecutorSchedulers())
//                    .subscribe(new Observer<File>() {
//                        @Override
//                        public void onCompleted() {
//                            Log.e(TAG, "onCompleted: ==========================");
//                            mManagerObject.writeToFile(TutoApplication.getInstance(), getProjectName());
//                            if (mView != null) {
//                                isReady = true;
//                                mView.dismissOpDialog();
//                                mView.loadDrawObject();
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (mView != null) {
//                                mView.dismissOpDialog();
//                                onErrorTips();
//                            }
//                            Log.e(TAG, "onError: ==============="+e.getMessage());
//                        }
//
//                        @Override
//                        public void onNext(File file) {
//                            try {
//                                FileInputStream input = new FileInputStream(file);
//                                int length = input.available();
//                                byte buffer[] = new byte[length];
//                                input.read(buffer);// 将数据读入数组
//                                input.close();// 读取完毕后要关闭流。
//                                String draws =new String(buffer, "UTF-8");// 设置取得的数据编码，防止乱码
//                                final JSONObject annotationJson = new JSONObject(draws);
//                                JSONArray drawsArray = annotationJson.optJSONArray("draws");
//                                JSONArray voicesArray = annotationJson.optJSONArray("voices");
//                                JSONArray textArray = annotationJson.optJSONArray("remarks");
//                                int sWidth = annotationJson.optInt("contentWidth");
//                                int sHeight = annotationJson.optInt("contentHeight");
//                                float density = annotationJson.optInt("density");
//                                if (density == 0) {
//                                    density = 2;
//                                }
//                                Bitmap bitMapSource = BitmapAndStringUtils.convertStringToIcon(annotationJson.optString("imageData"));
//
//                                String mUUID_PicturePath = String.format("ibosannotation%s", UUID.randomUUID().toString());
//                                mManagerObject.getListChooseDrawObject().get(mImageDrawObjectIndex).setOriginalImageGalleryPath(mUUID_PicturePath);
//                                Utilities.createFolderProject(getProjectName());
//                                String imagePathSDCard = Utilities.getImageDrawFileNamePath(getProjectName(), new StringBuilder(String.valueOf(mUUID_PicturePath)).append(Configs.FLAG_ORIGINAL_FILE_NAME).toString());
//                                File filedest = new File(imagePathSDCard);
//                                try {
//                                    if (filedest.exists()) {
//                                        filedest.delete();
//                                    }
//                                    Bitmap bitMapSourceSave = Utilities.scaleToActualAspectRatio(bitMapSource,
//                                            TutoApplication.getInstance().getDisplayMetrics().widthPixels, TutoApplication.getInstance().getDispalyMetrics().heightPixels - DisplayUtil.dip2px(TutoApplication.getInstance(), 110));
//                                    Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
//                                    bitMapSource.recycle();
//                                    bitMapSourceSave.recycle();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                mManagerObject.getListChooseDrawObject().get(mImageDrawObjectIndex).setOriginalImagePath(filedest.getAbsolutePath());
//                                if(TextUtils.isEmpty( mManagerObject.getListChooseDrawObject().get(mImageDrawObjectIndex).getEditImagePath())) {
//                                    mManagerObject.getListChooseDrawObject().get(mImageDrawObjectIndex).setEditImagePath(mManagerObject.getListChooseDrawObject().get(mImageDrawObjectIndex).getOriginalImagePath());
//                                }
//                                final ArrayList<CustomerShapeView> shapeList = mManagerObject.getListChooseDrawObject().get(mImageDrawObjectIndex).getListRootShape();
//                                if (drawsArray != null && drawsArray.length() > 0) {
//                                    int size = drawsArray.length();
//                                    for (int i = 0; i < size; i++) {
//                                        JSONObject shapeJson = drawsArray.optJSONObject(i);
//                                        CustomerShapeView shapeView = DrawingShapeTransformer.transJsonToShapeView(shapeJson,sWidth,sHeight,density);
//                                        if(shapeView!=null) {
//                                            shapeView._shapeId = i;
//                                            shapeList.add(shapeView);
//                                        }
//                                    }
//                                }
//
//                                if (voicesArray != null && voicesArray.length() > 0) {
//                                    int size = voicesArray.length();
//                                    int alreadySize = shapeList.size();
//                                    for(int i= 0;i<size;i++) {
//                                        JSONObject voiceJson = voicesArray.optJSONObject(i);
//                                        CustomerShapeView shapeView = DrawingShapeTransformer.transJsonToVoiceView(voiceJson,sWidth,sHeight,density);
//                                        if(shapeView!=null) {
//                                            shapeView._shapeId = i + alreadySize;
//                                            shapeList.add(shapeView);
//                                        }
//                                    }
//                                }
//
//                                if (textArray != null && textArray.length() > 0) {
//                                    int size = textArray.length();
//                                    int alreadySize = shapeList.size();
//                                    for(int i=0;i<size;i++) {
//                                        JSONObject textJson = textArray.optJSONObject(i);
//                                        CustomerShapeView textView = DrawingShapeTransformer.transJsonToTextView(textJson,sWidth,sHeight,density);
//                                        if(textView!=null) {
//                                            textView._shapeId = i + alreadySize;
//                                            shapeList.add(textView);
//                                        }
//                                    }
//                                }
//                                ImageDrawObject imageDrawObject = mManagerObject.getListChooseDrawObject().get(mImageDrawObjectIndex);
//                                imageDrawObject.setListRootShape(shapeList);
//                                imageDrawObject.setRemarkId(imageDrawObj.getRemarkId());
//                                imageDrawObject.setUrl(imageDrawObj.getUrl());
//                                mManagerObject.getListChooseDrawObject().set(mImageDrawObjectIndex,imageDrawObject);
//                            } catch (IOException | JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//        }else{
//            onErrorTips();
//        }
    }

    public void onErrorTips() {
        mView.onErrorTips();
    }

}
