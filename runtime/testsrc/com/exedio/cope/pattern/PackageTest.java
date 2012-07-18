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

package com.exedio.cope.pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTest extends TestCase
{
	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTestSuite(FindItemPatternTest.class);
		suite.addTestSuite(HashTest.class);
		suite.addTestSuite(MD5Test.class);
		suite.addTestSuite(MessageDigestHashTest.class);
		suite.addTestSuite(PartOfModelTest.class);
		suite.addTestSuite(PartOfTest.class);
		suite.addTestSuite(LimitedListFieldModelTest.class);
		suite.addTestSuite(LimitedListFieldTest.class);
		suite.addTestSuite(ListFieldTest.class);
		suite.addTestSuite(ListFieldTest2.class);
		suite.addTestSuite(SetFieldTest.class);
		suite.addTestSuite(EnumSetFieldTest.class);
		suite.addTestSuite(EnumMapFieldTest.class);
		suite.addTestSuite(MapFieldModelTest.class);
		suite.addTestSuite(MapFieldTest.class);
		suite.addTestSuite(JavaViewTest.class);
		suite.addTestSuite(MediaTest.class);
		suite.addTestSuite(MediaUrlTest.class);
		suite.addTestSuite(MediaDefaultTest.class);
		suite.addTestSuite(MediaEnumTest.class);
		suite.addTestSuite(MediaFixedTest.class);
		suite.addTestSuite(MediaSubTest.class);
		suite.addTestSuite(MediaFinalTest.class);
		suite.addTestSuite(MediaMandatoryTest.class);
		suite.addTestSuite(ThumbnailTest.class);
		suite.addTestSuite(ThumbnailMagickTest.class);
		suite.addTestSuite(TextUrlFilterTest.class);
		suite.addTestSuite(TextUrlFilterZipTest.class);
		suite.addTestSuite(CompositeFieldTest.class);
		suite.addTestSuite(CompositeDefaultTest.class);
		suite.addTestSuite(SerializerTest.class);
		suite.addTestSuite(DynamicModelTest.class);
		suite.addTestSuite(HistoryTest.class);
		suite.addTestSuite(DispatcherModelTest.class);
		suite.addTestSuite(DispatcherTest.class);
		suite.addTestSuite(DispatcherProbeTest.class);
		suite.addTestSuite(ImporterModelTest.class);
		suite.addTestSuite(ImporterTest.class);
		suite.addTestSuite(SingletonTest.class);
		suite.addTestSuite(PasswordLimiterModelTest.class);
		suite.addTestSuite(PasswordLimiterVerboseTest.class);
		suite.addTestSuite(PasswordLimiterTest.class);
		suite.addTestSuite(PasswordRecoveryTest.class);
		suite.addTestSuite(PriceFieldModelTest.class);
		suite.addTestSuite(PriceFieldTest.class);
		suite.addTestSuite(ScheduleTest.class);
		suite.addTestSuite(RangeTest.class);
		suite.addTestSuite(RangeFieldModelTest.class);
		suite.addTestSuite(RangeFieldTest.class);
		suite.addTestSuite(RangeFieldNullTest.class);
		suite.addTestSuite(PatternTest.class);
		suite.addTestSuite(RecursiveTest.class);
		return suite;
	}
}
