
package com.exedio.cope.lib;

import com.exedio.cope.lib.junit.CopeLibTest;
import com.exedio.cope.testmodel.Main;

public abstract class AbstractLibTest extends CopeLibTest
{
	
	public static final Type[] modelTypes = Main.modelTypes;
	
	AbstractLibTest()
	{
		super(Main.model);
	}
	
	final static Integer i1 = new Integer(1);
	final static Integer i2 = new Integer(2);
	final static Integer i3 = new Integer(3);
	final static Integer i4 = new Integer(4);
	final static Integer i5 = new Integer(5);
	final static Integer i6 = new Integer(6);
	final static Integer i7 = new Integer(7);
	final static Integer i8 = new Integer(8);
	
	final String pkString(final Item item)
	{
		return String.valueOf(item.getType().getPrimaryKeyIterator().pk2id(((Item)item).pk));
	}

}
