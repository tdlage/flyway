/**
 * Copyright 2010-2014 Axel Fontaine and the many contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.core.dbsupport.informix;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.flywaydb.core.DbCategory;
import org.flywaydb.core.dbsupport.DbSupport;
import org.flywaydb.core.dbsupport.JdbcTemplate;

/**
 * Test to demonstrate the migration functionality using DB2.
 */
@Category(DbCategory.Informix.class)
@RunWith(MockitoJUnitRunner.class)
public class InformixSchemaSmallTest {


    private InformixSchema informixSchema;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DbSupport dbSupport;

    @Test
    public void verifyDropVersioningIsCalled() throws SQLException {
        informixSchema = new InformixSchema(jdbcTemplate, dbSupport, "SCHEMA");

        
    }
}
