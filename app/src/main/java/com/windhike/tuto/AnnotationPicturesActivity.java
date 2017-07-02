package com.windhike.tuto;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.annotation.reuse.FileUtils;
import com.windhike.recyclerutils.RecyclerAdapter;
import com.windhike.recyclerutils.RecyclerHolder;
import com.windhike.tuto.presenter.AnnotationGalleryPresenter;
import com.windhike.tuto.reuse.ToolbarBuilder;
import com.windhike.tuto.view.IAnnotationSelectPictureView;
import com.windhike.tuto.widget.CardAdapterHelper;
import com.windhike.tuto.widget.CardScaleHelper;

import java.io.File;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * author: gzzyj on 2017/3/2.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationPicturesActivity extends BaseAty implements View.OnClickListener,IAnnotationSelectPictureView,AnnotationGalleryPresenter.IAnnotationGalleryView{

    @Bind(R.id.photoViewpager)
    RecyclerView photoViewpager;
    @Bind(R.id.fab_camera)
    FloatingActionButton fabCamera;
    @Bind(R.id.fab_pic)
    FloatingActionButton fabPic;
    AnnotationGalleryPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        ButterKnife.bind(this);
        getToolbarBuilder().showLeft(false)
                .withBack(true)
                .withTitle("批注")
                .showRight(true)
                .withRightImg(R.mipmap.ic_recent_annotation)
                .withRightClick(new ToolbarBuilder.OnClickRight() {
                    @Override
                    public void onClickRight() {
                        startActivity(AnnotationRecentActivity.getRecentAnnotationIntent(AnnotationPicturesActivity.this));
                    }
                }).show();
        fabCamera.setOnClickListener(this);
        fabPic.setOnClickListener(this);
        mPresenter = new AnnotationGalleryPresenter();
        mPresenter.attach(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        photoViewpager.setLayoutManager(linearLayoutManager);

        new CardScaleHelper().attachToRecyclerView(photoViewpager);
        photoViewpager.setAdapter(new RecyclerAdapter(mPresenter){
            @Override
            public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RecyclerHolder holder = super.onCreateViewHolder(parent, viewType);
                mAdapterHelper.onCreateViewHolder(parent,holder.itemView);
                return holder;
            }

            @Override
            public void onBindViewHolder(RecyclerHolder holder, int position) {
                mAdapterHelper.onBindViewHolder(holder.itemView,position,getPresenter().getItemCount());
                super.onBindViewHolder(holder, position);
            }
        });

      mPresenter.initPictures();
        getContentResolver().registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,observer);

    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);
        super.onDestroy();
    }

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mPresenter.initPictures();
        }
    };

    private CardAdapterHelper mAdapterHelper=new CardAdapterHelper();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            startActivity(AnnotationProjectEditActivity.getAnnotationNewDrawIntent(this,mPhotoUri.getPath()));
        } else if (requestCode == REQUEST_IMAGE_SELECT) {
            SaveSelectState selectState = data.getParcelableExtra(Intent.EXTRA_RETURN_RESULT);
            startActivity(AnnotationProjectEditActivity.getAnnotationNewDrawIntent(this,selectState.getPath()));
        }
    }
    private static final int REQUEST_IMAGE_CAPTURE = 170;
    private static final int REQUEST_IMAGE_SELECT = 171;
    Uri mPhotoUri;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_camera:
                Intent cameraIntent = new Intent();
                try {
                    File photoFile = FileUtils.createImageFile();
                    mPhotoUri = Uri.fromFile(photoFile);
                    cameraIntent.putExtra("output",mPhotoUri);
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fab_pic:
                startActivityForResult(SelectPictureAty.getPictureSingleSelectIntent(this), REQUEST_IMAGE_SELECT);
                break;
        }
    }

    @Override
    public void notifyDataChanged() {
        photoViewpager.getAdapter().notifyDataSetChanged();
    }

}
