package robu.dfer.mygallery.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import robu.dfer.mygallery.event.IntegerValueEvent;
import robu.dfer.mygallery.event.PositionEvent;
import robu.dfer.mygallery.R;
import robu.dfer.mygallery.widget.ThumbGalleryAdapter.GalleryVH;

/**
 * User: lgd(1973140289@qq.com)
 * Date: 2017-02-13
 * Function:
 */
class ThumbGalleryAdapter extends RecyclerView.Adapter<GalleryVH> {

    private static final int OFFSET_HOLDER = -1;

    private Context mContext;
    private final int mOffset;
    private List<Integer> mData;

    ThumbGalleryAdapter(List<Integer> data, int offset) {
        mData = data;
        mOffset = offset;
    }

    @Override
    public GalleryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image, parent, false);
        return new GalleryVH(view);
    }

    @Override
    public void onBindViewHolder(final GalleryVH holder, int position) {
        if (getItemViewType(position) != OFFSET_HOLDER) {
            Glide.with(mContext).load(mData.get(position - mOffset)).centerCrop().into(holder.imageView);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new PositionEvent(holder.getAdapterPosition() - mOffset));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + mOffset * 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mOffset || position > mOffset + mData.size() - 1) {
            return OFFSET_HOLDER;
        }
        return super.getItemViewType(position);
    }

    class GalleryVH extends RecyclerView.ViewHolder {
        ImageView imageView;

        GalleryVH(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

        private int lastX;

        void amplifyImage(int width) {
            if (width != itemView.getWidth()) {
                final RecyclerView.LayoutParams params;
                if (itemView.getLayoutParams() == null) {
                    params = new RecyclerView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    itemView.setLayoutParams(params);
                } else {
                    params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                }
                lastX = itemView.getWidth();
                ValueAnimator anim = ValueAnimator.ofInt(lastX, width);
                anim.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        params.width = (int) animation.getAnimatedValue();
                        EventBus.getDefault().post(new IntegerValueEvent(params.width - lastX));
                        lastX = params.width;
                        itemView.requestLayout();
                    }
                });
                anim.setDuration(200);
                anim.start();
            }
        }
    }
}