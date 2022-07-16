/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tests.apex.app;

import static com.google.common.truth.Truth.assertThat;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.test.platform.app.InstrumentationRegistry;

import com.android.cts.install.lib.Install;
import com.android.cts.install.lib.InstallUtils;
import com.android.cts.install.lib.TestApp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VendorApexTests {

    private static final String TAG = "VendorApexTests";

    private static final String PACKAGE_NAME = "com.android.apex.vendor.foo";
    private static final TestApp Apex2Rebootless = new TestApp(
            "com.android.apex.vendor.foo.v2", PACKAGE_NAME, 2,
            /*isApex*/true, "com.android.apex.vendor.foo.v2.apex");

    @Test
    public void testRebootlessUpdate() throws Exception {
        InstallUtils.dropShellPermissionIdentity();
        InstallUtils.adoptShellPermissionIdentity(Manifest.permission.INSTALL_PACKAGE_UPDATES);

        final PackageManager pm =
                InstrumentationRegistry.getInstrumentation().getContext().getPackageManager();
        {
            PackageInfo apex = pm.getPackageInfo(PACKAGE_NAME, PackageManager.MATCH_APEX);
            assertThat(apex.getLongVersionCode()).isEqualTo(1);
            assertThat(apex.applicationInfo.sourceDir).startsWith("/vendor/apex");
        }

        Install.single(Apex2Rebootless).commit();

        {
            PackageInfo apex = pm.getPackageInfo(PACKAGE_NAME, PackageManager.MATCH_APEX);
            assertThat(apex.getLongVersionCode()).isEqualTo(2);
            assertThat(apex.applicationInfo.sourceDir).startsWith("/data/apex/active");
        }
    }
}
