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

package com.exedio.cope.misc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageUtilTest extends TestCase
{
	@SuppressWarnings("deprecation")
	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTestSuite(CheckTest.class);
		suite.addTestSuite(ComputedTest.class);
		suite.addTestSuite(ConnectTokenNotSetTest.class);
		suite.addTestSuite(ConnectTokenSetTest.class);
		suite.addTestSuite(ConditionsTest.class);
		suite.addTestSuite(ConnectTokenTest.class);
		suite.addTestSuite(ConnectTokenNullModelTest.class);
		suite.addTestSuite(CopeNameUtilTest.class);
		suite.addTestSuite(CopeNameUtilFieldTest.class);
		suite.addTestSuite(EnumAnnotatedElementTest.class);
		suite.addTestSuite(IterablesTest.class);
		suite.addTestSuite(ListUtilTest.class);
		suite.addTestSuite(ServletUtilTest.class);
		suite.addTestSuite(ServletUtilContextTest.class);
		suite.addTestSuite(TimeUtilTest.class);
		suite.addTestSuite(CompareTest.class);
		suite.addTestSuite(SetValueUtilTest.class);
		suite.addTestSuite(SerializationCheckTest.class);
		suite.addTestSuite(DigitPinValidatorTest.class);
		suite.addTestSuite(ModelByStringTest.class);
		return suite;
	}
}
