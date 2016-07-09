package com.github.gfranks.fitfam.util;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.ImageView;

import com.github.gfranks.fitfam.BuildConfig;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.data.model.FFGymPhoto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GymUtils {

    public static String getRandomGymPhoto(Context context, List<FFGymPhoto> photos) {
        String photoUrl = null;
        if (photos != null && !photos.isEmpty()) {
            FFGymPhoto photo = photos.get(new Random().nextInt(photos.size()));
            StringBuilder sb = new StringBuilder(getDefaultPhotoUrl(context, photo));
            sb.append("&maxwidth=");
            sb.append(PicassoUtils.IMAGE_SCALED_SIZE);
            photoUrl = sb.toString();
        }

        return photoUrl;
    }

    public static List<String> getScaledGymPhotos(Context context, List<FFGymPhoto> photos) {
        List<String> photoUrls = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (FFGymPhoto photo : photos) {
                StringBuilder sb = new StringBuilder(getDefaultPhotoUrl(context, photo));
                sb.append("&maxwidth=");
                sb.append(PicassoUtils.IMAGE_SCALED_SIZE);
                photoUrls.add(sb.toString());
            }
        }

        return photoUrls;
    }

    public static List<String> getFullScreenGymPhotos(Context context, List<FFGymPhoto> photos) {
        List<String> photoUrls = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (FFGymPhoto photo : photos) {
                StringBuilder sb = new StringBuilder(getDefaultPhotoUrl(context, photo));
                sb.append("&maxwidth=");
                sb.append(PicassoUtils.IMAGE_LARGE_SIZE);
                photoUrls.add(sb.toString());
            }
        }

        return photoUrls;
    }

    private static String getDefaultPhotoUrl(Context context, FFGymPhoto photo) {
        StringBuilder sb = new StringBuilder(BuildConfig.API_GOOGLE_PLACES);
        sb.append("/maps/api/place/photo?");
        sb.append("photoreference=");
        sb.append(photo.getPhoto_reference());
        sb.append("&key=");
        sb.append(context.getString(R.string.api_places_key));
        return sb.toString();
    }

    public static void adjustImageViewsForRating(Context context, float rating, ImageView[] ivs) {
        for (ImageView iv : ivs) {
            if (rating >= 1f) {
                iv.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                iv.setImageDrawable(Utils.applyDrawableTint(context, R.drawable.ic_star,
                        ContextCompat.getColor(context, R.color.yellow)));
            } else if (rating > 0f) {
                iv.setBackground(Utils.applyDrawableTint(context, R.drawable.ic_star,
                        ContextCompat.getColor(context, R.color.theme_icon_color_light)));
                iv.setImageDrawable(new ClipDrawable(Utils.applyDrawableTint(context, R.drawable.ic_star,
                        ContextCompat.getColor(context, R.color.yellow)), Gravity.START, ClipDrawable.HORIZONTAL));
                iv.getDrawable().setLevel((int) (10000 * rating));
            } else {
                iv.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                iv.setImageDrawable(Utils.applyDrawableTint(context, R.drawable.ic_star,
                        ContextCompat.getColor(context, R.color.theme_icon_color_light)));
            }
            --rating;
        }
    }
}
