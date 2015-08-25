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

package com.exedio.cope.pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageUtilTest extends TestCase
{
	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTestSuite(BlockComputedTest.class);
		suite.addTestSuite(BlockErrorTest.class);
		suite.addTestSuite(BlockRenamedIdTest.class);
		suite.addTestSuite(BlockMountTest.class);
		suite.addTestSuite(ColorFieldRgbTest.class);
		suite.addTestSuite(CompositeTest.class);
		suite.addTestSuite(CompositeErrorTest.class);
		suite.addTestSuite(CompositeMountTest.class);
		suite.addTestSuite(CompositeFieldComputedTest.class);
		suite.addTestSuite(CompositeFieldRenamedIdTest.class);
		suite.addTestSuite(DispatcherConfigTest.class);
		suite.addTestSuite(EnumSetFieldFinalTest.class);
		suite.addTestSuite(EnumMapFieldSchemaNameTest.class);
		suite.addTestSuite(EnumSetFieldSchemaNameTest.class);
		suite.addTestSuite(EnumFieldNameTest.class);
		suite.addTestSuite(ErrorLogTest.class);
		suite.addTestSuite(HashAlgorithmAdapterTest.class);
		suite.addTestSuite(HashPlainTextLimitTest.class);
		suite.addTestSuite(MultiItemFieldStandardTest.class);
		suite.addTestSuite(MultiItemFieldErrorTest.class);
		suite.addTestSuite(JavaViewGetterMissingTest.class);
		suite.addTestSuite(JavaViewInPatternTest.class);
		suite.addTestSuite(JPEGCodecAccessTest.class);
		suite.addTestSuite(MediaPathConditionUnsupportedTest.class);
		suite.addTestSuite(MediaPathPostTest.class);
		suite.addTestSuite(MediaBase64Test.class);
		suite.addTestSuite(MediaFilterFinalTest.class);
		suite.addTestSuite(MediaFinalUrlFingerPrintingTest.class);
		suite.addTestSuite(MediaTypeTest.class);
		suite.addTestSuite(MediaTypeMediaTest.class);
		suite.addTestSuite(MediaRootUrlTest.class);
		suite.addTestSuite(MediaSummaryTest.class);
		suite.addTestSuite(MediaUrlFingerOffsetPropertiesTest.class);
		suite.addTestSuite(MessageDigestAlgorithmTest.class);
		suite.addTestSuite(MessageDigestHashAlgorithmTest.class);
		suite.addTestSuite(MoneyTest.class);
		suite.addTestSuite(MoneyFieldUtilTest.class);
		suite.addTestSuite(NestedHashAlgorithmTest.class);
		suite.addTestSuite(PartOfReverseTest.class);
		suite.addTestSuite(PartOfContainerReuseTest.class);
		suite.addTestSuite(PartOfOrderReuseTest.class);
		suite.addTestSuite(PasswordRecoveryConfigTest.class);
		suite.addTestSuite(PriceTest.class);
		suite.addTestSuite(PriceProportionatelyTest.class);
		suite.addTestSuite(TextUrlFilterAnnotationTest.class);
		suite.addTestSuite(UniqueHashedMediaAnnotationTest.class);
		suite.addTestSuite(UniqueHashedMediaErrorTest.class);
		return suite;
	}
}
