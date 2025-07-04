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

import static com.exedio.cope.SchemaInfo.check;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.Assert;
import com.exedio.cope.tojunit.SI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * @see CopyChoiceHierarchyTest
 */
public class CopyChoiceSimpleTest extends TestWithEnvironment
{
	@Test void testOk()
	{
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(Container.TYPE) + " " +
				"JOIN " + SI.tab(Part.TYPE) + " " +
				"ON " + SI.colq(Container.choice) + "=" + SI.pkq(Part.TYPE) + " " +
				"WHERE ("  + SI.pkq(Container.TYPE) +               "<>"    + SI.colq(Part.parent) + ") " +
					"OR ((" + SI.pkq(Container.TYPE) + " IS "+ "NULL) AND (" + SI.colq(Part.parent) + " IS NOT NULL)) " +
					"OR ((" + SI.pkq(Container.TYPE) + " IS NOT NULL) AND (" + SI.colq(Part.parent) + " IS NULL))",
				check(Container.choice.getChoice()));
		final Container c1 = new Container();
		final Part p1 = new Part(c1);
		final Part p2 = new Part(c1);
		assertEquals(c1, p1.getParent());
		assertEquals(c1, p2.getParent());
		assertEquals(null, c1.getChoice());
		assertEquals(0, Container.choice.getChoice().check());

		c1.setChoice(p1);
		assertEquals(c1, p1.getParent());
		assertEquals(c1, p2.getParent());
		assertEquals(p1, c1.getChoice());
		assertEquals(0, Container.choice.getChoice().check());

		c1.setChoice(p2);
		assertEquals(c1, p1.getParent());
		assertEquals(c1, p2.getParent());
		assertEquals(p2, c1.getChoice());
		assertEquals(0, Container.choice.getChoice().check());

		c1.setChoice(null);
		assertEquals(c1, p1.getParent());
		assertEquals(c1, p2.getParent());
		assertEquals(null, c1.getChoice());
		assertEquals(0, Container.choice.getChoice().check());
	}

	@Test void testWrongSetChoice()
	{
		final Container c1 = new Container();
		final Container c2 = new Container();
		final Part p = new Part(c2);
		assertEquals(c2, p.getParent());
		assertEquals(null, c1.getChoice());
		assertEquals(null, c2.getChoice());

		assertFails(
				() -> c1.setChoice(p),
				"choice violation on " + c1 + " " +
				"for Container.choiceChoice, " +
				"mismatching backPointer '" + c2 + "' at " + p,
				c1, c2, c1, p);
		assertEquals(c2, p.getParent());
		assertEquals(null, c1.getChoice());
		assertEquals(null, c2.getChoice());
		assertEquals(0, Container.choice.getChoice().check());
	}

	@Test void testWrongSetChoiceParentNull()
	{
		final Container c = new Container();
		final Part p = new Part((Container)null);
		assertEquals(null, p.getParent());
		assertEquals(null, c.getChoice());

		assertFails(
				() -> c.setChoice(p),
				"choice violation on " + c + " " +
				"for Container.choiceChoice, " +
				"mismatching backPointer null at " + p,
				c, null, c, p);
		assertEquals(null, p.getParent());
		assertEquals(null, c.getChoice());
		assertEquals(0, Container.choice.getChoice().check());
	}

	@Test void testWrongCreate()
	{
		final Container c1 = new Container();
		final Part p = new Part(c1);
		assertEquals(c1, p.getParent());
		assertEquals(null, c1.getChoice());

		assertFails(
				() -> new Container(p),
				"choice violation for Container.choiceChoice, " +
				"mismatching backPointer '" + c1 + "' at " + p,
				null, c1, null, p);
		assertEquals(asList(c1), Container.TYPE.search());
		assertEquals(0, Container.choice.getChoice().check());
	}

	@Test void testWrongCreateParentNull()
	{
		final Part p = new Part();
		assertEquals(null, p.getParent());
		assertFails(
				() -> new Container(p),
				"choice violation for Container.choiceChoice, " +
				"mismatching backPointer null at " + p,
				null, null, null, p);
		assertEquals(asList(), Container.TYPE.search());
		assertEquals(0, Container.choice.getChoice().check());
	}


	private static void assertFails(
			final Executable executable,
			final String message,
			final Container item,
			final Container expectedValue,
			final Container actualValue,
			final Part targetItem)
	{
		final CopyViolationException actual = Assert.assertFails(
				executable,
				CopyViolationException.class,
				message);
		assertSame(Container.choice.getChoice(), actual.getFeature(), "feature");
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
		static final ItemField<Part> choice = ItemField.create(Part.class).optional().choice(() -> Part.parent);

		private Container(final Part choice)
		{
			this(SetValue.map(Container.choice, choice));
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		Container()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Part getChoice()
		{
			return Container.choice.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setChoice(@javax.annotation.Nullable final Part choice)
		{
			Container.choice.set(this,choice);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class,Container::new);

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class Part extends Item
	{
		static final ItemField<Container> parent = ItemField.create(Container.class).toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		Part(
					@javax.annotation.Nullable final Container parent)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Part.parent,parent),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Container getParent()
		{
			return Part.parent.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Part> TYPE = com.exedio.cope.TypesBound.newType(Part.class,Part::new);

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(Container.TYPE, Part.TYPE);

	public CopyChoiceSimpleTest()
	{
		super(MODEL);
	}
}
