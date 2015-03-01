package com.github.gfx.hatebulet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.gfx.hatebulet.api.HatebuEntry;
import com.github.gfx.hatebulet.api.HatebuFeedConverter;
import com.github.gfx.hatebulet.api.HatebuFeedService;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class MainActivity extends Activity {
    static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        OkHttpClient httpClient = new OkHttpClient();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(httpClient))
                .setEndpoint("http://b.hatena.ne.jp/")
                .setConverter(new HatebuFeedConverter())
                .build();
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        restAdapter.create(HatebuFeedService.class).getEntries(new Callback<List<HatebuEntry>>() {
            @Override
            public void success(List<HatebuEntry> hatebuEntries, Response response) {
                Log.d(TAG, hatebuEntries.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.wtf(TAG, error);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
