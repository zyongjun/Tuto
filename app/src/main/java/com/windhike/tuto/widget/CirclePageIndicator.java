package com.windhike.tuto.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.windhike.tuto.R;


public class CirclePageIndicator extends View
implements PageIndicator {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	private float mRadius;
	private final Paint mPaintStroke;
	private final Paint mPaintFill;
	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mOnPageChangeListener;
	private int mCurrentPage;
	private int mSnapPage;
	private int mCurrentOffset;
	private int mScrollState;
	private int mPageSize;
	private int mOrientation;
	private boolean mIsJustified;
	private boolean mIsSnapped;
	
	public CirclePageIndicator(Context context) {
		this(context, null);
	}
	
	public CirclePageIndicator(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, R.attr.vpiCirclePageIndicatorStyle);
	}
	
	public CirclePageIndicator(Context context, AttributeSet attributeSet, int defaultStyle) {
		super(context, attributeSet, defaultStyle);
		
		final Resources resources = getResources();
		final int defaultFillColor = resources.getColor(R.color.default_circle_indicator_fill_color);
		final int defaultOrientation = resources.getInteger(R.integer.default_circle_indicator_orientation);
		final int defaultStrokeColor = resources.getColor(R.color.default_circle_indicator_stroke_color);
		final float defaultStrokeWidth = resources.getDimension(R.dimen.default_circle_indicator_stroke_width);
		final float defaultRadius = resources.getDimension(R.dimen.default_circle_indicator_radius);
		final boolean defaultIsJustified = resources.getBoolean(R.bool.default_circle_indicator_is_justified);
		final boolean defaultIsSnapped = resources.getBoolean(R.bool.default_circle_indicator_is_snapped);
		
		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CirclePageIndicator, defaultStyle, R.style.Widget_CirclePageIndicator);
		
		mIsJustified = typedArray.getBoolean(R.styleable.CirclePageIndicator_isJustified, defaultIsJustified);
		mOrientation = typedArray.getInt(R.styleable.CirclePageIndicator_orientation, defaultOrientation);
		mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintStroke.setStyle(Style.FILL);
		mPaintStroke.setColor(typedArray.getColor(R.styleable.CirclePageIndicator_strokeColor, defaultStrokeColor));
