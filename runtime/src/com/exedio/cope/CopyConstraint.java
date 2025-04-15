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

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class CopyConstraint extends Feature
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final ItemField<?> target;
	private final Copy copy;

	private final java.util.function.Function<String,String> origin;
	/**
	 * If this field is {@code false}, the CopyConstraint was created by
	 * {@link FunctionField#copyFrom(ItemField, Supplier)}
	 * or (the irrelevant case)
	 * {@link ItemField#choice(Supplier)}.
	 * If this field is {@code true}, the CopyConstraint was created by
	 * {@link ItemField#copyTo(FunctionField, Supplier)}.
	 * This field is just used for making an exception message more specific and helpful.
	 */
	private final boolean originTo;

	CopyConstraint(
			final ItemField<?> target,
			final FunctionField<?> copy,
			final Supplier<? extends FunctionField<?>> template,
			final java.util.function.Function<String,String> origin,
			final boolean originTo)
	{
		this.target = requireNonNull(target);
		this.copy = new CopyField(requireNonNull(copy), requireNonNull(template));
		this.origin = origin;
		this.originTo = originTo;

		if(target.isMandatory())
			copy.setRedundantByCopyConstraint();
	}


	static CopyConstraint choice(final ItemField<?> target, final BiFunction<Type<?>, CopyConstraint, Feature> backPointer)
	{
		return new CopyConstraint(target, backPointer);
	}

	private CopyConstraint(final ItemField<?> target, final BiFunction<Type<?>, CopyConstraint, Feature> backPointer)
	{
		this.target = target;
		this.copy = new CopyChoice(backPointer);
		this.origin = t -> target + ".choice(" + t + ')';
		this.originTo = false; // value does not matter in this case
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
	 * Such constraints are created by {@link ItemField#choice(java.util.function.Supplier)}.
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
	 * @throws IllegalArgumentException if this is a {@link ItemField#choice(java.util.function.Supplier) choice constraint}
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

		final Type<?> targetValueType = target.getValueType();
		final Feature feature = copy.getTemplate().apply(targetValueType, this);
		if(feature==null)
			throw new IllegalArgumentException(
					"insufficient template for CopyConstraint " + origin.apply("null") + " (" + this + "): " +
					"supplier returns null");
		if(!(feature instanceof final FunctionField<?> result))
			throw new ClassCastException(
					"insufficient template for CopyConstraint " + origin.apply(feature.toString()) + " (" + this + "): " +
					feature + " is not a FunctionField but " + feature.getClass().getName());
		if(!result.isfinal)
			throw new IllegalArgumentException(
					"insufficient template for CopyConstraint " + origin.apply(feature.toString()) + " (" + this + "): " +
					result + " is not final");
		if(!result.getType().isAssignableFrom(targetValueType))
			throw new IllegalArgumentException(
					"insufficient template for CopyConstraint " + origin.apply(feature.toString()) + " (" + this + "): " +
					result + " must belong to type " + targetValueType + ", " +
					"but belongs to type " + result.getType());
		if(!copy.overlaps(result))
			throw new IllegalArgumentException(
					"insufficient template for CopyConstraint " + origin.apply(feature.toString()) + " (" + this + "): " +
					result + "'s " + result.getValueClass().getName() + " does not overlap copy " +
					copy   + "'s " + copy  .getValueClass().getName());

		templateIfSet = result;
	}

	static BiFunction<Type<?>, CopyConstraint, Feature> resolveTemplateByName(final String name)
	{
		return (type,constraint) ->
		{
			final Feature feature = type.getFeature(name);
			if(feature==null)
				throw new IllegalArgumentException(
						"insufficient template for CopyConstraint " + constraint.origin.apply('"'+name+'"') + " (" + constraint + "): " +
						"feature >" + name + "< at type " + type + " not found");
			return feature;
		};
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
	private static Condition isNot(final Function f1, final Function f2)
	{
		return f1.isNot(f2);
	}

	/**
	 * @see SchemaInfo#check(CopyConstraint)
	 */
	public int check()
	{
		return checkQuery().total();
	}

	Query<?> checkQuery()
	{
		final Query<?> q = getType().newQuery();
		final Join j = q.join(target.getValueType(), target::isTarget);
		final Function<?> copy = this.copy.getFunction();
		final Function<?> template = getTemplate().bind(j);
		// TODO isNull/isNotNull could be omitted for mandatory fields of primary key
		q.setCondition(Cope.or(
				isNot(copy, template),
				copy.isNull   ().and(template.isNotNull()),
				copy.isNotNull().and(template.isNull   ())
		));
		return q;
	}


	private abstract static class Copy implements Serializable
	{
		abstract BiFunction<Type<?>, CopyConstraint, Feature> getTemplate();
		abstract boolean overlaps(FunctionField<?> template);
		abstract Class<?> getValueClass();
		@Nonnull abstract Function<?> getFunction();
		@Nonnull abstract FunctionField<?> getField();
		abstract Object getActualValueAndCheck(FieldValues fieldValues, Item targetItem, Object expectedValue);
		@Override public abstract String toString();
		@Serial
		private static final long serialVersionUID = -1l;
	}

	static final Supplier<? extends FunctionField<?>> RESOLVE_TEMPLATE = () -> { throw new AssertionError("ZACK"); };
	static final Supplier<? extends FunctionField<?>> SELF_TEMPLATE = () -> { throw new AssertionError("ZACK"); };

	private static final class CopyField extends Copy
	{
		final FunctionField<?> field;
		final Supplier<? extends FunctionField<?>> template;

		CopyField(final FunctionField<?> field, final Supplier<? extends FunctionField<?>> template)
		{
			this.field = field;
			this.template = template;
		}

		@Override BiFunction<Type<?>, CopyConstraint, Feature> getTemplate()
		{
			if(template==RESOLVE_TEMPLATE)
				return resolveTemplateByName(field.getName());
			if(template==SELF_TEMPLATE)
				return (type, constraint) -> constraint.getCopyField();

			return (type, constraint) ->
			{
				final Feature result = template.get();
				if(result==field)
					throw new IllegalArgumentException(
							"copy and template are identical for CopyConstraint " + constraint.origin.apply(result.toString()) + " (" + constraint + "), " +
							"use copy" + (constraint.originTo?"To":"From") + "Self instead");
				return result;
			};
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
		@Serial
		private static final long serialVersionUID = -1l;
	}

	private final class CopyChoice extends Copy
	{
		final BiFunction<Type<?>, CopyConstraint, Feature> backPointer;

		private CopyChoice(final BiFunction<Type<?>, CopyConstraint, Feature> backPointer)
		{
			this.backPointer = backPointer;
		}

		@Override BiFunction<Type<?>, CopyConstraint, Feature> getTemplate()
		{
			return backPointer;
		}
		@Override boolean overlaps(final FunctionField<?> template)
		{
			if(!(template instanceof final ItemField<?> t))
				return false;
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
				throw new CopyViolationException(targetItem, CopyConstraint.this, expectedValue);
			return actualValue;
		}
		@Override public String toString()
		{
			return getType().getThis().toString();
		}
		@Serial
		private static final long serialVersionUID = -1l;
	}
}
