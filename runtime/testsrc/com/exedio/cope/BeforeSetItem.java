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

import static com.exedio.cope.misc.SetValueUtil.add;
import static com.exedio.cope.misc.SetValueUtil.getFirst;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.pattern.ListField;
import java.util.Arrays;

final class BeforeSetItem extends Item
{
	static final IntegerField field1 = new IntegerField().defaultTo(11).min(1);
	static final IntegerField field2 = new IntegerField().defaultTo(21);

	void setFields(final int value1, final int value2)
	{
		set(
				field1.map(value1),
				field2.map(value2));
	}

	void setFieldsAndAction(final int value1, final int value2, final Action actionValue)
	{
		set(
				field1.map(value1),
				field2.map(value2),
				action.map(actionValue));
	}

	enum Action
	{
		constraintViolation
		{
			@Override SetValue<?>[] execute(final SetValue<?>[] setValues)
			{
				throw MandatoryViolationException.create(field1, null);
			}
		},
		runtimeException
		{
			@Override SetValue<?>[] execute(final SetValue<?>[] setValues)
			{
				throw new RuntimeException(Action.class.getName());
			}
		},
		addField1
		{
			@Override SetValue<?>[] execute(final SetValue<?>[] setValues)
			{
				return add(setValues, field1.map(99));
			}
		},
		addField1ConstraintViolation
		{
			@Override SetValue<?>[] execute(final SetValue<?>[] setValues)
			{
				return add(setValues, field1.map(-1));
			}
		},
		replaceField1
		{
			@Override SetValue<?>[] execute(final SetValue<?>[] setValues)
			{
				for(int i = 0; i<setValues.length; i++)
				{
					if(setValues[i].settable==field1)
						setValues[i] = field1.map(99);
				}
				return setValues;
			}
		},
		addDuplicate
		{
			@Override SetValue<?>[] execute(SetValue<?>[] setValues)
			{
				setValues = add(setValues, field1.map(99));
				setValues = add(setValues, field1.map(99));
				return setValues;
			}
		},
		returnNull
		{
			@Override SetValue<?>[] execute(final SetValue<?>[] setValues)
			{
				return null;
			}
		},
		returnEmpty
		{
			@Override SetValue<?>[] execute(final SetValue<?>[] setValues)
			{
				return SetValue.EMPTY_ARRAY;
			}
		};

		abstract SetValue<?>[] execute(SetValue<?>[] setValues);
	}

	static final EnumField<Action> action = EnumField.create(Action.class).optional();
	static final ListField<String> calls = ListField.create(new StringField().lengthMax(100));

	private boolean fail = false;

	void setFail()
	{
		fail = true;
	}

	@Override
	protected SetValue<?>[] beforeSetCopeItem(SetValue<?>[] setValues)
	{
		assertFalse(fail);

		addToCalls(Arrays.toString(setValues));

		final Action actionValue = getFirst(Arrays.asList(setValues), action);
		if(actionValue!=null)
			setValues = actionValue.execute(setValues);

		return setValues;
	}

	/**
	 * Creates a new BeforeSetItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	BeforeSetItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new BeforeSetItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private BeforeSetItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getField1()
	{
		return BeforeSetItem.field1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #field1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setField1(final int field1)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		BeforeSetItem.field1.set(this,field1);
	}

	/**
	 * Returns the value of {@link #field2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getField2()
	{
		return BeforeSetItem.field2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #field2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setField2(final int field2)
	{
		BeforeSetItem.field2.set(this,field2);
	}

	/**
	 * Returns the value of {@link #action}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Action getAction()
	{
		return BeforeSetItem.action.get(this);
	}

	/**
	 * Sets a new value for {@link #action}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAction(@javax.annotation.Nullable final Action action)
	{
		BeforeSetItem.action.set(this,action);
	}

	/**
	 * Returns the value of {@link #calls}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<String> getCalls()
	{
		return BeforeSetItem.calls.get(this);
	}

	/**
	 * Returns a query for the value of {@link #calls}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.Query<String> getCallsQuery()
	{
		return BeforeSetItem.calls.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #calls} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<BeforeSetItem> getDistinctParentsOfCalls(final String element)
	{
		return BeforeSetItem.calls.getDistinctParents(BeforeSetItem.class,element);
	}

	/**
	 * Adds a new value for {@link #calls}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addToCalls(@javax.annotation.Nonnull final String calls)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		BeforeSetItem.calls.add(this,calls);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #calls}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean removeAllFromCalls(@javax.annotation.Nonnull final String calls)
	{
		return BeforeSetItem.calls.removeAll(this,calls);
	}

	/**
	 * Sets a new value for {@link #calls}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCalls(@javax.annotation.Nonnull final java.util.Collection<? extends String> calls)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		BeforeSetItem.calls.set(this,calls);
	}

	/**
	 * Returns the parent field of the type of {@link #calls}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<BeforeSetItem> callsParent()
	{
		return BeforeSetItem.calls.getParent(BeforeSetItem.class);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for beforeSetItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<BeforeSetItem> TYPE = com.exedio.cope.TypesBound.newType(BeforeSetItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private BeforeSetItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
