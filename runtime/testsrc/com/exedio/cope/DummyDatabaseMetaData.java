/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public class DummyDatabaseMetaData implements DatabaseMetaData
{
	@Override
	public boolean allProceduresAreCallable() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean allTablesAreSelectable() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean deletesAreDetected(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getAttributes(final String catalog, final String schemaPattern,
			final String typeNamePattern, final String attributeNamePattern)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getBestRowIdentifier(final String catalog, final String schema,
			final String table, final int scope, final boolean nullable) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getCatalogSeparator() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getCatalogTerm() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getCatalogs() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getColumnPrivileges(final String catalog, final String schema,
			final String table, final String columnNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getColumns(final String catalog, final String schemaPattern,
			final String tableNamePattern, final String columnNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getCrossReference(final String parentCatalog,
			final String parentSchema, final String parentTable, final String foreignCatalog,
			final String foreignSchema, final String foreignTable) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getDatabaseMajorVersion() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getDatabaseMinorVersion() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getDatabaseProductName() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getDriverMajorVersion()
	{
		throw new RuntimeException();
	}

	@Override
	public int getDriverMinorVersion()
	{
		throw new RuntimeException();
	}

	@Override
	public String getDriverName() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getDriverVersion() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getExportedKeys(final String catalog, final String schema, final String table)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getExtraNameCharacters() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getFunctionColumns(final String catalog, final String schemaPattern,
			final String functionNamePattern, final String columnNamePattern)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getFunctions(final String catalog, final String schemaPattern,
			final String functionNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getImportedKeys(final String catalog, final String schema, final String table)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getIndexInfo(final String catalog, final String schema, final String table,
			final boolean unique, final boolean approximate) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getJDBCMajorVersion() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getJDBCMinorVersion() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxConnections() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxIndexLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxRowSize() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxStatementLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxStatements() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxTableNameLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getMaxUserNameLength() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getNumericFunctions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getPrimaryKeys(final String catalog, final String schema, final String table)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getProcedureColumns(final String catalog, final String schemaPattern,
			final String procedureNamePattern, final String columnNamePattern)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getProcedureTerm() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getProcedures(final String catalog, final String schemaPattern,
			final String procedureNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getResultSetHoldability() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getSQLKeywords() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public int getSQLStateType() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getSchemaTerm() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getSchemas() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getSchemas(final String catalog, final String schemaPattern)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getSearchStringEscape() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getStringFunctions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getSuperTables(final String catalog, final String schemaPattern,
			final String tableNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getSuperTypes(final String catalog, final String schemaPattern,
			final String typeNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getSystemFunctions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getTablePrivileges(final String catalog, final String schemaPattern,
			final String tableNamePattern) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getTableTypes() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getTables(final String catalog, final String schemaPattern,
			final String tableNamePattern, final String[] types) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getTimeDateFunctions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getTypeInfo() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getUDTs(final String catalog, final String schemaPattern,
			final String typeNamePattern, final int[] types) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getURL() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public String getUserName() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public ResultSet getVersionColumns(final String catalog, final String schema,
			final String table) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean insertsAreDetected(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean isReadOnly() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean locatorsUpdateCopy() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean othersDeletesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean othersInsertsAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean othersUpdatesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean ownDeletesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean ownInsertsAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean ownUpdatesAreVisible(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsConvert() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsConvert(final int fromType, final int toType) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsGroupBy() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsMultipleOpenResults() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsNamedParameters() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsResultSetConcurrency(final int type, final int concurrency)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsResultSetHoldability(final int holdability)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsResultSetType(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSavepoints() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsStatementPooling() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsTransactionIsolationLevel(final int level)
			throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsTransactions() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsUnion() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean supportsUnionAll() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean updatesAreDetected(final int type) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean usesLocalFiles() throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException
	{
		throw new SQLException();
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException
	{
		throw new SQLException();
	}

	@Override
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

	@Override
	public boolean generatedKeyAlwaysReturned() throws SQLException
	{
		throw new SQLException();
	}
}
