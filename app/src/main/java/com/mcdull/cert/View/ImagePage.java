package com.mcdull.cert.View;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.image.SmartImageView;
import com.mcdull.cert.R;

import java.util.LinkedList;

/**
 * Created by Begin on 15/11/24.
 */
public class ImagePage extends RelativeLayout {
    private Context context;
    private LinkedList<String> images;
    private int num;
    private LinkedList<View> views;
    private LinkedList<SmartImageView> smartImageViews;

    public ImagePage(Context context) {
        this(context, null);
    }

    public ImagePage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImagePage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setImages(LinkedList<String> list) {
        this.num = list.size();
        this.images = list;
        views = new LinkedList<>();
        smartImageViews = new LinkedList<>();
        addViewPage();
    }

    private void addViewPage() {
        ViewPager viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setBackgroundColor(0x99123321);
        LinearLayout layout = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(CENTER_HORIZONTAL);
        layoutParams.bottomMargin = 30;
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
        params.leftMargin = 20;
        for (int i = 0; i < num; i++) {
            if (num != 1) {
                View view = new View(context);
                view.setLayoutParams(params);
                if (i==0){
                    view.setBackgroundResource(R.drawable.circle);
                }else {
                    view.setBackgroundResource(R.drawable.circletm);
                }
                views.add(view);
                layout.addView(view);
            }
            SmartImageView smartImageView = new SmartImageView(context);
            smartImageView.setImageUrl(images.get(i));
            smartImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            smartImageViews.add(smartImageView);
        }
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new MyAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i=0;i<views.size();i++){
                    views.get(i).setBackgroundResource(R.drawable.circletm);
                }
                views.get(position).setBackgroundResource(R.drawable.circle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.addView(viewPager);
        this.addView(layout);
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (images == null)
                return 0;
            else
                return smartImageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(smartImageViews.get(position), 0);
            return smartImageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            smartImageViews.get(position).setDrawingCacheEnabled(true);
            if (smartImageViews.get(position).getDrawingCache()!=null)
                smartImageViews.get(position).getDrawingCache().recycle();
            smartImageViews.get(position).setDrawingCacheEnabled(false);
            container.removeView(smartImageViews.get(position));
        }
    }


}
