package com.liuguangqiang.bowevent.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.liuguangqiang.bowevent.BowEvent;
import com.liuguangqiang.bowevent.Subscribe;
import com.liuguangqiang.bowevent.sample.event.TestEvent;
import com.squareup.otto.Bus;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BowEvent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BowEvent.getInstance().register(this);
        Bus bus = new Bus();
        bus.register(this);
        bus.post("abc");
        bus.post(true);
        bus.post(123);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TestEvent event = new TestEvent();
        event.title = "test event";
//        BowEvent.getInstance().post("abc");
//        BowEvent.getInstance().post(event);
        BowEvent.getInstance().post(123456);
        BowEvent.getInstance().post(true);
    }

    @Subscribe
    public void test(String event) {
        Log.i(TAG, "test string : " + event);
    }

    @Subscribe
    public void test2(TestEvent event) {
        Log.i(TAG, "received event : " + event.title);
    }

    @Subscribe
    public void testInt(int event) {
        Log.i(TAG, "test int : " + event);
    }

    @Subscribe
    public void testBoolean(boolean b) {
        Log.i(TAG, "test int : " + b);
    }

}
