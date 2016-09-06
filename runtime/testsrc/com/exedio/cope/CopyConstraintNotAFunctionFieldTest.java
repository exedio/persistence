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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CopyConstraintNotAFunctionFieldTest
{
	@Test public void testIt()
	{
		try
		{
			new Model(Source.TYPE, Target.TYPE);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
				"insufficient template for CopyConstraint Source.fieldCopyFromtarget: " +
				"Target.field is not a FunctionField but com.exedio.cope.DataField",
				e.getMessage());
		}
	}

	static class Source extends Item
	{
		static final ItemField<Target> target = ItemField.create(Target.class).toFinal();
		static final StringField field = new StringField().toFinal().copyFrom(target);

		static final Type<Source> TYPE = TypesBound.newType(Source.class);
		private static final long serialVersionUID = 1l;
		private Source(final ActivationParameters ap) { super(ap); }
	}

	static class Target extends Item
	{
		static final DataField field = new DataField().toFinal();

		static final Type<Target> TYPE = TypesBound.newType(Target.class);
		private static final long serialVersionUID = 1l;
		private Target(final ActivationParameters ap) { super(ap); }
	}
}
