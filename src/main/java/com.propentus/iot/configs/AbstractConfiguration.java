/*
 *
 *  * Copyright 2016-2019
 *  *
 *  * Interreg Central Baltic 2014-2020 funded project
 *  * Smart Logistics and Freight Villages Initiative, CB426
 *  *
 *  * Kouvola Innovation Oy, FINLAND
 *  * Region Örebro County, SWEDEN
 *  * Tallinn University of Technology, ESTONIA
 *  * Foundation Valga County Development Agency, ESTONIA
 *  * Transport and Telecommunication Institute, LATVIA
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.propentus.iot.configs;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.reflection.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for configurations. Contain default validate methods for null values.
 */
public class AbstractConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AbstractConfiguration.class);

    /**
     * Validates that all field of object are not null. If atleast one null value is found, ConfigurationException is thrown,  with fields with null values listed.
     * Override this to add more specific validation logic, like valid file paths or so.
     * @throws ConfigurationException
     */
    public void validate() throws ConfigurationException {
        List<String> nullValues = new ArrayList<>();
        try {
           nullValues = ReflectionUtil.findNullValuesWithAnnotations(this);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            throw new ConfigurationException("Error validating instance of OrganisationConfiguration!" + e.getMessage());
        }

        if(!nullValues.isEmpty()) {
            String error = "Error validating instance of OrganisationConfiguration! Null configuration values: " + nullValues;
            throw new ConfigurationException(error);
        }

    }

}
