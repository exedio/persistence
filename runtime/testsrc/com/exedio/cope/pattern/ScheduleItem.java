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
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.pattern.ScheduleTest.Log;
import com.exedio.cope.util.JobContext;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public final class ScheduleItem extends Item
{
	static final Schedule report = Schedule.create(ZoneId.of("Europe/Berlin"), ScheduleItem::run);
	static final BooleanField fail = new BooleanField().defaultTo(false);
	static final IntegerField progress = new IntegerField().optional().min(0);

	private static final ArrayList<Log> logs = new ArrayList<>();

	@WrapInterim(methodBody=false)
	private void run(final Date from, final Date until, final JobContext ctx)
	{
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public ScheduleItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new ScheduleItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ScheduleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isEnabled")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isReportEnabled()
	{
		return ScheduleItem.report.isEnabled(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setEnabled")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setReportEnabled(final boolean enabled)
	{
		ScheduleItem.report.setEnabled(this,enabled);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getInterval")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Schedule.Interval getReportInterval()
	{
		return ScheduleItem.report.getInterval(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setInterval")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setReportInterval(@javax.annotation.Nonnull final com.exedio.cope.pattern.Schedule.Interval interval)
	{
		ScheduleItem.report.setInterval(this,interval);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="run")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void runReport(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		ScheduleItem.report.run(ScheduleItem.class,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #report}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="RunParent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<ScheduleItem> reportRunParent()
	{
		return ScheduleItem.report.getRunParent(ScheduleItem.class);
	}

	/**
	 * Returns the value of {@link #fail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean getFail()
	{
		return ScheduleItem.fail.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #fail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFail(final boolean fail)
	{
		ScheduleItem.fail.set(this,fail);
	}

	/**
	 * Returns the value of {@link #progress}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getProgress()
	{
		return ScheduleItem.progress.get(this);
	}

	/**
	 * Sets a new value for {@link #progress}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setProgress(@javax.annotation.Nullable final java.lang.Integer progress)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		ScheduleItem.progress.set(this,progress);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for scheduleItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<ScheduleItem> TYPE = com.exedio.cope.TypesBound.newType(ScheduleItem.class,ScheduleItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ScheduleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
