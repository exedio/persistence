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

import static com.exedio.cope.vault.VaultPropertiesTest.deresiliate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.tojunit.CopeRule;
import com.exedio.cope.tojunit.CopeRuntimeRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Properties;
import com.exedio.cope.vaultmock.VaultMockService;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.ForeignKeyConstraint;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.UniqueConstraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;

@TestWithEnvironment.Tag
@MainRule.Tag
@WrapInterim
public abstract class TestWithEnvironment
{
	@org.junit.jupiter.api.Tag("TestWithEnvironment")
	@Target(ElementType.TYPE)
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Tag {}

	public final CopeRule copeRule;

	/**
	 * Copy of {@link com.exedio.cope.tojunit.CopeRule#model}
	 */
	@SuppressWarnings("JavadocReference") // OK: don't care in tests
	protected final Model model;

	private final RuntimeTester tester;

	protected TestWithEnvironment(final Model model)
	{
		//noinspection ThisEscapedInObjectConstruction
		copeRule = new CopeRuntimeRule(model, this);
		this.model = model;
		tester = new RuntimeTester(model);
	}

	public Properties.Source override(final Properties.Source s)
	{
		return s;
	}

	protected RuntimeTester.Dialect dialect = null;
	protected boolean hsqldb;
	protected boolean mysql;
	protected boolean postgresql;
	protected boolean cache;
	protected boolean mariaDriver;

	@BeforeEach final void setUpAbstractRuntimeModelTest()
	{
		tester.setUp();
		model.connect().connectionFactory.isValidOnGetFails = true;
		dialect = tester.dialect;
		hsqldb = tester.hsqldb;
		mysql  = tester.mysql;
		postgresql = tester.postgresql;
		cache = tester.cache;
		mariaDriver = envInfo().getDriverName().startsWith("MariaDB");
		for(final String bucket : model.connect().vaultBuckets())
			((VaultMockService)deresiliate(model.connect().vault(bucket).service)).clear();
	}

	protected final void startTransaction()
	{
		model.startTransaction(getClass().getName());
	}

	protected final void commit()
	{
		model.commit();
	}

	protected final void assertIDFails(final String id, final String detail, final boolean notAnID)
	{
		tester.assertIDFails(id, detail, notAnID);
	}

	protected final TestByteArrayInputStream stream(final byte[] data)
	{
		return tester.stream(data);
	}

	protected final void assertStreamClosed()
	{
		tester.assertStreamClosed();
	}

	protected final void assertSchema()
	{
		tester.assertSchema();
	}

	protected final void assertSchema(final Schema schema)
	{
		tester.assertSchema(schema);
	}

	// copied from CopeTest
	protected final void restartTransaction()
	{
		restartTransaction(() -> {});
	}

	protected final void restartTransaction(final Runnable between)
	{
		final String oldName = model.currentTransaction().getName();
		model.commit();
		between.run();
		model.startTransaction( oldName+"-restart" );
	}

	/**
	 * NOTE:
	 * Vault Trail rows are inserted by a separate connection in auto-commit mode.
	 * Without restarting the transaction, the test code will not "see" the
	 * Vault Trail rows.
	 * I don't know, why MySQL does not require this, and why other tests doing
	 * similar things do not require this.
	 * TODO
	 * This is actually a problem. A transaction cannot see Vault Trail rows created
	 * during its execution. This may cause wrong result of queries using the Vault Trail.
	 */
	protected final void restartTransactionForVaultTrail(final DataField field)
	{
		if(field.getVaultBucket()!=null && !mysql)
			restartTransaction();
	}

	protected final String synthetic(final String name, final String global)
	{
		return tester.synthetic(name, global);
	}

	protected final String unq(final String s)
	{
		return model.getConnectProperties().redundantUnq(s);
	}

	protected final void assumeNotUnq()
	{
		Assumptions.assumeFalse(model.getConnectProperties().redundantUnq);
	}

	protected final void notAllowed(final Query<?> query, final String message)
	{
		try
		{
			final List<?> result = query.search();
			fail("search is expected to fail, but returned " + result);
		}
		catch(final SQLRuntimeException e)
		{
			assertEquals(message, dropMariaConnectionId(e.getCause().getMessage()));
		}
	}

