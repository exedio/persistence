package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
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
		//test types
		Type<?> abstractType = TypeInheritanceTestPatternItem.testPattern.getAbstractType();
		StringField abstractTypeString = (StringField)abstractType.getFeature(TypeInheritanceTestPattern.ABSTRACTTYPE_STRING);
		BooleanField abstractTypeBoolean = (BooleanField)abstractType.getFeature(TypeInheritanceTestPattern.ABSTRACTTYPE_BOOLEAN);
		
		assertEqualsUnmodifiable(
				list(
					abstractTypeString,
					abstractTypeBoolean
				),
				abstractType.getFields() );
		assertTrue(abstractType.isAbstract());

		Type<?> subType = TypeInheritanceTestPatternItem.testPattern.getSubType();
		IntegerField subTypeInteger = (IntegerField)subType.getFeature(TypeInheritanceTestPattern.SUBTYPE_INTEGER);
		
		
		assertEqualsUnmodifiable(
				list(
					abstractTypeString,
					abstractTypeBoolean,
					subTypeInteger
						
				),
				subType.getFields() );
		
		//test type hierarchy
		assertEquals(abstractType, subType.getSupertype());
		
		assertEqualsUnmodifiable(
				list(
						subType
					),
					abstractType.getTypesOfInstances() );
		
		
		//test assignable
		assertTrue(abstractType.isAssignableFrom(subType));
		
		//test creating instances
		try
		{
			abstractType.newItem(abstractTypeString.map("string1"), abstractTypeBoolean.map(Boolean.valueOf(true)));			
			fail();
		}
		catch (Exception e)
		{
			//ok
		}
		
		deleteOnTearDown(subType.newItem(abstractTypeString.map("string1"), abstractTypeBoolean.map(Boolean.valueOf(true)), subTypeInteger.map(1)));
	}
}
