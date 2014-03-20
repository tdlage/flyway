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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.flywaydb.core.dbsupport.DbSupport;
import org.flywaydb.core.dbsupport.Function;
import org.flywaydb.core.dbsupport.JdbcTemplate;
import org.flywaydb.core.dbsupport.Schema;
import org.flywaydb.core.dbsupport.Table;
import org.flywaydb.core.dbsupport.Type;
import org.flywaydb.core.util.StringUtils;


/**
 * DB2 implementation of Schema.
 */
public class InformixSchema extends Schema {
    /**
     * Creates a new DB2 schema.
     *
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param dbSupport    The database-specific support.
     * @param name         The name of the schema.
     */
    public InformixSchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        // since informix don´t need the owner to be previously created, we can assume that every schema exists
        return true;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = jdbcTemplate.queryForInt("select count(*) from sysmaster:systabnames where owner = ?", name);
        return objectCount == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
       //there is no need to create schema, since the owner can be specified on create objects
    }

    @Override
    protected void doDrop() throws SQLException {
        clean();
    }

    @Override
    protected void doClean() throws SQLException {
        // views
        for (String dropStatement : generateDropStatements(name, "V", "VIEW")) {
            jdbcTemplate.execute(dropStatement);
        }

        // aliases
        for (String dropStatement : generateDropStatements(name, "S", "SYNONYM")) {
            jdbcTemplate.execute(dropStatement);
        }

        for (Table table : allTables()) {
            table.drop();
        }

        // sequences
        for (String dropStatement : generateDropStatements(name, "Q", "SEQUENCE")) {
            jdbcTemplate.execute(dropStatement);
        }

        // procedures
        for (String dropStatement : generateDropStatementsForProcedures(name)) {
            jdbcTemplate.execute(dropStatement);
        }

        for (Function function : allFunctions()) {
            function.drop();
        }

        for (Type type : allTypes()) {
            type.drop();
        }
    }



    /**
     * Generates DROP statements for the procedures in this schema.
     *
     * @param schema The schema of the objects.
     * @return The drop statements.
     * @throws SQLException when the statements could not be generated.
     */
    private List<String> generateDropStatementsForProcedures(String schema) throws SQLException {
        String dropProcGenQuery = "select rtrim(PROCNAME) from SYSPROCEDURES where OWNER = '" + schema + "'";
        return buildDropStatements("DROP PROCEDURE", dropProcGenQuery, schema);
    }

    /**
     * Generates DROP statements for this type of table, representing this type of object in this schema.
     *
     * @param schema     The schema of the objects.
     * @param tableType  The type of table (Can be T, V, S, ...).
     * @param objectType The type of object.
     * @return The drop statements.
     * @throws SQLException when the statements could not be generated.
     */
    private List<String> generateDropStatements(String schema, String tableType, String objectType) throws SQLException {
        String dropTablesGenQuery = "select rtrim(TABNAME) from SYSTABLES where TABTYPE='" + tableType + "' and OWNER = '"
                + schema + "'";
        return buildDropStatements("DROP " + objectType, dropTablesGenQuery, schema);
    }

    /**
     * Builds the drop statements for database objects in this schema.
     *
     * @param dropPrefix The drop command for the database object (e.g. 'drop table').
     * @param query      The query to get all present database objects
     * @param schema     The schema for which to build the statements.
     * @return The statements.
     * @throws SQLException when the drop statements could not be built.
     */
    private List<String> buildDropStatements(final String dropPrefix, final String query, String schema) throws SQLException {
        List<String> dropStatements = new ArrayList<String>();
        List<String> dbObjects = jdbcTemplate.queryForStringList(query);
        for (String dbObject : dbObjects) {
            dropStatements.add(dropPrefix + " " + dbSupport.quote(schema, dbObject));
        }
        return dropStatements;
    }

    private Table[] findTables(String sqlQuery, String ... params) throws SQLException {
        List<String> tableNames = jdbcTemplate.queryForStringList(sqlQuery, params);
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); i++) {
            tables[i] = new InformixTable(jdbcTemplate, dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        return findTables("select rtrim(TABNAME) from SYSTABLES where TABID > 99 and TABTYPE='T' and OWNER = ?", name);
    }

    @Override
    protected Function[] doAllFunctions() throws SQLException {
        List<Map<String,String>> rows = jdbcTemplate.queryForList(
                "select rtrim(PROCNAME) FUNCNAME, PARAMTYPES from SYSPROCEDURES where ISPROC='f' and OWNER = ?", name);

        List<Function> functions = new ArrayList<Function>();
        for (Map<String,String> row : rows) {
            functions.add(getFunction(
                    row.get("FUNCNAME"),
                    StringUtils.tokenizeToStringArray(row.get("PARAMTYPES"), ",")));
        }

        return functions.toArray(new Function[functions.size()]);
    }

    @Override
    public Table getTable(String tableName) {
        return new InformixTable(jdbcTemplate, dbSupport, this, tableName);
    }

    @Override
    protected Type getType(String typeName) {
        return new InformixType(jdbcTemplate, dbSupport, this, typeName);
    }

    @Override
    public Function getFunction(String functionName, String... args) {
        return new InformixFunction(jdbcTemplate, dbSupport, this, functionName, args);
    }
}