//		mPaintStroke.setStrokeWidth(typedArray.getDimension(R.styleable.CirclePageIndicator_strokeWidth, defaultStrokeWidth));
		mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintFill.setStyle(Style.FILL);
		mPaintFill.setColor(typedArray.getColor(R.styleable.CirclePageIndicator_fillColor, defaultFillColor));
		mRadius = typedArray.getDimension(R.styleable.CirclePageIndicator_radius, defaultRadius);
		mIsSnapped = typedArray.getBoolean(R.styleable.CirclePageIndicator_isSnapped, defaultIsSnapped);
		
		typedArray.recycle();
	}
	
	public boolean isJustified() {
		return mIsJustified;
	}
	
	public void setIsJustified(boolean isJustified) {
		mIsJustified = isJustified;
		invalidate();
	}
	
	public int getFillColor() {
		return mPaintFill.getColor();
	}
	
	public void setFillColor(int fillColor) {
		mPaintFill.setColor(fillColor);
		invalidate();
	}
	
	public int getOrientation() {
		return mOrientation;
	}
	
	public void setOrientation(int orientation) {
		switch(orientation) {
		case HORIZONTAL:
		case VERTICAL:
			mOrientation = orientation;
			updatePageSize();
			requestLayout();
			break;
			
		default:
			throw new IllegalArgumentException("Orientation must be either HORIZONTAL or VERTICAL");
		}
	}
	
	public int getStrolorColor() {
		return mPaintStroke.getColor();
	}
	
	public void setStrokeColor(int strokeColor) {
		mPaintStroke.setColor(strokeColor);
		invalidate();
	}
	
	public float getStrokeWidth() {
		return mPaintStroke.getStrokeWidth();
	}
	
	public void setStrokeWidth(float strokeWidth) {
		mPaintStroke.setStrokeWidth(strokeWidth);
		invalidate();
	}
	
	public float getRadius() {
		return mRadius;
	}
	
	public void setRadius(float radius) {
		mRadius = radius;
		invalidate();
	}
	
	public boolean isSnapped() {
		return mIsSnapped;
	}
	
	public void setSnapped(boolean isSnapped) {
		mIsSnapped = isSnapped;
		invalidate();
	}

    public void setStrokeStyle(Style style){
        mPaintStroke.setStyle(style);
        invalidate();
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int longSize;
		int longPaddingBefore;
		int longPaddingAfter;
		int shortPaddingBefore;
		if(mOrientation == HORIZONTAL) {
			longSize = getWidth();
			longPaddingBefore = getPaddingLeft();
			longPaddingAfter = getPaddingRight();
			shortPaddingBefore = getPaddingTop();
		} else {
			longSize = getHeight();
			longPaddingBefore = getPaddingTop();
			longPaddingAfter = getPaddingBottom();
			shortPaddingBefore = getPaddingLeft();
		}
		if(mViewPager == null){
			return;
		}
		final int count = mViewPager.getAdapter().getCount();
		if(count==1){
			return;
		}
		final float threeRadius = mRadius*3;
		final float shortOffset = shortPaddingBefore + mRadius;
		float longOffset = longPaddingBefore + mRadius;
		if(mIsJustified) {
			longOffset += ((longSize - longPaddingBefore - longPaddingAfter)/2.0f) - ((count*threeRadius)/2.0f);
		}
		
		float dX = 0.0f;
		float dY = 0.0f;
		
		for(int index = 0; index < count; index++) {
			float drawLong = longOffset + (index*threeRadius);
			if(mOrientation == HORIZONTAL) {
				dX = drawLong;
				dY = shortOffset;
			} else {
				dX = shortOffset;
				dY = drawLong;
			}
			canvas.drawCircle(dX, dY, mRadius, mPaintStroke);
		}
		
		float cx = (mIsSnapped ? mSnapPage : mCurrentPage)*threeRadius;
		if(!mIsSnapped && (mPageSize != 0)) {
			cx += (mCurrentOffset*1.0f/mPageSize)*threeRadius;
		}
		if(mOrientation == HORIZONTAL) {
			dX = longOffset + cx;
			dY = shortOffset;
		} else {
			dX = shortOffset;
			dY = longOffset + cx;
		}
		canvas.drawCircle(dX, dY, mRadius, mPaintFill);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		boolean isEventConsumed = false;
		if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			final int count = mViewPager.getAdapter().getCount();
			final int longSize = (mOrientation == HORIZONTAL) ? getWidth() : getHeight();
			final float halfLongSize = longSize/2;
			final float halfCircleLongSize = (count*3*mRadius)/2;
			final float pointerValue = (mOrientation == HORIZONTAL) ? motionEvent.getX() : motionEvent.getY();
			
			if((mCurrentPage > 0) && (pointerValue < halfLongSize - halfCircleLongSize)) {
				setCurrentItem(mCurrentPage - 1);
				isEventConsumed = true;
			} else if((mCurrentPage < count - 1) && (pointerValue > halfLongSize + halfCircleLongSize)) {
				setCurrentItem(mCurrentPage + 1);
				isEventConsumed = true;
			}
		} else {
			isEventConsumed = super.onTouchEvent(motionEvent);
		}
		
		return isEventConsumed;
	}
	
	@Override
	public void setViewPager(ViewPager viewPager) {
		if(viewPager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter intance");
		}
		mViewPager = viewPager;
		mViewPager.setOnPageChangeListener(this);
		updatePageSize();
		invalidate();
	}
	
	private void updatePageSize() {
		if(mViewPager != null) {
			mPageSize = (mOrientation == HORIZONTAL) ? mViewPager.getWidth() : mViewPager.getHeight();
		}
	}
	
	@Override
	public void setViewPager(ViewPager viewPager, int initialPosition) {
		setViewPager(viewPager);
		setCurrentItem(initialPosition);
	}
	
	@Override
	public void setCurrentItem(int item) {
		if(mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound");
		}
		mViewPager.setCurrentItem(item);
		mCurrentPage = item;
		invalidate();
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
		mScrollState = state;
		if(mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		mCurrentPage = position;
		mCurrentOffset = positionOffsetPixels;
		updatePageSize();
		invalidate();
		
		if(mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}
	
	@Override
	public void onPageSelected(int position) {
		if(mIsSnapped || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
			mCurrentPage = position;
			mSnapPage = position;
			invalidate();
		}
		
		if(mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(position);
		}
	}
	
	@Override
	public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
		mOnPageChangeListener = onPageChangeListener;
	}

	@Override
	public void notifyDataSetChanged() {
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(mOrientation == HORIZONTAL) {
			setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
		} else {
			setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec));
		}
	}
	
	private int measureLong(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if(specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			if(mViewPager != null){
				final int count = mViewPager.getAdapter().getCount();
				result = (int)(getPaddingLeft() + getPaddingRight() + (count*2*mRadius) + (count -1)*mRadius + 1);
				if(specMode == MeasureSpec.AT_MOST) {
					result = Math.min(result, specSize);
				}
			}
		}
		
		return result;
	}
	
	private int measureShort(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if(specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int)(2*mRadius + getPaddingTop() + getPaddingBottom() +1);
			if(specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		
		return result;
	}
	
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState)state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mCurrentPage = savedState.currentPage;
		mSnapPage = savedState.currentPage;
		requestLayout();
	}
	
	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPage = mCurrentPage;
		return savedState;
	}
	
	static class SavedState
	extends BaseSavedState {
		int currentPage;
		
		public SavedState(Parcelable superState) {
			super(superState);
		}
		
		private SavedState(Parcel in) {
			super(in);
			currentPage = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPage);
		}
		
		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
			
			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
