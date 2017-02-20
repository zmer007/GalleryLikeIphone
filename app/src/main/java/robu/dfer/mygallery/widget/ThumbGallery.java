package robu.dfer.mygallery.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;

import java.util.List;

import robu.dfer.mygallery.R;
import robu.dfer.mygallery.widget.ThumbGalleryAdapter.GalleryVH;

/**
 * User: lgd(1973140289@qq.com)
 * Date: 2017-02-15
 * Function:
 */
public class ThumbGallery extends RecyclerView {
    private static final String TAG = "cccc";
    private static final float AMPLIFY_MULTIPLE = 2.5f;

    private View mLastView;
    private View mSnapView;

    boolean touched;
    boolean enableCallback;
    private final int mItemWidth;

    private List<Integer> mData;
    private LinearSnapHelper mLinearSnapHelper;
    private OnSelectListener mSelectListener;

    public ThumbGallery(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mItemWidth = getResources().getDimensionPixelOffset(R.dimen.gallery_item_width);
        mLinearSnapHelper = new LinearSnapHelper();
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mSelectListener != null && enableCallback) {
                    mSelectListener.onSelect(getCurrentPosition());
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    touched = true;
                    enableCallback = true;
                    restoreLastView();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE && touched) {
                    touched = false;
                    mSnapView = mLinearSnapHelper.findSnapView(getLayoutManager());
                    if (mLastView != mSnapView) {
                        amplifySnapView();
                        restoreLastView();
                    }
                    mLastView = mSnapView;
                }
            }
        });
    }

    private int getCurrentPosition() {
        return getChildAdapterPosition(mLinearSnapHelper.findSnapView(getLayoutManager())) - getItemsOffset();
    }

    private void amplifySnapView() {
        if (mSnapView == null) {
            return;
        }
        GalleryVH holder = (GalleryVH) getChildViewHolder(mSnapView);
        holder.amplifyImage((int) (mItemWidth * AMPLIFY_MULTIPLE));
        holder.imageView.setScaleType(ScaleType.FIT_CENTER);
        int realPosition = holder.getAdapterPosition() - getItemsOffset();
        if (realPosition >= 0 && realPosition < mData.size()) {
            Glide.with(getContext()).load(mData.get(realPosition)).override(400, 400).fitCenter().into(holder.imageView);
        }
    }

    private void restoreLastView() {
        if (mLastView == null) {
            return;
        }
        GalleryVH lastHolder = (GalleryVH) getChildViewHolder(mLastView);
        lastHolder.amplifyImage(mItemWidth);
        lastHolder.imageView.setScaleType(ScaleType.CENTER_CROP);
        int realPosition = lastHolder.getAdapterPosition() - getItemsOffset();
        if (realPosition >= 0 && realPosition < mData.size()) {
            Glide.with(getContext()).load(mData.get(realPosition)).override(400, 400).centerCrop().into(lastHolder.imageView);
        }
        mLastView = null;
    }

    public void movePositionTo(int position, boolean enableCallback) {
        View snap = mLinearSnapHelper.findSnapView(getLayoutManager());
        int deltaPosition = (position + getItemsOffset()) - getChildAdapterPosition(snap);
        if (deltaPosition == 0) {
            return;
        }

        restoreLastView();
        smoothScrollBy(deltaPosition * mItemWidth, 0);
        touched = true;
        this.enableCallback = enableCallback;
    }

    public void setData(List<Integer> data) {
        mData = data;
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getWidth() != 0) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setAdapter(new ThumbGalleryAdapter(mData, getItemsOffset()));
                }
            }
        });
    }

    private int getItemsOffset() {
        return getWidth() / mItemWidth / 2;
    }

    public void setSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }
}
