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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator")
public class DeleteChangeHookNullifyCascadeTest extends TestWithEnvironment
{
	private static final ChangeHook HOOK = new ChangeHook()
	{
		@Override
		public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] setValues)
		{
			HISTORY.append("beforeSet " + item + " " + Arrays.toString(setValues) + "\n");
			return setValues;
		}
		@Override
		public void beforeDelete(final Item item)
		{
			HISTORY.append("beforeDelete " + item + "\n");
		}
	};

	private static final Model MODEL = Model.builder().
			add(Container.TYPE, Part.TYPE).
			changeHooks(model -> HOOK).
			build();

	public DeleteChangeHookNullifyCascadeTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		assertHistory("");

		final Container container = new Container();
		assertHistory("");

		final Part part = new Part(container);
		assertHistory("");

		container.setDefaultPart(part);
		assertHistory(
				"beforeSet Container-0 [Container.defaultPart=Part-0]\n");

		container.deleteCopeItem();
		assertHistory(
				"beforeDelete Container-0\n" +
				"beforeDelete Part-0\n" +
				"beforeSet Container-0 [Container.defaultPart=null]\n");
	}


	private static final StringBuilder HISTORY = new StringBuilder();

	private static void assertHistory(final String history)
	{
		assertEquals(history, HISTORY.toString());
		HISTORY.setLength(0);
	}

	@BeforeEach void setUp()
	{
		HISTORY.setLength(0);
	}

	@AfterEach void tearDown()
	{
		HISTORY.setLength(0);
	}

	@WrapperType(indent=2, comments=false)
	private static final class Container extends Item
	{
		static final ItemField<Part> defaultPart = ItemField.create(Part.class).nullify();

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
		@javax.annotation.Nullable
		Part getDefaultPart()
		{
			return Container.defaultPart.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDefaultPart(@javax.annotation.Nullable final Part defaultPart)
		{
			Container.defaultPart.set(this,defaultPart);
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
	static final class Part extends Item
	{
		static final ItemField<Container> parent = ItemField.create(Container.class).cascade();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		Part(
					@javax.annotation.Nonnull final Container parent)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Part.parent,parent),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Container getParent()
		{
			return Part.parent.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setParent(@javax.annotation.Nonnull final Container parent)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Part.parent.set(this,parent);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Part> TYPE = com.exedio.cope.TypesBound.newType(Part.class,Part::new);

		@com.exedio.cope.instrument.Generated
		private Part(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
