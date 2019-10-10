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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class CopyChoiceMultiTest extends TestWithEnvironment
{
	@Test void testOk()
	{
		final Container c1 = new Container();
		final PartA pa1 = new PartA(c1);
		final PartA pa2 = new PartA(c1);
		final PartB pb1 = new PartB(c1);
		assertEquals(c1, pa1.getParent());
		assertEquals(c1, pa2.getParent());
		assertEquals(c1, pb1.getParent());
		assertEquals(null, c1.getChoiceA());
		assertEquals(null, c1.getChoiceB());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());

		c1.setChoiceA(pa1);
		assertEquals(c1, pa1.getParent());
		assertEquals(c1, pa2.getParent());
		assertEquals(c1, pb1.getParent());
		assertEquals(pa1, c1.getChoiceA());
		assertEquals(null, c1.getChoiceB());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());

		c1.setChoiceA(pa2);
		assertEquals(c1, pa1.getParent());
		assertEquals(c1, pa2.getParent());
		assertEquals(c1, pb1.getParent());
		assertEquals(pa2, c1.getChoiceA());
		assertEquals(null, c1.getChoiceB());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());

		c1.setChoiceB(pb1);
		assertEquals(c1, pa1.getParent());
		assertEquals(c1, pa2.getParent());
		assertEquals(c1, pb1.getParent());
		assertEquals(pa2, c1.getChoiceA());
		assertEquals(pb1, c1.getChoiceB());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());

		c1.setChoiceA(null);
		assertEquals(c1, pa1.getParent());
		assertEquals(c1, pa2.getParent());
		assertEquals(c1, pb1.getParent());
		assertEquals(null, c1.getChoiceA());
		assertEquals(pb1, c1.getChoiceB());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());

		c1.setChoiceB(null);
		assertEquals(c1, pa1.getParent());
		assertEquals(c1, pa2.getParent());
		assertEquals(c1, pb1.getParent());
		assertEquals(null, c1.getChoiceA());
		assertEquals(null, c1.getChoiceB());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongSetChoiceA()
	{
		final Container c1 = new Container();
		final Container c2 = new Container();
		final PartA p = new PartA(c2);
		assertEquals(c2, p.getParent());
		assertEquals(null, c1.getChoiceA());
		assertEquals(null, c2.getChoiceA());

		assertFails(
				() -> c1.setChoiceA(p),
				"copy violation on " + c1 + " " +
				"for Container.choiceAChoiceparent, " +
				"expected '" + c2 + "' from target " + p + ", " +
				"but was '" + c1 + "'",
				c1, c2, c1, p);
		assertEquals(c2, p.getParent());
		assertEquals(null, c1.getChoiceA());
		assertEquals(null, c2.getChoiceA());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongSetChoiceB()
	{
		final Container c1 = new Container();
		final Container c2 = new Container();
		final PartB p = new PartB(c2);
		assertEquals(c2, p.getParent());
		assertEquals(null, c1.getChoiceA());
		assertEquals(null, c2.getChoiceA());

		assertFails(
				() -> c1.setChoiceB(p),
				"copy violation on " + c1 + " " +
				"for Container.choiceBChoiceparent, " +
				"expected '" + c2 + "' from target " + p + ", " +
				"but was '" + c1 + "'",
				c1, c2, c1, p);
		assertEquals(c2, p.getParent());
		assertEquals(null, c1.getChoiceA());
		assertEquals(null, c2.getChoiceA());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongSetChoiceParentNullA()
	{
		final Container c = new Container();
		final PartA p = new PartA((Container)null);
		assertEquals(null, p.getParent());
		assertEquals(null, c.getChoiceA());

		assertFails(
				() -> c.setChoiceA(p),
				"copy violation on " + c + " " +
				"for Container.choiceAChoiceparent, " +
				"expected null from target " + p + ", " +
				"but was '" + c + "'",
				c, null, c, p);
		assertEquals(null, p.getParent());
		assertEquals(null, c.getChoiceA());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongSetChoiceParentNullB()
	{
		final Container c = new Container();
		final PartB p = new PartB((Container)null);
		assertEquals(null, p.getParent());
		assertEquals(null, c.getChoiceA());

		assertFails(
				() -> c.setChoiceB(p),
				"copy violation on " + c + " " +
				"for Container.choiceBChoiceparent, " +
				"expected null from target " + p + ", " +
				"but was '" + c + "'",
				c, null, c, p);
		assertEquals(null, p.getParent());
		assertEquals(null, c.getChoiceA());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongCreateA()
	{
		final Container c1 = new Container();
		final PartA p = new PartA(c1);
		assertEquals(c1, p.getParent());
		assertEquals(null, c1.getChoiceA());

		assertFails(
				() -> new Container(p),
				"copy violation for Container.choiceAChoiceparent, " +
				"expected '" + c1 + "' from target " + p + ", " +
				"but was newly created instance of type Container",
				null, c1, null, p);
		assertEquals(asList(c1), Container.TYPE.search());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongCreateB()
	{
		final Container c1 = new Container();
		final PartB p = new PartB(c1);
		assertEquals(c1, p.getParent());
		assertEquals(null, c1.getChoiceA());

		assertFails(
				() -> new Container(p),
				"copy violation for Container.choiceBChoiceparent, " +
				"expected '" + c1 + "' from target " + p + ", " +
				"but was newly created instance of type Container",
				null, c1, null, p);
		assertEquals(asList(c1), Container.TYPE.search());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongCreateParentNullA()
	{
		final PartA p = new PartA();
		assertEquals(null, p.getParent());
		assertFails(
				() -> new Container(p),
				"copy violation for Container.choiceAChoiceparent, " +
				"expected null from target " + p + ", " +
				"but was newly created instance of type Container",
				null, null, null, p);
		assertEquals(asList(), Container.TYPE.search());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}

	@Test void testWrongCreateParentNullB()
	{
		final PartB p = new PartB();
		assertEquals(null, p.getParent());
		assertFails(
				() -> new Container(p),
				"copy violation for Container.choiceBChoiceparent, " +
				"expected null from target " + p + ", " +
				"but was newly created instance of type Container",
				null, null, null, p);
		assertEquals(asList(), Container.TYPE.search());
		assertEquals(0, Container.choiceA.getChoice().check());
		assertEquals(0, Container.choiceB.getChoice().check());
	}


	private static void assertFails(
			final Executable executable,
			final String message,
			final Container item,
			final Container expectedValue,
			final Container actualValue,
			final PartA targetItem)
	{
		final CopyViolationException actual = Assert.assertFails(
				executable,
				CopyViolationException.class,
				message);
		assertSame(Container.choiceA.getChoice(), actual.getFeature(), "feature");
		assertSame(null, actual.getAdditionalFeature(), "additionalFeature");
		assertEquals(item, actual.getItem(), "item");
		assertEquals(expectedValue, actual.getExpectedValue(), "expectedValue");
		assertEquals(actualValue, actual.getActualValue(), "actualValue");
		assertEquals(targetItem, actual.getTargetItem(), "targetItem");
		assertEquals(null, actual.getAdditionalTargetItem(), "additionalTargetItem");
	}

	private static void assertFails(
			final Executable executable,
			final String message,
			final Container item,
			final Container expectedValue,
			final Container actualValue,
			final PartB targetItem)
	{
		final CopyViolationException actual = Assert.assertFails(
				executable,
				CopyViolationException.class,
				message);
		assertSame(Container.choiceB.getChoice(), actual.getFeature(), "feature");
		assertSame(null, actual.getAdditionalFeature(), "additionalFeature");
		assertEquals(item, actual.getItem(), "item");
		assertEquals(expectedValue, actual.getExpectedValue(), "expectedValue");
		assertEquals(actualValue, actual.getActualValue(), "actualValue");
		assertEquals(targetItem, actual.getTargetItem(), "targetItem");
		assertEquals(null, actual.getAdditionalTargetItem(), "additionalTargetItem");
	}

	@WrapperType(indent=2, comments=false)
	static final class Container extends Item
	{
		static final ItemField<PartA> choiceA = ItemField.create(PartA.class).optional().choice("parent");
		static final ItemField<PartB> choiceB = ItemField.create(PartB.class).optional().choice("parent");

		private Container(final PartA choice)
		{
			this(choiceA.map(choice));
		}

		private Container(final PartB choice)
		{
			this(choiceB.map(choice));
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		Container()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		PartA getChoiceA()
		{
			return Container.choiceA.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setChoiceA(@javax.annotation.Nullable final PartA choiceA)
		{
			Container.choiceA.set(this,choiceA);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		PartB getChoiceB()
		{
			return Container.choiceB.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setChoiceB(@javax.annotation.Nullable final PartB choiceB)
		{
			Container.choiceB.set(this,choiceB);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class);

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class PartA extends Item
	{
		static final ItemField<Container> parent = ItemField.create(Container.class).toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		PartA(
					@javax.annotation.Nullable final Container parent)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				PartA.parent.map(parent),
			});
		}

		@com.exedio.cope.instrument.Generated
		private PartA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Container getParent()
		{
			return PartA.parent.get(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<PartA> TYPE = com.exedio.cope.TypesBound.newType(PartA.class);

		@com.exedio.cope.instrument.Generated
		private PartA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class PartB extends Item
	{
		static final ItemField<Container> parent = ItemField.create(Container.class).toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		PartB(
					@javax.annotation.Nullable final Container parent)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				PartB.parent.map(parent),
			});
		}

		@com.exedio.cope.instrument.Generated
		private PartB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Container getParent()
		{
			return PartB.parent.get(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<PartB> TYPE = com.exedio.cope.TypesBound.newType(PartB.class);

		@com.exedio.cope.instrument.Generated
		private PartB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(Container.TYPE, PartA.TYPE, PartB.TYPE);

	public CopyChoiceMultiTest()
	{
		super(MODEL);
	}
}
