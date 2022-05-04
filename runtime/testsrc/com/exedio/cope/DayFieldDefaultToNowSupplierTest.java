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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Day;
import java.time.ZoneId;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DayFieldDefaultToNowSupplierTest extends TestWithEnvironment
{
	public DayFieldDefaultToNowSupplierTest()
	{
		super(MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();


	@BeforeEach
	final void setUp()
	{
		clockRule.override(clock);
	}

	@Test
	void testBerlin()
	{
		clock.add(new Day(2014, 1, 3).getTimeInMillisFrom(getTimeZone("Europe/Berlin")));
		setDefaultZone(() -> ZoneId.of("Europe/Berlin"));

		final AnItem item = new AnItem();
		clock.assertEmpty();
		assertDefaultZoneUnset();
		assertEquals(new Day(2014, 1, 3), item.getField());

		setDefaultZone(() -> ZoneId.of("Europe/Berlin"));
		assertEquals(ZoneId.of("Europe/Berlin"), AnItem.field.getDefaultNowZone());
		assertDefaultZoneUnset();
	}
	@Test
	void testCanada()
	{
		clock.add(new Day(2015, 5, 14).getTimeInMillisFrom(getTimeZone("Canada/Mountain")));
		setDefaultZone(() -> ZoneId.of("Canada/Mountain"));

		final AnItem item = new AnItem();
		clock.assertEmpty();
		assertDefaultZoneUnset();
		assertEquals(new Day(2015, 5, 14), item.getField());

		setDefaultZone(() -> ZoneId.of("Canada/Mountain"));
		assertEquals(ZoneId.of("Canada/Mountain"), AnItem.field.getDefaultNowZone());
		assertDefaultZoneUnset();
	}
	@Test
	void testNull()
	{
		clock.add(new Day(2015, 5, 14).getTimeInMillisFrom(getTimeZone("Canada/Mountain")));
		setDefaultZone(() -> null);

		assertFails(
				AnItem::new,
				NullPointerException.class,
				"zone supplier did return null");
		clock.assertEmpty();
		assertDefaultZoneUnset();

		setDefaultZone(() -> null);
		assertFails(
				AnItem.field::getDefaultNowZone,
				NullPointerException.class,
				"zone supplier did return null");
		assertDefaultZoneUnset();
	}
	@Test
	void testFails()
	{
		clock.add(new Day(2015, 5, 14).getTimeInMillisFrom(getTimeZone("Canada/Mountain")));
		setDefaultZone(() -> { throw new IllegalStateException("example failure"); });

		assertFails(
				AnItem::new,
				IllegalStateException.class,
				"example failure");
		clock.assertEmpty();
		assertDefaultZoneUnset();

		setDefaultZone(() -> { throw new IllegalStateException("example failure 2"); });
		assertFails(
				AnItem.field::getDefaultNowZone,
				IllegalStateException.class,
				"example failure 2");
		assertDefaultZoneUnset();
	}


	private static Supplier<ZoneId> defaultZone = null;

	@BeforeEach @AfterEach
	void resetDefaultZone()
	{
		//noinspection AssignmentToStaticFieldFromInstanceMethod OK: required by static method AnItem#zone
		defaultZone = null;
	}

	void setDefaultZone(final Supplier<ZoneId> zone)
	{
		assertDefaultZoneUnset();
		assertNotNull(zone);
		//noinspection AssignmentToStaticFieldFromInstanceMethod OK: required by static method AnItem#zone
		defaultZone = zone;
	}

	void assertDefaultZoneUnset()
	{
		assertNull(defaultZone);
	}


	@WrapperType(indent=2, comments=false)
	static final class AnItem extends Item
	{
		static final DayField field = new DayField().optional().toFinal().defaultToNow(AnItem::zone);

		@WrapInterim(methodBody = false)
		static ZoneId zone()
		{
			assertNotNull(defaultZone, "defaultZone not set");
			final Supplier<ZoneId> result = defaultZone;
			defaultZone = null;
			return result.get();
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		com.exedio.cope.util.Day getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
