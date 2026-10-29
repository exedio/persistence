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

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.SetField;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ItemFieldBySubtypeSetTest extends TestWithEnvironment
{
	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class Source extends Item
	{
		static final SetField<Target> field = SetField.create(ItemField.createBySubtypeEXPERIMENTAL(Target.class));

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.Set<Target> getField()
		{
			return Source.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.Query<Target> getFieldQuery()
		{
			return Source.field.getQuery(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static java.util.List<Source> getParentsOfField(@javax.annotation.Nonnull final Target element)
		{
			return Source.field.getParents(Source.class,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final java.util.Collection<? extends Target> field)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			Source.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean addToField(@javax.annotation.Nonnull final Target element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return Source.field.add(this,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean removeFromField(@javax.annotation.Nonnull final Target element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return Source.field.remove(this,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static com.exedio.cope.ItemField<Source> fieldParent()
		{
			return Source.field.getParent(Source.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class,Source::new);

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private abstract static class Target extends Item // TODO should be an interface, once Item itself is an interface
	{
		// MUST NOT define any fields or non-abstract methods, otherwise it cannot be transferred into an interface

		@com.exedio.cope.instrument.Generated
		protected Target(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(Target.class);

		@com.exedio.cope.instrument.Generated
		protected Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	// -------------------------------------------------------------------------------------
	// Border between framework and project
	// TargetImpl must not be mentioned above, but only below this border.
	// -------------------------------------------------------------------------------------

	@WrapperType(indent=2, comments=false)
	private static final class TargetImpl extends Target
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private TargetImpl()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private TargetImpl(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<TargetImpl> TYPE = com.exedio.cope.TypesBound.newType(TargetImpl.class,TargetImpl::new);

		@com.exedio.cope.instrument.Generated
		private TargetImpl(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(Source.TYPE, Target.TYPE, TargetImpl.TYPE);

	public ItemFieldBySubtypeSetTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		assertEquals(TargetImpl.class, Source.field.getElement().getValueClass());
		assertEquals(TargetImpl.TYPE, Source.field.getElement().getValueType());
		assertEquals(ItemField.DeletePolicy.FORBID, ((ItemField<?>)Source.field.getElement()).getDeletePolicy());

		final var source = new Source();
		assertEquals(Set.of(), source.getField());
		final var target = new TargetImpl();
		source.addToField(target);
		assertEquals(Set.of(target), source.getField());
	}
}
