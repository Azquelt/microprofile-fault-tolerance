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
package org.eclipse.microprofile.fault.tolerance.tck.circuitbreaker.lifecycle;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

import javax.enterprise.context.Dependent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Dependent
@CBLifecycle(CBLifecycleServiceType.BASE_C)
@CircuitBreaker(requestVolumeThreshold = 8)
public class BaseCircuitBreakerOnClass implements CircuitBreakerLifecycleService {
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int instanceId = COUNTER.incrementAndGet();

    @Override
    public int instanceId() {
        return instanceId;
    }

    @Override
    public void service() throws IOException {
        throw new IOException();
    }
}