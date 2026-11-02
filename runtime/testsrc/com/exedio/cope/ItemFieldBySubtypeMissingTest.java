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

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ItemFieldBySubtypeMissingTest
{
	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class Source extends Item
	{
		static final ItemField<Target> field = ItemField.createBySubtypeEXPERIMENTAL(Target.class);

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Target getField()
		{
			return Source.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final Target field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class,Source::new);

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false) @CopeNoType // TODO should imply @WrapperType(type,constructor=NONE)
	@SuppressWarnings("AbstractClassNeverImplemented") // OK: test bad API usage
	private abstract static class Target extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		protected Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Test void test()
	{
		assertFails(
				() -> new Model(Source.TYPE),
				IllegalArgumentException.class,
				"createBySubtypeEXPERIMENTAL: no type assignable to " + Target.class.getName());
	}
}
