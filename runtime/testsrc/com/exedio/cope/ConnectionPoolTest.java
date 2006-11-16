/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.exedio.cope.junit.CopeAssert;

public class ConnectionPoolTest extends CopeAssert
{

	public void testCp() throws SQLException
	{
		final Conn c1 = new Conn();
		final Factory f = new Factory(listg(c1));
		f.assertV(0);

		final ConnectionPool cp = new ConnectionPool(f, 1, 0);
		c1.assertV(false, 0, 0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true, 1, 0, 0);
		f.assertV(1);
		
		// put into idle
		cp.putConnection(c1);
		c1.assertV(true, 1, 1, 0);
		f.assertV(1);

		// get from idle
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true, 2, 1, 0);
		f.assertV(1);
		
		// put into idle
		cp.putConnection(c1);
		c1.assertV(true, 2, 2, 0);
		f.assertV(1);

		// get from idle with other autoCommit
		assertSame(c1, cp.getConnection(false));
		c1.assertV(false, 3, 2, 0);
		f.assertV(1);
	}
	
	public void testOverflow() throws SQLException
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final ConnectionPool cp = new ConnectionPool(f, 1, 0);
		c1.assertV(false, 0, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true,  1, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);
		
		// get and create (2)
		assertSame(c2, cp.getConnection(true));
		c1.assertV(true, 1, 0, 0);
		c2.assertV(true, 1, 0, 0);
		f.assertV(2);
		
		// put into idle
		cp.putConnection(c1);
		c1.assertV(true, 1, 1, 0);
		c2.assertV(true, 1, 0, 0);
		f.assertV(2);
		
		// put and close
		cp.putConnection(c2);
		c1.assertV(true, 1, 1, 0);
		c2.assertV(true, 1, 1, 1);
		f.assertV(2);
	}
	
	public void testIdleInitial() throws SQLException
	{
		final Conn c1 = new Conn();
		final Factory f = new Factory(listg(c1));
		f.assertV(0);

		final ConnectionPool cp = new ConnectionPool(f, 1, 1);
		c1.assertV(false, 0, 0, 0);
		f.assertV(1); // already created
		
		// get and create
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true, 1, 0, 0);
		f.assertV(1);
	}
	
	public void testIsClosed() throws SQLException
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final ConnectionPool cp = new ConnectionPool(f, 1, 0);
		c1.assertV(false, 0, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true,  1, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);
		
		// dont put into idle, because its closed
		c1.isClosed = true;
		try
		{
			cp.putConnection(c1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("unexpected closed connection", e.getMessage());
		}
		c1.assertV(true,  1, 1, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);

		// create new because no idle available
		assertSame(c2, cp.getConnection(true));
		c1.assertV(true, 1, 1, 0);
		c2.assertV(true, 1, 0, 0);
		f.assertV(2);
	}
	
	public void testFlush() throws SQLException
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final ConnectionPool cp = new ConnectionPool(f, 1, 0);
		c1.assertV(false, 0, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true,  1, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);
		
		// put into idle
		cp.putConnection(c1);
		c1.assertV(true,  1, 1, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);

		// flush closes c1
		cp.flush();
		c1.assertV(true,  1, 1, 1);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);

		// create new because flushed
		assertSame(c2, cp.getConnection(true));
		c1.assertV(true, 1, 1, 1);
		c2.assertV(true, 1, 0, 0);
		f.assertV(2);
	}
	
	public void testNoPool() throws SQLException
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final ConnectionPool cp = new ConnectionPool(f, 0, 0);
		c1.assertV(false, 0, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true,  1, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);
		
		// put and close because no idle
		cp.putConnection(c1);
		c1.assertV(true,  1, 1, 1);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);

		// create new because no idle
		assertSame(c2, cp.getConnection(true));
		c1.assertV(true, 1, 1, 1);
		c2.assertV(true, 1, 0, 0);
		f.assertV(2);
	}
	
	public void testTimeout() throws SQLException
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final ConnectionPool cp = new ConnectionPool(f, 1, 0);
		c1.assertV(false, 0, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.getConnection(true));
		c1.assertV(true,  1, 0, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);
		
		// put into idle
		cp.putConnection(c1);
		c1.assertV(true,  1, 1, 0);
		c2.assertV(false, 0, 0, 0);
		f.assertV(1);

		// create new because c1 timed out
		c1.timeout = true;
		assertSame(c2, cp.getConnection(true));
		c1.assertV(true, 1, 1, 0);
		c2.assertV(true, 1, 0, 0);
		f.assertV(2);
	}
	
	static class Factory implements ConnectionPool.Factory
	{
		final Iterator<Conn> connections;
		int createCount = 0;
		
		Factory(final List<Conn> connections)
		{
			this.connections = connections.iterator();
		}

		void assertV(final int createCount)
		{
			assertEquals(createCount, this.createCount);
		}
		
		public java.sql.Connection createConnection() throws SQLException
		{
			createCount++;
			return connections.next();
		}
	}
	
	static class Conn implements Connection
	{
		boolean autoCommit = false;
		int autoCommitCount = 0;
		boolean isClosed = false;
		int isClosedCount = 0;
		int closedCount = 0;
		boolean timeout = false;
		
		void assertV(final boolean autoCommit, final int autoCommitCount, final int isClosedCount, final int closedCount)
		{
			assertEquals(autoCommit, this.autoCommit);
			assertEquals(autoCommitCount, this.autoCommitCount);
			assertEquals(isClosedCount, this.isClosedCount);
			assertEquals(closedCount, this.closedCount);
		}
		
		public void setAutoCommit(final boolean autoCommit) throws SQLException
		{
			this.autoCommit = autoCommit;
			this.autoCommitCount++;
		}

		public boolean isClosed() throws SQLException
		{
			isClosedCount++;
			return isClosed;
		}

		public void close() throws SQLException
		{
			closedCount++;
		}

		public DatabaseMetaData getMetaData() throws SQLException
		{
			return new DMD(timeout);
		}
		
		public Statement createStatement() throws SQLException
		{
			throw new RuntimeException();
		}

		public PreparedStatement prepareStatement(String sql) throws SQLException
		{
			throw new RuntimeException();
		}

		public CallableStatement prepareCall(String sql) throws SQLException
		{
			throw new RuntimeException();
		}

		public String nativeSQL(String sql) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean getAutoCommit() throws SQLException
		{
			throw new RuntimeException();
		}

		public void commit() throws SQLException
		{
			throw new RuntimeException();
		}

		public void rollback() throws SQLException
		{
			throw new RuntimeException();
		}

		public void setReadOnly(boolean readOnly) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean isReadOnly() throws SQLException
		{
			throw new RuntimeException();
		}

		public void setCatalog(String catalog) throws SQLException
		{
			throw new RuntimeException();
		}

		public String getCatalog() throws SQLException
		{
			throw new RuntimeException();
		}

		public void setTransactionIsolation(int level) throws SQLException
		{
			throw new RuntimeException();
		}

		public int getTransactionIsolation() throws SQLException
		{
			throw new RuntimeException();
		}

		public SQLWarning getWarnings() throws SQLException
		{
			throw new RuntimeException();
		}

		public void clearWarnings() throws SQLException
		{
			throw new RuntimeException();
		}

		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
		{
			throw new RuntimeException();
		}

		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
		{
			throw new RuntimeException();
		}

		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
		{
			throw new RuntimeException();
		}

		public Map<String, Class<?>> getTypeMap() throws SQLException
		{
			throw new RuntimeException();
		}

		public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException
		{
			throw new RuntimeException();
		}

		public void setHoldability(int holdability) throws SQLException
		{
			throw new RuntimeException();
		}

		public int getHoldability() throws SQLException
		{
			throw new RuntimeException();
		}

		public Savepoint setSavepoint() throws SQLException
		{
			throw new RuntimeException();
		}

		public Savepoint setSavepoint(String name) throws SQLException
		{
			throw new RuntimeException();
		}

		public void rollback(Savepoint savepoint) throws SQLException
		{
			throw new RuntimeException();
		}

		public void releaseSavepoint(Savepoint savepoint) throws SQLException
		{
			throw new RuntimeException();
		}

		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
		{
			throw new RuntimeException();
		}

		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
		{
			throw new RuntimeException();
		}

		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
		{
			throw new RuntimeException();
		}

		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
		{
			throw new RuntimeException();
		}

		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
		{
			throw new RuntimeException();
		}

		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
		{
			throw new RuntimeException();
		}
	}
	
	static class DMD implements DatabaseMetaData
	{
		final boolean timeout;
		
		DMD(final boolean timeout)
		{
			this.timeout = timeout;
		}

		public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException
		{
			if(timeout)
				throw new SQLException("faked timeout");
			else
				return new RS();
		}

		public boolean allProceduresAreCallable() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean allTablesAreSelectable() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getURL() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getUserName() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean isReadOnly() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean nullsAreSortedHigh() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean nullsAreSortedLow() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean nullsAreSortedAtStart() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean nullsAreSortedAtEnd() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getDatabaseProductName() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getDatabaseProductVersion() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getDriverName() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getDriverVersion() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getDriverMajorVersion()
		{
			throw new RuntimeException();
		}

		public int getDriverMinorVersion()
		{
			throw new RuntimeException();
		}

		public boolean usesLocalFiles() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean usesLocalFilePerTable() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsMixedCaseIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean storesUpperCaseIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean storesLowerCaseIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean storesMixedCaseIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean storesUpperCaseQuotedIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean storesLowerCaseQuotedIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean storesMixedCaseQuotedIdentifiers() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getIdentifierQuoteString() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getSQLKeywords() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getNumericFunctions() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getStringFunctions() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getSystemFunctions() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getTimeDateFunctions() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getSearchStringEscape() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getExtraNameCharacters() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsAlterTableWithAddColumn() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsAlterTableWithDropColumn() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsColumnAliasing() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean nullPlusNonNullIsNull() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsConvert() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsConvert(int fromType, int toType) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsTableCorrelationNames() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsDifferentTableCorrelationNames() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsExpressionsInOrderBy() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsOrderByUnrelated() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsGroupBy() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsGroupByUnrelated() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsGroupByBeyondSelect() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsLikeEscapeClause() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsMultipleResultSets() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsMultipleTransactions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsNonNullableColumns() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsMinimumSQLGrammar() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsCoreSQLGrammar() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsExtendedSQLGrammar() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsANSI92EntryLevelSQL() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsANSI92IntermediateSQL() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsANSI92FullSQL() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsIntegrityEnhancementFacility() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsOuterJoins() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsFullOuterJoins() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsLimitedOuterJoins() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getSchemaTerm() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getProcedureTerm() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getCatalogTerm() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean isCatalogAtStart() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getCatalogSeparator() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSchemasInDataManipulation() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSchemasInProcedureCalls() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSchemasInTableDefinitions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSchemasInIndexDefinitions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsCatalogsInDataManipulation() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsCatalogsInProcedureCalls() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsCatalogsInTableDefinitions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsCatalogsInIndexDefinitions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsPositionedDelete() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsPositionedUpdate() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSelectForUpdate() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsStoredProcedures() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSubqueriesInComparisons() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSubqueriesInExists() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSubqueriesInIns() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSubqueriesInQuantifieds() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsCorrelatedSubqueries() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsUnion() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsUnionAll() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsOpenCursorsAcrossCommit() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsOpenCursorsAcrossRollback() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsOpenStatementsAcrossCommit() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsOpenStatementsAcrossRollback() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxBinaryLiteralLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxCharLiteralLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxColumnNameLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxColumnsInGroupBy() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxColumnsInIndex() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxColumnsInOrderBy() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxColumnsInSelect() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxColumnsInTable() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxConnections() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxCursorNameLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxIndexLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxSchemaNameLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxProcedureNameLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxCatalogNameLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxRowSize() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean doesMaxRowSizeIncludeBlobs() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxStatementLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxStatements() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxTableNameLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxTablesInSelect() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getMaxUserNameLength() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getDefaultTransactionIsolation() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsTransactions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsTransactionIsolationLevel(int level) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsDataManipulationTransactionsOnly() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean dataDefinitionCausesTransactionCommit() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean dataDefinitionIgnoredInTransactions() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getSchemas() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getCatalogs() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getTableTypes() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getTypeInfo() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsResultSetType(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean ownUpdatesAreVisible(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean ownDeletesAreVisible(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean ownInsertsAreVisible(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean othersUpdatesAreVisible(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean othersDeletesAreVisible(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean othersInsertsAreVisible(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean updatesAreDetected(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean deletesAreDetected(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean insertsAreDetected(int type) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsBatchUpdates() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException
		{
			throw new RuntimeException();
		}

		public Connection getConnection() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsSavepoints() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsNamedParameters() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsMultipleOpenResults() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsGetGeneratedKeys() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsResultSetHoldability(int holdability) throws SQLException
		{
			throw new RuntimeException();
		}

		public int getResultSetHoldability() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getDatabaseMajorVersion() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getDatabaseMinorVersion() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getJDBCMajorVersion() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getJDBCMinorVersion() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getSQLStateType() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean locatorsUpdateCopy() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean supportsStatementPooling() throws SQLException
		{
			throw new RuntimeException();
		}
	}
	
	static class RS implements ResultSet
	{

		public boolean next() throws SQLException
		{
			return true;
		}

		public void close() throws SQLException
		{
			// OK
		}

		public boolean wasNull() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getString(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean getBoolean(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public byte getByte(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public short getShort(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public int getInt(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public long getLong(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public float getFloat(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public double getDouble(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		@Deprecated
		public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
		{
			throw new RuntimeException();
		}

		public byte[] getBytes(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public Date getDate(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public Time getTime(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public Timestamp getTimestamp(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public InputStream getAsciiStream(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		@Deprecated
		public InputStream getUnicodeStream(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public InputStream getBinaryStream(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public String getString(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean getBoolean(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public byte getByte(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public short getShort(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public int getInt(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public long getLong(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public float getFloat(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public double getDouble(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		@Deprecated
		public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException
		{
			throw new RuntimeException();
		}

		public byte[] getBytes(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Date getDate(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Time getTime(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Timestamp getTimestamp(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public InputStream getAsciiStream(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		@Deprecated
		public InputStream getUnicodeStream(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public InputStream getBinaryStream(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public SQLWarning getWarnings() throws SQLException
		{
			throw new RuntimeException();
		}

		public void clearWarnings() throws SQLException
		{
			throw new RuntimeException();
		}

		public String getCursorName() throws SQLException
		{
			throw new RuntimeException();
		}

		public ResultSetMetaData getMetaData() throws SQLException
		{
			throw new RuntimeException();
		}

		public Object getObject(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public Object getObject(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public int findColumn(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Reader getCharacterStream(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public Reader getCharacterStream(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public BigDecimal getBigDecimal(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public BigDecimal getBigDecimal(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean isBeforeFirst() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean isAfterLast() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean isFirst() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean isLast() throws SQLException
		{
			throw new RuntimeException();
		}

		public void beforeFirst() throws SQLException
		{
			throw new RuntimeException();
		}

		public void afterLast() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean first() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean last() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getRow() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean absolute(int row) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean relative(int rows) throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean previous() throws SQLException
		{
			throw new RuntimeException();
		}

		public void setFetchDirection(int direction) throws SQLException
		{
			throw new RuntimeException();
		}

		public int getFetchDirection() throws SQLException
		{
			throw new RuntimeException();
		}

		public void setFetchSize(int rows) throws SQLException
		{
			throw new RuntimeException();
		}

		public int getFetchSize() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getType() throws SQLException
		{
			throw new RuntimeException();
		}

		public int getConcurrency() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean rowUpdated() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean rowInserted() throws SQLException
		{
			throw new RuntimeException();
		}

		public boolean rowDeleted() throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateNull(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBoolean(int columnIndex, boolean x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateByte(int columnIndex, byte x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateShort(int columnIndex, short x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateInt(int columnIndex, int x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateLong(int columnIndex, long x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateFloat(int columnIndex, float x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateDouble(int columnIndex, double x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateString(int columnIndex, String x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBytes(int columnIndex, byte[] x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateDate(int columnIndex, Date x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateTime(int columnIndex, Time x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateObject(int columnIndex, Object x, int scale) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateObject(int columnIndex, Object x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateNull(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBoolean(String columnName, boolean x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateByte(String columnName, byte x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateShort(String columnName, short x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateInt(String columnName, int x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateLong(String columnName, long x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateFloat(String columnName, float x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateDouble(String columnName, double x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateString(String columnName, String x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBytes(String columnName, byte[] x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateDate(String columnName, Date x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateTime(String columnName, Time x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateTimestamp(String columnName, Timestamp x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateObject(String columnName, Object x, int scale) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateObject(String columnName, Object x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void insertRow() throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateRow() throws SQLException
		{
			throw new RuntimeException();
		}

		public void deleteRow() throws SQLException
		{
			throw new RuntimeException();
		}

		public void refreshRow() throws SQLException
		{
			throw new RuntimeException();
		}

		public void cancelRowUpdates() throws SQLException
		{
			throw new RuntimeException();
		}

		public void moveToInsertRow() throws SQLException
		{
			throw new RuntimeException();
		}

		public void moveToCurrentRow() throws SQLException
		{
			throw new RuntimeException();
		}

		public Statement getStatement() throws SQLException
		{
			throw new RuntimeException();
		}

		public Object getObject(int arg0, Map<String, Class<?>> arg1) throws SQLException
		{
			throw new RuntimeException();
		}

		public Ref getRef(int i) throws SQLException
		{
			throw new RuntimeException();
		}

		public Blob getBlob(int i) throws SQLException
		{
			throw new RuntimeException();
		}

		public Clob getClob(int i) throws SQLException
		{
			throw new RuntimeException();
		}

		public Array getArray(int i) throws SQLException
		{
			throw new RuntimeException();
		}

		public Object getObject(String arg0, Map<String, Class<?>> arg1) throws SQLException
		{
			throw new RuntimeException();
		}

		public Ref getRef(String colName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Blob getBlob(String colName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Clob getClob(String colName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Array getArray(String colName) throws SQLException
		{
			throw new RuntimeException();
		}

		public Date getDate(int columnIndex, Calendar cal) throws SQLException
		{
			throw new RuntimeException();
		}

		public Date getDate(String columnName, Calendar cal) throws SQLException
		{
			throw new RuntimeException();
		}

		public Time getTime(int columnIndex, Calendar cal) throws SQLException
		{
			throw new RuntimeException();
		}

		public Time getTime(String columnName, Calendar cal) throws SQLException
		{
			throw new RuntimeException();
		}

		public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
		{
			throw new RuntimeException();
		}

		public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException
		{
			throw new RuntimeException();
		}

		public URL getURL(int columnIndex) throws SQLException
		{
			throw new RuntimeException();
		}

		public URL getURL(String columnName) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateRef(int columnIndex, Ref x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateRef(String columnName, Ref x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBlob(int columnIndex, Blob x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateBlob(String columnName, Blob x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateClob(int columnIndex, Clob x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateClob(String columnName, Clob x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateArray(int columnIndex, Array x) throws SQLException
		{
			throw new RuntimeException();
		}

		public void updateArray(String columnName, Array x) throws SQLException
		{
			throw new RuntimeException();
		}
	}
}
