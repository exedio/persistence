package com.exedio.cope;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public class DummyDatabaseMetaData implements DatabaseMetaData
{
	public boolean allProceduresAreCallable() throws SQLException
	{
		throw new SQLException();
	}

	public boolean allTablesAreSelectable() throws SQLException
	{
		throw new SQLException();
	}

	public boolean autoCommitFailureClosesAllResultSets() throws SQLException
	{
		throw new SQLException();
	}

	public boolean dataDefinitionCausesTransactionCommit() throws SQLException
	{
		throw new SQLException();
	}

	public boolean dataDefinitionIgnoredInTransactions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean deletesAreDetected(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getAttributes(final String catalog, final String schemaPattern,
			final String typeNamePattern, final String attributeNamePattern)
			throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getBestRowIdentifier(final String catalog, final String schema,
			final String table, final int scope, final boolean nullable) throws SQLException
	{
		throw new SQLException();
	}

	public String getCatalogSeparator() throws SQLException
	{
		throw new SQLException();
	}

	public String getCatalogTerm() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getCatalogs() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getClientInfoProperties() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getColumnPrivileges(final String catalog, final String schema,
			final String table, final String columnNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getColumns(final String catalog, final String schemaPattern,
			final String tableNamePattern, final String columnNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	public Connection getConnection() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getCrossReference(final String parentCatalog,
			final String parentSchema, final String parentTable, final String foreignCatalog,
			final String foreignSchema, final String foreignTable) throws SQLException
	{
		throw new SQLException();
	}

	public int getDatabaseMajorVersion() throws SQLException
	{
		throw new SQLException();
	}

	public int getDatabaseMinorVersion() throws SQLException
	{
		throw new SQLException();
	}

	public String getDatabaseProductName() throws SQLException
	{
		throw new SQLException();
	}

	public String getDatabaseProductVersion() throws SQLException
	{
		throw new SQLException();
	}

	public int getDefaultTransactionIsolation() throws SQLException
	{
		throw new SQLException();
	}

	public int getDriverMajorVersion()
	{
		throw new RuntimeException();
	}

	public int getDriverMinorVersion()
	{
		throw new RuntimeException();
	}

	public String getDriverName() throws SQLException
	{
		throw new SQLException();
	}

	public String getDriverVersion() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getExportedKeys(final String catalog, final String schema, final String table)
			throws SQLException
	{
		throw new SQLException();
	}

	public String getExtraNameCharacters() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getFunctionColumns(final String catalog, final String schemaPattern,
			final String functionNamePattern, final String columnNamePattern)
			throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getFunctions(final String catalog, final String schemaPattern,
			final String functionNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	public String getIdentifierQuoteString() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getImportedKeys(final String catalog, final String schema, final String table)
			throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getIndexInfo(final String catalog, final String schema, final String table,
			final boolean unique, final boolean approximate) throws SQLException
	{
		throw new SQLException();
	}

	public int getJDBCMajorVersion() throws SQLException
	{
		throw new SQLException();
	}

	public int getJDBCMinorVersion() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxBinaryLiteralLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxCatalogNameLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxCharLiteralLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxColumnNameLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxColumnsInGroupBy() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxColumnsInIndex() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxColumnsInOrderBy() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxColumnsInSelect() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxColumnsInTable() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxConnections() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxCursorNameLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxIndexLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxProcedureNameLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxRowSize() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxSchemaNameLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxStatementLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxStatements() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxTableNameLength() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxTablesInSelect() throws SQLException
	{
		throw new SQLException();
	}

	public int getMaxUserNameLength() throws SQLException
	{
		throw new SQLException();
	}

	public String getNumericFunctions() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getPrimaryKeys(final String catalog, final String schema, final String table)
			throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getProcedureColumns(final String catalog, final String schemaPattern,
			final String procedureNamePattern, final String columnNamePattern)
			throws SQLException
	{
		throw new SQLException();
	}

	public String getProcedureTerm() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getProcedures(final String catalog, final String schemaPattern,
			final String procedureNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	public int getResultSetHoldability() throws SQLException
	{
		throw new SQLException();
	}

	public RowIdLifetime getRowIdLifetime() throws SQLException
	{
		throw new SQLException();
	}

	public String getSQLKeywords() throws SQLException
	{
		throw new SQLException();
	}

	public int getSQLStateType() throws SQLException
	{
		throw new SQLException();
	}

	public String getSchemaTerm() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getSchemas() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getSchemas(final String catalog, final String schemaPattern)
			throws SQLException
	{
		throw new SQLException();
	}

	public String getSearchStringEscape() throws SQLException
	{
		throw new SQLException();
	}

	public String getStringFunctions() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getSuperTables(final String catalog, final String schemaPattern,
			final String tableNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getSuperTypes(final String catalog, final String schemaPattern,
			final String typeNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	public String getSystemFunctions() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getTablePrivileges(final String catalog, final String schemaPattern,
			final String tableNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getTableTypes() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getTables(final String catalog, final String schemaPattern,
			final String tableNamePattern, final String[] types) throws SQLException
	{
		throw new SQLException();
	}

	public String getTimeDateFunctions() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getTypeInfo() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getUDTs(final String catalog, final String schemaPattern,
			final String typeNamePattern, final int[] types) throws SQLException
	{
		throw new SQLException();
	}

	public String getURL() throws SQLException
	{
		throw new SQLException();
	}

	public String getUserName() throws SQLException
	{
		throw new SQLException();
	}

	public ResultSet getVersionColumns(final String catalog, final String schema,
			final String table) throws SQLException
	{
		throw new SQLException();
	}

	public boolean insertsAreDetected(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean isCatalogAtStart() throws SQLException
	{
		throw new SQLException();
	}

	public boolean isReadOnly() throws SQLException
	{
		throw new SQLException();
	}

	public boolean locatorsUpdateCopy() throws SQLException
	{
		throw new SQLException();
	}

	public boolean nullPlusNonNullIsNull() throws SQLException
	{
		throw new SQLException();
	}

	public boolean nullsAreSortedAtEnd() throws SQLException
	{
		throw new SQLException();
	}

	public boolean nullsAreSortedAtStart() throws SQLException
	{
		throw new SQLException();
	}

	public boolean nullsAreSortedHigh() throws SQLException
	{
		throw new SQLException();
	}

	public boolean nullsAreSortedLow() throws SQLException
	{
		throw new SQLException();
	}

	public boolean othersDeletesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean othersInsertsAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean othersUpdatesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean ownDeletesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean ownInsertsAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean ownUpdatesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean storesLowerCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean storesMixedCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean storesUpperCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsANSI92EntryLevelSQL() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsANSI92FullSQL() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsANSI92IntermediateSQL() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsAlterTableWithAddColumn() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsAlterTableWithDropColumn() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsBatchUpdates() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsCatalogsInDataManipulation() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsCatalogsInIndexDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsCatalogsInProcedureCalls() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsCatalogsInTableDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsColumnAliasing() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsConvert() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsConvert(final int fromType, final int toType) throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsCoreSQLGrammar() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsCorrelatedSubqueries() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsDifferentTableCorrelationNames() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsExpressionsInOrderBy() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsExtendedSQLGrammar() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsFullOuterJoins() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsGetGeneratedKeys() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsGroupBy() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsGroupByBeyondSelect() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsGroupByUnrelated() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsIntegrityEnhancementFacility() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsLikeEscapeClause() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsLimitedOuterJoins() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsMinimumSQLGrammar() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsMixedCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsMultipleOpenResults() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsMultipleResultSets() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsMultipleTransactions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsNamedParameters() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsNonNullableColumns() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsOpenCursorsAcrossCommit() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsOpenCursorsAcrossRollback() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsOpenStatementsAcrossCommit() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsOpenStatementsAcrossRollback() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsOrderByUnrelated() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsOuterJoins() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsPositionedDelete() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsPositionedUpdate() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsResultSetConcurrency(final int type, final int concurrency)
			throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsResultSetHoldability(final int holdability)
			throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsResultSetType(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSavepoints() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSchemasInDataManipulation() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSchemasInIndexDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSchemasInProcedureCalls() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSchemasInTableDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSelectForUpdate() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsStatementPooling() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsStoredProcedures() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSubqueriesInComparisons() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSubqueriesInExists() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSubqueriesInIns() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsSubqueriesInQuantifieds() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsTableCorrelationNames() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsTransactionIsolationLevel(final int level)
			throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsTransactions() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsUnion() throws SQLException
	{
		throw new SQLException();
	}

	public boolean supportsUnionAll() throws SQLException
	{
		throw new SQLException();
	}

	public boolean updatesAreDetected(final int type) throws SQLException
	{
		throw new SQLException();
	}

	public boolean usesLocalFilePerTable() throws SQLException
	{
		throw new SQLException();
	}

	public boolean usesLocalFiles() throws SQLException
	{
		throw new SQLException();
	}

	public boolean isWrapperFor(final Class<?> iface) throws SQLException
	{
		throw new SQLException();
	}

	public <T> T unwrap(final Class<T> iface) throws SQLException
	{
		throw new SQLException();
	}

	/**
	 * @since needed since JDK 1.7
	 */
	@SuppressWarnings("unused")
	public ResultSet getPseudoColumns(
			final String catalog,
			final String schemaPattern,
			final String tableNamePattern,
			final String columnNamePattern)
	throws SQLException
	{
		throw new SQLException();
	}

	/**
	 * @since needed since JDK 1.7
	 */
	public boolean generatedKeyAlwaysReturned() throws SQLException
	{
		throw new SQLException();
	}
}
