package com.windhike.tuto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.annotation.reuse.FileUtils;
import com.windhike.recyclerutils.RecyclerAdapter;
import com.windhike.tuto.utils.AnnotationRecordsPresenter;
import com.windhike.tuto.utils.AnnotationSelectRecordsPresenter;
import com.windhike.tuto.utils.BaseAnnotationRecordsPresenter;
import java.io.File;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * author: gzzyj on 2017/3/2.
 * email:zhyongjun@windhike.cn
 */

public class AnnotationRecentActivity extends BaseAty implements AnnotationRecordsPresenter.IAnnotationRecordsView,
        AnnotationSelectRecordsPresenter.IAnnotationSelectRecordsView,View.OnClickListener{
    private static final int REQUEST_IMAGE_CAPTURE = 170;
    private static final int REQUEST_IMAGE_SELECT = 171;
    Uri mPhotoUri;

    @Bind(R.id.recordRecyclerView)
    RecyclerView recordRecyclerView;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.fab_camera)
    FloatingActionButton fabCamera;
    @Bind(R.id.fab_pic)
    FloatingActionButton fabPic;
    private BaseAnnotationRecordsPresenter mPresenter;

    public static Intent getRecentAnnotationIntent(Context context) {
        Intent intent = new Intent(context, AnnotationRecentActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_records);
        ButterKnife.bind(this);
        mPresenter = new AnnotationRecordsPresenter();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recordRecyclerView.setLayoutManager(manager);

        mPresenter.attach(this);
        mPresenter.initHead();
        recordRecyclerView.setAdapter(new RecyclerAdapter(mPresenter));
        fabCamera.setOnClickListener(this);
        fabPic.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.loadProject();
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            startActivity(AnnotationProjectEditActivity.getAnnotationNewDrawIntent(this,
                    mPhotoUri.getPath()));
        } else if (requestCode == REQUEST_IMAGE_SELECT) {
            SaveSelectState selectState = data.getParcelableExtra(Intent.EXTRA_RETURN_RESULT);
            startActivity(AnnotationProjectEditActivity.getAnnotationNewDrawIntent(this,
                    selectState.getPath()));
        }
    }

    @Override
    public void updateEmptyView(boolean isVisible) {
        llEmpty.setVisibility(isVisible? View.VISIBLE: View.GONE);
    }

    @Override
    public void notifyDataChanged() {
        recordRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
