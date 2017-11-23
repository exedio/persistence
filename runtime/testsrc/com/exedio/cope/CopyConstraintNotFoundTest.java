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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopyConstraintNotFoundTest
{
	@Test public void testIt()
	{
		try
		{
			new Model(Source.TYPE, Target.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"insufficient template for CopyConstraint Source.fieldCopyFromtarget: " +
				"not found",
				e.getMessage());
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Source extends Item
	{
		@WrapperIgnore
		static final ItemField<Target> target = ItemField.create(Target.class).toFinal();
		@WrapperIgnore
		static final StringField field = new StringField().toFinal().copyFrom(target);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Target extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
