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

import static com.exedio.cope.CastUtils.toIntCapped;
import static com.exedio.cope.Executor.longResultSetHandler;
import static com.exedio.cope.TypesBound.future;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class ItemField<E extends Item> extends FunctionField<E>
	implements ItemFunction<E>
{
	private static final long serialVersionUID = 1l;

	private final TypeFuture<E> valueTypeFuture;
	private final DeletePolicy policy;
	private final CopyTo[] copyTo;
	private final CopyConstraint[] implicitCopyConstraintsTo;
	private final BiFunction<Type<?>, CopyConstraint, Feature> choiceBackPointer;
	private final CopyConstraint choice;

	private ItemField(
			final boolean isfinal,
			final boolean optional,
			final Class<E> valueClass,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final CopyTo[] copyTo,
			final BiFunction<Type<?>, CopyConstraint, Feature> choiceBackPointer,
			final TypeFuture<E> valueTypeFuture,
			final DeletePolicy policy)
	{
		super(isfinal, optional, valueClass, unique, copyFrom, null/* defaultS makes no sense for ItemField */);
		checkValueClass(Item.class);
		if(Item.class.equals(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Item.class.getName() + " but Item itself");
		this.valueTypeFuture = requireNonNull(valueTypeFuture, "valueType");
		this.policy = requireNonNull(policy, "policy");
		if(policy==DeletePolicy.NULLIFY)
		{
			if(isfinal)
				throw new IllegalArgumentException("final item field cannot have delete policy nullify");
			if(!optional)
				throw new IllegalArgumentException("mandatory item field cannot have delete policy nullify");
		}
		this.copyTo = copyTo;
		this.implicitCopyConstraintsTo = (copyTo!=null) ? newCopyConstraintsTo(copyTo) : null;
		this.choiceBackPointer = choiceBackPointer;
		//noinspection ThisEscapedInObjectConstruction
		this.choice = (this.choiceBackPointer!=null) ? CopyConstraint.choice(this, this.choiceBackPointer) : null;
		if(choiceBackPointer!=null)
		{
			if(isfinal)
				throw new IllegalArgumentException("final item field cannot have choice constraint");
			if(!optional)
				throw new IllegalArgumentException("mandatory item field cannot have choice constraint");
		}
		mountDefault();
	}

	private CopyConstraint[] newCopyConstraintsTo(final CopyTo[] copyFrom)
	{
		assert copyFrom.length>0;
		final CopyConstraint[] result = new CopyConstraint[copyFrom.length];
		for(int i = 0; i<copyFrom.length; i++)
			result[i] = new CopyConstraint(this, copyFrom[i].copy, copyFrom[i].template);
		return result;
	}

	ItemField(final Class<E> valueClass, final TypeFuture<E> valueTypeFuture, final DeletePolicy policy)
	{
		this(false, policy==DeletePolicy.NULLIFY, valueClass, false, null, null, null, valueTypeFuture, policy);
	}

	public static <E extends Item> ItemField<E> create(final Class<E> valueClass)
	{
		return create(valueClass, DeletePolicy.FORBID);
	}

	public static <E extends Item> ItemField<E> create(final Class<E> valueClass, final DeletePolicy policy)
	{
		return new ItemField<>(valueClass, future(valueClass), policy);
	}

	public static <E extends Item> ItemField<E> create(
			final Class<E> valueClass,
			final TypeFuture<E> valueType,
			final DeletePolicy policy)
	{
		return new ItemField<>(valueClass, valueType, policy);
	}

	@Override
	public ItemField<E> copy()
	{
		return new ItemField<>(isfinal, optional, valueClass, unique, copyFrom, copyTo, choiceBackPointer, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> toFinal()
	{
		return new ItemField<>(true, optional, valueClass, unique, copyFrom, copyTo, choiceBackPointer, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> optional()
	{
		return new ItemField<>(isfinal, true, valueClass, unique, copyFrom, copyTo, choiceBackPointer, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> unique()
	{
		return new ItemField<>(isfinal, optional, valueClass, true, copyFrom, copyTo, choiceBackPointer, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> nonUnique()
	{
		return new ItemField<>(isfinal, optional, valueClass, false, copyFrom, copyTo, choiceBackPointer, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target));
	}

	@Override
	public ItemField<E> copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<E>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public ItemField<E> copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private ItemField<E> copyFrom(final CopyFrom copyFrom)
	{
		return new ItemField<>(isfinal, optional, valueClass, unique, addCopyFrom(copyFrom), copyTo, choiceBackPointer, valueTypeFuture, policy);
	}

	/**
	 * To be deprecated, use {@link #copyTo(FunctionField, Supplier)} instead.
	 * @see FunctionField#copyFrom(ItemField)
	 */
	public ItemField<E> copyTo(final FunctionField<?> copy)
	{
		return copyTo(new CopyTo(copy, CopyConstraint.RESOLVE_TEMPLATE));
	}

	/**
	 * @see #copyToSelf(FunctionField)
	 * @see FunctionField#copyFrom(ItemField,Supplier)
	 */
	public <C> ItemField<E> copyTo(final FunctionField<C> copy, final Supplier<FunctionField<C>> template)
	{
		return copyTo(new CopyTo(copy, template));
	}

	/**
	 * Shortcut for {@link #copyTo(FunctionField, Supplier) copyTo(copy, template)}
	 * when copy and template are identical.
	 * That means
	 * <pre>
	 * static final ItemField&lt;MyItem&gt; myTarget = ItemField.create(MyItem.class).copyTo(myField, () -&gt; myField);
	 * </pre>
	 * can und must be rewritten as
	 * <pre>
	 * static final ItemField&lt;MyItem&gt; myTarget = ItemField.create(MyItem.class).copyToSelf(myField);
	 * </pre>
	 * @see #copyTo(FunctionField, Supplier)
	 * @see FunctionField#copyFromSelf(ItemField)
	 */
	public <C> ItemField<E> copyToSelf(final FunctionField<C> copyAndTemplate)
	{
		return copyTo(new CopyTo(copyAndTemplate, CopyConstraint.SELF_TEMPLATE));
	}

	private ItemField<E> copyTo(final CopyTo copyTo)
	{
		return new ItemField<>(isfinal, optional, valueClass, unique, copyFrom, addCopyTo(copyTo), choiceBackPointer, valueTypeFuture, policy);
	}

	private CopyTo[] addCopyTo(final CopyTo copyTo)
	{
		requireNonNull(copyTo);
		if(this.copyTo==null)
			return new CopyTo[]{copyTo};

		final int length = this.copyTo.length;
		final CopyTo[] result = new CopyTo[length+1];
		System.arraycopy(this.copyTo, 0, result, 0, length);
		result[length] = copyTo;
		return result;
	}

	private static final class CopyTo
	{
		final FunctionField<?> copy;
		final Supplier<? extends FunctionField<?>> template;

		CopyTo(final FunctionField<?> copy, final Supplier<? extends FunctionField<?>> template)
		{
			this.copy = requireNonNull(copy, "copy");
			this.template = requireNonNull(template, "template");
		}
	}

	@Override
	public ItemField<E> noCopyFrom()
	{
		return new ItemField<>(isfinal, optional, valueClass, unique, null, copyTo, choiceBackPointer, valueTypeFuture, policy);
	}

	/**
	 * Causes this ItemField to create a {@link CopyConstraint#isChoice() choice constraint}.
	 * <p>
	 * This ItemField becomes the {@link CopyConstraint#getTarget() target} of the copy constraint.
	 * The field pointing back becomes the {@link CopyConstraint#getTemplate() template},
	 * and {@link Type#getThis()} of the type of this ItemField becomes the {@link CopyConstraint#getCopyFunction() copy}.
	 *
	 * @param backPointer a supplier of the field pointing back; at {@code E}, this has to be an
	 *        {@code ItemField} where the {@link #getValueType() value type} overlaps with this
	 *        item field's {@link #getType()}
	 */
	public ItemField<E> choice(final Supplier<ItemField<?>> backPointer)
	{
		requireNonNull(backPointer, "backPointer");
		return choice((type,constraint) -> backPointer.get());
	}

	/**
	 * Causes this ItemField to create a {@link CopyConstraint#isChoice() choice constraint}.
	 * <p>
	 * This ItemField becomes the {@link CopyConstraint#getTarget() target} of the copy constraint.
	 * The field pointing back becomes the {@link CopyConstraint#getTemplate() template},
	 * and {@link Type#getThis()} of the type of this ItemField becomes the {@link CopyConstraint#getCopyFunction() copy}.
	 *
	 * @param backPointerName the name of the field pointing back; at {@code E}, this has to be the name of an
	 *        {@code ItemField} where the {@link #getValueType() value type} overlaps with this
	 *        item field's {@link #getType()}
	 * @deprecated Use {@link #choice(Supplier)} instead
	 */
	@Deprecated
	public ItemField<E> choice(final String backPointerName)
	{
		requireNonEmpty(backPointerName, "backPointerName");
		return choice(CopyConstraint.resolveTemplateByName(backPointerName));
	}

	private ItemField<E> choice(final BiFunction<Type<?>, CopyConstraint, Feature> backPointer)
	{
		if(choiceBackPointer!=null)
			throw new IllegalArgumentException("choice already set");

		return new ItemField<>(isfinal, optional, valueClass, unique, copyFrom, copyTo, backPointer, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> noDefault()
	{
		return copy(); // no defaults for item fields
	}

	/**
	 * Additionally makes the field {@link #optional() optional}.
	 */
	public ItemField<E> nullify()
	{
		return new ItemField<>(isfinal, true, valueClass, unique, copyFrom, copyTo, choiceBackPointer, valueTypeFuture, DeletePolicy.NULLIFY);
	}

	public ItemField<E> cascade()
	{
		return new ItemField<>(isfinal, optional, valueClass, unique, copyFrom, copyTo, choiceBackPointer, valueTypeFuture, DeletePolicy.CASCADE);
	}

	/**
	 * @deprecated defaults make no sense for ItemField
	 */
	@Deprecated
	@Override
	public ItemField<E> defaultTo(final E defaultConstant)
	{
		if(defaultConstant!=null)
			throw new IllegalArgumentException("no defaults for item fields " + this);

		return copy(); // no defaults for item fields
	}

	@Override
	boolean overlaps(final FunctionField<?> other)
	{
		//noinspection OverlyStrongTypeCast ItemFunction
		return
				super.overlaps(other) &&
				getValueType().overlaps(((ItemField<?>)other).getValueType());
	}

	public CopyConstraint getChoice()
	{
		return choice;
	}

	/**
	 * @see #asExtends(Class)
	 * @see #asSuper(Class)
	 * @see EnumField#as(Class)
	 * @see Class#asSubclass(Class)
	 */
	public <X extends Item> ItemField<X> as(final Class<X> clazz)
	{
		if(!valueClass.equals(clazz))
		{
			final String n = ItemField.class.getName();
			// exception message consistent with Cope.verboseCast(Class, Object)
			throw new ClassCastException(
					"expected a " + n + '<' + clazz.getName() +
					">, but was a " + n + '<' + valueClass.getName() + '>');
		}

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final ItemField<X> result = (ItemField<X>)this;
		return result;
	}

	/**
	 * @see #as(Class)
	 * @see Class#asSubclass(Class)
	 */
	public <X extends Item> ItemField<? extends X> asExtends(final Class<X> clazz)
	{
		if(!clazz.isAssignableFrom(valueClass))
		{
			final String n = ItemField.class.getName();
			// exception message consistent with Cope.verboseCast(Class, Object)
			throw new ClassCastException(
					"expected a " + n + "<? extends " + clazz.getName() +
					">, but was a " + n + '<' + valueClass.getName() + '>');
		}

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final ItemField<X> result = (ItemField<X>)this;
		return result;
	}

	/**
	 * @see #as(Class)
	 */
	public <X extends Item> ItemField<? super X> asSuper(final Class<X> clazz)
	{
		if(!valueClass.isAssignableFrom(clazz))
		{
			final String n = ItemField.class.getName();
			// exception message consistent with Cope.verboseCast(Class, Object)
			throw new ClassCastException(
					"expected a " + n + "<? super " + clazz.getName() +
					">, but was a " + n + '<' + valueClass.getName() + '>');
		}

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final ItemField<X> result = (ItemField<X>)this;
		return result;
	}

	public DeletePolicy getDeletePolicy()
	{
		return policy;
	}


	@Override
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		if(implicitCopyConstraintsTo!=null)
			for(final CopyConstraint constraint : implicitCopyConstraintsTo)
				constraint.mount(type, constraint.getCopyField().getName() + "CopyFrom" + name, null);

		if(choice!=null)
			choice.mount(type, name + "Choice", null);
	}

	private Type<E> valueType = null;

	private Type<E> resolveValueTypeFuture()
	{
		final Type<E> result = valueTypeFuture.get();
		if(result==null)
			throw new NullPointerException(
					"ItemField " + this + ": " +
					"resolving TypeFuture " + valueTypeFuture + " " +
					"expected " + valueClass.getName() + ", " +
					"but was null.");
		if(!valueClass.equals(result.getJavaClass()))
			throw new IllegalArgumentException(
					"ItemField " + this + ": " +
					"resolving TypeFuture " + valueTypeFuture + " " +
					"expected " + valueClass.getName() + ", " +
					"but was " + result.getJavaClass().getName() + " " +
					"from " + result + '.');
		return result;
	}

	void resolveValueType(final Set<Type<?>> typesAllowed)
	{
		if(!isMountedToType())
			throw new RuntimeException();
		if(valueType!=null)
			throw new RuntimeException(getID());

		final Type<E> valueType = resolveValueTypeFuture();
		if(!typesAllowed.contains(valueType))
			throw new IllegalArgumentException("value type of " + this + " (" + valueTypeFuture + ") does not belong to the same model");
		this.valueType = valueType;
	}

	/**
	 * Returns the type of items, this field accepts instances of.
	 */
	@Override
	public Type<E> getValueType()
	{
		if(valueType==null)
		{
			if(!isMountedToType())
				return resolveValueTypeFuture();

			throw new IllegalStateException(
					"item field " + this + " (" + valueTypeFuture + ") does not belong to any model");
		}

		return valueType;
	}

	/**
	 * Returns the same value as {@link #getValueType()}
	 * for the model this field (and its {@link #getType() type}) is part of.
	 * For other models it returns the same value as {@link #getValueType()}
	 * if this field (and its {@link #getType() type}) had been added to that model.
	 */
	public Type<E> getValueType(final Model model)
	{
		requireNonNull(model, "model"); // probably will be needed for TypeFuture sometimes
		return resolveValueTypeFuture();
	}

	@Override
	public ItemFunction<E> bind(final Join join)
	{
		return BindItemFunction.create(this, join);
	}


	private boolean connected = false;
	private Type<? extends E> onlyPossibleValueType = null;
	private StringColumn typeColumn = null;

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		final Type<E> valueType = getValueType();
		if(connected)
			throw new RuntimeException(toString());
		if(onlyPossibleValueType!=null)
			throw new RuntimeException(toString());
		if(typeColumn!=null)
			throw new RuntimeException(toString());

		final ItemColumn result = new ItemColumn(table, name, optional, valueType);

		final SortedSet<String> typeColumnValues = valueType.getTypesOfInstancesColumnValues();
		if(typeColumnValues==null)
			onlyPossibleValueType = valueType.getOnlyPossibleTypeOfInstances();
		else
			typeColumn = new TypeColumn(table, result, optional, valueType.typeColumnMinLength, typeColumnValues);

		connected = true;

		return result;
	}

	@Override
	void disconnect()
	{
		if(!connected)
			throw new RuntimeException(toString());
		if(onlyPossibleValueType==null && typeColumn==null)
			throw new RuntimeException(toString());

		super.disconnect();
		connected = false;
		onlyPossibleValueType = null;
		typeColumn = null;
	}

	private Type<? extends E> getOnlyPossibleValueType()
	{
		if(!connected)
			throw new RuntimeException(toString());

		return onlyPossibleValueType;
	}

	StringColumn getTypeColumn()
	{
		if(!connected)
			throw new RuntimeException(toString());

		return typeColumn;
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	@SuppressWarnings("deprecation") // needed for idea
	public void append(@SuppressWarnings("ClassEscapesDefinedScope") final Statement bf, final Join join)
	{
		super.append(bf, join);
		bf.appendTypeColumnIfRequired(getTypeColumn(), join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendType(@SuppressWarnings("ClassEscapesDefinedScope") final Statement bf, final Join join)
	{
		bf.append(Statement.assertTypeColumn(getTypeColumn(), getValueType()), join);
	}

	@Override
	E get(final Row row)
	{
		final StringColumn typeColumn = getTypeColumn();
		final Object cell = row.get(getColumn());

		if(cell==null)
		{
			if(typeColumn!=null && row.get(typeColumn)!=null)
				throw new RuntimeException("inconsistent type column on field " + this + ": " + row.get(typeColumn) + " --- row: " + row);

			return null;
		}
		else
		{
			final Type<? extends E> cellType;
			if(typeColumn!=null)
			{
				final String cellTypeID = (String)row.get(typeColumn);

				if(cellTypeID==null)
					throw new RuntimeException("inconsistent type column on field " + this);

				cellType = getValueType().getTypeOfInstance(cellTypeID);

				if(cellType==null)
					throw new RuntimeException(cellTypeID);
			}
			else
			{
				cellType = getOnlyPossibleValueType();

				if(cellType==null)
					throw new RuntimeException();
			}

			return cellType.getItemObject(((Number)cell).longValue());
		}
	}

	@Override
	void set(final Row row, final E surface)
	{
		final StringColumn typeColumn = getTypeColumn();

		if(surface==null)
		{
			row.put(getColumn(), null);
			if(typeColumn!=null)
				row.put(typeColumn, null);
		}
		else
		{
			row.put(getColumn(), surface.pk);
			if(typeColumn!=null)
				row.put(typeColumn, surface.type.schemaId);
		}
	}

	@Override
	public boolean needsCheckTypeColumn()
	{
		return getTypeColumn()!=null;
	}

	@Override
	public long checkTypeColumnL()
	{
		final Statement statement = // must be first to throw Model.NotConnectedException when needed
				checkTypeColumnStatement(Statement.Mode.NORMAL);
		final Transaction tx = getType().getModel().currentTransaction();
		return tx.connect.executor.query(
				tx.getConnection(),
				statement,
				null, false, longResultSetHandler);
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public Statement checkTypeColumnStatement(final Statement.Mode mode)
	{
		ItemFunctionUtil.checkTypeColumnNeeded(this);

		final Type<?> type = getType();
		final Executor executor = type.getModel().connect().executor;
		final Table table = type.getTable();
		final Table valueTable = getValueType().getTable();
		final String alias1 = executor.dialect.dsmfDialect.quoteName(Table.SQL_ALIAS_1);
		final String alias2 = executor.dialect.dsmfDialect.quoteName(Table.SQL_ALIAS_2);

		final Statement bf = executor.newStatement(false, mode);
		//language=SQL
		bf.append("SELECT COUNT(*) FROM ").
			append(table).append(' ').append(alias1).
			append(',').
			append(valueTable).append(' ').append(alias2).
			append(" WHERE ").
			append(alias1).append('.').append(getColumn()).
			append('=').
			append(alias2).append('.').append(valueTable.primaryKey).
			append(" AND ").
			append(alias1).append('.').append(getTypeColumn()).
			append("<>").
			append(alias2).append('.').append(valueTable.typeColumn);

		//System.out.println("CHECKA:"+bf.toString());

		return bf;
	}

	public enum DeletePolicy
	{
		/**
		 * Corresponds to SQL referential action
		 * <a href="https://en.wikipedia.org/wiki/Foreign_key#RESTRICT">RESTRICT</a>.
		 */
		FORBID,
		/**
		 * Corresponds to SQL referential action
		 * <a href="https://en.wikipedia.org/wiki/Foreign_key#SET_DEFAULT_,_SET_NULL">SET NULL</a>.
		 */
		NULLIFY,
		/**
		 * Corresponds to SQL referential action
		 * <a href="https://en.wikipedia.org/wiki/Foreign_key#CASCADE">CASCADE</a>.
		 */
		CASCADE
	}

	// ------------------- deprecated stuff -------------------

	@Override
	@Deprecated
	public int checkTypeColumn()
	{
		return toIntCapped(checkTypeColumnL());
	}
}
