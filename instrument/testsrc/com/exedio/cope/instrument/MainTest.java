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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.WrapFeature;
import java.lang.reflect.Method;
import org.junit.Test;

public class MainTest
{
	@Test
	public void findGenerateDeprecateds() throws HumanReadableException, NoSuchMethodException
	{
		assertExceptionMessage("X#x()", "class not found for <generateDeprecated>: X");
		assertExceptionMessage("java.lang.String#x()", "method not found for <generateDeprecated>: java.lang.String#x()");
		assertExceptionMessage("java.lang.String#zz(X)", "can't resolve parameter type 'X' for <generateDeprecated>: java.lang.String#zz(X)");
		assertExceptionMessage("java.lang.String#hashCode()", "method listed in <generateDeprecated> is not deprecated: java.lang.String#hashCode()");
		assertExceptionMessage("java.lang.Thread#stop()", "method listed in <generateDeprecated> is not wrapped: java.lang.Thread#stop()");
		assertGenerateDeprecated(WrapFeature.class.getName()+"#deprecation("+Item.class.getName()+")", WrapFeature.class.getMethod("deprecation", Item.class));
	}

	private static void assertExceptionMessage(final String generateDeprecatedLine, final String expectedExceptionMessage)
	{
		try
		{
			new Main().findGenerateDeprecateds(new CopeNameSpace(null, "test"), asList(new Params.Method(generateDeprecatedLine)));
			fail();
		}
		catch (final HumanReadableException e)
		{
			assertEquals(expectedExceptionMessage, e.getMessage());
		}
	}

	private static void assertGenerateDeprecated(final String generateDeprecatedLine, final Method expectedMethod)
	{
		try
		{
			assertEquals(
				singleton(expectedMethod),
				new Main().findGenerateDeprecateds(new CopeNameSpace(null, "test"), asList(new Params.Method(generateDeprecatedLine)))
			);
		}
		catch (final HumanReadableException e)
		{
			throw new RuntimeException(e);
		}
	}


}