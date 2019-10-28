/*
 * Copyright 2019 Akashic Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.yggdrash.common.utils;

import io.yggdrash.common.contract.Contract;
import io.yggdrash.common.contract.methods.ContractMethod;
import io.yggdrash.common.exception.FailedOperationException;
import io.yggdrash.contract.core.annotation.ContractBranchStateStore;
import io.yggdrash.contract.core.annotation.ContractChannelField;
import io.yggdrash.contract.core.annotation.ContractReceipt;
import io.yggdrash.contract.core.annotation.ContractStateStore;
import io.yggdrash.contract.core.annotation.ParamValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContractUtils {
    private static final Logger log = LoggerFactory.getLogger(ContractUtils.class);

    private ContractUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Boolean contractValidation(Object contract) {
        Map<String, ContractMethod> validationMethods =
                ContractUtils.contractMethods(contract, ParamValidation.class);
        return validationMethods != null;
    }

    public static List<Field> txReceiptFields(Object contract) {
        return ContractUtils.contractFields(contract, ContractReceipt.class);
    }

    public static List<Field> stateStoreFields(Object contract) {
        return ContractUtils.contractFields(contract, ContractStateStore.class);
    }

    public static List<Field> branchStateStoreField(Object contract) {
        return ContractUtils.contractFields(contract, ContractBranchStateStore.class);
    }

    public static List<Field> contractChannelField(Object contract) {
        return ContractUtils.contractFields(contract, ContractChannelField.class);
    }

    public static void updateContractFields(Contract contract, List<Field> fields, Object store) {
        // init state Store
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                f.set(contract, store);
            } catch (IllegalAccessException e) {
                log.debug("updateContractFields() is failed. {}", e.getMessage());
            }
        }
    }

    public static List<Field> contractFields(Object contract, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(contract.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .collect(Collectors.toList());
    }

    public static Map<String, ContractMethod> contractMethods(
            Object contract, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(contract.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .collect(
                        Collectors.toMap(Method::getName, ContractMethod::new)
                );
    }

    public static Contract contractInstance(Contract contract) {
        try {
            return contract.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.debug("contractInstance() is failed. {}", e.getMessage());
            throw new FailedOperationException("Contract instance Fail");
        }
    }
}
