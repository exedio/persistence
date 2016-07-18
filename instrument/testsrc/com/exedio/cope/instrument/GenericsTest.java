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

package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Generics.get;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GenericsTest
{
	@Test public void testIt()
	{
		assertEquals(asList(), get(""));
		assertEquals(asList(), get("Raw"));
		assertEquals(asList("Gen1"), get("Raw<Gen1>"));
		assertEquals(asList("Gen1"), get("Raw< Gen1 >"));
		assertEquals(asList("Gen1", "Gen2"), get("Raw<Gen1,Gen2>"));
		assertEquals(asList("Gen1", "Gen2"), get("Raw< Gen1 , Gen2 >"));
		assertEquals(asList("Gen1", "Gen2<NestGen2A>"), get("Raw<Gen1,Gen2<NestGen2A>>"));
		assertEquals(asList("Gen1", "Gen2<?>"), get("Raw<Gen1,Gen2<?>>"));
		assertEquals(asList("Gen1", "Gen2< NestGen2A >"), get("Raw< Gen1, Gen2< NestGen2A >>"));
		assertEquals(asList("Gen1<NestGen1A>", "Gen2"), get("Raw<Gen1<NestGen1A>,Gen2>"));
		assertEquals(asList("Gen1<NestGen1A>", "Gen2<NestGen2A>"), get("Raw<Gen1<NestGen1A>,Gen2<NestGen2A>>"));
		assertEquals(asList("Gen1<NestGen1A>", "Gen2<NestGen2A,NestGen2B,NestGen2C>"), get("Raw<Gen1<NestGen1A>,Gen2<NestGen2A,NestGen2B,NestGen2C>>"));
	}

	@Test public void testRemove()
	{
		assertEquals("new Foo()", Generics.remove("new Foo()"));
		assertEquals("new Foo()", Generics.remove("new Foo<x>()"));
		assertEquals("new Foo().x(\"y\")", Generics.remove("new Foo<x>().x(\"y\")"));
		assertEquals("new Foo().x(\"\\u003C\")", Generics.remove("new Foo<x>().x(\"\\u003C\")"));
		assertEquals("new Foo().x(\"<\")", Generics.remove("new Foo().x(\"<\")"));
		assertEquals("new Foo('>')", Generics.remove("new Foo('>')"));
		assertEquals("'\"'", Generics.remove("'\"'<>"));
		assertEquals("'\\''", Generics.remove("'\\''"));
	}
}
