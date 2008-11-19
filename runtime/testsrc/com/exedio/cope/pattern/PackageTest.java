/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTest extends TestCase
{
	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTestSuite(HashTest.class);
		suite.addTestSuite(MD5Test.class);
		suite.addTestSuite(PartOfTest.class);
		suite.addTestSuite(LimitedListFieldTest.class);
		suite.addTestSuite(ListFieldTest.class);
		suite.addTestSuite(ListFieldTest2.class);
		suite.addTestSuite(SetFieldTest.class);
		suite.addTestSuite(EnumSetFieldTest.class);
		suite.addTestSuite(EnumMapFieldTest.class);
		suite.addTestSuite(MapFieldTest.class);
		suite.addTestSuite(JavaViewTest.class);
		suite.addTestSuite(MediaTest.class);
		suite.addTestSuite(MediaDefaultTest.class);
		suite.addTestSuite(MediaEnumTest.class);
		suite.addTestSuite(MediaFixedTest.class);
		suite.addTestSuite(MediaMajorTest.class);
		suite.addTestSuite(MediaMandatoryTest.class);
		suite.addTestSuite(ThumbnailTest.class);
		suite.addTestSuite(CompositeTest.class);
		suite.addTestSuite(SerializerTest.class);
		suite.addTestSuite(DynamicModelTest.class);
		suite.addTestSuite(HistoryTest.class);
		suite.addTestSuite(DispatcherTest.class);
		suite.addTestSuite(SingletonTest.class);
		suite.addTestSuite(PasswordRecoveryTest.class);
		suite.addTestSuite(PriceTest.class);
		suite.addTestSuite(PriceFieldTest.class);
		return suite;
	}
}
