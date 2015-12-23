package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by matt on 12/22/15.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class FootballScoresWidgetService extends RemoteViewsService {

    private static final String TAG = FootballScoresWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FootballScoresRemoteViewFactory(getApplicationContext(), intent);
    }

    private class FootballScoresRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private Cursor cursor;

        public FootballScoresRemoteViewFactory(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            if (this.cursor != null) {
                this.cursor.close();
            }

            Date fragmentdate = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = format.format(fragmentdate);

            cursor = context.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                    null, null, new String[]{formattedDate}, null);

            Log.d(TAG, "onDataSetChanged: cursor");
        }

        @Override
        public void onDestroy() {
            if (this.cursor != null) {
                this.cursor.close();
            }
        }

        @Override
        public int getCount() {
            return (cursor != null) ? cursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.d(TAG, "getViewAt: GET VIEW");
            cursor.moveToPosition(i);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_scores_list_item);
            rv.setTextViewText(R.id.home_name, cursor.getString(cursor.getColumnIndex("home")));
            rv.setTextViewText(R.id.away_name, cursor.getString(cursor.getColumnIndex("away")));
            String score = cursor.getString(cursor.getColumnIndex("home_goals")) + " - " + cursor.getString(cursor.getColumnIndex("away_goals"));
            rv.setTextViewText(R.id.score_textview,  score);
            rv.setTextViewText(R.id.data_textview, cursor.getString(cursor.getColumnIndex("time")));
            rv.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(cursor.getString(cursor.getColumnIndex("home"))));
            rv.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(cursor.getString(cursor.getColumnIndex("away"))));

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}