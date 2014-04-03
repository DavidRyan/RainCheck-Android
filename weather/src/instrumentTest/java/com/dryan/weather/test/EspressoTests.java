package com.dryan.weather.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EspressoTests extends TestSuite {
    public static Test suite(){
        return new TestSuiteBuilder(EspressoTests.class)
                .includeAllPackagesUnderHere()
                .build();
    }
}
