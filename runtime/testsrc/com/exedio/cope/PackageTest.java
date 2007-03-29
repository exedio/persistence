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

import java.util.Enumeration;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.exedio.cope.badquery.BadQueryTest;

public class PackageTest extends TestCase
{

	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		
		suite.addTestSuite( TrimTest.class );
		suite.addTestSuite( IsInitialTest.class );
		suite.addTestSuite( GetModelTest.class );
		suite.addTestSuite( TestGetModelTest.class );
		suite.addTestSuite( TypeCollisionTest.class );
		suite.addTestSuite( QueryTest.class );
		suite.addTestSuite( QueryKeyTest.class );
		suite.addTestSuite( PkSourceSequentialTest.class );
		suite.addTestSuite( PkSourceButterflyTest.class );
		suite.addTestSuite( ModelTest.class );
		suite.addTestSuite( HiddenFeatureTest.class );
		suite.addTestSuite( ItemTest.class );
		suite.addTestSuite( ItemSerializationTest.class );
		suite.addTestSuite( PoolTest.class );
		
		suite.addTestSuite( FieldIntegerTest.class );
		suite.addTestSuite( FieldLongTest.class );
		suite.addTestSuite( FieldDoubleTest.class );
		suite.addTestSuite( FieldBooleanTest.class );
		suite.addTestSuite( FieldDateTest.class );
		suite.addTestSuite( DayFieldTest.class );
		suite.addTestSuite( FieldItemTest.class );
		suite.addTestSuite( FieldEnumTest.class );
		suite.addTestSuite( FieldMediaTest.class );
		suite.addTestSuite( FieldQualifierTest.class );
		
		suite.addTestSuite( DeleteTest.class );
		suite.addTestSuite( DeleteHierarchyTest.class );
		suite.addTestSuite( NameTest.class );
		suite.addTestSuite( StringTest.class );
		suite.addTestSuite( EnumTest.class );
		suite.addTestSuite( DefaultToTest.class );
		suite.addTestSuite( MatchTest.class );
		suite.addTestSuite( DataTest.class );
		suite.addTestSuite( DataFinalTest.class );
		suite.addTestSuite( DataMandatoryTest.class );
		suite.addTestSuite( UniqueItemTest.class );
		suite.addTestSuite( HierarchyTest.class );
		suite.addTestSuite( HierarchyEmptyTest.class );
		suite.addTestSuite( SearchTest.class );
		suite.addTestSuite( PlusTest.class );
		suite.addTestSuite( PlusOrderTest.class );
		suite.addTestSuite( OrderByTest.class );
		suite.addTestSuite( SelectTest.class );
		suite.addTestSuite( DistinctTest.class );
		suite.addTestSuite( FunctionTest.class );
		suite.addTestSuite( CompareConditionTest.class );
		suite.addTestSuite( CompareFunctionConditionTest.class );
		suite.addTestSuite( InstanceOfTest.class );
		suite.addTestSuite( JoinTest.class );
		suite.addTestSuite( JoinOuterTest.class );
		suite.addTestSuite( JoinMultipleTest.class );
		suite.addTestSuite( JoinFunctionTest.class );
		suite.addTestSuite( HardJoinTest.class );
		suite.addTestSuite( SchemaTest.class );
		suite.addTestSuite( StatementInfoTest.class );
		suite.addTestSuite( TransactionTest.class );
		suite.addTestSuite( CacheIsolationTest.class );
		suite.addTestSuite( PolymorphQueryCacheInvalidationTest.class );
		suite.addTestSuite( TransactionOnlyTest.class );
		suite.addTestSuite( ModificationListenerTest.class );
		suite.addTestSuite( MigrationTest.class );
		suite.addTestSuite( MigrateTest.class );
		suite.addTestSuite( DatabaseLogTest.class );
		suite.addTestSuite( QueryCacheTest.class );
		suite.addTestSuite( UniqueHierarchyTest.class );

		suite.addTestSuite( BadQueryTest.class );
		suite.addTest( com.exedio.cope.pattern.PackageTest.suite() );
		suite.addTest( com.exedio.cope.util.PackageTest.suite() );
		
		return suite;
	}

	private static final void collectModels(final Test test, final HashMap<Model, Properties> models)
	{
		if(test instanceof com.exedio.cope.junit.CopeTest)
		{
			final com.exedio.cope.junit.CopeTest copeTest = (com.exedio.cope.junit.CopeTest)test;
			final Model model = copeTest.model;
			if(!models.containsKey(model))
				models.put(model, copeTest.getProperties());
		}
		else if(test instanceof TestSuite)
		{
			for(Enumeration e = ((TestSuite)test).tests(); e.hasMoreElements(); )
				collectModels((Test)e.nextElement(), models);
		}
	}
	
	static HashMap<Model, Properties> getModels(final Test test)
	{
		final HashMap<Model, Properties> models = new HashMap<Model, Properties>();
		collectModels(test, models);
		return models;
	}
	
	public static void main(String[] args)
	{
		final HashMap<Model, Properties> models = getModels(PackageTest.suite());
		for(final Model m : models.keySet())
		{
			//System.out.println("teardown " + m.getTypes());
			m.connect(models.get(m));
			m.tearDownDatabase();
		}
	}
}