	protected final void notAllowedTotal(final Query<?> query, final String message)
	{
		try
		{
			final int result = query.total();
			fail("total is expected to fail, but returned " + result);
		}
		catch(final SQLRuntimeException e)
		{
			assertEquals(message, dropMariaConnectionId(e.getCause().getMessage()));
		}
	}

	final String primaryKeySequenceName(final String nameBase)
	{
		return tester.primaryKeySequenceName(nameBase);
	}

	protected final void assertPrimaryKeySequenceName(final String sequenceNameBase, final Type<?> type)
	{
		tester.assertPrimaryKeySequenceName(sequenceNameBase, type);
	}

	protected final PrimaryKeyConstraint assertPkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String column)
	{
		return tester.assertPkConstraint(table, name, column);
	}

	protected final ForeignKeyConstraint assertFkConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String column,
			final String targetTable,
			final String targetColumn)
	{
		return tester.assertFkConstraint(table, name, column, targetTable, targetColumn);
	}

	protected final UniqueConstraint assertUniqueConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String clause)
	{
		return tester.assertUniqueConstraint(table, name, clause);
	}

	@SuppressWarnings("UnusedReturnValue")
	protected final CheckConstraint assertCheckConstraint(
			final com.exedio.dsmf.Table table,
			final String name,
			final String condition)
	{
		return tester.assertCheckConstraint(table, name, condition);
	}

	protected final void assertDefaultToNextSequenceName(final String name, final IntegerField field)
	{
		tester.assertDefaultToNextSequenceName(name, field);
	}

	final void assertCheckUpdateCounters()
	{
		tester.assertCheckUpdateCounters();
	}

	final void assertSameCache(final Object o1, final Object o2)
	{
		tester.assertSameCache(o1, o2);
	}

	protected final void assertCause(final UniqueViolationException e)
	{
		tester.assertCause(e);
	}

	protected final void assertCacheInfo(final Type<?>... types)
	{
		tester.assertCacheInfo(types);
	}


	protected final boolean propertiesHsqldbMysql57()
	{
		return propertiesHsqldb("mysql57");
	}

	private boolean propertiesHsqldb(final String approximate)
	{
		if(!hsqldb)
			return false;

		final Properties.Field<?> field = model.getConnectProperties().getField("dialect.approximate");
		assertNotNull(field);
		return approximate.equals(((Enum<?>)field.get()).name());
	}

	protected final String ifPrep(final String s)
	{
		return
				model.getConnectProperties().isSupportDisabledForPreparedStatements()
				? "" : s;
	}

	@SuppressWarnings("RegExpSimplifiable") // OK: [0-9] is easier to understand than \d
	protected final String dropMariaConnectionId(final String message)
	{
		if(!mysql || !mariaDriver)
			return message;

		final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^\\(conn=[0-9]+\\) (.*)$");
		final Matcher matcher = pattern.matcher(message);
		if(!matcher.matches())
			return message;

		return matcher.group(1);
	}

	protected final EnvironmentInfo envInfo()
	{
		return model.getEnvironmentInfo();
	}

	private boolean dbAtLeast(final String name, final int major, final int minor)
	{
		final EnvironmentInfo info = envInfo();
		assertEquals(name, info.getDatabaseProductName());
		return info.isDatabaseVersionAtLeast(major, minor);
	}

	private boolean dbAtLeastMysql(final int major, final int minor)
	{
		return dbAtLeast("MySQL", major, minor);
	}

	protected final boolean atLeastMysql8()
	{
		return dbAtLeastMysql(8, 0);
	}

	protected final String dbCat()
	{
		return envInfo().getCatalog();
	}

	protected final String checkViolationMessage(final String table, final String constraint)
	{
		return switch(dialect)
		{
			case hsqldb -> "integrity constraint violation: check constraint ; " + constraint + " table: " + table;
			case mysql -> "Check constraint '" + constraint +  "' is violated.";
			case postgresql -> "ERROR: new row for relation \"" + table + "\" violates check constraint \"" + constraint + "\"";
		};
	}

	static
	{
		PrometheusMeterRegistrar.load();
	}
}
