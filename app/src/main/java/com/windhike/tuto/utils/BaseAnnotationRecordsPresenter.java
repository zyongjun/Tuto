package com.windhike.tuto.utils;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.windhike.annotation.common.AESHelper;
import com.windhike.annotation.common.Utilities;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.datastorage.ExternalStorage;
import com.windhike.annotation.model.ImageDrawObject;
import com.windhike.annotation.model.ManagerImageObject;
import com.windhike.mvputils.BaseRecyclerPresenter;
import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.view.IBaseRecordsView;
import java.io.File;
import java.text.SimpleDateFormat;
import rx.Observable;
import rx.Observer;

/**
 * author: zyongjun on 2017/3/12 0012.
 * email: zhyongjun@windhike.cn
 */

public class BaseAnnotationRecordsPresenter<T extends IBaseRecordsView> extends BaseRecyclerPresenter<T>{
    protected ManagerImageObject managerImageObjects;

    public void initHead() {

    }
    @Override
    public int getItemCount() {
        return managerImageObjects==null?0:managerImageObjects.getListChooseDrawObject().size();
    }

    public ManagerImageObject getManagerImageObjects() {
        return managerImageObjects;
    }

    public View.OnClickListener deleteProjectListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDeleteDialog((Integer) v.getTag(R.id.position_id));
            }
        };
    }

    public View.OnClickListener itemClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    public View.OnClickListener shareClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer)v.getTag(R.id.position_id);
                shareAnnoation(position);
            }
        };
    }

    private static final String TAG = "BaseAnnotationRecordsPr";
    private void showShareDialog(final String shareId, final ImageDrawObject imageObject) {
//        ShareDialog dialog = new ShareDialog(mView.getViewContext()).builder().show();
//        dialog.setShareContentCallback(new ShareContentCallback() {
//            @Override
//            public void onOtherSharePlatform(Platform platform, Platform.ShareParams params) {
//                if ("SinaWeibo".equals(platform.getName())) {
//                    params.setShareType(Platform.SHARE_TEXT);
//                    params.setImageData(BitmapFactory.decodeResource(TutoApplication.getInstance().getResources(),R.drawable.ic_ibos_nomal));
//                    params.setTitle(String.format("你的好友 %s 分享了一个批注，快去打开看看吧...", TutoApplication.user.userinfo.userName));
//                    params.setText(String.format("我在酷办公分享了一个批注，点击查看: %s", String.format("http://app.ibos.cn/page/remark/share.html?id=%s&accesstoken=%s", shareId, TutoApplication.user.account.userToken)));
//                }else {
//                    params.setShareType(Platform.SHARE_WEBPAGE);
//                    params.setTitle(String.format("你的好友 %s 分享了一个批注，快去打开看看吧...", TutoApplication.user.userinfo.userName));
//                    params.setUrl(String.format("http://app.ibos.cn/page/remark/share.html?id=%s&accesstoken=%s", shareId, TutoApplication.user.account.userToken));
//                }
//                super.onOtherSharePlatform(platform, params);
//            }
//
//            @Override
//            public void onIbosSharePlatform(Context context, IbosShare params) {
//                String editPath = imageObject.getEditImagePath();
//                Bitmap bmp= BitmapFactory.decodeFile(editPath);
//                JSONObject json = new JSONObject();
//                try {
//                    json.put("type","postil");
//                    json.put("url",imageObject.getUrl());
//                    json.put("info", String.format(Locale.CHINA,"[批注]%s给你发来一条批注", TutoApplication.user.userinfo.userName));
//                    json.put("annotationId",imageObject.getRemarkId());
//                    json.put("thumbnail", BitmapAndStringUtils.convertIconToString(ThumbnailUtils.extractThumbnail(bmp,200,200)));
//                    params.setExtra(json.toString());
//                    params.setType(IbosShare.SHARE_ANNOTATION);
//                    params.setContent(String.format("%s的批注",TutoApplication.user.userinfo.userName));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }finally {
//                    bmp.recycle();
//                }
//                super.onIbosSharePlatform(context, params);
//            }
//        });
    }

    public void shareAnnoation(final int position) {
//        mView.showWaitingDialog(mView.getViewContext());
//        JSONObject requestJson = new JSONObject();
//        try {
//            requestJson.put("remarkid",managerImageObjects.getListChooseDrawObject().get(position).getRemarkId());
//            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),requestJson.toString());
//            RxApiClient.getInstance().getAnnotationApi()
//                    .shareAnnotation(body)
//                    .compose(SchedulersTransFormer.<Response<ResponseBody>>applyExecutorSchedulers())
//                    .subscribe(new Observer<Response<ResponseBody>>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            if (mView != null) {
//                                mView.dismissOpDialog();
//                            }
//                        }
//
//                        @Override
//                        public void onNext(Response<ResponseBody> result) {
//                            if (mView != null) {
//                                mView.dismissOpDialog();
//                            }
//                            try {
//                                JSONObject jsonObject = new JSONObject(new String(result.body().bytes()));
//                                String shareId = jsonObject.getString("data");
//                                if (!TextUtils.isEmpty(shareId)) {
//                                    showShareDialog(shareId,managerImageObjects.getListChooseDrawObject().get(position));
//                                }
//                            } catch (JSONException | IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//        } catch (JSONException e) {
//            mView.dismissOpDialog();
//            e.printStackTrace();
//        }

    }

    private void showConfirmDeleteDialog(final int position) {
//        final AlertDialog aDialog = new AlertDialog(mView.getViewContext()).builder();
//        aDialog.setTitle("提示");
//        aDialog.setMsg("确定删除当前批注吗?");
//        aDialog.setCanceledOnTouchOutside(true);
//        aDialog.setPositiveButton("确定", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteProject(position);
//            }
//        });
//        aDialog.setNegativeButton("", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                aDialog.dismiss();
//            }
//        });
//        aDialog.show();

    }

