/*
 *******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.eclipse.microprofile.fault.tolerance.tck.bulkhead.lifecycle;

import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.eclipse.microprofile.fault.tolerance.tck.bulkhead.lifecycle.AsyncInvoker.assertFutureFailsWith;

/**
 * Test that bulkhead is a singleton, even if the bean is not.
 */
public class BulkheadLifecycleTest extends Arquillian {
    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
                .create(JavaArchive.class, "ftBulkheadLifecycle.jar")
                .addPackage(BulkheadLifecycleService1.class.getPackage())
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .as(JavaArchive.class);

        return ShrinkWrap
                .create(WebArchive.class, "ftBulkheadLifecycle.war")
                .addAsLibrary(testJar);
    }

    // verify that bulkhead is shared between instances of the same class and for the same method,
    // but not shared between different classes and different methods of the same class

    @Inject
    private AsyncInvoker async;

    @Inject
    private Instance<BulkheadLifecycleService1> service1;

    @Inject
    private Instance<BulkheadLifecycleService2> service2;

    @Inject
    private Instance<MutlipleMethodsBulkheadLifecycleService> multipleMethodsService;

    @Test
    public void noSharingBetweenClasses() throws InterruptedException, ExecutionException, TimeoutException {
        Barrier barrier = new Barrier();

        BulkheadLifecycleService1 service1a = service1.get();
        BulkheadLifecycleService1 service1b = service1.get();

        BulkheadLifecycleService2 service2a = service2.get();
        BulkheadLifecycleService2 service2b = service2.get();

        List<Future<Void>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 4; i++) {
                futures.add(async.run(() -> service1a.service(barrier)));
                futures.add(async.run(() -> service2a.service(barrier)));
                futures.add(async.run(() -> service1b.service(barrier)));
                futures.add(async.run(() -> service2b.service(barrier)));
            }

            assertFutureFailsWith(BulkheadException.class, async.run(() -> service1a.service(barrier)));
            assertFutureFailsWith(BulkheadException.class, async.run(() -> service2a.service(barrier)));
            assertFutureFailsWith(BulkheadException.class, async.run(() -> service1b.service(barrier)));
            assertFutureFailsWith(BulkheadException.class, async.run(() -> service2b.service(barrier)));
        }
        finally {
            try {
                barrier.open();

                for (Future<Void> future : futures) {
                    future.get(1, TimeUnit.MINUTES);
                }
            }
            finally {
                service1.destroy(service1a);
                service1.destroy(service1b);
                service2.destroy(service2a);
                service2.destroy(service2b);
            }
        }
    }

    @Test
    public void noSharingBetweenMethodsOfOneClass() throws InterruptedException, ExecutionException, TimeoutException {
        Barrier barrier = new Barrier();

        MutlipleMethodsBulkheadLifecycleService multipleMethodsService1 = multipleMethodsService.get();
        MutlipleMethodsBulkheadLifecycleService multipleMethodsService2 = multipleMethodsService.get();

        List<Future<Void>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 4; i++) {
                futures.add(async.run(() -> multipleMethodsService1.service1(barrier)));
                futures.add(async.run(() -> multipleMethodsService1.service2(barrier)));
                futures.add(async.run(() -> multipleMethodsService2.service1(barrier)));
                futures.add(async.run(() -> multipleMethodsService2.service2(barrier)));
            }

            assertFutureFailsWith(BulkheadException.class, async.run(() -> multipleMethodsService1.service1(barrier)));
            assertFutureFailsWith(BulkheadException.class, async.run(() -> multipleMethodsService1.service2(barrier)));
            assertFutureFailsWith(BulkheadException.class, async.run(() -> multipleMethodsService2.service1(barrier)));
            assertFutureFailsWith(BulkheadException.class, async.run(() -> multipleMethodsService2.service2(barrier)));
        }
        finally {
            try {
                barrier.open();

                for (Future<Void> future : futures) {
                    future.get(1, TimeUnit.MINUTES);
                }
            }
            finally {
                multipleMethodsService.destroy(multipleMethodsService1);
                multipleMethodsService.destroy(multipleMethodsService2);
            }
        }
    }
}
