package com.liuguangqiang.bowevent.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.liuguangqiang.bowevent.BowEvent;
import com.liuguangqiang.bowevent.Subscribe;
import com.liuguangqiang.bowevent.sample.event.AEvent;
import com.liuguangqiang.bowevent.sample.event.TestEvent;

public class MainActivity extends BaseActivity {

  private static final String TAG = "BowEvent";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    BowEvent bowEvent = BowEvent.getInstance();
    bowEvent.register(this);

    FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    floatingActionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), TestActivity.class));
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BowEvent.getInstance().unregister(this);
  }

  @Subscribe(tag = "test")
  public void testByTag(AEvent event) {
    Log.i(TAG, "接收到结果 testByTag() : " + event.title);
  }

  @Subscribe
  public void test(TestEvent event) {
    Log.i(TAG, "接收到结果 test() : " + event.title);
  }

}
