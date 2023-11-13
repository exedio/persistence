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
import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.UsageEntryPoint;
import org.junit.jupiter.api.Test;

public class CopyConstraintNotAFunctionFieldTest
{
	@Test void testIt()
	{
		assertFails(
				() -> new Model(Source.TYPE, Target.TYPE),
				ClassCastException.class,
				"insufficient template for CopyConstraint Source.fieldCopyFromtarget: " +
				"Target.field is not a FunctionField but com.exedio.cope.DataField");
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Source extends Item
	{
		@WrapperIgnore
		static final ItemField<Target> target = ItemField.create(Target.class).toFinal();
		@WrapperIgnore
		@UsageEntryPoint // OK: test bad API usage
		static final StringField field = new StringField().toFinal().copyFrom(target);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class,Source::new);

		@com.exedio.cope.instrument.Generated
		protected Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Target extends Item
	{
		@WrapperIgnore
		@SuppressWarnings("unused") // OK: test bad API usage
		static final DataField field = new DataField().toFinal();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class,Target::new);

		@com.exedio.cope.instrument.Generated
		protected Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
