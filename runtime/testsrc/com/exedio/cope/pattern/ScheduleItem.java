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

package com.exedio.cope.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.pattern.ScheduleTest.Log;
import com.exedio.cope.util.JobContext;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public final class ScheduleItem extends Item implements Scheduleable
{
	static final Schedule report = new Schedule(ZoneId.of("Europe/Berlin"));
	static final BooleanField fail = new BooleanField().defaultTo(false);
	static final IntegerField progress = new IntegerField().optional().min(0);

	private static final ArrayList<Log> logs = new ArrayList<>();

	@Override
	public void run(final Schedule schedule, final Date from, final Date until, final JobContext ctx)
	{
		assertSame(report, schedule);
		assertNotNull(ctx);
		assertTrue(TYPE.getModel().hasCurrentTransaction());
		logs.add(new Log(this, from, until, TYPE.getModel().currentTransaction().getName()));
		if(getFail())
			throw new RuntimeException("schedule test failure");

		assertTrue(ctx.supportsProgress());
		{
			final Integer progress = getProgress();
			if(progress!=null)
				ctx.incrementProgress(progress);
		}
	}

	static void assertLogs(final Log... expected)
	{
		//noinspection MisorderedAssertEqualsArguments
		assertEquals(Arrays.asList(expected), logs);
		logs.clear();
	}

	static void clearLogs()
	{
		logs.clear();
	}


	/**
	 * Creates a new ScheduleItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public ScheduleItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new ScheduleItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ScheduleItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isEnabled")
	boolean isReportEnabled()
	{
		return ScheduleItem.report.isEnabled(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setEnabled")
	void setReportEnabled(final boolean enabled)
	{
		ScheduleItem.report.setEnabled(this,enabled);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getInterval")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Schedule.Interval getReportInterval()
	{
		return ScheduleItem.report.getInterval(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setInterval")
	void setReportInterval(@javax.annotation.Nonnull final com.exedio.cope.pattern.Schedule.Interval interval)
	{
		ScheduleItem.report.setInterval(this,interval);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="run")
	@java.lang.Deprecated
	static int runReport(@javax.annotation.Nullable final com.exedio.cope.util.Interrupter interrupter)
	{
		return ScheduleItem.report.run(ScheduleItem.class,interrupter);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="run")
	static void runReport(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		ScheduleItem.report.run(ScheduleItem.class,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #report}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="RunParent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<ScheduleItem> reportRunParent()
	{
		return ScheduleItem.report.getRunParent(ScheduleItem.class);
	}

	/**
	 * Returns the value of {@link #fail}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getFail()
	{
		return ScheduleItem.fail.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #fail}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFail(final boolean fail)
	{
		ScheduleItem.fail.set(this,fail);
	}

	/**
	 * Returns the value of {@link #progress}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getProgress()
	{
		return ScheduleItem.progress.get(this);
	}

	/**
	 * Sets a new value for {@link #progress}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setProgress(@javax.annotation.Nullable final java.lang.Integer progress)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		ScheduleItem.progress.set(this,progress);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for scheduleItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<ScheduleItem> TYPE = com.exedio.cope.TypesBound.newType(ScheduleItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ScheduleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
