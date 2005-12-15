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

public class PackageTest extends TestCase
{

	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		
		suite.addTestSuite( TrimTest.class );
		suite.addTestSuite( PkSourceSequentialTest.class );
		suite.addTestSuite( PkSourceButterflyTest.class );
		suite.addTestSuite( ModelTest.class );
		suite.addTestSuite( ItemTest.class );
		
		suite.addTestSuite( AttributeStringTest.class );
		suite.addTestSuite( AttributeIntegerTest.class );
		suite.addTestSuite( AttributeLongTest.class );
		suite.addTestSuite( AttributeDoubleTest.class );
		suite.addTestSuite( AttributeBooleanTest.class );
		suite.addTestSuite( AttributeDateTest.class );
		suite.addTestSuite( DayAttributeTest.class );
		suite.addTestSuite( AttributeItemTest.class );
		suite.addTestSuite( AttributeEnumTest.class );
		suite.addTestSuite( AttributeDataTest.class );
		suite.addTestSuite( AttributeQualifiedTest.class );
		
		suite.addTestSuite( DeleteTest.class );
		suite.addTestSuite( NameTest.class );
		suite.addTestSuite( StringTest.class );
		suite.addTestSuite( MatchTest.class );
		suite.addTestSuite( DataTest.class );
		suite.addTestSuite( NullEmptyTest.class );
		suite.addTestSuite( UniqueItemTest.class );
		suite.addTestSuite( HierarchyTest.class );
		suite.addTestSuite( SearchTest.class );
		suite.addTestSuite( SumTest.class );
		suite.addTestSuite( SumOrderTest.class );
		suite.addTestSuite( OrderByTest.class );
		suite.addTestSuite( SelectTest.class );
		suite.addTestSuite( FunctionTest.class );
		suite.addTestSuite( LiteralConditionTest.class );
		suite.addTestSuite( JoinTest.class );
		suite.addTestSuite( JoinOuterTest.class );
		suite.addTestSuite( JoinMultipleTest.class );
		suite.addTestSuite( FindByIDTest.class );
		suite.addTestSuite( SchemaTest.class );
		suite.addTestSuite( StatementInfoTest.class );
		suite.addTestSuite( TransactionTest.class );
		suite.addTestSuite( CacheIsolationTest.class );
		suite.addTestSuite( TransactionOnlyTest.class );

		suite.addTest( com.exedio.cope.pattern.PackageTest.suite() );
		suite.addTest( com.exedio.cope.util.PackageTest.suite() );
		
		return suite;
	}

}
