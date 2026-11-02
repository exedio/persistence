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
import static com.exedio.cope.pattern.CompositeType.forClass;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.pattern.CompositeField;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class ItemFieldBySubtypeCompositeBlockTest extends TestWithEnvironment
{
	@WrapperType(indent=2, comments=false)
	private static final class SourceComp extends Composite
	{
		static final ItemField<Target> field = ItemField.createBySubtypeEXPERIMENTAL(Target.class);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private SourceComp(
					@javax.annotation.Nonnull final Target field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(SourceComp.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private SourceComp(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Target getField()
		{
			return get(SourceComp.field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final Target field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			set(SourceComp.field,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(indent=2, comments=false)
	private static final class SourceItem extends Item
	{
		static final CompositeField<SourceComp> field = CompositeField.create(SourceComp.class);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private SourceItem(
					@javax.annotation.Nonnull final SourceComp field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(SourceItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private SourceItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		SourceComp getField()
		{
			return SourceItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final SourceComp field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			SourceItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SourceItem> TYPE = com.exedio.cope.TypesBound.newType(SourceItem.class,SourceItem::new);

		@com.exedio.cope.instrument.Generated
		private SourceItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false) @CopeNoType // TODO should imply @WrapperType(type,constructor=NONE)
	private abstract static class Target extends Item
	{
		// MUST NOT define any fields or non-abstract methods, otherwise it cannot be transferred into an interface

		@com.exedio.cope.instrument.Generated
		protected Target(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

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

	private static final Model MODEL = new Model(SourceItem.TYPE, TargetImpl.TYPE);

	public ItemFieldBySubtypeCompositeBlockTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		assertEquals(TargetImpl.class, SourceComp.field.getValueClass());
		//noinspection Convert2MethodRef
		assertFails(
				() -> SourceComp.field.getValueType(), // TODO should work
				IllegalArgumentException.class,
				"createBySubtypeEXPERIMENTAL: no type assignable to " + Target.class.getName());

		final ItemField<Target> sourceItemField = SourceItem.field.of(SourceComp.field);
		assertEquals(TargetImpl.class, sourceItemField.getValueClass());
		assertEquals(TargetImpl.TYPE, sourceItemField.getValueType());

		assertEquals(SourceComp.class, SourceItem.field.getValueClass());
		assertEquals(forClass(SourceComp.class), SourceItem.field.getValueType());

		final var target = new TargetImpl();
		final var sourceComp = new SourceComp(target);
		assertEquals(target, sourceComp.getField());
		final var sourceItem = new SourceItem(sourceComp);
		assertEquals(target, sourceItem.getField().getField());
		assertEquals(List.of(sourceItem), SourceItem.TYPE.search(sourceItemField.is(target)));

		SourceComp.field.check(target);
		sourceItemField.check(target);
		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
		final ItemField<SourceItem> sourceFieldCompWrong = (ItemField)SourceComp.field;
		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
		final ItemField<SourceItem> sourceFieldItemWrong = (ItemField)sourceItemField;
		assertFailsCheck(() -> sourceFieldCompWrong.check(sourceItem), SourceComp.field);
		assertFailsCheck(() -> sourceFieldItemWrong.check(sourceItem), sourceItemField);
		final var sourceValueCompWrong = SetValue.map(sourceFieldCompWrong, sourceItem);
		final var sourceValueItemWrong = SetValue.map(sourceFieldItemWrong, sourceItem);
		assertFailsCheck(() -> new SourceComp(sourceValueCompWrong), SourceComp.field);
		assertFailsCheck(() -> new SourceItem(sourceValueItemWrong), sourceItemField);
		assertFailsCheck(() -> sourceComp.set(sourceValueCompWrong), SourceComp.field);
		assertFailsCheck(() -> sourceItem.set(sourceValueItemWrong), sourceItemField);

		final var target2 = new TargetImpl();

		sourceComp.setField(target2);
		assertEquals(target2, sourceComp.getField());

		sourceItem.setField(new SourceComp(target2));
		assertEquals(target2, sourceItem.getField().getField());
	}

	private static void assertFailsCheck(final Executable executable, final Field<?> field)
	{
		assertFails(
				executable,
				ClassCastException.class,
				"expected a " + TargetImpl.class.getName() + ", " +
				"but was a " + SourceItem.class.getName() + " for " + field + ".");
	}
}
