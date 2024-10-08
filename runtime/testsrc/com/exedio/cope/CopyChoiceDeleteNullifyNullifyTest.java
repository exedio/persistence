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

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CopyChoiceDeleteNullifyNullifyTest extends TestWithEnvironment
{
	@WrapInterim private static final DeletePolicy POLICY1 = DeletePolicy.NULLIFY;
	@WrapInterim private static final DeletePolicy POLICY2 = DeletePolicy.NULLIFY;

	public CopyChoiceDeleteNullifyNullifyTest() { super(MODEL); }

	private Container c;
	private Part p1, p2;

	@BeforeEach void before()
	{
		c = new Container();
		p1 = new Part(c);
		p2 = new Part(c);
		c.setChoice(p1);
	}

	@Test void testDeleteContainer()
	{
		c.deleteCopeItem();

		assertEquals(asList( ), Container.search());
		assertEquals(asList( asList(p1, null), asList(p2, null) ), Part.search());
		changeHooks.assertDeleted(c);
	}

	@Test void testDeletePartChosen()
	{
		p1.deleteCopeItem();

		assertEquals(asList( asList(c, null) ), Container.search());
		assertEquals(asList( asList(p2, c) ), Part.search());
		changeHooks.assertDeleted(p1);
	}

	@Test void testDeletePartChosenOnly()
	{
		p2.deleteCopeItem();
		changeHooks.assertDeleted(p2);
		p1.deleteCopeItem();

		assertEquals(asList( asList(c, null) ), Container.search());
		assertEquals(asList( ), Part.search());
		changeHooks.assertDeleted(p1);
	}

	@Test void testDeletePartOther()
	{
		p2.deleteCopeItem();

		assertEquals(asList( asList(c, p1) ), Container.search());
		assertEquals(asList( asList(p1, c) ), Part.search());
		changeHooks.assertDeleted(p2);
	}


	@WrapperType(indent=2, comments=false)
	private static final class Container extends Item
	{
		@Wrapper(wrap="get", visibility=Visibility.NONE)
		static final ItemField<Part> choice = ItemField.create(Part.class, POLICY1).optional();

		static List<List<Object>> search()
		{
			return Query.newQuery(new Selectable<?>[]{TYPE.getThis(), choice}, TYPE, null).search();
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Container()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

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
		private static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class,Container::new);

		@com.exedio.cope.instrument.Generated
		private Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class Part extends Item
	{
		@WrapperInitial @Wrapper(wrap="*", visibility=Visibility.NONE)
		static final ItemField<Container> parent = ItemField.create(Container.class, POLICY2);

		static List<List<Object>> search()
		{
			return Query.newQuery(new Selectable<?>[]{TYPE.getThis(), parent}, TYPE, null).search();
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Part(
					@javax.annotation.Nullable final Container parent)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Part.parent,parent),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Part> TYPE = com.exedio.cope.TypesBound.newType(Part.class,Part::new);

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private final CopyChoiceDeleteRule changeHooks = new CopyChoiceDeleteRule(MODEL);

	static final Model MODEL = new ModelBuilder().
			add(Container.TYPE, Part.TYPE).
			changeHooks(CopyChoiceDeleteChangeHook::new).
			build();
}
