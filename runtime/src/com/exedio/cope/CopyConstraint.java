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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class CopyConstraint extends Feature
{
	private static final long serialVersionUID = 1l;

	private final ItemField<?> target;
	private final Copy copy;

	CopyConstraint(final ItemField<?> target, final FunctionField<?> copy)
	{
		this.target = requireNonNull(target);
		this.copy = new CopyField(requireNonNull(copy));

		if(target.isMandatory())
			copy.setRedundantByCopyConstraint();
	}


	static CopyConstraint choice(final ItemField<?> target, final String backPointer)
	{
		return new CopyConstraint(target, backPointer);
	}

	private CopyConstraint(final ItemField<?> target, final String backPointer)
	{
		this.target = target;
		this.copy = new CopyChoice(backPointer);
	}


	public ItemField<?> getTarget()
	{
		return target;
	}

	/**
	 * Returns whether this copy constraint is a choice constraint.
	 * A choice constraint is a special copy constraint.
	 * It constrains {@link #getTarget() target} values to items,
	 * that do point back to the source of {@code target}.
	 * Such constraints are created by {@link ItemField#choice(String)}.
	 */
	public boolean isChoice()
	{
		return copy instanceof CopyChoice;
	}

	/**
	 * @deprecated
	 * Use either {@link #getCopyField()} or {@link #getCopyFunction()} instead.
	 * Choose carefully!
	 * <b>BEWARE:</b> {@link #getCopyField()} may throw IllegalArgumentException
	 */
	@Nonnull
	@Deprecated
	public FunctionField<?> getCopy()
	{
		return getCopyField();
	}

	/**
	 * Returns the copy of this copy constraint,
	 * if it is not a {@link CopyConstraint#isChoice() choice constraint}.
	 *
	 * @see #getCopyFunction()
	 * @throws IllegalArgumentException if this is a {@link ItemField#choice(String) choice constraint}
	 */
	@Nonnull
	public FunctionField<?> getCopyField()
	{
		return copy.getField();
	}

	/**
	 * Returns the copy of this copy constraint.
	 * It is either a {@link FunctionField} or
	 * (for {@link CopyConstraint#isChoice() choice constraints}) a {@link This}.
	 *
	 * @see #getCopyField()
	 */
	@Nonnull
	public Function<?> getCopyFunction()
	{
		return copy.getFunction();
	}

	private FunctionField<?> templateIfSet = null;

	void resolveTemplate()
	{
		if(templateIfSet!=null)
			throw new RuntimeException(toString());

		final Feature feature = target.getValueType().getFeature(copy.getTemplateName());
		if(feature==null)
			throw new IllegalArgumentException(
					"insufficient template for CopyConstraint " + this + ": " +
					"not found");
		if(!(feature instanceof FunctionField<?>))
			throw new ClassCastException(
					"insufficient template for CopyConstraint " + this + ": " +
					feature + " is not a FunctionField but " + feature.getClass().getName());
		final FunctionField<?> result = (FunctionField<?>)feature;
		if(!result.isfinal)
			throw new IllegalArgumentException(
					"insufficient template for CopyConstraint " + this + ": " +
					result + " is not final");
		if(!copy.overlaps(result))
			throw new IllegalArgumentException(
					"insufficient template for CopyConstraint " + this + ": " +
					result + "'s " + result.getValueClass().getName() + " does not overlap copy " +
					copy   + "'s " + copy  .getValueClass().getName());

		templateIfSet = result;
	}

	public FunctionField<?> getTemplate()
	{
		final FunctionField<?> result = templateIfSet;
		if(result==null)
			throw new IllegalStateException("template not resolved " + this);
		return result;
	}

	void check(final FieldValues fieldValues)
	{
		final Item targetItem = fieldValues.get(target);
		if(targetItem==null)
			return;

		final Object expectedValue = getTemplate().get(targetItem);
		final Object actualValue = copy.getActualValueAndCheck(fieldValues, targetItem, expectedValue);
		if(!Objects.equals(expectedValue, actualValue))
			throw new CopyViolationException(fieldValues, targetItem, this, expectedValue, actualValue);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Condition notEqual(final Copy f1, final Function f2)
	{
		return f1.getFunction().notEqual(f2);
	}

	public int check()
	{
		final Query<?> q = getType().newQuery();
		final Join j = q.join(target.getValueType());
		j.setCondition(target.equalTarget(j));
		q.setCondition(notEqual(copy, getTemplate().bind(j)));
		return q.total();
	}


	private abstract static class Copy implements Serializable
	{
		abstract String getTemplateName();
		abstract boolean overlaps(FunctionField<?> template);
		abstract Class<?> getValueClass();
		@Nonnull abstract Function<?> getFunction();
		@Nonnull abstract FunctionField<?> getField();
		abstract Object getActualValueAndCheck(FieldValues fieldValues, Item targetItem, Object expectedValue);
		@Override public abstract String toString();
		private static final long serialVersionUID = -1l;
	}

	private static final class CopyField extends Copy
	{
		final FunctionField<?> field;

		CopyField(final FunctionField<?> field)
		{
			this.field = field;
		}

		@Override String getTemplateName()
		{
			return field.getName();
		}
		@Override boolean overlaps(final FunctionField<?> template)
		{
			return field.overlaps(template);
		}
		@Override Class<?> getValueClass()
		{
			return field.getValueClass();
		}
		@Override Function<?> getFunction()
		{
			return field;
		}
		@Override FunctionField<?> getField()
		{
			return field;
		}
		@Override Object getActualValueAndCheck(
				final FieldValues fieldValues,
				final Item targetItem,
				final Object expectedValue)
		{
			return fieldValues.get(field);
		}
		@Override public String toString()
		{
			return field.toString();
		}
		private static final long serialVersionUID = -1l;
	}

	private final class CopyChoice extends Copy
	{
		final String backPointer;

		private CopyChoice(final String backPointer)
		{
			this.backPointer = backPointer;
		}

		@Override String getTemplateName()
		{
			return backPointer;
		}
		@Override boolean overlaps(final FunctionField<?> template)
		{
			if(!(template instanceof ItemField))
				return false;
			final ItemField<?> t = (ItemField<?>)template;
			return getType().overlaps(t.getValueType());
		}
		@Override Class<?> getValueClass()
		{
			return getType().getJavaClass();
		}
		@Override Function<?> getFunction()
		{
			return getType().getThis();
		}
		@Override FunctionField<?> getField()
		{
			throw new IllegalArgumentException(getID() + " is choice");
		}
		@Override Object getActualValueAndCheck(
				final FieldValues fieldValues,
				final Item targetItem,
				final Object expectedValue)
		{
			final Object actualValue = fieldValues.getBackingItem();
			// special case if actualValue is an item about to be created
			if(actualValue==null)
				throw new CopyViolationException(targetItem, CopyConstraint.this, expectedValue, fieldValues.backingType);
			return actualValue;
		}
		@Override public String toString()
		{
			return getType().getThis().toString();
		}
		private static final long serialVersionUID = -1l;
	}
}
