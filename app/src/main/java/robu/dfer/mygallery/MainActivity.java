package robu.dfer.mygallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import robu.dfer.mygallery.event.IntegerValueEvent;
import robu.dfer.mygallery.event.PositionEvent;
import robu.dfer.mygallery.widget.ThumbGallery;
import robu.dfer.mygallery.widget.ThumbGallery.OnSelectListener;

public class MainActivity extends AppCompatActivity {
    private static List<Integer> data = Arrays.asList(
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,
            R.drawable.f,
            R.drawable.g,
            R.drawable.h,
            R.drawable.i,
            R.drawable.j,
            R.drawable.k,
            R.drawable.l);

    private static final String TAG = "MainActivity";

    ThumbGallery mThumbGallery;

    private ViewPager mViewPager;
    private List<ImageFragment> mImageFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mThumbGallery = (ThumbGallery) findViewById(R.id.recyclerView);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mThumbGallery.setData(data);
        mThumbGallery.setSelectListener(new OnSelectListener() {
            @Override
            public void onSelect(int position) {
                mViewPager.setCurrentItem(position, false);
            }
        });

        mImageFragments = new ArrayList<>(data.size() + 2);
        for (int drawableId : data) {
            mImageFragments.add(ImageFragment.newInstance(drawableId));
        }
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mImageFragments.get(position);
            }

            @Override
            public int getCount() {
                return mImageFragments.size();
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                mThumbGallery.movePositionTo(position, false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void moveGalleryPositionTo(PositionEvent positionEvent) {
        mThumbGallery.movePositionTo(positionEvent.position, true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scrollGalleryBy(IntegerValueEvent mEvent) {
        mThumbGallery.scrollBy(mEvent.x / 2, 0);
    }

}
