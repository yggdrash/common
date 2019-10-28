/*
 * Copyright 2018 Akashic Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.yggdrash.common.store.datasource;

import io.yggdrash.common.utils.FileUtil;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LevelDbDataSourceTest {
    private static final String dbPath = "testOutput";

    @AfterClass
    public static void destroy() {
        FileUtil.recursiveDelete(Paths.get(dbPath));
    }

    @Test
    public void shouldBeUpdateByBatch() {
        LevelDbDataSource ds = new LevelDbDataSource(dbPath, "batch-test");
        ds.init();

        Map<byte[], byte[]> rows = new HashMap<>();
        byte[] key = randomBytes(32);
        byte[] value = randomBytes(32);
        rows.put(key, value);
        rows.put(randomBytes(32), randomBytes(32));
        rows.put(randomBytes(32), randomBytes(32));
        rows.put(randomBytes(32), randomBytes(32));

        ds.updateByBatch(rows);
        byte[] foundValue = ds.get(key);
        Assertions.assertThat(foundValue).isEqualTo(value);
    }

    @Test
    public void shouldBeReset() {
        LevelDbDataSource ds = new LevelDbDataSource(dbPath, "reset-test");
        ds.init();

        byte[] key = randomBytes(32);
        byte[] value = putDummyRow(ds, key);
        byte[] foundValue = ds.get(key);
        Assertions.assertThat(foundValue).isEqualTo(value);

        ds.reset();

        foundValue = ds.get(key);
        Assertions.assertThat(foundValue).isNull();
    }

    @Test
    public void shouldPutSomeThing() {
        LevelDbDataSource ds = new LevelDbDataSource(dbPath, "put-test");
        ds.init();

        byte[] key = randomBytes(32);
        byte[] value = putDummyRow(ds, key);
        byte[] foundValue = ds.get(key);
        Assertions.assertThat(foundValue).isEqualTo(value);
    }

    private byte[] putDummyRow(LevelDbDataSource ds, byte[] key) {
        byte[] value = randomBytes(32);
        ds.put(key, value);

        return value;
    }

    @Test
    public void shouldInitialize() {
        String dbName = "initial-test";
        LevelDbDataSource ds = new LevelDbDataSource(dbPath, dbName);
        ds.init();

        Assertions.assertThat(ds).isNotNull();
        Assertions.assertThat(FileUtil.isExists(Paths.get(dbPath, dbName))).isTrue();
    }

    private byte[] randomBytes(int length) {
        byte[] result = new byte[length];
        new Random().nextBytes(result);
        return result;
    }
}
