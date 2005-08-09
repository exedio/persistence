/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.PoolCounterTest;


public class PackageTest extends TestCase
{

	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		
		suite.addTest(new TestSuite(TrimTest.class));
		suite.addTest(new TestSuite(Id2PkTest.class));
		suite.addTest(new TestSuite(ModelTest.class));
		suite.addTest(new TestSuite(ItemTest.class));
		
		suite.addTest(new TestSuite(AttributeStringTest.class));
		suite.addTest(new TestSuite(AttributeIntegerTest.class));
		suite.addTest(new TestSuite(AttributeLongTest.class));
		suite.addTest(new TestSuite(AttributeDoubleTest.class));
		suite.addTest(new TestSuite(AttributeBooleanTest.class));
		suite.addTest(new TestSuite(AttributeDateTest.class));
		suite.addTest(new TestSuite(AttributeItemTest.class));
		suite.addTest(new TestSuite(AttributeEnumTest.class));
		suite.addTest(new TestSuite(AttributeDataTest.class));
		suite.addTest(new TestSuite(AttributeQualifiedTest.class));
		
		suite.addTest(new TestSuite(StringTest.class));
		suite.addTest(new TestSuite(DataTest.class));
		suite.addTest(new TestSuite(NullEmptyTest.class));
		suite.addTest(new TestSuite(UniqueItemTest.class));
		suite.addTest(new TestSuite(HierarchyTest.class));
		suite.addTest(new TestSuite(SearchTest.class));
		suite.addTest(new TestSuite(SumTest.class));
		suite.addTest(new TestSuite(SumOrderTest.class));
		suite.addTest(new TestSuite(OrderByTest.class));
		suite.addTest(new TestSuite(SelectTest.class));
		suite.addTest(new TestSuite(FunctionTest.class));
		suite.addTest(new TestSuite(LiteralConditionTest.class));
		suite.addTest(new TestSuite(JoinTest.class));
		suite.addTest(new TestSuite(JoinOuterTest.class));
		suite.addTest(new TestSuite(JoinMultipleTest.class));
		suite.addTest(new TestSuite(FindByIDTest.class));
		suite.addTest(new TestSuite(SchemaTest.class));
		suite.addTest(new TestSuite(StatementInfoTest.class));
		suite.addTest(new TestSuite(TransactionTest.class));

		suite.addTest(com.exedio.cope.pattern.PackageTest.suite());

		suite.addTest(new TestSuite(PoolCounterTest.class));
		
		return suite;
	}

}
