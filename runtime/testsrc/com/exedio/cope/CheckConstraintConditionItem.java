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

import com.exedio.cope.instrument.WrapperType;

@CopeSchemaName("Checkconstraintconditionitem")
@WrapperType(constructor=NONE, genericConstructor=NONE, comments=false)
final class CheckConstraintConditionItem extends Item
{
	static final IntegerField integer1 = new IntegerField();
	static final IntegerField integer2 = new IntegerField();
	static final ItemField<CheckConstraintConditionItemTarget> item = ItemField.create(CheckConstraintConditionItemTarget.class);
	static final StringField string = new StringField();

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint isNull     = new CheckConstraint(integer1.isNull());
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint isNotNull  = new CheckConstraint(integer1.isNotNull());

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compare         = new CheckConstraint(integer1.less(0));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint lessNot         = new CheckConstraint(integer1.less(0).not());
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint lessOrEqual     = new CheckConstraint(integer1.lessOrEqual(0));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint lessOrEqualNot    = new CheckConstraint(integer1.lessOrEqual(0).not());
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint greater         = new CheckConstraint(integer1.greater(0));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint greaterNot      = new CheckConstraint(integer1.greater(0).not());
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint greaterOrEqual  = new CheckConstraint(integer1.greaterOrEqual(0));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint greaterOrEqualNot = new CheckConstraint(integer1.greaterOrEqual(0).not());
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compareFunction = new CheckConstraint(integer1.less(integer2));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint lessOrEqualFunction       = new CheckConstraint(integer1.lessOrEqual(integer2));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint greaterFunction           = new CheckConstraint(integer1.greater(integer2));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint greaterOrEqualFunction    = new CheckConstraint(integer1.greaterOrEqual(integer2));

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint not = new CheckConstraint(string.regexpLike("[a-z]").not());

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compositeAnd = new CheckConstraint(integer1.isNull().and(integer2.isNull()));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint compositeOr  = new CheckConstraint(integer1.isNull().or (integer2.isNull()));

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint regexpLike = new CheckConstraint(string.regexpLike("[A-Z]"));
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

	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint upper = new CheckConstraint(string.toUpperCase().equal("up"));
	@SuppressWarnings("unused") // OK: CheckConstraint
	static final CheckConstraint lower = new CheckConstraint(string.toLowerCase().equal("low"));


	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getInteger1()
	{
		return CheckConstraintConditionItem.integer1.getMandatory(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInteger1(final int integer1)
	{
		CheckConstraintConditionItem.integer1.set(this,integer1);
	}

	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getInteger2()
	{
		return CheckConstraintConditionItem.integer2.getMandatory(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInteger2(final int integer2)
	{
		CheckConstraintConditionItem.integer2.set(this,integer2);
	}

	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CheckConstraintConditionItemTarget getItem()
	{
		return CheckConstraintConditionItem.item.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItem(@javax.annotation.Nonnull final CheckConstraintConditionItemTarget item)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CheckConstraintConditionItem.item.set(this,item);
	}

	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getString()
	{
		return CheckConstraintConditionItem.string.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setString(@javax.annotation.Nonnull final java.lang.String string)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		CheckConstraintConditionItem.string.set(this,string);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	@com.exedio.cope.instrument.Generated
	static final com.exedio.cope.Type<CheckConstraintConditionItem> TYPE = com.exedio.cope.TypesBound.newType(CheckConstraintConditionItem.class,CheckConstraintConditionItem::new);

	@com.exedio.cope.instrument.Generated
	private CheckConstraintConditionItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
