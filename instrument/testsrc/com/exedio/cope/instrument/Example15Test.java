/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;

public class Example15Test extends ParserTest
{
	// TODO spaces in generics
	// TODO constructor with generics
	// TODO enums

	public Example15Test()
	{
		super("Example15.java", false);
	}

	@Override
	public void assertParse()
	{
		assertPackage("com.exedio.cope.instrument");

		assertImport("java.util.Date");
		assertImport("java.util.HashMap");
		assertImport("java.util.HashSet");

		final JavaClass exampleClass = assertClass("Example15", null, null);

		final JavaField name =
			assertFieldHeader("name", "String", PRIVATE);
		assertField("name", null, name);

		final JavaField dates =
			assertFieldHeader("dates", "HashSet<Date>", PUBLIC);
		assertField("dates", null, dates);

		final JavaField primes =
			assertFieldHeader("primes", "HashMap<Integer, Boolean>", 0);
		assertField("primes", null, primes);

		final JavaBehaviour constructor =
			assertBehaviourHeader("Example15", null, PUBLIC);
		assertMethod("Example15", null, constructor);

		final JavaBehaviour setter =
			assertBehaviourHeader("set", "void", PUBLIC);
		assertMethod("set", null, setter);

		final JavaBehaviour getDates =
			assertBehaviourHeader("getDates", "HashSet<Date>", PRIVATE);
		assertMethod("getDates", null, getDates);

		final JavaBehaviour getPrimes =
			assertBehaviourHeader("getPrimes", "HashMap<Integer, Boolean>", 0);
		assertMethod("getPrimes", null, getPrimes);

		final JavaClass colorClass = assertClass("Color", null, exampleClass);
		assertClassEnd(colorClass);
		assertInnerClassAttribute("Color", null);

		final JavaClass weekdayClass = assertClass("Weekday", null, exampleClass);
		assertClassEnd(weekdayClass);
		assertInnerClassAttribute("Weekday", null);

		final JavaBehaviour equalsMethod =
			assertBehaviourHeader("equals", "boolean", PUBLIC);
		assertMethod("equals", null, equalsMethod);

		assertClassEnd(exampleClass);
	}

}
