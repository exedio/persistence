package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;

public class TypeInheritancePatternTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(TypeInheritanceTestPatternItem.TYPE);
	
	public TypeInheritancePatternTest()
	{
		super(MODEL);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void testIt()
	{
		//abstract type
		final Type<?> abstractType = TypeInheritanceTestPatternItem.testPattern.getAbstractType();
		final StringField abstractTypeString = (StringField)abstractType.getFeature(TypeInheritanceTestPattern.ABSTRACTTYPE_STRING);
		final BooleanField abstractTypeBoolean = (BooleanField)abstractType.getFeature(TypeInheritanceTestPattern.ABSTRACTTYPE_BOOLEAN);
		
		assertEqualsUnmodifiable(
				list(
					abstractTypeString,
					abstractTypeBoolean
				),
				abstractType.getFields() );
		
		assertTrue(abstractType.isAbstract());

		//sub type
		final Type<?> subType = TypeInheritanceTestPatternItem.testPattern.getSubType();
		final IntegerField subTypeInteger = (IntegerField)subType.getFeature(TypeInheritanceTestPattern.SUBTYPE_INTEGER);
		
		assertEqualsUnmodifiable(
				list(
					abstractTypeString,
					abstractTypeBoolean,
					subTypeInteger
				),
				subType.getFields() );
		
		//type hierarchy
		assertEquals(abstractType, subType.getSupertype());
		
		assertEqualsUnmodifiable(
				list(
					subType
				),
				abstractType.getTypesOfInstances() );
		
		
		//assignable
		assertTrue(abstractType.isAssignableFrom(subType));
		
		//creating instances
		try
		{
			abstractType.newItem(abstractTypeString.map("string1"), abstractTypeBoolean.map(Boolean.valueOf(true)));			
			fail();
		}
		catch (Exception e)
		{
			//ok
		}
		
		final Item item = subType.newItem(abstractTypeString.map("string1"), abstractTypeBoolean.map(Boolean.valueOf(true)), subTypeInteger.map(1));
		deleteOnTearDown(item);
		
		//casting
		final Item item2 = abstractType.cast(item);
	}
}
