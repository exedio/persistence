/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.pattern.ClockTest;
import com.exedio.cope.pattern.JavaViewGetterMissingTest;
import com.exedio.cope.pattern.JavaViewInPatternTest;
import com.exedio.cope.pattern.MediaSummaryTest;
import com.exedio.cope.pattern.MessageDigestAlgorithmTest;

public class PackageUtilTest extends TestCase
{
	static
	{
		ThreadSwarm.logger.setUseParentHandlers(false);
		ConnectToken.logger.setUseParentHandlers(false);
	}

	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTestSuite(StringEscapeTest.class);
		suite.addTestSuite(SetValueTest.class);
		suite.addTestSuite(LimitedQueueTest.class);
		suite.addTestSuite(ChangeListenersTest.class);
		suite.addTestSuite(LRUMapTest.class);
		suite.addTestSuite(TrimmerTest.class);
		suite.addTestSuite(PrefixSourceTest.class);
		suite.addTestSuite(IsInitialTest.class);
		suite.addTestSuite(GetModelTest.class);
		suite.addTestSuite(TypeMountTest.class);
		suite.addTestSuite(TypeMountWrongTest.class);
		suite.addTestSuite(TypeCompareTest.class);
		suite.addTestSuite(TypeSetTest.class);
		suite.addTestSuite(TypeCollisionTest.class);
		suite.addTestSuite(TypesBoundTest.class);
		suite.addTestSuite(TypesBoundErrorTest.class);
		suite.addTestSuite(TypesBoundComplexTest.class);
		suite.addTestSuite(DateFieldWrongDefaultNowTest.class);
		suite.addTestSuite(DayFieldWrongDefaultNowTest.class);
		suite.addTestSuite(EnumSchemaTest.class);
		suite.addTestSuite(ClusterUnpaddedTest.class);
		suite.addTestSuite(ClusterPaddedTest.class);
		suite.addTestSuite(ClusterIntTest.class);
		suite.addTestSuite(ClusterIdToStringTest.class);
		suite.addTestSuite(ClusterIterTest.class);
		suite.addTestSuite(ClusterPropertiesTest.class);
		suite.addTestSuite(ClusterSenderMulticastTest.class);
		suite.addTestSuite(ClusterNetworkPingTest.class);
		suite.addTestSuite(ClusterNetworkChangeListenerTest.class);
		suite.addTestSuite(ClusterNetworkModificationListenerTest.class);
		suite.addTestSuite(EnviromentInfoTest.class);
		suite.addTestSuite(ArraysTest.class);
		suite.addTestSuite(MessageDigestAlgorithmTest.class);
		suite.addTestSuite(ByteAlgorithmTest.class);
		suite.addTestSuite(CompositeConditionTest.class);
		suite.addTestSuite(CompositeConditionCopeTest.class);
		suite.addTestSuite(ItemCacheSummaryTest.class);
		suite.addTestSuite(MediaSummaryTest.class);
		suite.addTestSuite(JavaViewGetterMissingTest.class);
		suite.addTestSuite(JavaViewInPatternTest.class);
		suite.addTestSuite(ClockTest.class);
		suite.addTest(com.exedio.cope.misc.PackageUtilTest.suite());
		suite.addTest(com.exedio.cope.util.PackageUtilTest.suite());
		return suite;
	}
}
