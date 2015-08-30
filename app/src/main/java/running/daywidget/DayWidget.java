package running.daywidget;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DayWidget extends AppWidgetProvider {

    static final Intent DONE_SERVICE_INTENT = new Intent(
            "running.daywidget.DAY_WIDGET_SERVICE");
    private final static String UPDATE_CONTENT = "running.daywidget.UPDATE_CONTENT";
    private AppWidgetManager dayWidgetManager;
//    private Timer timer;
    private static int[] img = {R.drawable.amazed_big, R.drawable.angry_big,
            R.drawable.anxious_big, R.drawable.happy_big, R.drawable.sleepy_big, R.drawable.weepy_big};

    private AppWidgetManager appWidgetManager;
    private static Map<Integer, Timer> timers;

    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
                newOptions);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        if(timers != null){
            for(int i = 0; i < appWidgetIds.length; i++){
                Timer timer = timers.get(appWidgetIds[i]);
                if(timer != null){
                    timer.cancel();
                    Toast.makeText(context,"此Widget已被移除", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(DONE_SERVICE_INTENT);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(DONE_SERVICE_INTENT);
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        int n = appWidgetIds.length;
        if(timers == null){
            timers = new HashMap<Integer, Timer>();
        }
        for(int i = 0 ; i < n; i++){
            int moodId = SettingActivity.getMoodType(context, appWidgetIds[i], R.id.sleepy_iv);
            if(timers.get(appWidgetIds[i]) != null){
                timers.get(appWidgetIds[i]).cancel();
            }
            Message message = new Message();
            message.arg1 = appWidgetIds[i];
            message.arg2 = moodId;
            updateView(context, appWidgetManager , appWidgetIds[i], message);
            startTimer(context, appWidgetManager, appWidgetIds[i], moodId);
        }
    }


    private static void updateView(Context context, AppWidgetManager appWidgetManager, int appWidgetId,Message msg) {
        RemoteViews views = null;
        Intent fullIntent = new Intent(context, SettingActivity.class);
        fullIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                fullIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        int index = new Random().nextInt(img.length);
        if (msg.arg2 == R.id.sleepy_iv) {
            views = new RemoteViews(context.getPackageName(), R.layout.day_widget);
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.day_widget2);
        }
        views.setImageViewResource(R.id.widget_content, img[index]);
        views.setOnClickPendingIntent(R.id.widget_content,
                pendingIntent);
        appWidgetManager.updateAppWidget(msg.arg1, views);
    }


    public static void startTimer(Context context, AppWidgetManager appWidgetManger, int appWidgetId, int moodType) {
        if(timers == null){
            timers = new HashMap<Integer, Timer>();
        }
        if (timers.get(appWidgetId) != null) {
            timers.get(appWidgetId).cancel();
        }
        TimerTask timerTask = getTimerTask(context, appWidgetManger, appWidgetId , moodType);
        Timer timer = new Timer();
        timer.schedule(timerTask, 0 , 1000);
        timers.put(appWidgetId , timer);
    }

    private static Handler getHandler(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                updateView(context, appWidgetManager, appWidgetId, msg);
                super.handleMessage(msg);
            }
        };
        return  handler;
    }

    private static TimerTask getTimerTask(final Context context,AppWidgetManager appWidgetManager ,final int appWidgetId, final int moodType) {
        final Handler handler = getHandler(context, appWidgetManager, appWidgetId);
        return new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.arg1 = appWidgetId;
                message.arg2 = moodType;
                handler.sendMessage(message);
            }
        };
    }

}
