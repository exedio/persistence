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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageUtilTest extends TestCase
{
	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTestSuite(IntegerFieldTest.class);
		suite.addTestSuite(IntegerRangeDigitsTest.class);
		suite.addTestSuite(LongRangeDigitsTest.class);
		suite.addTestSuite(StringCopyTest.class);
		suite.addTestSuite(StringEscapeTest.class);
		suite.addTestSuite(SetValueTest.class);
		suite.addTestSuite(LimitedQueueTest.class);
		suite.addTestSuite(ChangeListenersTest.class);
		suite.addTestSuite(LRUMapTest.class);
		suite.addTestSuite(TrimmerTest.class);
		suite.addTestSuite(IsInitialTest.class);
		suite.addTestSuite(TypeMountTest.class);
		suite.addTestSuite(TypeMountWrongTest.class);
		suite.addTestSuite(TypeCompareTest.class);
		suite.addTestSuite(TypeSetTest.class);
		suite.addTestSuite(TypeCollisionTest.class);
		suite.addTestSuite(TypesBoundFeaturesTest.class);
		suite.addTestSuite(TypesBoundTest.class);
		suite.addTestSuite(TypesBoundErrorTest.class);
		suite.addTestSuite(TypesBoundComplexTest.class);
		suite.addTestSuite(TypeSetModelTest.class);
		suite.addTestSuite(TypeSetModelComplexTest.class);
		suite.addTestSuite(TypeSetModelErrorTest.class);
		suite.addTestSuite(TypeColumnTypeTest.class);
		suite.addTestSuite(TypeColumnTypeErrorTest.class);
		suite.addTestSuite(DateFieldWrongDefaultNowTest.class);
		suite.addTestSuite(DayFieldWrongDefaultNowTest.class);
		suite.addTestSuite(PatternComputedTest.class);
		suite.addTestSuite(EnumSchemaTest.class);
		suite.addTestSuite(EnumClassTest.class);
		suite.addTestSuite(QuerySerializeTest.class);
		suite.addTestSuite(ClusterUnpaddedTest.class);
		suite.addTestSuite(ClusterPaddedTest.class);
		suite.addTestSuite(ClusterIntTest.class);
		suite.addTestSuite(ClusterIdToStringTest.class);
		suite.addTestSuite(ClusterIterTest.class);
		suite.addTestSuite(ClusterPropertiesTest.class);
		suite.addTestSuite(ClusterPropertiesNoContextTest.class);
		suite.addTestSuite(ClusterSenderMulticastTest.class);
		suite.addTestSuite(ClusterNetworkPingTest.class);
		suite.addTestSuite(ClusterNetworkChangeListenerTest.class);
		suite.addTestSuite(EnviromentInfoTest.class);
		suite.addTestSuite(ArraysTest.class);
		suite.addTestSuite(ByteAlgorithmTest.class);
		suite.addTestSuite(CompositeConditionTest.class);
		suite.addTestSuite(CompositeConditionCopeTest.class);
		suite.addTestSuite(ItemCacheSummaryTest.class);
		suite.addTestSuite(LongFieldTest.class);
		suite.addTestSuite(LongFieldRandomTest.class);
		suite.addTestSuite(RevisionTest.class);
		suite.addTestSuite(RevisionsTest.class);
		suite.addTestSuite(RevisionInfoTest.class);
		suite.addTestSuite(DirectRevisionsFactoryTest.class);
		suite.addTestSuite(DirectRevisionsFutureTest.class);
		suite.addTestSuite(ConnectPropertiesTest.class);
		suite.addTestSuite(MediaUrlSecretTest.class);
		suite.addTestSuite(IntRatioTest.class);
		suite.addTestSuite(ItemCacheLimitTest.class);
		suite.addTestSuite(DefaultToReuseTest.class);
		suite.addTestSuite(CopyConstraintNotFoundTest.class);
		suite.addTestSuite(CopyConstraintNotAFunctionFieldTest.class);
		suite.addTestSuite(CopyConstraintNotFinalTest.class);
		suite.addTestSuite(TimerTest.class);
		suite.addTestSuite(DataDigestTest.class);
		suite.addTestSuite(QuerySelectTest.class);
		suite.addTestSuite(QueryRangeTest.class);
		suite.addTestSuite(SequenceCounterTest.class);
		suite.addTestSuite(PostgresqlDoubleTest.class);
		suite.addTest(com.exedio.cope.junit.PackageUtilTest.suite());
		suite.addTest(com.exedio.cope.misc.PackageUtilTest.suite());
		suite.addTest(com.exedio.cope.pattern.PackageUtilTest.suite());
		suite.addTest(com.exedio.cope.revstat.PackageUtilTest.suite());
		suite.addTest(com.exedio.cope.util.PackageUtilTest.suite());
		suite.addTest(com.exedio.cope.mxsampler.PackageTest.suite());
		return suite;
	}
}
