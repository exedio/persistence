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

import static com.exedio.cope.SchemaInfo.getDefaultToNextSequenceName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeySequenceName;
import static com.exedio.cope.SchemaInfo.getSequenceName;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import com.exedio.cope.vaultmock.VaultMockService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
@SuppressWarnings("HardcodedLineSeparator")
public class SchemaPurgeTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(AnItem.TYPE);

	public SchemaPurgeTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	private boolean sequences;
	private boolean batch;

	private static final String NO_SEQUENCE = "NOSEQ";
	private String thisSeq;
	private String nextSeq;
	private String typeSeq;

	private boolean vault;
	private VaultMockService vaultService;

	@BeforeEach final void setUp()
	{
		final PrimaryKeyGenerator pkg = model.getConnectProperties().primaryKeyGenerator;
		sequences = pkg!=PrimaryKeyGenerator.memory;
		batch = pkg==PrimaryKeyGenerator.batchedSequence;
		thisSeq = sequences ? getPrimaryKeySequenceName(AnItem.TYPE) : NO_SEQUENCE;
		nextSeq = getDefaultToNextSequenceName(AnItem.next);
		typeSeq = getSequenceName(AnItem.sequence);
		vault = MODEL.getConnectProperties().getVaultProperties()!=null;
		vaultService = (VaultMockService)VaultTest.singleton(MODEL.connect().vaults);
	}

	@Test void testPurge() throws SQLException
	{
		final JC jc = new JC();
		final JobContext ctx = (mysql||vault) ? jc : new AssertionErrorJobContext();

		assertSeq(   0, 0, thisSeq);
		assertSeq(1000, 1, nextSeq);
		assertSeq(2000, 1, typeSeq);
		assertVault("");

		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query\n" + STOP ) +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1000 limit 10000\n" + STOP +
				"PROGRESS 0\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOP +
				"MESSAGE sequence " + typeSeq + " purge less 2000 limit 10000\n" + STOP +
				"PROGRESS 0\n") +
			ifVault("MESSAGE vault VaultMockService:exampleDefault\n" + STOP),
				jc.fetchEvents());
		assertVault("purgeSchema\n");
		assertSeq(   0, 0, thisSeq);
		assertSeq(1000, 1, nextSeq);
		assertSeq(2000, 1, typeSeq);

		model.startTransaction(SchemaPurgeTest.class.getName());
		new AnItem(0);
		model.commit();
		assertEquals(2000, AnItem.nextSequence());
		assertSeq(   1, 1, thisSeq);
		assertSeq(1001, 2, nextSeq);
		assertSeq(2001, 2, typeSeq);
		assertVault("");
		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + thisSeq + " purge less 1 limit 10000\n" + STOP +
				"PROGRESS 0\n" ) +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1001 limit 10000\n" + STOP +
				"PROGRESS 1\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOP +
				"MESSAGE sequence " + typeSeq + " purge less 2001 limit 10000\n" + STOP +
				"PROGRESS 1\n") +
			ifVault("MESSAGE vault VaultMockService:exampleDefault\n" + STOP),
				jc.fetchEvents());
		assertVault("purgeSchema\n");
		assertSeq(   1, 1, thisSeq);
		assertSeq(1001, 1, nextSeq);
		assertSeq(2001, 1, typeSeq);
		assertVault("");

		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + thisSeq + " purge less 1 limit 10000\n" + STOP +
				"PROGRESS 0\n" ) +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1001 limit 10000\n" + STOP +
				"PROGRESS 0\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOP +
				"MESSAGE sequence " + typeSeq + " purge less 2001 limit 10000\n" + STOP +
				"PROGRESS 0\n") +
			ifVault("MESSAGE vault VaultMockService:exampleDefault\n" + STOP),
				jc.fetchEvents());
		assertVault("purgeSchema\n");
		assertSeq(   1, 1, thisSeq);
		assertSeq(1001, 1, nextSeq);
		assertSeq(2001, 1, typeSeq);

		model.startTransaction(SchemaPurgeTest.class.getName());
		new AnItem(0);
		new AnItem(0);
		model.commit();
		assertEquals(2001, AnItem.nextSequence());
		assertEquals(2002, AnItem.nextSequence());
		assertSeq(batch?1:3, batch?1:3, thisSeq);
		assertSeq(1003, 3, nextSeq);
		assertSeq(2003, 3, typeSeq);
		assertVault("");
		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + thisSeq + " purge less " + (batch?1:3) + " limit 10000\n" + STOP +
				"PROGRESS " + (batch?0:2) + "\n" ) +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1003 limit 10000\n" + STOP +
				"PROGRESS 2\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOP +
				"MESSAGE sequence " + typeSeq + " purge less 2003 limit 10000\n" + STOP +
				"PROGRESS 2\n") +
			ifVault("MESSAGE vault VaultMockService:exampleDefault\n" + STOP),
				jc.fetchEvents());
		assertVault("purgeSchema\n");
		assertSeq(batch?1:3, 1, thisSeq);
		assertSeq(1003, 1, nextSeq);
		assertSeq(2003, 1, typeSeq);
		assertVault("");

		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + thisSeq + " purge less " + (batch?1:3) + " limit 10000\n" + STOP +
				"PROGRESS 0\n" ) +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1003 limit 10000\n" + STOP +
				"PROGRESS 0\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOP +
				"MESSAGE sequence " + typeSeq + " purge less 2003 limit 10000\n" + STOP +
				"PROGRESS 0\n") +
			ifVault("MESSAGE vault VaultMockService:exampleDefault\n" + STOP),
				jc.fetchEvents());
		assertVault("purgeSchema\n");
		assertSeq(batch?1:3, 1, thisSeq);
		assertSeq(1003, 1, nextSeq);
		assertSeq(2003, 1, typeSeq);
	}

	private String ifMysql(final String message)
	{
		return mysql ? message : "";
	}

	private String ifSequences(final String message)
	{
		return sequences ? message : "";
	}

	private String ifVault(final String message)
	{
		return vault ? message : "";
	}

	private void assertVault(final String history)
	{
		if(vault)
			vaultService.assertIt(history);
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private void assertSeq(final int max, final int count, final String name) throws SQLException
	{
		if(!mysql || NO_SEQUENCE.equals(name))
			return;

		assertFalse(model.hasCurrentTransaction());
		try(ResultSet rs = connection.
					executeQuery(
							"select max(" +
							sequenceColumnName() +
							"),count(*) from " +
							quoteName(model, name)))
		{
			rs.next();
			assertEquals(max,   rs.getInt(1), "max");
			assertEquals(count, rs.getInt(2), "count");
		}
	}

	// duplicate from MysqlDialect
	private String sequenceColumnName()
	{
		return MysqlDialect.sequenceColumnName(propertiesFullSequenceColumnName());
	}

	@Test void testPurgeLimit()
	{
		assumeTrue(sequences, "sequences");
		assumeTrue(mysql, "mysql");
		assumeFalse(vault);

		model.startTransaction(SchemaPurgeTest.class.getName());
		for(int i = 0; i<20; i++)
			new AnItem(0);
		model.commit();
		for(int i = 0; i<20; i++)
			assertEquals(2000+i, AnItem.nextSequence());

		final JC ctx = new JC();

		model.disconnect();
		model.connect(ConnectProperties.create(cascade(
				single("dialect.purgeSequenceLimit", 7),
				copeRule.getConnectProperties().getSourceObject()
		)));
		model.purgeSchema(ctx);
		model.disconnect();
		model.connect(ConnectProperties.create(copeRule.getConnectProperties().getSourceObject()));

		assertEquals(
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
			(batch
			 ? "MESSAGE sequence " + thisSeq + " purge less 1 limit 7\n" + STOP + "PROGRESS 0\n"
			 : "MESSAGE sequence " + thisSeq + " purge less 20 limit 7\n" + STOP + "PROGRESS 7\n" +
				"MESSAGE sequence " + thisSeq + " purge less 20 limit 7\n" + STOP + "PROGRESS 7\n" +
				"MESSAGE sequence " + thisSeq + " purge less 20 limit 7\n" + STOP + "PROGRESS 5\n") + // just 5 because pk sequence starts with zero and is therefore initially empty
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1020 limit 7\n" + STOP + "PROGRESS 7\n" +
				"MESSAGE sequence " + nextSeq + " purge less 1020 limit 7\n" + STOP + "PROGRESS 7\n" +
				"MESSAGE sequence " + nextSeq + " purge less 1020 limit 7\n" + STOP + "PROGRESS 6\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOP +
				"MESSAGE sequence " + typeSeq + " purge less 2020 limit 7\n" + STOP + "PROGRESS 7\n" +
				"MESSAGE sequence " + typeSeq + " purge less 2020 limit 7\n" + STOP + "PROGRESS 7\n" +
				"MESSAGE sequence " + typeSeq + " purge less 2020 limit 7\n" + STOP + "PROGRESS 6\n",
				ctx.fetchEvents());
	}

	@Test void testStop()
	{
		assumeTrue(sequences, "sequences");
		assumeTrue(mysql, "mysql");
		assumeFalse(vault);

		{
			final JC ctx = new JC(5);
			model.purgeSchema(ctx);
			assertEquals(
					"MESSAGE sequence " + thisSeq + " query\n" + STOP +
					"MESSAGE sequence " + nextSeq + " query\n" + STOP +
					"MESSAGE sequence " + nextSeq + " purge less 1000 limit 10000\n" + STOP +
					"PROGRESS 0\n" +
					"MESSAGE sequence " + typeSeq + " query\n" + STOP +
					"MESSAGE sequence " + typeSeq + " purge less 2000 limit 10000\n" + STOP +
					"PROGRESS 0\n",
					ctx.fetchEvents());
		}
		assertStop(4,
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1000 limit 10000\n" + STOP +
				"PROGRESS 0\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOP +
				"MESSAGE sequence " + typeSeq + " purge less 2000 limit 10000\n" + STOPPING);
		assertStop(3,
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1000 limit 10000\n" + STOP +
				"PROGRESS 0\n" +
				"MESSAGE sequence " + typeSeq + " query\n" + STOPPING);
		assertStop(2,
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " purge less 1000 limit 10000\n" + STOPPING);
		assertStop(1,
				"MESSAGE sequence " + thisSeq + " query\n" + STOP +
				"MESSAGE sequence " + nextSeq + " query\n" + STOPPING);
		assertStop(0,
				"MESSAGE sequence " + thisSeq + " query\n" + STOPPING);
	}

	private void assertStop(final int n, final String message)
	{
		assertFalse(model.hasCurrentTransaction());

		final JC ctx = new JC(n);
		try
		{
			model.purgeSchema(ctx);
			fail();
		}
		catch(final JobStop s)
		{
			assertEquals("JobStopMessage", s.getMessage());
		}
		assertEquals(message, ctx.fetchEvents());
	}

	private static final String STOP = "STOP\nDEFER\n";
	private static final String STOPPING = "STOPPING\n";

	private static final class JC extends AssertionErrorJobContext
	{
		private final int stopRequests;
		private int stopRequestsEncountered = 0;
		private final StringBuilder events = new StringBuilder();

		JC()
		{
			this(Integer.MAX_VALUE);
		}

		JC(final int stopRequests)
		{
			this.stopRequests = stopRequests;
		}

		@Override
		public void stopIfRequested()
		{
			assertFalse(MODEL.hasCurrentTransaction());
			stopRequestsEncountered++;
			if(stopRequestsEncountered>stopRequests)
			{
				events.append("STOPPING\n");
				throw new JobStop("JobStopMessage");
			}
			else
				events.append("STOP\n");
		}

		@Override
		public Duration requestsDeferral()
		{
			assertFalse(MODEL.hasCurrentTransaction());
			events.append("DEFER\n");
			return Duration.ZERO;
		}

		@Override
		public boolean supportsMessage()
		{
			events.append("MESS");
			return true;
		}

		@Override
		public void setMessage(final String message)
		{
			events.append("AGE " + message + "\n");
		}

		@Override
		public void incrementProgress(final int delta)
		{
			events.append("PROGRESS " + delta + "\n");
		}

		String fetchEvents()
		{
			final String result = events.toString();
			events.setLength(0);
			stopRequestsEncountered = 0;
			return result;
		}
	}

	@Test void testNullContext()
	{
		try
		{
			model.purgeSchema(null);
			fail();
		}
		catch(final NullPointerException s)
		{
			assertEquals("ctx", s.getMessage());
		}
	}

	private static final class AnItem extends Item
	{
		static final IntegerField next = new IntegerField().defaultToNext(1000);
		static final Sequence sequence = new Sequence(2000);

		AnItem(@SuppressWarnings("unused") final int n)
		{
			this();
		}


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private AnItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #next}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getNext()
	{
		return AnItem.next.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #next}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNext(final int next)
	{
		AnItem.next.set(this,next);
	}

	/**
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="next")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int nextSequence()
	{
		return AnItem.sequence.next();
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
