/*
 *  Copyright 2015 Eric Liu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.liuguangqiang.bowevent.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.liuguangqiang.bowevent.BowEvent;
import com.liuguangqiang.bowevent.Subscribe;
import com.liuguangqiang.bowevent.sample.event.AppEvent;
import com.liuguangqiang.bowevent.sample.event.TestEvent;

public class TestActivity extends AppCompatActivity {

  private String TAG = "BowEvent";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    BowEvent.getInstance().register(this);
    BowEvent.getInstance().post(new TestEvent("1212121212"));

    Button btnClose = (Button) findViewById(R.id.btn_close);
    btnClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i("BowEvent", "post close all");
        AppEvent event = new AppEvent();
        event.close = true;
        BowEvent.getInstance().post(event);
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BowEvent.getInstance().unregister(this);
  }

  @Subscribe
  public void testEvent(TestEvent event) {
    Log.i(TAG, "TestActivity 接收到结果 test() : " + event.title);
  }

}
