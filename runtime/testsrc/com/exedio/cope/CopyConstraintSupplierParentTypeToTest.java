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

public class CopyConstraintSupplierParentTypeToTest
{
	@Test void testIt()
	{
		assertFails(
				() -> new Model(Source.TYPE, Target.TYPE),
				IllegalArgumentException.class,
				"insufficient template for CopyConstraint Source.target.copyTo(field,Parent.field) (Source.fieldCopyFromtarget): " +
				Parent.field + " must belong to type " + Target.TYPE + ", " +
				"but belongs to type " + Parent.TYPE);
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Source extends Item
	{
		@WrapperIgnore
		static final StringField field = new StringField().toFinal();
		@WrapperIgnore
		@UsageEntryPoint
		static final ItemField<Target> target = ItemField.create(Target.class).copyTo(field, () -> Parent.field);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class,Source::new);

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Target extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class,Target::new);

		@com.exedio.cope.instrument.Generated
		private Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Parent extends Item
	{
		@WrapperIgnore
		static final StringField field = new StringField().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Parent> TYPE = com.exedio.cope.TypesBound.newType(Parent.class,Parent::new);

		@com.exedio.cope.instrument.Generated
		private Parent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
