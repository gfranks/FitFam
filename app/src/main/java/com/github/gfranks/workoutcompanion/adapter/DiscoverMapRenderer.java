package com.github.gfranks.workoutcompanion.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCGym;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

public class DiscoverMapRenderer extends DefaultClusterRenderer<WCGym> {

    private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;

    private float mDensity;
    private ShapeDrawable mClusterDrawable;
    private ShapeDrawable mMarkerDrawable;
    private int mClusterIconColor;
    private int mMarkerIconColor;

    public DiscoverMapRenderer(Context context, GoogleMap map, ClusterManager<WCGym> clusterManager) {
        super(context, map, clusterManager);

        mDensity = context.getResources().getDisplayMetrics().density;

        mIconGenerator = new IconGenerator(context);
        mIconGenerator.setBackground(makeMarkerBackground(context));
        mIconGenerator.setContentView(makeMarkerSquareTextView(context));

        mClusterIconGenerator = new IconGenerator(context);
        mClusterIconGenerator.setBackground(makeClusterBackground(context));
        mClusterIconGenerator.setContentView(makeClusterSquareTextView(context));
        mClusterIconGenerator.setTextAppearance(R.style.DefaultAppTheme_ClusterTextAppearance);

        mClusterIconColor = ContextCompat.getColor(context, R.color.theme_primary);
        mMarkerIconColor = ContextCompat.getColor(context, R.color.red);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<WCGym> cluster, MarkerOptions markerOptions) {
        mClusterDrawable.getPaint().setColor(mClusterIconColor);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()))));
    }

    @Override
    protected void onBeforeClusterItemRendered(WCGym item, MarkerOptions markerOptions) {
        mMarkerDrawable.getPaint().setColor(mMarkerIconColor);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon()));
        markerOptions.title(item.getName());
        markerOptions.snippet(item.getVicinity());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }

    private Drawable makeClusterBackground(Context context) {
        mClusterDrawable = new ShapeDrawable(new OvalShape());
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(ContextCompat.getColor(context, R.color.white_translucent));
        LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mClusterDrawable});
        int strokeWidth = (int) (mDensity * 3.0F);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }

    private Drawable makeMarkerBackground(Context context) {
        mMarkerDrawable = new ShapeDrawable(new OvalShape());
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(ContextCompat.getColor(context, R.color.white_translucent));
        LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mMarkerDrawable});
        int strokeWidth = (int) (2.5F * mDensity);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }

    private SquareTextView makeClusterSquareTextView(Context context) {
        SquareTextView squareTextView = getSquareTextView(context);
        int twelveDpi = (int) (12.0F * this.mDensity);
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
        return squareTextView;
    }

    private SquareTextView makeMarkerSquareTextView(Context context) {
        SquareTextView squareTextView = getSquareTextView(context);
        int threeDpi = (int) (3.0F * this.mDensity);
        squareTextView.setPadding(threeDpi, threeDpi, threeDpi, threeDpi);
        return squareTextView;
    }

    private SquareTextView getSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(com.google.maps.android.R.id.text);
        return squareTextView;
    }
}
