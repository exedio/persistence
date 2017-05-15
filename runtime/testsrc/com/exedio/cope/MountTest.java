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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockType;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.pattern.CompositeField;
import com.exedio.cope.pattern.CompositeType;
import java.util.function.Supplier;
import org.junit.Test;

public class MountTest
{
	@Test public void testItemType()
	{
		final Type<?> t = MyItem.TYPE;
		assertEquals( MyItem.class, t.getJavaClass());
		assertEquals("MyItem",      t.toString());
		assertEquals("MyItem",      t.getID());
		assertLocale("MyItem",      t);
		assertSerializedSame(t, 259);
	}

	@Test public void testItemTypeRenamed()
	{
		final Type<?> t = PureItem.TYPE;
		assertEquals(   PureItem.class, t.getJavaClass());
		assertEquals("ActualItem",      t.toString());
		assertEquals("ActualItem",      t.getID());
		assertLocale("ActualItem",      t);
		assertSerializedSame(t, 263);
	}

	@Test public void testItemField()
	{
		final Feature f = MyItem.field;
		assertEquals(       "field", f.getName());
		assertLocale("MyItem.field", f);
		assertEquals("MyItem.field", f.toString());
		assertEquals("MyItem.field", f.getID());
		assertEquals( MyItem.TYPE,   f.getType());
		assertEquals( MyItem.TYPE,   f.getAbstractType());
		assertSerializedSame(f, 362);
	}

	@Test public void testItemFieldRenamed()
	{
		final Feature f = MyItem.pure;
		assertEquals(       "actual", f.getName());
		assertLocale("MyItem.actual", f);
		assertEquals("MyItem.actual", f.toString());
		assertEquals("MyItem.actual", f.getID());
		assertEquals( MyItem.TYPE,    f.getType());
		assertEquals( MyItem.TYPE,    f.getAbstractType());
		assertSerializedSame(f, 363);
	}

	@Test public void testItemRenamedField()
	{
		final Feature f = PureItem.field;
		assertEquals(           "field", f.getName());
		assertLocale("ActualItem.field", f);
		assertEquals("ActualItem.field", f.toString());
		assertEquals("ActualItem.field", f.getID());
		assertEquals(   PureItem.TYPE,   f.getType());
		assertEquals(   PureItem.TYPE,   f.getAbstractType());
		assertSerializedSame(f, 366);
	}

	@Test public void testItemRenamedFieldRenamed()
	{
		final Feature f = PureItem.pure;
		assertEquals(           "actual", f.getName());
		assertLocale("ActualItem.actual", f);
		assertEquals("ActualItem.actual", f.toString());
		assertEquals("ActualItem.actual", f.getID());
		assertEquals(   PureItem.TYPE,    f.getType());
		assertEquals(   PureItem.TYPE,    f.getAbstractType());
		assertSerializedSame(f, 367);
	}

	@Test public void testCompositeType()
	{
		final CompositeType<?> t = MyComposite.TYPE;
		assertEquals(MyComposite.class, t.getJavaClass());
		assertEquals(PREFIX + "MyComposite", t.toString());
		assertLocale(         "MyComposite", t);
		assertSerializedSame(t, 234);
	}

	@Test public void testCompositeTypeRenamed()
	{
		final CompositeType<?> t = PureComposite.TYPE;
		assertEquals(PureComposite.class, t.getJavaClass());
		assertEquals(PREFIX + "PureComposite", t.toString()); // pure is ok
		assertLocale(       "ActualComposite", t);
		assertSerializedSame(t, 236);
	}

	@Test public void testCompositeField()
	{
		final Feature f = MyComposite.field;
		assertEquals(                     "field", f.getName());
		assertLocale(         "MyComposite.field", f);
		assertEquals(PREFIX + "MyComposite#field", f.toString());
		assertFails (PREFIX + "MyComposite#field", f::getID);
		assertFails (PREFIX + "MyComposite#field", f::getType);
		assertEquals(          MyComposite.TYPE,   f.getAbstractType());
		assertSerializedSame(f, 272);
	}

	@Test public void testCompositeFieldRenamed()
	{
		final Feature f = MyComposite.pure;
		assertEquals(                     "actual", f.getName());
		assertLocale(         "MyComposite.actual", f);
		assertEquals(PREFIX + "MyComposite#pure",   f.toString()); // pure is ok
		assertFails (PREFIX + "MyComposite#pure",   f::getID);    // pure is ok
		assertFails (PREFIX + "MyComposite#pure",   f::getType);  // pure is ok
		assertEquals(          MyComposite.TYPE,    f.getAbstractType());
		assertSerializedSame(f, 271);
	}

	@Test public void testCompositeRenamedField()
	{
		final Feature f = PureComposite.field;
		assertEquals(                       "field", f.getName());
		assertLocale(       "ActualComposite.field", f);
		assertEquals(PREFIX + "PureComposite#field", f.toString()); // pure is ok
		assertFails (PREFIX + "PureComposite#field", f::getID);    // pure is ok
		assertFails (PREFIX + "PureComposite#field", f::getType);  // pure is ok
		assertEquals(          PureComposite.TYPE,   f.getAbstractType());
		assertSerializedSame(f, 274);
	}

	@Test public void testCompositeRenamedFieldRenamed()
	{
		final Feature f = PureComposite.pure;
		assertEquals(                       "actual", f.getName());
		assertLocale(       "ActualComposite.actual", f);
		assertEquals(PREFIX + "PureComposite#pure",   f.toString()); // pure is ok
		assertFails (PREFIX + "PureComposite#pure",   f::getID);    // pure is ok
		assertFails (PREFIX + "PureComposite#pure",   f::getType);  // pure is ok
		assertEquals(          PureComposite.TYPE,    f.getAbstractType());
		assertSerializedSame(f, 273);
	}

