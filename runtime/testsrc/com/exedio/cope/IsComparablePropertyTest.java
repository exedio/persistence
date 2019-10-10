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

import static com.exedio.cope.IsComparablePropertyTest.MyItem.intA;
import static com.exedio.cope.IsComparablePropertyTest.MyItem.stringAInt;
import static com.exedio.cope.IsComparableTest.cast;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.tojunit.TestSources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class IsComparablePropertyTest
{
	@Test void testDisconnected()
	{
		assertEquals(false, MODEL.isConnected());
		assertFails(
				() -> stringAInt.equal(12345),
				IllegalArgumentException.class,
				"MyItem.stringA not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> stringAInt.equal(intA),
				IllegalArgumentException.class,
				"MyItem.stringA not comparable to MyItem.intA");
	}

	@Test void testEnabled()
	{
		final ConnectProperties props = ConnectProperties.create(
				TestSources.minimal());
		assertEquals(true, props.comparableCheck);
		MODEL.connect(props);
		assertFails(
				() -> stringAInt.equal(12345),
				IllegalArgumentException.class,
				"MyItem.stringA not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> stringAInt.equal(intA),
				IllegalArgumentException.class,
				"MyItem.stringA not comparable to MyItem.intA");
	}

	@Test void testDisabled()
	{
		final ConnectProperties props = ConnectProperties.create(cascade(
				TestSources.single("comparableCheck", false),
				TestSources.minimal()));
		assertEquals(false, props.comparableCheck);
		MODEL.connect(props);
		assertEquals("MyItem.stringA='12345'", stringAInt.equal(12345).toString());
		assertEquals("MyItem.stringA=MyItem.intA", stringAInt.equal(intA).toString());
	}


	@AfterEach void tearDown()
	{
		if(MODEL.isConnected())
			MODEL.disconnect();
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		@WrapperIgnore static final StringField stringA = new StringField();
		static final Function<Integer> stringAInt = cast(stringA);
		@WrapperIgnore static final IntegerField intA = new IntegerField();


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}
