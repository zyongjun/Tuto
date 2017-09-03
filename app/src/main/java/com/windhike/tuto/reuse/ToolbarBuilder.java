package com.windhike.tuto.reuse;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windhike.fastcoding.util.InputWindowUtil;
import com.windhike.tuto.R;

/**
 * author: zyongjun on 2017/6/29 0029.
 * email: zhyongjun@windhike.cn
 */

public class ToolbarBuilder {
        private Activity mActivity;
        private Context mContext;
        private String mTitle;
        private String mRightText;
        private String mLeftText;
        private boolean mIsLeftVisible;
        private boolean mIsRightVisible;
        private int mRightImgResId;
        private TextView mTvRight;
        private TextView mTvLeft;
        private boolean isShowBack = true;

        private OnClickRight onClickRight;
        private OnClickLeft onClickLeft;
        private OnMenuClick onMenuClick;

        private ViewGroup mVRoot;


        public ToolbarBuilder(Activity activity) {
            mActivity = activity;
        }

        public ToolbarBuilder(ViewGroup viewGroup) {
            mVRoot = viewGroup;
        }

        public interface OnClickRight{
            void onClickRight();
        }

        public interface OnMenuClick{
            void onMenuClick();
        }

        public interface OnClickLeft{
            void onClickLeft();
        }

        private View findViewById(int id) {
            if (mVRoot != null) {
                return mVRoot.findViewById(id);
            }
            return mActivity.findViewById(id);
        }

    public ToolbarBuilder withLeftClick(OnClickLeft onClickLeft) {
        this.onClickLeft = onClickLeft;
        if (mTvLeft != null) {
            mTvLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolbarBuilder.this.onClickLeft.onClickLeft();
                }
            });
        }
        return this;
    }

    public ToolbarBuilder withMenuClick(OnMenuClick onMenuClick) {
        this.onMenuClick = onMenuClick;
        return this;
    }

    public ToolbarBuilder withRightClick(OnClickRight onClickRight) {
        this.onClickRight = onClickRight;
        if (mTvRight != null) {
            mTvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolbarBuilder.this.onClickRight.onClickRight();
                }
            });
        }
        return this;
    }

    public ToolbarBuilder showLeft(boolean isShowLeft) {
        mIsLeftVisible = isShowLeft;
        return this;
    }


    public ToolbarBuilder showRight(boolean isShowRight) {
        mIsRightVisible = isShowRight;
        return this;
    }

    public ToolbarBuilder withLeft(String left) {
        this.mLeftText = left;
//        if (!TextUtils.isEmpty(left)
//                && "返回".equals(mLeftText)
//                ) {
//            this.mLeftText = "";
//        }
        this.mIsLeftVisible = !TextUtils.isEmpty(left);
        return this;
    }

    public ToolbarBuilder withRight(String right){
        this.mRightText = right;
        this.mIsRightVisible = !TextUtils.isEmpty(right);
        return this;
    }

    public ToolbarBuilder withTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public ToolbarBuilder withRightImg(int resId) {
        this.mRightImgResId = resId;
        if (resId != 0) {
            mIsRightVisible = true;
        }
        return this;
    }

    public ToolbarBuilder withBack(boolean isShowBack) {
        this.isShowBack = isShowBack;
        return this;
    }

    public void show(){
        if (mVRoot != null) {
            mContext = mVRoot.getContext();
        } else {
            mContext = mActivity;
        }
        TextView mTvTitle = (TextView) findViewById(R.id.txtTitle);
        mTvRight = (TextView) findViewById(R.id.txtRight);
        mTvLeft = (TextView) findViewById(R.id.txtLeft);

        if(mTvLeft != null) {
            if (!TextUtils.isEmpty(mLeftText) && !"返回".equals(mLeftText)) {
                mTvLeft.setText(mLeftText);
            } else {
                mTvLeft.setText("");
            }

            if (mIsLeftVisible || isShowBack) {
                mTvLeft.setVisibility(View.VISIBLE);
                if (isShowBack) {
                    Drawable back = ResourcesCompat.getDrawable(mContext.getResources(), R.mipmap.ic_back_def, null);
                    back.setBounds(0, 0, back.getMinimumWidth(), back.getMinimumHeight());
                    mTvLeft.setCompoundDrawables(back, null, null, null);
                } else {
                    mTvLeft.setCompoundDrawables(null, null, null, null);
                }
                mTvLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickLeft != null) {
                            onClickLeft.onClickLeft();
                        } else {
                            InputWindowUtil.setInputMethodHide((Activity) mContext);
                            ((Activity) mContext).onBackPressed();
                        }
                    }
                });
            } else {
                mTvLeft.setVisibility(View.GONE);
            }
        }

        if(mTvTitle != null) {
            mTvTitle.setMaxEms(10);
            mTvTitle.setSingleLine();
            mTvTitle.setEllipsize(TextUtils.TruncateAt.END);
            mTvTitle.setText(mTitle);
            mTvTitle.setCompoundDrawables(null, null, null, null);
            mTvTitle.setOnClickListener(null);

        }

        if(mTvRight != null) {
            if (mIsRightVisible) {
                mTvRight.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mRightText)) {
                    mTvRight.setText(mRightText);
                } else {
                    if (mRightImgResId != 0) {
                        Drawable btnRight = ResourcesCompat.getDrawable(mContext.getResources(), mRightImgResId, null);
                        btnRight.setBounds(0, 0, btnRight.getMinimumWidth(), btnRight.getMinimumHeight());
                        mTvRight.setCompoundDrawables(btnRight, null, null, null);
                    }
                }
                mTvRight.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (onClickRight != null) {
                            onClickRight.onClickRight();
                        }
                    }
                });
            } else {
                mTvRight.setVisibility(View.INVISIBLE);
            }
        }
    }

}