	@Test public void testBlockType()
	{
		final BlockType<?> t = MyBlock.TYPE;
		assertEquals(MyBlock.class, t.getJavaClass());
		assertEquals(PREFIX + "MyBlock", t.toString());
		assertLocale(         "MyBlock", t);
		assertSerializedSame(t, 270);
	}

	@Test public void testBlockTypeRenamed()
	{
		final BlockType<?> t = PureBlock.TYPE;
		assertEquals(PureBlock.class, t.getJavaClass());
		assertEquals(PREFIX + "PureBlock", t.toString()); // pure is ok
		assertLocale(       "ActualBlock", t);
		assertSerializedSame(t, 272);
	}

	@Test public void testBlockField()
	{
		final Feature f = MyBlock.field;
		assertEquals(                 "field", f.getName());
		assertLocale(         "MyBlock.field", f);
		assertEquals(PREFIX + "MyBlock#field", f.toString());
		assertFails (PREFIX + "MyBlock#field", f::getID);
		assertFails (PREFIX + "MyBlock#field", f::getType);
		assertEquals(          MyBlock.TYPE,   f.getAbstractType());
		assertSerializedSame(f, 312);
	}

	@Test public void testBlockFieldRenamed()
	{
		final Feature f = MyBlock.pure;
		assertEquals(                 "actual", f.getName());
		assertLocale(         "MyBlock.actual", f);
		assertEquals(PREFIX + "MyBlock#pure",   f.toString()); // pure is ok
		assertFails (PREFIX + "MyBlock#pure",   f::getID);    // pure is ok
		assertFails (PREFIX + "MyBlock#pure",   f::getType);  // pure is ok
		assertEquals(          MyBlock.TYPE,    f.getAbstractType());
		assertSerializedSame(f, 311);
	}

	@Test public void testBlockRenamedField()
	{
		final Feature f = PureBlock.field;
		assertEquals(                   "field", f.getName());
		assertLocale(       "ActualBlock.field", f);
		assertEquals(PREFIX + "PureBlock#field", f.toString()); // pure is ok
		assertFails (PREFIX + "PureBlock#field", f::getID);    // pure is ok
		assertFails (PREFIX + "PureBlock#field", f::getType);  // pure is ok
		assertEquals(          PureBlock.TYPE,   f.getAbstractType());
		assertSerializedSame(f, 314);
	}

	@Test public void testBlockRenamedFieldRenamed()
	{
		final Feature f = PureBlock.pure;
		assertEquals(                   "actual", f.getName());
		assertLocale(       "ActualBlock.actual", f);
		assertEquals(PREFIX + "PureBlock#pure",   f.toString()); // pure is ok
		assertFails (PREFIX + "PureBlock#pure",   f::getID);    // pure is ok
		assertFails (PREFIX + "PureBlock#pure",   f::getType);  // pure is ok
		assertEquals(          PureBlock.TYPE,    f.getAbstractType());
		assertSerializedSame(f, 313);
	}


	@WrapperType(constructor=NONE, indent=2, comments=false)
	static class MyItem extends Item
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@WrapperIgnore
		@CopeName("actual")
		static final IntegerField pure = new IntegerField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	@CopeName("ActualItem")
	static class PureItem extends Item
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@WrapperIgnore
		@CopeName("actual")
		static final IntegerField pure = new IntegerField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected PureItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<PureItem> TYPE = com.exedio.cope.TypesBound.newType(PureItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected PureItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE, PureItem.TYPE);

	static
	{
		MODEL.enableSerialization(MountTest.class, "MODEL");
	}


	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyComposite extends Composite
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@WrapperIgnore
		@CopeName("actual")
		static final IntegerField pure = new IntegerField();

		static final CompositeType<MyComposite> TYPE = CompositeField.create(MyComposite.class).getValueType();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyComposite(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	@CopeName("ActualComposite")
	static final class PureComposite extends Composite
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@WrapperIgnore
		@CopeName("actual")
		static final IntegerField pure = new IntegerField();

		static final CompositeType<PureComposite> TYPE = CompositeField.create(PureComposite.class).getValueType();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private PureComposite(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;
	}


	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyBlock extends Block
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@WrapperIgnore
		@CopeName("actual")
		static final IntegerField pure = new IntegerField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.pattern.BlockType<MyBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlock.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	@CopeName("ActualBlock")
	static final class PureBlock extends Block
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@WrapperIgnore
		@CopeName("actual")
		static final IntegerField pure = new IntegerField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.pattern.BlockType<PureBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(PureBlock.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private PureBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	private static void assertFails(final String message, final Supplier<Object> supplier)
	{
		try
		{
			supplier.get();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + message, e.getMessage());
		}
	}

	private static final String PREFIX = "com.exedio.cope.MountTest$";


	private static void assertLocale(final String expected, final AbstractType<?> t)
	{
		assertEqualsUnmodifiable(asList(
				"com.exedio.cope.MountTest." + expected,
				                "MountTest." + expected),
				t.getLocalizationKeys());
	}

	private static void assertLocale(final String expected, final Feature f)
	{
		assertEqualsUnmodifiable(asList(
				"com.exedio.cope.MountTest." + expected,
				                "MountTest." + expected),
				f.getLocalizationKeys());
	}
}
