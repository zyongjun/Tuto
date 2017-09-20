package com.windhike.tuto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.windhike.annotation.configsapp.Configs;
import com.windhike.annotation.model.PreferenceConnector;
import com.windhike.fastcoding.CommonFragmentActivity;
import com.windhike.fastcoding.base.BaseFragment;
import com.windhike.tuto.R;
import com.windhike.tuto.widget.PageIndicator;
import butterknife.BindView;

/**
 * author:gzzyj on 2017/9/19 0019.
 * email:zhyongjun@windhike.cn
 */
public class GuideFragment extends BaseFragment {
    private static final String TAG = "NormalGuideFragment";
    @BindView(R.id.btn_enter)
    View mStartText;
    private GuidePageAdapter mGuidePageAdapter;

    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    PageIndicator indicator;

    public static void enterGuide(Context context) {
        Bundle bundle = new Bundle();
//        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_TRANSLUCENT,true);
        bundle.putBoolean(CommonFragmentActivity.BUNDLE_KEY_FULLSCREEN,true);
        CommonFragmentActivity.start(context,GuideFragment.class.getName(),bundle);
    }

    @Override
    public int getLayouId() {
        return R.layout.layout_normal_guide;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGuidePageAdapter = new GuidePageAdapter(getActivity());
        viewPager.setAdapter(mGuidePageAdapter);
        indicator.setViewPager(viewPager);
        indicator.setOnPageChangeListener(mOnPageChangeListener);
        mStartText.setOnClickListener(mStartClickListener);
        mRootView.findViewById(R.id.tv_ignore).setOnClickListener(mStartClickListener);
//        mRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private View.OnClickListener mStartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PreferenceConnector.writeBoolean(getActivity(),"ISFIRST",false);
            CommonFragmentActivity.start(getActivity(), MainPageFragment.class.getName(), null);
            onBackPressed();
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            boolean isLast = (position == mGuidePageAdapter.getCount() - 1) ;
            mStartText.setVisibility(isLast?View.VISIBLE:View.GONE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private class GuidePageAdapter extends PagerAdapter {
        private int[] mDrawables = {R.mipmap.a, R.mipmap.b, R.mipmap.d,R.mipmap.e,R.mipmap.f};
        private Context mContext;

        public GuidePageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mDrawables.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setAdjustViewBounds(true);
            Glide.with(container.getContext()).load(mDrawables[position]).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            ApplicationDelegate.unbindDrawables((View) object);
        }
    }
}
