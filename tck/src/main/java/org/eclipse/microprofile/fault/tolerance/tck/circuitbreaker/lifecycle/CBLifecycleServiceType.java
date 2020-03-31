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

public enum CBLifecycleServiceType {
    // C:    circuit breaker defined on Class
    // M:    circuit breaker defined on Method
    // CM:   circuit breaker defined both on Class and Method

    BASE_C,                            // CB defined on class
    BASE_C_DERIVED_C,                  // derived class with CB redefined on class, service method NOT overridden
    BASE_C_DERIVED_C_METHOD_OVERRIDE,  // derived class with CB redefined on class, service method overridden
    BASE_C_DERIVED_M,                  // derived class with CB redefined on overridden service method
    BASE_C_DERIVED_MISSING_ON_METHOD,  // derived class with no CB annotation on overridden service method
    BASE_C_DERIVED_NONE,               // derived class, no annotation at class level, no method override

    BASE_M,                            // CB defined on service method
    BASE_M_DERIVED_C,                  // derived class with CB redefined on class, service method NOT overridden
    BASE_M_DERIVED_C_METHOD_OVERRIDE,  // derived class with CB redefined on class, service method overridden
    BASE_M_DERIVED_M,                  // derived class with CB redefined on overridden service method
    BASE_M_DERIVED_MISSING_ON_METHOD,  // derived class with no CB annotation on overridden service method
    BASE_M_DERIVED_NONE,               // derived class, no annotation at class level, no method override

    BASE_CM,                           // CB defined on class and service method
    BASE_CM_DERIVED_C,                 // derived class with CB redefined on class, service method NOT overridden
    BASE_CM_DERIVED_C_METHOD_OVERRIDE, // derived class with CB redefined on class, service method overridden
    BASE_CM_DERIVED_M,                 // derived class with CB redefined on overridden service method
    BASE_CM_DERIVED_MISSING_ON_METHOD, // derived class with no CB annotation on overridden service method
    BASE_CM_DERIVED_NONE,              // derived class, no annotation at class level, no method override
    ;
}
