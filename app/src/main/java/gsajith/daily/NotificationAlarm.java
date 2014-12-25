package gsajith.daily;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.util.Date;

/**
 * Created by gsajith on 12/23/14.
 */
public class NotificationAlarm extends AlarmModel {
  String title;
  String message;
  int icon;
  private NotificationManager nm;
  public NotificationAlarm(Date date, AlarmType type, String title, String message, int icon) {
    super(date, type);
    this.title = title;
    this.message = message;
    this.icon = icon;
  }

  public void notify(Context context, Intent intent) {
    nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    PendingIntent contentIntent = PendingIntent.getActivity(context, getID(),
      intent, PendingIntent.FLAG_CANCEL_CURRENT);
    Resources res = context.getResources();
    Notification.Builder builder = new Notification.Builder(context);

    builder.setContentIntent(contentIntent)
      .setSmallIcon(icon)
      .setLargeIcon(BitmapFactory.decodeResource(res, icon))
      .setTicker(title+": " +message)
      .setWhen(System.currentTimeMillis())
      .setAutoCancel(true)
      .setContentTitle(title)
      .setContentText(message);
    Notification notif = builder.build();
    nm.notify(getID(), notif);
  }

  public void removeNotification(Context context) {
    nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.cancel(getID());
  }
}
