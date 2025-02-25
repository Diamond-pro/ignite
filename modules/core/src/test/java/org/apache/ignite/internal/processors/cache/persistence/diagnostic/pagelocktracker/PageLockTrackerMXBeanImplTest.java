/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache.persistence.diagnostic.pagelocktracker;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.ignite.Ignite;
import org.apache.ignite.internal.processors.cache.CacheDiagnosticManager;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Assert;
import org.junit.Test;

import static javax.management.MBeanServerInvocationHandler.newProxyInstance;

/**
 * {@link PageLockTrackerMXBean} test.
 */
public class PageLockTrackerMXBeanImplTest extends GridCommonAbstractTest {
    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        super.beforeTest();

        stopAllGrids();
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        super.afterTest();

        stopAllGrids();
    }

    /**
     * Simple chekc that mxbean is registrated on node start.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testSimple() throws Exception {
        Ignite ig = startGrid();

        PageLockTrackerMXBean pageLockTrackerMXBean = getPageLockTrackerMXBean(ig);

        Assert.assertNotNull(pageLockTrackerMXBean);
    }

    /**
     * @param ignite Ignite.
     */
    private PageLockTrackerMXBean getPageLockTrackerMXBean(Ignite ignite) throws Exception {
        ObjectName mBeanName = U.makeMBeanName(
            ignite.name(),
            CacheDiagnosticManager.MBEAN_GROUP,
            PageLockTrackerMXBean.MBEAN_NAME
        );

        MBeanServer mBeanSrv = ManagementFactory.getPlatformMBeanServer();

        if (!mBeanSrv.isRegistered(mBeanName))
            fail("MBean is not registered: " + mBeanName.getCanonicalName());

        return newProxyInstance(mBeanSrv, mBeanName, PageLockTrackerMXBean.class, true);
    }
}