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

package io.yggdrash.common.store;

import io.yggdrash.common.Sha3Hash;
import io.yggdrash.common.contract.BranchContract;
import io.yggdrash.common.contract.vo.dpoa.ValidatorSet;
import java.util.List;

public interface BranchStateStore {
    Long getLastExecuteBlockIndex();

    Sha3Hash getLastExecuteBlockHash();

    Sha3Hash getGenesisBlockHash();

    Sha3Hash getBranchIdHash();

    ValidatorSet getValidators();

    void setValidators(ValidatorSet validatorSet);

    boolean isValidator(String address);

    List<BranchContract> getBranchContacts();

    String getContractVersion(String contractName);

    String getContractName(String contractVersion);
}
