package com.example;

import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

public class Main {

  public static void main(final String[] args) {

    final String schema = "DEVELOP";
    final String tableName = "TEST240104";
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(schema))
            .tableNamePattern(tableName);
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder()
            .setRetrievePrimaryKeys(true)
            .setRetrieveColumnDataTypes(true)
            .setRetrieveTableConstraintInformation(true)
            .setRetrieveTableConstraintDefinitions(true)
            .setRetrieveTableConstraints(true)
            .setRetrieveIndexes(true)
            .setRetrieveRoutineParameters(true)
            .setRetrieveTableColumns(true)
            .setRetrieveTables(true)
            .setRetrieveRoutines(true)
            .setRetrieveDatabaseInfo(true)
            .setRetrieveForeignKeys(false);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            // Set what details are required in the schema - this affects the
            // time taken to crawl the schema
            .withSchemaInfoLevelBuilder(schemaInfoLevelBuilder);
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());
    final DatabaseConnectionSource dataSource = getDataSource();
    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaCrawlerUtility.matchSchemaRetrievalOptions(dataSource);
    final Catalog catalog =
        SchemaCrawlerUtility.getCatalog(
            dataSource, schemaRetrievalOptions, schemaCrawlerOptions, new Config());
    final Table table = catalog.lookupTable(new SchemaReference(null, schema), tableName).get();
    System.out.println("table:" + table);
    System.out.println("table:constraints:" + table.getTableConstraints());
  }

  private static DatabaseConnectionSource getDataSource() {
    return DatabaseConnectionSources.newDatabaseConnectionSource(
        "jdbc:oracle:thin:@//localhost:1521/XEPDB1",
        new MultiUseUserCredentials("SYS AS SYSDBA", "test"));
  }
}
