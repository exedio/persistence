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

package com.exedio.cope.misc;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.SerializationCheck.check;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockField;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.pattern.CompositeField;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

@SuppressWarnings("StaticVariableMayNotBeInitialized")
public class SerializationCheckTest
{
	@Test void testNull()
	{
		try
		{
			check(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void testOk()
	{
		assertEqualsUnmodifiable(asList(), check(MODEL_OK));
	}

	@Test void testWrong() throws NoSuchFieldException
	{
		final Field field1 = Item1.class.getDeclaredField("serializedField1");
		final Field field2 = Item2.class.getDeclaredField("serializedField2");
		final Field patternField1 = PatternItem.class.getDeclaredField("serializedField1");
		final Field compositeField1 = CompositeWrong.class.getDeclaredField("serializedField1");
		final Field blockField1 = BlockWrong.class.getDeclaredField("serializedField1");

		assertEqualsUnmodifiable(asList(
				field1,
				compositeField1,
				blockField1,
				patternField1,
				field2),
				check(MODEL));
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("unused") // OK: found by reflection
	private static class ItemOk extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		@SuppressWarnings("UnnecessaryModifier") // OK: testing weird stuff
		static transient int staticTransientField2;


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemOk> TYPE = com.exedio.cope.TypesBound.newType(ItemOk.class,ItemOk::new);

		@com.exedio.cope.instrument.Generated
		protected ItemOk(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL_OK = new Model(ItemOk.TYPE);


	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("unused") // OK: found by reflection
	private static final class PatternItem extends Item
	{
		static int staticField1;
		transient int transientField1;
		@SuppressWarnings("UnnecessaryModifier") // OK: testing weird stuff
		static transient int staticTransientField1;
		int serializedField1;


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private PatternItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused")
	private static class PatternFeature extends Pattern
	{
		@Override
		protected void onMount()
		{
			super.onMount();
			newSourceType(PatternItem.class, PatternItem::new, new Features());
		}
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	@SuppressWarnings("unused") // OK: found by reflection
	private static final class CompositeWrong extends Composite
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f1 = new StringField();

		static int staticField1;
		transient int transientField1;
		@SuppressWarnings("UnnecessaryModifier") // OK: testing weird stuff
		static transient int staticTransientField1;
		int serializedField1;


		@com.exedio.cope.instrument.Generated
		private CompositeWrong(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	@SuppressWarnings("unused") // OK: found by reflection
	private static final class BlockWrong extends Block
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f1 = new StringField();

		static int staticField1;
		transient int transientField1;
		@SuppressWarnings("UnnecessaryModifier") // OK: testing weird stuff
		static transient int staticTransientField1;
		int serializedField1;


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.pattern.BlockType<BlockWrong> TYPE = com.exedio.cope.pattern.BlockType.newType(BlockWrong.class,BlockWrong::new);

		@com.exedio.cope.instrument.Generated
		private BlockWrong(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("unused") // OK: found by reflection
	private static class Item1 extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f1 = new StringField();

		static final PatternFeature p1 = new PatternFeature();
		static final PatternFeature p2 = new PatternFeature(); // check there are no duplicates

		@Wrapper(wrap="*", visibility=NONE)
		static final CompositeField<CompositeWrong> composite = CompositeField.create(CompositeWrong.class);
		@Wrapper(wrap="*", visibility=NONE)
		static final CompositeField<CompositeWrong> composite2 = CompositeField.create(CompositeWrong.class); // check there are no duplicates
		@Wrapper(wrap="*", visibility=NONE)
		static final BlockField<BlockWrong> block = BlockField.create(BlockWrong.TYPE);
		@Wrapper(wrap="*", visibility=NONE)
		static final BlockField<BlockWrong> block2 = BlockField.create(BlockWrong.TYPE);// check there are no duplicates

		static int staticField1;
		transient int transientField1;
		@SuppressWarnings("UnnecessaryModifier") // OK: testing weird stuff
		static transient int staticTransientField1;
		int serializedField1;


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item1> TYPE = com.exedio.cope.TypesBound.newType(Item1.class,Item1::new);

		@com.exedio.cope.instrument.Generated
		protected Item1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("unused") // OK: found by reflection
	private static class Item2 extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		@SuppressWarnings("UnnecessaryModifier") // OK: testing weird stuff
		static transient int staticTransientField2;
		int serializedField2;


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item2> TYPE = com.exedio.cope.TypesBound.newType(Item2.class,Item2::new);

		@com.exedio.cope.instrument.Generated
		protected Item2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(Item1.TYPE, Item2.TYPE);
}
