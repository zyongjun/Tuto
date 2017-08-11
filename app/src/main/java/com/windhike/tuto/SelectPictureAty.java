package com.windhike.tuto;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import com.windhike.annotation.model.SaveSelectState;
import com.windhike.tuto.presenter.BasePictureSelectPresenter;
import com.windhike.tuto.presenter.PictureMultiSelectAdapter;
import com.windhike.tuto.presenter.PictureMultiSelectPresenter;
import com.windhike.tuto.presenter.PictureSingleSelectPresenter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gzzyj on 2016/6/28 0028.
 */

public class SelectPictureAty extends BaseAty implements Handler.Callback,View.OnClickListener
        ,PictureMultiSelectPresenter.ISelectMultiPictureView ,PictureSingleSelectPresenter.ISelectSinglePictureView{
    @BindView(android.R.id.list)
    GridView mGridView;
    private BaseAdapter mAdapter;
    @BindView(android.R.id.button1)
    Button mButOK;
    @BindView(R.id.txtLeft)
    TextView mTvLeft;
    @BindView(R.id.rc_checkbox)
    CheckBox mCbCheckedSize;
    @BindView(R.id.tv_select_size)
    TextView mTvSelectSize;
    @BindView(R.id.rl_bottom)
    View mRlBottom;
    @BindView(R.id.btn_annotation)
    Button btnAnnotation;

    private static int MAX_NUM = 9;
    public static final String INTENT_MAX_NUM = "intent_max_num";
    private static final String KEY_SEND_ORIGIN = "sendOrigin";
    private static final String KEY_SINGLE_SELECT = "key_single_select";

    private static final int EVENT_PREVIEW_SEND = 0;
    private static final int EVENT_PREVIEW_SELECT = 1;
    private static final int EVENT_CACULATE_SELECT_SIZE = 2;
    private static final int EVENT_UPDATE_BOTTOM_UI = 3;
    private static final int EVENT_CACULATE_SELECT_COUNT = 4;

    private static final int CODE_REQUEST_CHANGE_PICTURE_DIRS = 1;
    private static final int CODE_REQUEST_PREVIEW_PICTURES = 2;
    private static final int RESULT_CODE_SELECT_ANNOTATION = 10;
    private Handler mHandler = new Handler(this);

    private BasePictureSelectPresenter mPresenter;

    public SelectPictureAty() {
    }

    public static Intent getPictureSingleSelectIntent(Context context) {
        Intent intent = new Intent(context,SelectPictureAty.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SINGLE_SELECT,true);
        intent.putExtras(bundle);
        return intent;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_select_pictures);
        ButterKnife.bind(this);
        boolean isSingleSelect = getIntent().getBooleanExtra(KEY_SINGLE_SELECT,false);
        if (isSingleSelect) {
            mPresenter = new PictureSingleSelectPresenter();
        }else {
            mPresenter = new PictureMultiSelectPresenter();
        }
        mPresenter.attach(this);
//        tintManager.setTintColor(R.color.title_bg);
//        tintManager.setStatusBarTintResource(R.color.title_bg);

        mPresenter.initHead();
        mPresenter.initBottomUI();
        MAX_NUM = getIntent().getIntExtra(INTENT_MAX_NUM, MAX_NUM);
        mPresenter.initPictureAndFolders();
        mCbCheckedSize.setOnCheckedChangeListener(mOnCheckChangeListener);
    }

    @Override
    public void hideBottomUI() {
        mRlBottom.setVisibility(View.GONE);
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                if(mHandler.hasMessages(EVENT_CACULATE_SELECT_SIZE)){
                    mHandler.removeMessages(EVENT_CACULATE_SELECT_SIZE);
                }
                mHandler.sendEmptyMessageDelayed(EVENT_CACULATE_SELECT_SIZE,30);
            }else{
                mTvSelectSize.setText("原图");
            }
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        if(mCbCheckedSize == null){
            return true;
        }
        switch(msg.what) {
            case EVENT_PREVIEW_SEND:
                ArrayList<String> list = (ArrayList) msg.obj;
                if (list != null && list.size() > 0) {
                    Intent preData = new Intent();
                    ArrayList<Uri> selectList = new ArrayList<>();
                    for (String url : list) {
                        selectList.add(Uri.parse("file://" + url));
                    }
                    preData.putExtra(KEY_SEND_ORIGIN, mCbCheckedSize.isChecked() || mPresenter.isSendOriginOnPreview());
                    preData.putExtra(Intent.EXTRA_RETURN_RESULT, selectList);
                    setResult(-1, preData);
                    finish();
                }
                break;
            case EVENT_PREVIEW_SELECT:
                mPresenter.setCurrentStateList((ArrayList) msg.obj);
                sendEmptyMessage(EVENT_CACULATE_SELECT_COUNT);
                notifyDataSetChanged();
                break;
            case EVENT_CACULATE_SELECT_SIZE:
                if (mTvSelectSize != null) {
                    long fileSize = 0;
                    Iterator mIntent = mPresenter.getCurrentStateList().iterator();
                    File file;
                    while (mIntent.hasNext()) {
                        SaveSelectState state = (SaveSelectState) mIntent.next();
                        if (state.isSelect()) {
                            file = new File(state.getPath());
                            fileSize += file.length();
                        }
                    }
//                    String textSize;
//                    if (fileSize == 0) {
//                        textSize = getString(R.string.pattern_picture_size_zero);
//                    } else {
//                        textSize = String.format(getString(R.string.pattern_picture_size), FileUtils.getExactFormatSize(fileSize));
//                    }
                    mTvSelectSize.setText("原图");
                }
                break;
            case EVENT_UPDATE_BOTTOM_UI:
                if (mPresenter instanceof PictureMultiSelectPresenter){
                    updateBottomUI((PictureMultiSelectPresenter) mPresenter);
                }
                break;
            case EVENT_CACULATE_SELECT_COUNT:
                mPresenter.caculateSelectCount();
                sendEmptyMessage(EVENT_UPDATE_BOTTOM_UI);
                if(mCbCheckedSize.isChecked()) {
                    sendEmptyMessage(EVENT_CACULATE_SELECT_SIZE);
                }
                break;
        }

        return true;
    }

    @Override
    public void updateBottomUI(PictureMultiSelectPresenter presenter) {
        if(mTvSelectSize != null){
            Resources resource = getResources();
            if(mButOK.isSelected()){
                if(presenter.getSelectCount() == 0){
                    mButOK.setSelected(false);
                    mButOK.setTextColor(resource.getColor(R.color.c_picture_ok_select));
                    mTvSelectSize.setTextColor(resource.getColor(R.color.c_yuantu_no));
                }
            }else{
                if(presenter.getSelectCount() > 0){
                    mButOK.setSelected(true);
                    mButOK.setTextColor(resource.getColor(R.color.white));
                    mTvSelectSize.setTextColor(resource.getColor(R.color.c_yuantu));
                }
            }
            mButOK.setText(String.format(resource.getString(R.string.rc_send_format), String.valueOf(presenter.getSelectCount())));

            if (presenter.getSelectCount() == 1) {
                btnAnnotation.setSelected(true);
                btnAnnotation.setTextColor(ResourcesCompat.getColor(getResources(),R.color.blue,null));
            }else{
                btnAnnotation.setSelected(false);
                btnAnnotation.setTextColor(ResourcesCompat.getColor(getResources(),R.color.c_yuantu_no,null));
            }
        }
    }

    @Override
    public void sendEmptyMessage(int what) {
        mHandler.sendEmptyMessage(what);
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCELED){
            setResult(RESULT_CANCELED);
            finish();
        }

        if (resultCode == RESULT_CODE_SELECT_ANNOTATION) {
            setResult(RESULT_CODE_SELECT_ANNOTATION,data);
            finish();
        }

        if(requestCode == CODE_REQUEST_PREVIEW_PICTURES) {
            if (data != null) {
                Message msg;
                if (data.getParcelableArrayListExtra("BACK_RESULT") != null) {
                    msg = Message.obtain();
                    msg.what = EVENT_PREVIEW_SELECT;
                    msg.obj = data.getParcelableArrayListExtra("BACK_RESULT");
                    mHandler.sendMessage(msg);
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                    msg = Message.obtain();
                    msg.what = EVENT_PREVIEW_SEND;
                    mPresenter.setSendOriginOnPreview(data.getBooleanExtra(KEY_SEND_ORIGIN,false));
                    msg.obj = data.getSerializableExtra("PREVIEW_RESULT");
                    mHandler.sendMessage(msg);
                }

            }
        }else if(requestCode == CODE_REQUEST_CHANGE_PICTURE_DIRS){
            mPresenter.onSelectedFolderResult(resultCode);
        }
    }

    @OnClick(value = {android.R.id.button1,R.id.btn_annotation})
    public void onClick(View v) {
        switch(v.getId()) {
            case android.R.id.button1:
                mPresenter.sendSelectImages(mCbCheckedSize.isChecked());
                break;
            case R.id.btn_annotation:
                if (btnAnnotation.isSelected()) {
                    mPresenter.clickOnAnnotation();
                }
                break;
        }
    }

    public void back(View v) {
        this.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    @Override
    public void initSingleSelectCurrentFolderUI(PictureSingleSelectPresenter presenter, BaseAdapter adapter) {
        mAdapter = adapter;
        mGridView.setAdapter(mAdapter);
        setTitleCustomer(true, true, "返回",
                mPresenter.getCurrentFolder().getFolderName(),
                "取消", true);
    }

    @Override
    public void initMultiCurrentFolderUI(PictureMultiSelectPresenter presenter) {
//        String formatString = getResources().getString(io.rong.imkit.R.string.rc_send_format);
        mButOK.setText(String.format("发送（%s）", String.valueOf(0)));
        setTitleCustomer(true, true, "返回",
                String.format("%s(%s)",mPresenter.getCurrentFolder().getFolderName(),mPresenter.getCurrentStateList().size()),
                "取消", true);
        mTvSelectSize.setText(getString(R.string.pattern_picture_size_zero));
        mAdapter = new PictureMultiSelectAdapter(presenter);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onPictureSelected(PictureMultiSelectPresenter presenter,int position,boolean isChecked,CompoundButton buttonView) {
        if(mCbCheckedSize.isChecked()) {
            if (mHandler.hasMessages(EVENT_CACULATE_SELECT_SIZE)) {
                mHandler.removeMessages(EVENT_CACULATE_SELECT_SIZE);
            }
            mHandler.sendEmptyMessageDelayed(EVENT_CACULATE_SELECT_SIZE, 100);
        }
        if(isChecked) {
            if( presenter.getSelectCount() >= MAX_NUM){
                buttonView.setChecked(false);
                return;
            }
            presenter.setSelectCount(presenter.getSelectCount()+1);
            presenter.getCurrentStateList().get(position).setIsSelect(true);
        } else {
            presenter.setSelectCount(presenter.getSelectCount()-1);
            presenter.getCurrentStateList().get(position).setIsSelect(false);
        }
        sendEmptyMessage(EVENT_UPDATE_BOTTOM_UI);
        String formatString = getResources().getString(R.string.rc_send_format);
        mButOK.setText(String.format(formatString, String.valueOf(presenter.getSelectCount())));
        notifyDataSetChanged();
    }
}
