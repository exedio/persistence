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
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Before;
import org.junit.Test;

public class SchemaPurgeTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(AnItem.TYPE);

	public SchemaPurgeTest()
	{
		super(MODEL);
	}

	private boolean sequences;
	private boolean batch;

	private static final String NO_SEQUENCE = "NOSEQ";
	private String thisSeq;
	private String nextSeq;

	@Before public final void setUp()
	{
		final PrimaryKeyGenerator pkg = model.getConnectProperties().primaryKeyGenerator;
		sequences = pkg!=PrimaryKeyGenerator.memory;
		batch = pkg==PrimaryKeyGenerator.batchedSequence;
		thisSeq = sequences ? getPrimaryKeySequenceName(AnItem.TYPE) : NO_SEQUENCE;
		nextSeq = getDefaultToNextSequenceName(AnItem.next);
	}

	@Test public void testPurge() throws SQLException
	{
		final JC jc = new JC();
		final JobContext ctx = mysql ? jc : new AssertionErrorJobContext();

		assertSeq(   0, 0, thisSeq);
		assertSeq(1000, 1, nextSeq);
		assertSeq(2000, 1, "AnItem_sequence");

		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query" ) +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence AnItem_next_Seq purge less 1000" +
				"PROGRESS 0" +
				"MESSAGE sequence AnItem_sequence query" +
				"MESSAGE sequence AnItem_sequence purge less 2000" +
				"PROGRESS 0"),
				jc.fetchEvents());
		assertSeq(   0, 0, thisSeq);
		assertSeq(1000, 1, nextSeq);
		assertSeq(2000, 1, "AnItem_sequence");

		new AnItem(0);
		assertEquals(2000, AnItem.nextSequence());
		assertSeq(   1, 1, thisSeq);
		assertSeq(1001, 2, nextSeq);
		assertSeq(2001, 2, "AnItem_sequence");
		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + thisSeq + " purge less 1" +
				"PROGRESS 0" ) +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence " + nextSeq + " purge less 1001" +
				"PROGRESS 1" +
				"MESSAGE sequence AnItem_sequence query" +
				"MESSAGE sequence AnItem_sequence purge less 2001" +
				"PROGRESS 1"),
				jc.fetchEvents());
		assertSeq(   1, 1, thisSeq);
		assertSeq(1001, 1, nextSeq);
		assertSeq(2001, 1, "AnItem_sequence");

		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + thisSeq + " purge less 1" +
				"PROGRESS 0" ) +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence " + nextSeq + " purge less 1001" +
				"PROGRESS 0" +
				"MESSAGE sequence AnItem_sequence query" +
				"MESSAGE sequence AnItem_sequence purge less 2001" +
				"PROGRESS 0"),
				jc.fetchEvents());
		assertSeq(   1, 1, thisSeq);
		assertSeq(1001, 1, nextSeq);
		assertSeq(2001, 1, "AnItem_sequence");

		new AnItem(0);
		new AnItem(0);
		assertEquals(2001, AnItem.nextSequence());
		assertEquals(2002, AnItem.nextSequence());
		assertSeq(batch?1:3, batch?1:3, thisSeq);
		assertSeq(1003, 3, nextSeq);
		assertSeq(2003, 3, "AnItem_sequence");
		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + thisSeq + " purge less " + (batch?1:3) +
				"PROGRESS " + (batch?0:2) ) +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence " + nextSeq + " purge less 1003" +
				"PROGRESS 2" +
				"MESSAGE sequence AnItem_sequence query" +
				"MESSAGE sequence AnItem_sequence purge less 2003" +
				"PROGRESS 2"),
				jc.fetchEvents());
		assertSeq(batch?1:3, 1, thisSeq);
		assertSeq(1003, 1, nextSeq);
		assertSeq(2003, 1, "AnItem_sequence");

		model.purgeSchema(ctx);
		assertEquals(ifMysql(
			ifSequences(
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + thisSeq + " purge less " + (batch?1:3) +
				"PROGRESS 0" ) +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence " + nextSeq + " purge less 1003" +
				"PROGRESS 0" +
				"MESSAGE sequence AnItem_sequence query" +
				"MESSAGE sequence AnItem_sequence purge less 2003" +
				"PROGRESS 0"),
				jc.fetchEvents());
		assertSeq(batch?1:3, 1, thisSeq);
		assertSeq(1003, 1, nextSeq);
		assertSeq(2003, 1, "AnItem_sequence");
	}

	private String ifMysql(final String message)
	{
		return mysql ? message : "";
	}

	private String ifSequences(final String message)
	{
		return sequences ? message : "";
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private void assertSeq(final int max, final int count, final String name) throws SQLException
	{
		if(!mysql || NO_SEQUENCE.equals(name))
			return;

		model.commit();
		try(
			final Connection con = newConnection(model);
			final Statement stmt = con.createStatement();
			final ResultSet rs = stmt.
					executeQuery("select max(x),count(*) from " + quoteName(model, name)))
		{
			rs.next();
			assertEquals("max",   max,   rs.getInt(1));
			assertEquals("count", count, rs.getInt(2));
		}
		model.startTransaction(SchemaPurgeTest.class.getName());
	}

	@Test public void testStop()
	{
		if(!(sequences && mysql))
			return;

		{
			final JC ctx = new JC(5);
			model.purgeSchema(ctx);
			assertEquals(
					"MESSAGE sequence " + thisSeq + " query" +
					"MESSAGE sequence " + nextSeq + " query" +
					"MESSAGE sequence " + nextSeq + " purge less 1000" +
					"PROGRESS 0" +
					"MESSAGE sequence AnItem_sequence query" +
					"MESSAGE sequence AnItem_sequence purge less 2000" +
					"PROGRESS 0",
					ctx.fetchEvents());
		}
		assertStop(4,
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence " + nextSeq + " purge less 1000" +
				"PROGRESS 0" +
				"MESSAGE sequence AnItem_sequence query" +
				"MESSAGE sequence AnItem_sequence purge less 2000");
		assertStop(3,
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence " + nextSeq + " purge less 1000" +
				"PROGRESS 0" +
				"MESSAGE sequence AnItem_sequence query");
		assertStop(2,
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + nextSeq + " query" +
				"MESSAGE sequence " + nextSeq + " purge less 1000");
		assertStop(1,
				"MESSAGE sequence " + thisSeq + " query" +
				"MESSAGE sequence " + nextSeq + " query");
		assertStop(0,
				"MESSAGE sequence " + thisSeq + " query");
	}

	private void assertStop(final int n, final String message)
	{
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
			stopRequestsEncountered++;
			if(stopRequestsEncountered>stopRequests)
				throw new JobStop("JobStopMessage");
		}

		@Override
		public boolean supportsMessage()
		{
			return true;
		}

		@Override
		public void setMessage(final String message)
		{
			events.append("MESSAGE " +  message);
		}

		@Override
		public void incrementProgress(final int delta)
		{
			events.append("PROGRESS " + delta);
		}

		String fetchEvents()
		{
			final String result = events.toString();
			events.setLength(0);
			stopRequestsEncountered = 0;
			return result;
		}
	}

	@Test public void testNullContext()
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

	static final class AnItem extends Item
	{
		static final IntegerField next = new IntegerField().defaultToNext(1000);
		static final Sequence sequence = new Sequence(2000);

		/**
		 * @param n suppress warning
		 */
		AnItem(final int n)
		{
			this();
		}


		/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #next}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final int getNext()
	{
		return AnItem.next.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #next}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNext(final int next)
	{
		AnItem.next.set(this,next);
	}/**

	 **
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.next public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final int nextSequence()
	{
		return AnItem.sequence.next();
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}
