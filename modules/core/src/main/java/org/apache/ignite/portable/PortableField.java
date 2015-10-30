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

package org.apache.ignite.portable;

import org.apache.ignite.igniteobject.IgniteObject;

/**
 * Portable object field. Can be used to speed object field lookup.
 */
public interface PortableField {
    /**
     * Check whether field exists in the object.
     *
     * @param obj Object.
     * @return {@code True} if exists.
     */
    public boolean exists(IgniteObject obj);

    /**
     * Get field's value from the given object.
     *
     * @param obj Object.
     * @return Value.
     */
    public <T> T value(IgniteObject obj);
}
