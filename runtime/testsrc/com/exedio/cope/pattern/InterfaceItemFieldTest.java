package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Model;
import org.junit.Test;

public class InterfaceItemFieldTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(InterfaceItemFieldItem.TYPE,
			InterfaceItemFieldItemInterfaceImplementationA.TYPE,
			InterfaceItemFieldItemInterfaceImplementationB.TYPE);

	static
	{
		MODEL.enableSerialization(InterfaceItemFieldTest.class, "MODEL");
	}

	public InterfaceItemFieldTest()
	{
		super(MODEL);
	}

	@Test
	public void testGetSource()
	{
		final InterfaceItemFieldItemInterfaceImplementationA fieldValue = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem expected = new InterfaceItemFieldItem(fieldValue);
		assertEquals(expected, InterfaceItemFieldItem.field.getSource(InterfaceItemFieldItem.class, fieldValue));
	}

	@Test
	public void testGet()
	{
		final InterfaceItemFieldItemInterfaceImplementationA expected = new InterfaceItemFieldItemInterfaceImplementationA();
		final InterfaceItemFieldItem item = new InterfaceItemFieldItem(expected);
		assertEquals(expected, item.getField());
	}
}
