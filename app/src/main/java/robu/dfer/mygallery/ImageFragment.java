package robu.dfer.mygallery;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * User: lgd(1973140289@qq.com)
 * Date: 2017-02-14
 * Function:
 */
public class ImageFragment extends Fragment {
    private static final String DRAWABLE_ID = "drawableId";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        Glide.with(this).load(getArguments().getInt(DRAWABLE_ID)).centerCrop().into(imageView);
        return v;
    }

    public static ImageFragment newInstance(@DrawableRes int drawable) {
        Bundle args = new Bundle();
        args.putInt(DRAWABLE_ID, drawable);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
