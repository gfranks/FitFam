package com.github.gfranks.workoutcompanion.util;

import android.content.Context;

import com.github.gfranks.workoutcompanion.BuildConfig;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCGymPhoto;

import java.util.ArrayList;
import java.util.List;

public class GymPhotoHelper {

    public static List<String> getScaledGymPhotos(Context context, List<WCGymPhoto> photos) {
        List<String> photoUrls = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (WCGymPhoto photo : photos) {
                StringBuilder sb = new StringBuilder(getDefaultPhotoUrl(context, photo));
                sb.append("&maxwidth=");
                sb.append(PicassoUtils.IMAGE_SCALED_SIZE);
                photoUrls.add(sb.toString());
            }
        }

        return photoUrls;
    }

    public static List<String> getFullScreenGymPhotos(Context context, List<WCGymPhoto> photos) {
        List<String> photoUrls = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (WCGymPhoto photo : photos) {
                StringBuilder sb = new StringBuilder(getDefaultPhotoUrl(context, photo));
                sb.append("&maxwidth=");
                sb.append(PicassoUtils.IMAGE_LARGE_SIZE);
                photoUrls.add(sb.toString());
            }
        }

        return photoUrls;
    }

    private static String getDefaultPhotoUrl(Context context, WCGymPhoto photo) {
        StringBuilder sb = new StringBuilder(BuildConfig.API_GOOGLE_PLACES);
        sb.append("/maps/api/place/photo?");
        sb.append("photoreference=");
        sb.append(photo.getPhoto_reference());
        sb.append("&key=");
        sb.append(context.getString(R.string.api_places_key));
        return sb.toString();
    }
}
