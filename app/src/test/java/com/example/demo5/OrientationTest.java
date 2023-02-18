package com.example.demo5;

import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class OrientationTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void test_orientation_service() {
        var testValue = Constants.SOUTH;

        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            var orientationService = OrientationService.singleton(activity);

            var mockOrientation = new MutableLiveData<Float>();
            orientationService.setMockOrientationData(mockOrientation);
            // We don't want to have to do this! It's not our job to tell the activity!
            activity.reobserveOrientation();

            mockOrientation.setValue(testValue);
            //TextView textView = activity.findViewById(R.id.orientationText);

            var expected = Utilities.formatOrientation(testValue);
            //var observed = textView.getText().toString();
            var observed = "";
            assertEquals(expected, observed);
        });
    }
}