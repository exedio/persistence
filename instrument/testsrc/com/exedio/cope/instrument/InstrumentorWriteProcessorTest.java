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

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.WrapDeprecationFeature;
import com.exedio.cope.instrument.testfeature.WrapFeature;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

public class InstrumentorWriteProcessorTest
{
	@Test void findMethods() throws HumanReadableException, NoSuchMethodException
	{
		assertExceptionMessage("X#x()", "class not found for <testtag>: X");
		assertExceptionMessage("java.lang.String#x()", "method not found for <testtag>: java.lang.String#x()");
		assertExceptionMessage("java.lang.String#zz(X)", "can't resolve parameter type 'X' for <testtag>: java.lang.String#zz(X)");
		assertExceptionMessage("java.lang.String#hashCode()", "method listed in <testtag> is not annotated as @Deprecated: java.lang.String#hashCode()");
		assertExceptionMessage("java.lang.Thread#stop()", "method listed in <testtag> is not annotated as @Wrap: java.lang.Thread#stop()");
		assertFindMethods(WrapDeprecationFeature.class.getName()+"#goneAlmost("+Item.class.getName()+")", WrapDeprecationFeature.class.getMethod("goneAlmost", Item.class));
		assertFindMethods(
			WrapFeature.class.getName()+"#varargsMethod("+Item.class.getName()+",java.lang.String,[Ljava.lang.Integer;)",
			WrapFeature.class.getMethod("varargsMethod", Item.class, String.class, Integer[].class)
		);
		assertFindMethods(
			WrapFeature.class.getName()+"#arrayMethod("+Item.class.getName()+",[Ljava.lang.Integer;)",
			WrapFeature.class.getMethod("arrayMethod", Item.class, Integer[].class)
		);
		assertFindMethods(
			WrapFeature.class.getName()+"#disabledInBuildXml([[I)",
			WrapFeature.class.getMethod("disabledInBuildXml", int[][].class)
		);
	}

	private static void assertExceptionMessage(final String methodLine, final String expectedExceptionMessage)
	{
		try
		{
			InstrumentorWriteProcessor.findMethods(InstrumentorWriteProcessorTest.class.getClassLoader(), asList(new Params.Method(methodLine)), "<testtag>", asList(Deprecated.class, Wrap.class));
			fail();
		}
		catch (final HumanReadableException e)
		{
			assertEquals(expectedExceptionMessage, e.getMessage());
		}
	}

	private static void assertFindMethods(final String methodLine, final Method expectedMethod) throws HumanReadableException
	{
		assertEquals(
			singleton(expectedMethod),
			InstrumentorWriteProcessor.findMethods(InstrumentorWriteProcessorTest.class.getClassLoader(), asList(new Params.Method(methodLine)), "<testtag>", asList(Wrap.class))
		);
	}
}
