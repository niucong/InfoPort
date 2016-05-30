package com.niucong.infoport.view;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.niucong.infoport.R;
import com.niucong.infoport.util.L;

public class RefreshListView extends ListView implements OnScrollListener {
	private final String TAG = "RefreshListView";

	private final int RELEASE_To_REFRESH = 0;
	private final int PULL_To_REFRESH = 1;
	private final int REFRESHING = 2;
	private final int DONE = 3;
	private final int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final int RATIO = 2;
	// 允许拉伸的最大的header的倍数
	private double MAXPULL = 2;

	private LayoutInflater inflater;

	private LinearLayout headView, footerView, defFooterView;
	private View tmpFooterView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;

	private ImageView arrowImageView;
	private ProgressBar progressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	// private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex, visibleCount;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;
	private OnSizeChangeListener sizeListener;
	private OnLoadMoreListener moreListener;

	public boolean isRefreshable;

	public RefreshListView(Context context) {
		super(context);
		init(context);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		setSelector(context.getResources().getDrawable(R.drawable.nothing));
		// setDivider(context.getResources().getDrawable(R.drawable.line1));
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.head, null);
		footerView = new LinearLayout(getContext());
		footerView.setOrientation(LinearLayout.VERTICAL);
		defFooterView = (LinearLayout) inflater.inflate(R.layout.foot, null);
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		// headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;

