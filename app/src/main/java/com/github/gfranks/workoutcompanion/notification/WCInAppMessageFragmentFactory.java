package com.github.gfranks.workoutcompanion.notification;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.gfranks.workoutcompanion.R;
import com.urbanairship.json.JsonMap;
import com.urbanairship.push.iam.InAppMessage;
import com.urbanairship.push.iam.InAppMessageFragment;
import com.urbanairship.push.iam.InAppMessageFragmentFactory;
import com.urbanairship.push.iam.view.BannerCardView;

public class WCInAppMessageFragmentFactory extends InAppMessageFragmentFactory {

    @Override
    public InAppMessageFragment createFragment(InAppMessage inAppMessage) {
        return new LPInAppMessageFragment();
    }

    public static class LPInAppMessageFragment extends InAppMessageFragment {

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            View v = view.findViewById(R.id.in_app_message);
            if (v instanceof BannerCardView) {
                JsonMap map = getMessage().getExtras();
                WCInAppMessageManagerConstants.MessageType type = WCInAppMessageManagerConstants.MessageType.DEFAULT;
                if (map.containsKey(WCInAppMessageManagerConstants.TYPE)) {
                    type = WCInAppMessageManagerConstants.MessageType.parse(map.get(WCInAppMessageManagerConstants.TYPE).toString());
                }

                int cardBackgroundColor = R.color.blue;
                int textColor = R.color.gray_super_lighter;
                switch (type) {
                    case ERROR:
                        cardBackgroundColor = R.color.red;
                        textColor = R.color.white;
                        break;
                    case SUCCESS:
                        cardBackgroundColor = R.color.green;
                        textColor = R.color.theme_default_text;
                        break;
                    case INFO:
                        cardBackgroundColor = R.color.orange;
                        textColor = R.color.white;
                        break;
                    case WARNING:
                        cardBackgroundColor = R.color.yellow;
                        textColor = R.color.theme_default_text;
                        break;
                }

                ((BannerCardView) v).setPrimaryColor(ContextCompat.getColor(getActivity(), cardBackgroundColor));
                ((BannerCardView) v).setSecondaryColor(ContextCompat.getColor(getActivity(), textColor));
            }

            if (getMessage().getPosition() == InAppMessage.POSITION_TOP) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + ((int) (60.0F * view.getContext().getResources().getDisplayMetrics().density)),
                        view.getPaddingRight(), view.getPaddingRight());
            }
        }
    }
}
