package com.mounacheikhna.screenshots;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.squareup.spoon.Spoon;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author cheikhnamouna.
 */
@RunWith(AndroidJUnit4.class)
public class MainTest {

  @Rule ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

  @Before public void setUp() throws Exception {
  }

  @Test public void sampleTest() throws Exception {
    Spoon.screenshot(activityRule.getActivity(), "firsttag");
  }

}