//    public Observable<Result<List<AnnotationModel>>> getRemarkListObeserver() {
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("limit",9999);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),obj.toString());
//        return RxApiClient.getInstance().getAnnotationApi()
//                .obtainRemarkList(body);
//    }

//    public Observable<AnnotationModel> getRemarkListFlatMapObserver() {
//        return getRemarkListObeserver()
//                .flatMap(new Func1<Result<List<AnnotationModel>>, Observable<AnnotationModel>>() {
//                    @Override
//                    public Observable<AnnotationModel> call(final Result<List<AnnotationModel>> listResult) {
//                        return Observable.create(new Observable.OnSubscribe<AnnotationModel>() {
//                            @Override
//                            public void call(Subscriber<? super AnnotationModel> subscriber) {
//                                deleteProject(Configs.ANNOTATION_IBOS);
//                                deleteProject(Configs.ANNOTATION_IBOS_TEMP);
//                                if (listResult.getData() != null && listResult.getData().size() >= 0) {
//                                    managerImageObjects = new ManagerImageObject();
//                                    List<AnnotationModel> remarks = listResult.getData();
//                                    Collections.sort(remarks, new Comparator<AnnotationModel>() {
//                                        @Override
//                                        public int compare(AnnotationModel o1, AnnotationModel o2) {
//                                            if (o1.getCreatetime() < o2.getCreatetime()) {
//                                                return -1;
//                                            }
//                                            return 1;
//                                        }
//                                    });
//                                    for (AnnotationModel remark : remarks) {
//                                        subscriber.onNext(remark);
//                                    }
//                                    subscriber.onCompleted();
//                                }else{
//                                    subscriber.onError(new Exception("empty list"));
//                                }
//                            }
//                        }).flatMap(new Func1<AnnotationModel, Observable<AnnotationModel>>() {
//                            @Override
//                            public Observable<AnnotationModel> call(AnnotationModel remark) {
//                                Utilities.createFolderProject(Configs.ANNOTATION_IBOS);
//                                ImageDrawObject drawObj = new ImageDrawObject();
//                                drawObj.setRemarkId(remark.getRemarkid());
//                                drawObj.setUrl(remark.getUrl());
//                                drawObj.setmDate(getDrawObjUpdateFormat(remark.getCreatetime()*1000));
//                                Bitmap oldBitmap = BitmapAndStringUtils.convertStringToIcon(remark.getThumbnail());
//                                String mUUID_PicturePath = new StringBuilder("thumbnail").append(UUID.randomUUID().toString()).toString();
//                                String imagePathSDCard = Utilities.getImageDrawFileNamePath(Configs.ANNOTATION_IBOS,new StringBuilder(String.valueOf(mUUID_PicturePath)).append(Configs.FLAG_THUMBNAIL_FILE_NAME).toString());
//                                File filedest = new File(imagePathSDCard);
//                                try {
//                                    if (filedest.exists()) {
//                                        filedest.delete();
//                                    }
//                                    Bitmap bitMapSourceSave = Utilities.scaleToActualAspectRatio(oldBitmap,
//                                            TutoApplication.getInstance().getDisplayMetrics().widthPixels, DisplayUtil.dip2px(TutoApplication.getInstance(),150));
//                                    Utilities.saveToSdcardPNG(filedest, bitMapSourceSave);
//                                    drawObj.setEditImagePath(filedest.getAbsolutePath());
//                                    oldBitmap.recycle();
//                                    bitMapSourceSave.recycle();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                managerImageObjects.getListChooseDrawObject().add(drawObj);
//                                return Observable.just(remark);
//                            }
//                        });
//
//                    }
//                });
//    }

    @Override
    public void attach(T view) {
        super.attach(view);
        managerImageObjects = ManagerImageObject.readFromFile(TutoApplication.getInstance(),Utilities.encryptFileName(Configs.ANNOTATION_IBOS));
        if (managerImageObjects != null) {
            initOtherBaseOnProject();
        }
    }

    public void loadProject() {
        mView.showWaitingDialog(mView.getViewContext());
        Observable.just(1)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (mView != null) {
                            if (managerImageObjects != null) {
                                initOtherBaseOnProject();
                            }
                            mView.dismissOpDialog();
                                Log.e(TAG, "onCompleted: =========="+managerImageObjects.getListChooseDrawObject().size() );
                                mView.updateEmptyView(managerImageObjects.getListChooseDrawObject().size()==0);
                                mView.notifyDataChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(TutoApplication.getInstance(),"同步失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        if(mView!=null) {
                            mView.dismissOpDialog();
                            mView.updateEmptyView(true);
                        }
                    }

                    @Override
                    public void onNext(Integer model) {
                        managerImageObjects = ManagerImageObject.readFromFile(mView.getViewContext(), Utilities.encryptFileName(Configs.ANNOTATION_IBOS));
                    }
                });

    }

    protected void initOtherBaseOnProject(){

    }

    public String getDrawObjUpdateFormat(long updatetime) {
        return new SimpleDateFormat("yyyy-MM-dd").format(updatetime);
    }

    public void deleteProject(String project) {
        try {
            String storeprojectNameDelete = AESHelper.encrypt(Configs.ENCRYPT_KEY, project);
            File[] fs = Utilities.filterDataFiles(mView.getViewContext().getFilesDir().listFiles());
            for (File f : fs) {
                try {
                    String projectName = Utilities.getFileName(f.getAbsoluteFile().getAbsolutePath());
                    if (storeprojectNameDelete.equals(projectName)) {
                        f.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ExternalStorage.getObj();
            ExternalStorage.isDeleteForder(Configs.ROOT_FOLDER_NAME+"/" + storeprojectNameDelete);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    protected void deleteProject(final int position) {

        mView.showWaitingDialog(mView.getViewContext());
        Observable.just(1)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        if(mView!=null) {
                            mView.dismissOpDialog();
                            managerImageObjects.getListChooseDrawObject().remove(position);
                            managerImageObjects.writeToFile(TutoApplication.getInstance(),Configs.ANNOTATION_IBOS);
                            managerImageObjects = ManagerImageObject.readFromFile(mView.getViewContext(), Utilities.encryptFileName(Configs.ANNOTATION_IBOS));
                            showOrHideEmptyView();
                            mView.notifyDataChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(mView!=null) {
                            mView.dismissOpDialog();
                        }
                    }

                    @Override
                    public void onNext(Integer result) {

                    }
                });

    }

    public void showOrHideEmptyView() {
        mView.updateEmptyView(managerImageObjects == null || managerImageObjects.getListChooseDrawObject().size() == 0);
    }
}
