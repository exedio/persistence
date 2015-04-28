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

import com.exedio.cope.badquery.BadQueryTest;
import com.exedio.cope.instanceOfQuery.InstanceOfQueryTest;
import com.exedio.cope.serialize.ItemSerializationTest;
import com.exedio.cope.serialize.ModelSerializationTest;
import com.exedio.cope.serialize.SerializationSizeTest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTest extends TestCase
{

	public static Test suite()
	{
		final TestSuite suite = new TestSuite();

		suite.addTest( com.exedio.cope.junit.PackageTest.suite() );

		suite.addTestSuite( ConnectPropertiesValidTest.class );
		suite.addTestSuite( PrimaryKeyTest.class );
		suite.addTestSuite( CapabilitiesTest.class );
		suite.addTestSuite( ModelCharSetTest.class );
		suite.addTestSuite( TestGetModelTest.class );
		suite.addTestSuite( FeaturesTest.class );
		suite.addTestSuite( FinalTest.class );
		suite.addTestSuite( QueryTest.class );
		suite.addTestSuite( QueryGroupingTest.class );
		suite.addTestSuite( QueryKeyTest.class );
		suite.addTestSuite( QuerySearchSizeLimitTest.class );
		suite.addTestSuite( QuerySearchSizeLimitSetTest.class );
		suite.addTestSuite( QuerySearchSizeCacheLimitTest.class );
		suite.addTestSuite( QuerySearchSizeCacheLimitSetTest.class );
		suite.addTestSuite( ConnectTest.class );
		suite.addTestSuite( TypesBoundAnnotationTest.class );
		suite.addTestSuite( FeatureTest.class );
		suite.addTestSuite( HiddenFeatureTest.class );
		suite.addTestSuite( ItemTest.class );
		suite.addTestSuite( FindItemHierarchyTest.class );
		suite.addTestSuite( FindItemHierarchyEmptyTest.class );
		suite.addTestSuite( ModelTest.class );
		suite.addTestSuite( ModelSerializationTest.class );
		suite.addTestSuite( ItemSerializationTest.class );
		suite.addTestSuite( SerializationSizeTest.class );
		suite.addTestSuite( CreateTest.class );
		suite.addTestSuite( BeforeSetTest.class );
		suite.addTestSuite( SchemaInfoTest.class );
		suite.addTestSuite( SchemaInfoConnectionTest.class );
		suite.addTestSuite( DumperTest.class );

		suite.addTestSuite( FieldIntegerTest.class );
		suite.addTestSuite( FieldLongTest.class );
		suite.addTestSuite( FieldDoubleTest.class );
		suite.addTestSuite( FieldBooleanTest.class );
		suite.addTestSuite( FieldDateTest.class );
		suite.addTestSuite( DayFieldTest.class );
		suite.addTestSuite( FieldItemTest.class );
		suite.addTestSuite( FieldEnumTest.class );
		suite.addTestSuite( FieldMediaTest.class );

		suite.addTestSuite( DeleteTest.class );
		suite.addTestSuite( DeleteHierarchyTest.class );
		suite.addTestSuite( DeleteSchemaTest.class );
		suite.addTestSuite( NameTest.class );
		suite.addTestSuite( StringModelTest.class );
		suite.addTestSuite( StringTest.class );
		suite.addTestSuite( StringCharSetTest.class );
		suite.addTestSuite( IntegerModelTest.class );
		suite.addTestSuite( IntegerTest.class );
		suite.addTestSuite( LongModelTest.class );
		suite.addTestSuite( LongTest.class );
		suite.addTestSuite( DoubleTest.class );
		suite.addTestSuite( EnumTest.class );
		suite.addTestSuite( DefaultToModelTest.class );
		suite.addTestSuite( DefaultToTest.class );
		suite.addTestSuite( SequenceModelTest.class );
		suite.addTestSuite( SequenceTest.class );
		suite.addTestSuite( MatchTest.class );
		suite.addTestSuite( DataModelTest.class );
		suite.addTestSuite( DataTest.class );
		suite.addTestSuite( DataFinalTest.class );
		suite.addTestSuite( DataMandatoryTest.class );
		suite.addTestSuite( UniqueTest.class );
		suite.addTestSuite( UniqueDoubleModelTest.class );
		suite.addTestSuite( UniqueDoubleTest.class );
		suite.addTestSuite( UniqueDoubleNullTest.class );
		suite.addTestSuite( CheckConstraintModelTest.class );
		suite.addTestSuite( CheckConstraintSchemaTest.class );
		suite.addTestSuite( CheckConstraintTest.class );
		suite.addTestSuite( CheckConstraintHierarchyTest.class );
		suite.addTestSuite( CheckConstraintHierarchySchemaTest.class );
		suite.addTestSuite( CheckConstraintConditionTest.class );
		suite.addTestSuite( HierarchyTest.class );
		suite.addTestSuite( HierarchyEmptyTest.class );
		suite.addTestSuite( HierarchyCompareTest.class );
		suite.addTestSuite( SearchTest.class );
		suite.addTestSuite( AsStringTest.class );
		suite.addTestSuite( PlusIntegerTest.class );
		suite.addTestSuite( PlusIntegerOrderTest.class );
		suite.addTestSuite( PlusIntegerOrderNullTest.class );
		suite.addTestSuite( PlusLongTest.class );
		suite.addTestSuite( PlusLongOrderTest.class );
		suite.addTestSuite( PlusDoubleTest.class );
		suite.addTestSuite( PlusDoubleOrderTest.class );
		suite.addTestSuite( MinusIntegerTest.class );
		suite.addTestSuite( MinusLongTest.class );
		suite.addTestSuite( MinusDoubleTest.class );
		suite.addTestSuite( DivideIntegerTest.class );
		suite.addTestSuite( DivideLongTest.class );
		suite.addTestSuite( DivideDoubleTest.class );
		suite.addTestSuite( OrderByTest.class );
		suite.addTestSuite( SelectTest.class );
		suite.addTestSuite( SelectBindTest.class );
		suite.addTestSuite( DistinctTest.class );
		suite.addTestSuite( DistinctOrderByTest.class );
		suite.addTestSuite( FunctionTest.class );
		suite.addTestSuite( CoalesceTest.class );
		suite.addTestSuite( CoalesceFunctionTest.class );
		suite.addTestSuite( CompareConditionTest.class );
		suite.addTestSuite( CompareFunctionConditionTest.class );
		suite.addTestSuite( InstanceOfModelTest.class );
		suite.addTestSuite( InstanceOfTest.class );
		suite.addTestSuite( JoinTest.class );
		suite.addTestSuite( JoinOuterTest.class );
		suite.addTestSuite( JoinMultipleTest.class );
		suite.addTestSuite( JoinFunctionTest.class );
		suite.addTestSuite( HardJoinTest.class );
		suite.addTestSuite( RandomTest.class );
		suite.addTestSuite( SchemaTest.class );
		suite.addTestSuite( RenamedSchemaTest.class );
		suite.addTestSuite( RenamedPatternSchemaTest.class );
		suite.addTestSuite( SchemaModifyTest.class );
		suite.addTestSuite( SchemaTypeIntegerTest.class );
		suite.addTestSuite( SchemaTypeStringMysqlTest.class );
		suite.addTestSuite( SchemaTypeStringPostgresqlTest.class );
		suite.addTestSuite( QueryInfoTest.class );
		suite.addTestSuite( TransactionTest.class );
		suite.addTestSuite( TransactionEmptyTest.class );
		suite.addTestSuite( TransactionCountersTest.class );
		suite.addTestSuite( CacheIsolationTest.class );
		suite.addTestSuite( CacheTouchTest.class );
		suite.addTestSuite( UpdateCounterRecoverTest.class );
		suite.addTestSuite( ItemCacheDataTest.class );
		suite.addTestSuite( ItemCacheStampPurgeTest.class );
		suite.addTestSuite( PolymorphQueryCacheInvalidationTest.class );
		suite.addTestSuite( TransactionOnlyTest.class );
		suite.addTestSuite( ChangeListenerTest.class );
		suite.addTestSuite( ModificationListenerTest.class );
		suite.addTestSuite( ReviseTest.class );
		suite.addTestSuite( DatabaseLogTest.class );
		suite.addTestSuite( QueryCacheTest.class );
		suite.addTestSuite( UniqueHierarchyTest.class );
		suite.addTestSuite( CopyModelTest.class );
		suite.addTestSuite( CopyTest.class );
		suite.addTestSuite( CopyMultiModelTest.class );
		suite.addTestSuite( CopyMultiTest.class );
		suite.addTestSuite( CopyMultiToModelTest.class );
		suite.addTestSuite( CopyMultiToTest.class );
		suite.addTestSuite( SchemaNamePolymorphicTest.class );
		suite.addTestSuite( CheckTypeColumnTest.class );
		suite.addTestSuite( CheckTypeColumnAbstractTest.class );
		suite.addTestSuite( CheckTypeColumnAbstractLinearTest.class );
		suite.addTestSuite( SchemaPurgeTest.class );
		suite.addTestSuite( TransactionTryTest.class );

		suite.addTestSuite( BadQueryTest.class );
		suite.addTestSuite( InstanceOfQueryTest.class );
		suite.addTestSuite( DefaultInheritanceTest.class );
		suite.addTestSuite( PolymorphicBoundSelectTest.class );
		suite.addTestSuite( DeleteAfterUniqueViolationTest.class );
		suite.addTestSuite( FieldDateDaylightSavingTest.class );
		suite.addTest( com.exedio.cope.reflect.PackageTest.suite() );
		suite.addTest( com.exedio.cope.pattern.PackageTest.suite() );
		suite.addTest( com.exedio.cope.util.PackageTest.suite() );
		suite.addTest( com.exedio.cope.misc.PackageTest.suite() );
		suite.addTest( com.exedio.cope.sampler.PackageTest.suite() );

		return suite;
	}

	private static final void collectModels(final Test test, final HashMap<Model, ConnectProperties> models)
	{
		if(test instanceof com.exedio.cope.junit.CopeTest)
		{
			final com.exedio.cope.junit.CopeTest copeTest = (com.exedio.cope.junit.CopeTest)test;
			final Model model = copeTest.model;
			if(!models.containsKey(model))
				models.put(model, copeTest.getConnectProperties());
		}
		else if(test instanceof com.exedio.cope.junit.CopeModelTest)
		{
			final com.exedio.cope.junit.CopeModelTest copeTest = (com.exedio.cope.junit.CopeModelTest)test;
			final Model model = copeTest.getModel();
			if(!models.containsKey(model))
				models.put(model, copeTest.getConnectProperties());
		}
		else if(test instanceof TestSuite)
		{
			for(final Enumeration<?> e = ((TestSuite)test).tests(); e.hasMoreElements(); )
				collectModels((Test)e.nextElement(), models);
		}
	}

	static HashMap<Model, ConnectProperties> getModels(final Test test)
	{
		final HashMap<Model, ConnectProperties> models = new HashMap<>();
		collectModels(test, models);
		return models;
	}

	public static void main(final String[] args)
	{
		final HashMap<Model, ConnectProperties> models = getModels(PackageTest.suite());
		for(final Map.Entry<Model, ConnectProperties> e : models.entrySet())
		{
			final Model m = e.getKey();
			//System.out.println("teardown " + m.getTypes());
			m.connect(e.getValue());
			m.tearDownSchema();
			m.disconnect();
		}
	}
}
