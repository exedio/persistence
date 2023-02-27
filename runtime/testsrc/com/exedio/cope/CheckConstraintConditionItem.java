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

@CopeSchemaName("Checkconstraintconditionitem")
final class CheckConstraintConditionItem extends Item
{
	static final IntegerField integer1 = new IntegerField();
	static final IntegerField integer2 = new IntegerField();
	static final ItemField<CheckConstraintConditionItemTarget> item = ItemField.create(CheckConstraintConditionItemTarget.class);
	//static final StringField string = new StringField();

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint isNull     = new CheckConstraint(integer1.isNull());
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint isNotNull  = new CheckConstraint(integer1.isNotNull());

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compare         = new CheckConstraint(integer1.less(0));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compareFunction = new CheckConstraint(integer1.less(integer2));

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint not = new CheckConstraint(integer1.less(integer2).not());

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compositeAnd = new CheckConstraint(integer1.isNull().and(integer2.isNull()));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compositeOr  = new CheckConstraint(integer1.isNull().or (integer2.isNull()));

	//static final CheckConstraint charSet = new CheckConstraint(new CharSetCondition(string, CharSet.ALPHA_LOWER)); TODO
	//static final CheckConstraint like = new CheckConstraint(string.like("like it")); TODO

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint       instanceOf    = new CheckConstraint(item.   instanceOf(CheckConstraintConditionItemSub   .TYPE));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint       instanceOfNot = new CheckConstraint(item.notInstanceOf(CheckConstraintConditionItemSub   .TYPE));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint singleInstanceOf    = new CheckConstraint(item.   instanceOf(CheckConstraintConditionItemBottom.TYPE));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint singleInstanceOfNot = new CheckConstraint(item.notInstanceOf(CheckConstraintConditionItemBottom.TYPE));

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint startsWith = new CheckConstraint(integer1.isNotNull());


	/**
	 * Creates a new CheckConstraintConditionItem with all the fields initially needed.
	 * @param integer1 the initial value for field {@link #integer1}.
	 * @param integer2 the initial value for field {@link #integer2}.
	 * @param item the initial value for field {@link #item}.
	 * @throws com.exedio.cope.MandatoryViolationException if item is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CheckConstraintConditionItem(
				final int integer1,
				final int integer2,
				@javax.annotation.Nonnull final CheckConstraintConditionItemTarget item)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CheckConstraintConditionItem.integer1,integer1),
			com.exedio.cope.SetValue.map(CheckConstraintConditionItem.integer2,integer2),
			com.exedio.cope.SetValue.map(CheckConstraintConditionItem.item,item),
		});
	}

	/**
	 * Creates a new CheckConstraintConditionItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CheckConstraintConditionItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #integer1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getInteger1()
	{
		return CheckConstraintConditionItem.integer1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integer1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInteger1(final int integer1)
	{
		CheckConstraintConditionItem.integer1.set(this,integer1);
	}

	/**
	 * Returns the value of {@link #integer2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getInteger2()
	{
		return CheckConstraintConditionItem.integer2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integer2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInteger2(final int integer2)
	{
		CheckConstraintConditionItem.integer2.set(this,integer2);
	}

	/**
	 * Returns the value of {@link #item}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CheckConstraintConditionItemTarget getItem()
	{
		return CheckConstraintConditionItem.item.get(this);
	}

	/**
	 * Sets a new value for {@link #item}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItem(@javax.annotation.Nonnull final CheckConstraintConditionItemTarget item)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CheckConstraintConditionItem.item.set(this,item);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for checkConstraintConditionItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CheckConstraintConditionItem> TYPE = com.exedio.cope.TypesBound.newType(CheckConstraintConditionItem.class,CheckConstraintConditionItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CheckConstraintConditionItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
