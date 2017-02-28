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

package com.exedio.cope.pattern;

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.CopeName;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
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
		assertSerializedSame(t, 267);
	}

	@Test public void testItemTypeRenamed()
	{
		final Type<?> t = PureItem.TYPE;
		assertEquals(   PureItem.class, t.getJavaClass());
		assertEquals("ActualItem",      t.toString());
		assertEquals("ActualItem",      t.getID());
		assertSerializedSame(t, 271);
	}

	@Test public void testItemField()
	{
		final Feature f = MyItem.field;
		assertEquals(       "field", f.getName());
		assertEquals("MyItem.field", f.toString());
		assertEquals("MyItem.field", f.getID());
		assertEquals( MyItem.TYPE,   f.getType());
		assertSerializedSame(f, 370);
	}

	@Test public void testItemFieldRenamed()
	{
		final Feature f = MyItem.pure;
		assertEquals(       "actual", f.getName());
		assertEquals("MyItem.actual", f.toString());
		assertEquals("MyItem.actual", f.getID());
		assertEquals( MyItem.TYPE,    f.getType());
		assertSerializedSame(f, 371);
	}

	@Test public void testItemRenamedField()
	{
		final Feature f = PureItem.field;
		assertEquals(           "field", f.getName());
		assertEquals("ActualItem.field", f.toString());
		assertEquals("ActualItem.field", f.getID());
		assertEquals(   PureItem.TYPE,   f.getType());
		assertSerializedSame(f, 374);
	}

	@Test public void testItemRenamedFieldRenamed()
	{
		final Feature f = PureItem.pure;
		assertEquals(           "actual", f.getName());
		assertEquals("ActualItem.actual", f.toString());
		assertEquals("ActualItem.actual", f.getID());
		assertEquals(   PureItem.TYPE,    f.getType());
		assertSerializedSame(f, 375);
	}

	@Test public void testCompositeType()
	{
		final CompositeType<?> t = MyComposite.TYPE;
		// TODO t.getJavaClass()
		// TODO t.toString()
		assertSerializedSame(t, 242);
	}

	@Test public void testCompositeTypeRenamed()
	{
		final CompositeType<?> t = PureComposite.TYPE;
		// TODO t.getJavaClass()
		// TODO t.toString()
		assertSerializedSame(t, 244);
	}

	@Test public void testCompositeField()
	{
		final Feature f = MyComposite.field;
		assertFails (PREFIX + "MyComposite#field", ()->f.getName());
		assertEquals(PREFIX + "MyComposite#field",     f.toString());
		assertFails (PREFIX + "MyComposite#field", ()->f.getID());
		assertFails (PREFIX + "MyComposite#field", ()->f.getType());
		assertSerializedSame(f, 280);
	}

	@Test public void testCompositeFieldRenamed()
	{
		final Feature f = MyComposite.pure;
		assertFails (PREFIX + "MyComposite#pure", ()->f.getName());
		assertEquals(PREFIX + "MyComposite#pure",     f.toString()); // pure is ok
		assertFails (PREFIX + "MyComposite#pure", ()->f.getID());    // pure is ok
		assertFails (PREFIX + "MyComposite#pure", ()->f.getType());  // pure is ok
		assertSerializedSame(f, 279);
	}

	@Test public void testCompositeRenamedField()
	{
		final Feature f = PureComposite.field;
		assertFails (PREFIX + "PureComposite#field", ()->f.getName());
		assertEquals(PREFIX + "PureComposite#field",     f.toString()); // pure is ok
		assertFails (PREFIX + "PureComposite#field", ()->f.getID());    // pure is ok
		assertFails (PREFIX + "PureComposite#field", ()->f.getType());  // pure is ok
		assertSerializedSame(f, 282);
	}

	@Test public void testCompositeRenamedFieldRenamed()
	{
		final Feature f = PureComposite.pure;
		assertFails (PREFIX + "PureComposite#pure", ()->f.getName());
		assertEquals(PREFIX + "PureComposite#pure",     f.toString()); // pure is ok
		assertFails (PREFIX + "PureComposite#pure", ()->f.getID());    // pure is ok
		assertFails (PREFIX + "PureComposite#pure", ()->f.getType());  // pure is ok
		assertSerializedSame(f, 281);
	}

	@Test public void testBlockType()
	{
		final BlockType<?> t = MyBlock.TYPE;
		// TODO t.getJavaClass()
		assertEquals(PREFIX + "MyBlock", t.toString());
		assertSerializedSame(t, 278);
	}

	@Test public void testBlockTypeRenamed()
	{
		final BlockType<?> t = PureBlock.TYPE;
		// TODO t.getJavaClass()
		assertEquals(PREFIX + "PureBlock", t.toString()); // pure is ok
		assertSerializedSame(t, 280);
	}

	@Test public void testBlockField()
	{
		final Feature f = MyBlock.field;
		assertFails (PREFIX + "MyBlock#field", ()->f.getName());
		assertEquals(PREFIX + "MyBlock#field",     f.toString());
		assertFails (PREFIX + "MyBlock#field", ()->f.getID());
		assertFails (PREFIX + "MyBlock#field", ()->f.getType());
		assertSerializedSame(f, 320);
	}

	@Test public void testBlockFieldRenamed()
	{
		final Feature f = MyBlock.pure;
		assertFails (PREFIX + "MyBlock#pure", ()->f.getName());
		assertEquals(PREFIX + "MyBlock#pure",     f.toString()); // pure is ok
		assertFails (PREFIX + "MyBlock#pure", ()->f.getID());    // pure is ok
		assertFails (PREFIX + "MyBlock#pure", ()->f.getType());  // pure is ok
		assertSerializedSame(f, 319);
	}

	@Test public void testBlockRenamedField()
	{
		final Feature f = PureBlock.field;
		assertFails (PREFIX + "PureBlock#field", ()->f.getName());
		assertEquals(PREFIX + "PureBlock#field",     f.toString()); // pure is ok
		assertFails (PREFIX + "PureBlock#field", ()->f.getID());    // pure is ok
		assertFails (PREFIX + "PureBlock#field", ()->f.getType());  // pure is ok
		assertSerializedSame(f, 322);
	}

	@Test public void testBlockRenamedFieldRenamed()
	{
		final Feature f = PureBlock.pure;
		assertFails (PREFIX + "PureBlock#pure", ()->f.getName());
		assertEquals(PREFIX + "PureBlock#pure",     f.toString()); // pure is ok
		assertFails (PREFIX + "PureBlock#pure", ()->f.getID());    // pure is ok
		assertFails (PREFIX + "PureBlock#pure", ()->f.getType());  // pure is ok
		assertSerializedSame(f, 321);
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

		static final CompositeType<MyComposite> TYPE = CompositeField.create(MyComposite.class).valueType();

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

		static final CompositeType<PureComposite> TYPE = CompositeField.create(PureComposite.class).valueType();

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
	@CopeName("ActualComposite")
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

	private static final String PREFIX = "com.exedio.cope.pattern.MountTest$";
}
