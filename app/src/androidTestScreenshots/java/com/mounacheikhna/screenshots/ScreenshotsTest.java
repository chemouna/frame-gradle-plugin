package com.mounacheikhna.screenshots;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.squareup.spoon.Spoon;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * @author cheikhnamouna.
 */
@RunWith(AndroidJUnit4.class)
public class ScreenshotsTest {

  @Rule public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

  @Before public void setUp() throws Exception {
  }

  @Test public void screenshotsTest() throws Exception {
    String locale = InstrumentationRegistry.getArguments().getString("locale");
    onView(withId(R.id.fab)).perform(click());
    Spoon.screenshot(activityRule.getActivity(), locale + "_from_screenshots");
  }


}
