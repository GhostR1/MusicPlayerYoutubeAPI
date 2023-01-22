package nataliia.semenova.musicplayeryoutubeapi.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import nataliia.semenova.musicplayeryoutubeapi.R;
import nataliia.semenova.musicplayeryoutubeapi.data.model.Song;
import nataliia.semenova.musicplayeryoutubeapi.service.NotificationService;

public class CreateNotification {
    public static final String CHANNEL_ID = "666";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification;

    public static void createNotification(Context context, Song song) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat( context, "tag");


            ImageView ivCover = new ImageView(context);
            ivCover.setImageURI(song.getCoverUri());
            BitmapDrawable drawable = (BitmapDrawable) ivCover.getDrawable();
            Bitmap bitmapCover;
            if(drawable == null) {
                bitmapCover = BitmapFactory.decodeResource(context.getResources(), R.drawable.song_cover);
            } else {
                bitmapCover = drawable.getBitmap();
            }
            PendingIntent pendingIntentPrevious;
            int drw_previous = R.drawable.previous;
            Intent intentPrevious = new Intent(context, NotificationService.class)
                        .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                        intentPrevious, PendingIntent.FLAG_IMMUTABLE);

            Intent intentPlay = new Intent(context, NotificationService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentNext;
            int drw_next = R.drawable.next;
                Intent intentNext = new Intent(context, NotificationService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                        intentNext, PendingIntent.FLAG_IMMUTABLE);

            //create notification
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setLargeIcon(bitmapCover)
                    .setSmallIcon(R.id.iv_song_cover)
                    .setContentTitle(song.getTitle())
                    .setContentText(song.getArtist())
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(R.drawable.pause_play, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(127, notification);

        }
    }
}