		setFastScrollEnabled(true);
		footerView.addView(defFooterView);
		addFooterView(footerView, null, false);
	}

	public void setFooter(View footer) {
		setFooter(footer, false);
	}

	public void setFooter(View footer, boolean isPaddingFooter) {
		if (getFooterViewsCount() <= 0) {
			addFooterView(footerView, null, false);
		}
		if (null == getFooterViewContent()) {
			if (!isPaddingFooter) {
				footerView.removeAllViews();
			}
			tmpFooterView = footer;
			footerView.addView(tmpFooterView);
			measureView(footerView);
			footerView.invalidate();
		}
	}

	public void addFooter(View footer, boolean isPaddingFooter) {
		if (getFooterViewsCount() <= 0) {
			addFooterView(footerView, null, false);
		}
		if (!isPaddingFooter) {
			footerView.removeAllViews();
		}
		tmpFooterView = footer;
		footerView.addView(tmpFooterView);
		measureView(footerView);
		footerView.invalidate();
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
		visibleCount = arg2;
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		switch (arg1) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			if (getAdapter().getCount() > 10
					&& firstItemIndex + visibleCount >= getAdapter().getCount() - 1) {
				if (null != moreListener) {
					moreListener.onLoadMore();
				}
			}
			L.d(TAG, "OnScrollListener.SCROLL_STATE_IDLE");
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			L.d(TAG, "OnScrollListener.SCROLL_STATE_TOUCH_SCROLL");
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			L.d(TAG, "OnScrollListener.SCROLL_STATE_FLING");
			break;
		}
	}

	public View getFooterViewContent() {
		return tmpFooterView;
	}

	public void removeFooterView() {
		removeFooterView(footerView);
	}

	public void showLoadFooter() {
		if (getFooterViewsCount() <= 0) {
			addFooterView(footerView, null, false);
		}
		if (null != tmpFooterView)
			tmpFooterView.setVisibility(View.GONE);
		defFooterView.setVisibility(View.VISIBLE);
		// ((TextView)
		// defFooterView.findViewById(R.id.text)).setText("正在加载中...");
		// defFooterView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
	}

	public void hideLoadFooter() {
		// ((TextView) defFooterView.findViewById(R.id.text)).setText("");
		// defFooterView.findViewById(R.id.progress).setVisibility(View.GONE);
		defFooterView.setVisibility(View.GONE);
	}

	public void showTempFooter() {
		// ((TextView) defFooterView.findViewById(R.id.text)).setText("");
		// defFooterView.findViewById(R.id.progress).setVisibility(View.GONE);
		defFooterView.setVisibility(View.GONE);
		tmpFooterView.setVisibility(View.VISIBLE);
	}

	public void hideTempFooter() {
		tmpFooterView.setVisibility(View.GONE);
	}

	public void showFinishLoadFooter() {
		defFooterView.setVisibility(View.GONE);
		// ((TextView) defFooterView.findViewById(R.id.text)).setText("");
		// defFooterView.findViewById(R.id.progress).setVisibility(View.GONE);
	}

	public void setFirstItem(int firstVisiableItem) {
		firstItemIndex = firstVisiableItem;
	}

	public int getFirstItem() {
		return firstItemIndex;
	}

	/**
	 * 开始加载页面
	 * 
	 * 是否刷新数据
	 */
	public void refreshUI() {
		isRefreshable = false;
		// state = REFRESHING;
		// changeHeaderViewByState();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在down时候记录当前位置  ");
				}
				break;

			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					// if (state == DONE) {
					// // 什么都不做
					// }
					// if (state == PULL_To_REFRESH) {
					// state = DONE;
					// changeHeaderViewByState();
					//
					// Log.v(TAG, "由下拉刷新状态，到done状态");
					// }
					// else if (state == RELEASE_To_REFRESH) {
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();

						Log.v(TAG, "由松开刷新状态，到done状态");
					} else {
						state = DONE;
						changeHeaderViewByState();

						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
					return true;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {
					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();

							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						int requestPadding = (tempY - startY) / RATIO;
						if (requestPadding <= MAXPULL * headContentHeight) {
							headView.setPadding(0, requestPadding
									- headContentHeight, 0, 0);
						} else {
							headView.setPadding(0, (int) (MAXPULL
									* headContentHeight - headContentHeight),
									0, 0);
						}
					}

				}

				break;
			}
		}
		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开刷新");

			// Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			// Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.icon_arrow);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public void setonSizeChangeListener(OnSizeChangeListener sizeChangeListener) {
		this.sizeListener = sizeChangeListener;
	}

	public void setonLoadMoreListener(OnLoadMoreListener loadMoreListener) {
		this.moreListener = loadMoreListener;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public interface OnLoadMoreListener {
		public void onLoadMore();
	}

	public interface OnSizeChangeListener {
		public void onResize(int w, int h, int oldw, int oldh);
	}

	@SuppressWarnings("deprecation")
	public void onRefreshComplete() {
		isRefreshable = true;
		state = DONE;
		lastUpdatedTextView.setText("最近更新："
				+ new Date().toLocaleString().substring(5));
		changeHeaderViewByState();
		if (getItemCount() < 10) {
			try {
				removeFooterView();
			} catch (NullPointerException e) {
				L.d(TAG, "removeFooterView NullPointerException");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void onRefreshComplete(boolean isRemoveFooter) {
		isRefreshable = true;
		state = DONE;
		lastUpdatedTextView.setText("最近更新："
				+ new Date().toLocaleString().substring(5));
		changeHeaderViewByState();
		if (isRemoveFooter) {
			try {
				removeFooterView();
			} catch (NullPointerException e) {
				L.d(TAG, "removeFooterView NullPointerException");
			}
		}
	}

	public int getItemCount() {
		Adapter ad = getAdapter();
		if (null != ad) {
			return ad.getCount();
		}
		return 0;
	}

	private void onRefresh() {
		L.i(TAG, "refresh");
		if (refreshListener != null) {
			refreshListener.onRefresh();
			showFinishLoadFooter();
		}
	}

	// 此方法是估算headView的width以及height
	@SuppressWarnings("deprecation")
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@SuppressWarnings("deprecation")
	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText("最近更新:"
				+ new Date().toLocaleString().substring(5));
		super.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (null != sizeListener) {
			sizeListener.onResize(w, h, oldw, oldh);
		}
	}

}
