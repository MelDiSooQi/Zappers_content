package com.zapper;

import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.zapper.view.GetStartedActivity;
import com.zapper.view.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by MelDiSooQi on 3/27/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class Testing {
    @Rule
    public ActivityTestRule<GetStartedActivity> activityTestRule =
            new ActivityTestRule<GetStartedActivity>(GetStartedActivity.class);

    @Test
    public void testLoginWithIdAndPass() {
        Intents.init();
        onView(withId(R.id.btn_letsGo)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
        Intents.init();


       // onView(withId(R.id.fab)).perform(click());

    }


}