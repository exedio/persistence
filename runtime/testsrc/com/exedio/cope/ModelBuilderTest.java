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

import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class ModelBuilderTest
{
	@Test void testType()
	{
		final Model m = Model.builder().
				add(ItemType.TYPE).
				build();
		assertEquals(asList(ItemType.TYPE), m.getTypes());
		assertRevisionsDisabled(m);
	}

	@Test void testTypeSet()
	{
		final Model m = Model.builder().
				add(new TypeSet(ItemTypeSet.TYPE)).
				build();
		assertEquals(asList(ItemTypeSet.TYPE), m.getTypes());
		assertRevisionsDisabled(m);
	}

	@Test void testAll()
	{
		final Model m = Model.builder().
				add(ItemAllType1.TYPE, ItemAllType2.TYPE).
				add(new TypeSet(ItemAllTypeSet1.TYPE, ItemAllTypeSet2.TYPE)).
				add((ctx) -> {throw new RuntimeException();}).
				build();
		assertEquals(
				asList(ItemAllTypeSet1.TYPE, ItemAllTypeSet2.TYPE, ItemAllType1.TYPE, ItemAllType2.TYPE),
				m.getTypes());
		assertRevisionsEnabled(m);
	}

	@Test void testFailTypeNone()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.build();
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("explicitTypes", e.getMessage());
		}
	}

	@Test void testFailTypeNull()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.add((Type<?>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types[0]", e.getMessage());
		}
	}

	@Test void testFailTypesNull()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.add((Type<?>[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types", e.getMessage());
		}
	}

	@Test void testFailTypesEmpty()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.add(new Type<?>[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}
	}

	@Test void testFailTypeSetNull()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.add((TypeSet)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("typeSets[0]", e.getMessage());
		}
	}

	@SuppressWarnings("OverlyStrongTypeCast") // bug in idea inspection
	@Test void testFailTypeSetsNull()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.add((TypeSet[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("typeSets", e.getMessage());
		}
	}

	@Test void testFailTypeSetsEmpty()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.add(new TypeSet[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("typeSets must not be empty", e.getMessage());
		}
	}

	@Test void testFailRevisionsNull()
	{
		final ModelBuilder m = Model.builder();
		try
		{
			m.add((Revisions.Factory)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("revisions", e.getMessage());
		}
	}

	@Test void testAlreadySetTypes()
	{
		final ModelBuilder m = Model.builder().add(ItemFail.TYPE);
		try
		{
			m.add((Type<?>)null);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("already set", e.getMessage());
		}
	}

	@Test void testAlreadySetTypeSets()
	{
		final ModelBuilder m = Model.builder().add(new TypeSet(ItemFail.TYPE));
		try
		{
			m.add((TypeSet)null);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("already set", e.getMessage());
		}
	}

	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	@Test void testAlreadySetRevisions()
	{
		final ModelBuilder m = Model.builder().add((ctx) -> {throw new RuntimeException();});
		try
		{
			m.add((Revisions.Factory)null);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("already set", e.getMessage());
		}
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemType extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemType> TYPE = com.exedio.cope.TypesBound.newType(ItemType.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemType(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemTypeSet extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemTypeSet> TYPE = com.exedio.cope.TypesBound.newType(ItemTypeSet.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemTypeSet(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemAllType1 extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemAllType1> TYPE = com.exedio.cope.TypesBound.newType(ItemAllType1.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemAllType1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemAllType2 extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemAllType2> TYPE = com.exedio.cope.TypesBound.newType(ItemAllType2.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemAllType2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemAllTypeSet1 extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemAllTypeSet1> TYPE = com.exedio.cope.TypesBound.newType(ItemAllTypeSet1.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemAllTypeSet1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemAllTypeSet2 extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemAllTypeSet2> TYPE = com.exedio.cope.TypesBound.newType(ItemAllTypeSet2.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemAllTypeSet2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemFail extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemFail> TYPE = com.exedio.cope.TypesBound.newType(ItemFail.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemFail(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static void assertRevisionsDisabled(final Model model)
	{
		try
		{
			model.revise();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("revisions are not enabled", e.getMessage());
		}
	}

	private static void assertRevisionsEnabled(final Model model)
	{
		try
		{
			model.revise();
			fail();
		}
		catch(final Model.NotConnectedException e)
		{
			assertSame(model, e.getModel());
		}
	}
}
