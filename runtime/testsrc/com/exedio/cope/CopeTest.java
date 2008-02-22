/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Integrates test for dsmf and cope into a single test.
 * Used for target copetest in projects with exedio-cope-test.jar.
 * @author Ralf Wiebicke
 */
public class CopeTest extends TestCase
{

	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		try
		{
			final Class<?> c = Class.forName("com.exedio.dsmf.PackageTest");
			final Method m = c.getDeclaredMethod("suite", (Class[])null);
			final Test test = (Test)m.invoke(null, (Object[])null);
			suite.addTest(test);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new RuntimeException(e.getTargetException());
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		suite.addTest( PackageTest.suite() );
		return suite;
	}

}
