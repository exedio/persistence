package com.exedio.cope.lib;

import java.util.Iterator;

import com.exedio.cope.testmodel.AttributeEmptyItem;
import com.exedio.cope.testmodel.EmptyItem;


public class AttributeQualifiedTest extends AttributeTest
{
	public void testSomeQualifiedAttribute()
			throws IntegrityViolationException, NotNullViolationException, LengthViolationException, ReadOnlyViolationException
	{
		assertEquals(AttributeEmptyItem.TYPE, AttributeEmptyItem.someQualifiedString.getType());
		final EmptyItem someItem2 = new EmptyItem();
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, "someQualifiedValue");
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.passivate();
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, null);
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));

		assertDelete(someItem2);
	}

	public void tearDown() throws Exception
	{
		for(Iterator i = AttributeEmptyItem.TYPE.search(null).iterator(); i.hasNext(); )
		{
			final Item item = (Item)i.next();
			item.delete();
		}

		super.tearDown();
	}
}
