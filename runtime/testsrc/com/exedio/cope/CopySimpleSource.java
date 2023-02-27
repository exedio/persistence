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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.WrapperInitial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

final class CopySimpleSource extends Item
{
	@WrapperInitial
	static final ItemField<CopySimpleTarget> targetItem = ItemField.create(CopySimpleTarget.class).optional();

	@WrapperInitial
	static final StringField templateString = new StringField().optional().copyFrom(targetItem);

	@WrapperInitial
	static final ItemField<CopyValue> templateItem = ItemField.create(CopyValue.class).optional().copyFrom(targetItem);


	private static SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] setValues)
	{
		beforeCopeItemLog.add(new BeforeLog(null, setValues));
		return setValues;
	}

	@Override
	protected SetValue<?>[] beforeSetCopeItem(final SetValue<?>[] setValues)
	{
		beforeCopeItemLog.add(new BeforeLog(this, setValues));
		return setValues;
	}

	static void assertBeforeNewCopeItem(final SetValue<?>... expected)
	{
		assertBeforeCopeItem(new BeforeLog(null, expected));
	}

	static void assertBeforeSetCopeItem(final CopySimpleSource item, final SetValue<?>... expected)
	{
		assertNotNull(item);
		assertBeforeCopeItem(new BeforeLog(item, expected));
	}

	private static void assertBeforeCopeItem(final BeforeLog expected)
	{
		assertEquals(1, beforeCopeItemLog.size());
		assertEquals(expected, beforeCopeItemLog.get(0));
		beforeCopeItemLog.clear();
	}

	static void clearBeforeCopeItemLog()
	{
		beforeCopeItemLog.clear();
	}

	private static final ArrayList<BeforeLog> beforeCopeItemLog = new ArrayList<>();

	private static final class BeforeLog
	{
		private final Item item;
		private final SetValue<?>[] setValues;

		private BeforeLog(final Item item, final SetValue<?>[] setValues)
		{
			this.item = item;
			this.setValues = setValues;
			assertNotNull(setValues);
		}

		@Override
		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		public boolean equals(final Object other)
		{
			final BeforeLog o = (BeforeLog)other;
			return
					Objects.equals(item, o.item) &&
					Arrays.equals(setValues, o.setValues);
		}

		@Override
		public String toString()
		{
			return item + Arrays.toString(setValues);
		}

		@Override
		public int hashCode()
		{
			throw new RuntimeException();
		}
	}


	@Override
	public String toString()
	{
		// for testing, that CopyViolation#getMessage does not call toString(), but getCopeID()
		return "toString(" + getCopeID() + ')';
	}

	static CopySimpleSource omitCopy(final CopySimpleTarget targetItem)
	{
		return new CopySimpleSource(
			CopySimpleSource.targetItem.map(targetItem)
		);
	}

	static CopySimpleSource omitTarget(
			final String templateString,
			final CopyValue templateItem)
	{
		return new CopySimpleSource(
			CopySimpleSource.templateString.map(templateString),
			CopySimpleSource.templateItem.map(templateItem)
		);
	}

	static CopySimpleSource omitAll()
	{
		return new CopySimpleSource(SetValue.EMPTY_ARRAY);
	}

	void setTemplateStringAndTargetItem(
			final String templateString,
			final CopySimpleTarget targetItem)
	{
		set(
				CopySimpleSource.templateString.map(templateString),
				CopySimpleSource.targetItem.map(targetItem));
	}

	/**
	 * Creates a new CopySimpleSource with all the fields initially needed.
	 * @param targetItem the initial value for field {@link #targetItem}.
	 * @param templateString the initial value for field {@link #templateString}.
	 * @param templateItem the initial value for field {@link #templateItem}.
	 * @throws com.exedio.cope.StringLengthViolationException if templateString violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CopySimpleSource(
				@javax.annotation.Nullable final CopySimpleTarget targetItem,
				@javax.annotation.Nullable final java.lang.String templateString,
				@javax.annotation.Nullable final CopyValue templateItem)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CopySimpleSource.targetItem,targetItem),
			com.exedio.cope.SetValue.map(CopySimpleSource.templateString,templateString),
			com.exedio.cope.SetValue.map(CopySimpleSource.templateItem,templateItem),
		});
	}

	/**
	 * Creates a new CopySimpleSource and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CopySimpleSource(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #targetItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CopySimpleTarget getTargetItem()
	{
		return CopySimpleSource.targetItem.get(this);
	}

	/**
	 * Sets a new value for {@link #targetItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTargetItem(@javax.annotation.Nullable final CopySimpleTarget targetItem)
	{
		CopySimpleSource.targetItem.set(this,targetItem);
	}

	/**
	 * Returns the value of {@link #templateString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getTemplateString()
	{
		return CopySimpleSource.templateString.get(this);
	}

	/**
	 * Sets a new value for {@link #templateString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTemplateString(@javax.annotation.Nullable final java.lang.String templateString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CopySimpleSource.templateString.set(this,templateString);
	}

	/**
	 * Returns the value of {@link #templateItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CopyValue getTemplateItem()
	{
		return CopySimpleSource.templateItem.get(this);
	}

	/**
	 * Sets a new value for {@link #templateItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTemplateItem(@javax.annotation.Nullable final CopyValue templateItem)
	{
		CopySimpleSource.templateItem.set(this,templateItem);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copySimpleSource.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopySimpleSource> TYPE = com.exedio.cope.TypesBound.newType(CopySimpleSource.class,CopySimpleSource::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CopySimpleSource(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
