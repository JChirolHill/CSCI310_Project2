package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("projects.chirolhill.juliette.csci310_project2", appContext.getPackageName());
    }
}