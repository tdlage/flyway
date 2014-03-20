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

import org.flywaydb.core.DbCategory;
import org.flywaydb.core.migration.MigrationTestCase;
import org.flywaydb.core.util.jdbc.DriverDataSource;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.sql.DataSource;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Test to demonstrate the migration functionality using Informix.
 */
@Category(DbCategory.Informix.class)
public class InformixMigrationMediumTest extends MigrationTestCase {
    @Override
    protected DataSource createDataSource(Properties customProperties) throws Exception {
        String user = customProperties.getProperty("informix.user", "ge");
        String password = customProperties.getProperty("informix.password", "oracle");
        String url = customProperties.getProperty("informix.url", "jdbc:informix-sqli://192.168.46.68:10145/ge_pln_geo:INFORMIXSERVER=operacao;");

        return new DriverDataSource(null, url, user, password);
    }

    @Override
    protected String getQuoteLocation() {
        return "migration/quote";
    }
}
