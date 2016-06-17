package com.github.gfranks.workoutcompanion.util;

import android.content.Context;

import com.github.gfranks.workoutcompanion.BuildConfig;
import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.data.model.WCGymPhoto;

import java.util.ArrayList;
import java.util.List;

public class GymPhotoHelper {

    public static List<String> getGymPhotos(Context context, List<WCGymPhoto> photos) {
        List<String> photoUrls = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (WCGymPhoto photo : photos) {
                StringBuilder sb = new StringBuilder(BuildConfig.API_GOOGLE_PLACES);
                sb.append("/maps/api/place/photo?maxwidth=");
                sb.append(PicassoUtils.IMAGE_SCALED_SIZE);
                sb.append("&photoreference=");
                sb.append(photo.getPhoto_reference());
                sb.append("&key=");
                sb.append(context.getString(R.string.api_places_key));
                photoUrls.add(sb.toString());
            }
        }

        return photoUrls;
    }
}
