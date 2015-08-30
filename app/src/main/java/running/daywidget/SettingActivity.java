package running.daywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class SettingActivity extends Activity implements View.OnClickListener {
    DayWidget dayWidget;
    private ImageView mSleepIV, mAngerIV;
    private int appWidgetId;
    private static final String DAY_WEIGHT = "day_weight";
//    private static final String MOOD_TYPE = "mood_type";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        dayWidget = new DayWidget();
        mSleepIV = (ImageView) findViewById(R.id.sleepy_iv);
        mAngerIV = (ImageView)findViewById(R.id.angary_iv);
        mSleepIV.setOnClickListener(this);
        mAngerIV.setOnClickListener(this);

    }

    public static void saveWidgetData(Context context, int appWidgetId, int moodType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DAY_WEIGHT, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DAY_WEIGHT + appWidgetId, moodType);
        editor.commit();
    }

    public static int getMoodType(Context context, int appWidgetId, int defaultMoodType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DAY_WEIGHT, Activity.MODE_PRIVATE);
        int modeype = sharedPreferences.getInt(DAY_WEIGHT + appWidgetId, defaultMoodType);
        return modeype;
    }

    @Override
    public void onClick(View view) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SettingActivity.this);
        Intent resultIntent = new Intent();
        switch (view.getId()) {
            case R.id.sleepy_iv:
                saveWidgetData(SettingActivity.this, appWidgetId, R.id.sleepy_iv);
                resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultIntent);
                DayWidget.startTimer(SettingActivity.this, appWidgetManager, appWidgetId, R.id.sleepy_iv);
                finish();
                break;
            case R.id.angary_iv:
                saveWidgetData(SettingActivity.this, appWidgetId, R.id.angary_iv);
                resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultIntent);
                DayWidget.startTimer(SettingActivity.this, appWidgetManager, appWidgetId, R.id.angary_iv);
                finish();
                break;
        }
    }
}
