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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR","IC_INIT_CIRCULARITY"})
public class CopyChoiceDeleteCascadeNullifyTest extends TestWithEnvironment
{
	@WrapInterim private static final DeletePolicy POLICY1 = DeletePolicy.CASCADE;
	@WrapInterim private static final DeletePolicy POLICY2 = DeletePolicy.NULLIFY;

	public CopyChoiceDeleteCascadeNullifyTest() { super(MODEL); }

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
	}

	@Test void testDeletePartChosen()
	{
		p1.deleteCopeItem();

		assertEquals(asList( ), Container.search());
		assertEquals(asList( asList(p2, null) ), Part.search());
	}

	@Test void testDeletePartChosenOnly()
	{
		p2.deleteCopeItem();
		p1.deleteCopeItem();

		assertEquals(asList( ), Container.search());
		assertEquals(asList( ), Part.search());
	}

	@Test void testDeletePartOther()
	{
		p2.deleteCopeItem();

		assertEquals(asList( asList(c, p1) ), Container.search());
		assertEquals(asList( asList(p1, c) ), Part.search());
	}


	@WrapperType(indent=2, comments=false)
	static final class Container extends Item
	{
		@Wrapper(wrap="get", visibility=Visibility.NONE)
		static final ItemField<Part> choice = ItemField.create(Part.class, POLICY1).optional();

		static List<List<Object>> search()
		{
			return Query.newQuery(new Selectable<?>[]{TYPE.getThis(), choice}, TYPE, null).search();
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		Container()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private Container(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setChoice(@javax.annotation.Nullable final Part choice)
		{
			Container.choice.set(this,choice);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class Part extends Item
	{
		@WrapperInitial @Wrapper(wrap="*", visibility=Visibility.NONE)
		static final ItemField<Container> parent = ItemField.create(Container.class, POLICY2);

		static List<List<Object>> search()
		{
			return Query.newQuery(new Selectable<?>[]{TYPE.getThis(), parent}, TYPE, null).search();
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		Part(
					@javax.annotation.Nullable final Container parent)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				Part.parent.map(parent),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private Part(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Part> TYPE = com.exedio.cope.TypesBound.newType(Part.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private Part(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(Container.TYPE, Part.TYPE);
}
