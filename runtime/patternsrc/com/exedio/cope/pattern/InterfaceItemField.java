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

package com.exedio.cope.pattern;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class InterfaceItemField<I> extends Pattern implements Settable<I>
{
	private static final long serialVersionUID = 1L;
	private static final String INTERFACEITEMFIELD = "interfaceItem";

	private final Class<I> commonInterface;
	private final Class<? extends Item>[] classes;
	private final List<ItemField<? extends Item>> fields;
	private final boolean mandatory;
	private final boolean isFinal;
	private final boolean unique;

	private InterfaceItemField(final Class<I> commonInterface, final Class<? extends Item>[] classes)
	{
		this(false, false, false, commonInterface, classes);
	}

	private InterfaceItemField(
			final boolean isFinal,
			final boolean optional,
			final boolean unique,
			final Class<I> commonInterface,
			final Class<? extends Item>[] classes)
	{
		this.isFinal = isFinal;
		this.mandatory = !optional;
		this.unique = unique;
		this.fields = checkClass(isFinal, unique, commonInterface, classes);

		this.commonInterface = commonInterface;
		this.classes = Arrays.copyOf(classes);

		for(final ItemField<? extends Item> field : fields)
		{
			// TODO: simpleName might not be unique
			addSource(field, field.getValueClass().getSimpleName());
		}
		addSource(
				new CheckConstraint(buildXORCondition(fields, this)),
				"xor");
	}

	public static <K> InterfaceItemField<K> create(
			final Class<K> commonInterface,
			final Class<? extends Item>[] classes)
	{
		return new InterfaceItemField<>(commonInterface, classes);
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: generic array
	public static <K> InterfaceItemField<K> create(
			final Class<K> commonInterface,
			final Class<? extends Item> class1,
			final Class<? extends Item> class2)
	{
		return create(commonInterface, new Class[]{class1, class2});
	}

	public Class<? extends Item>[] getClasses()
	{
		return Arrays.copyOf(classes);
	}

	@SuppressWarnings("unchecked")
	// checked in constructor
	@Wrap(order = 10, name = "get{0}", doc = "Returns the value of {0}.")
	public I get(final Item item)
	{
		return get(this, item, fields);
	}

	@Wrap(order = 100, name = "get{0}Source",
			doc = "Returns the source item referencing <tt>"+INTERFACEITEMFIELD+"</tt>.")
	public <K extends Item> K getSource(
			final Class<K> sourceType,
			@Parameter(INTERFACEITEMFIELD) final I interfaceItem)
	{
		for(final ItemField<? extends Item> field : fields)
		{
			if(field.getValueClass().isInstance(interfaceItem))
			{
				return getType().as(sourceType).searchSingleton(equal(interfaceItem, field));
			}
		}
		throw new IllegalArgumentException(interfaceItem+" is not in "+this);
	}

	public List<ItemField<?>> getComponents()
	{
		return Collections.unmodifiableList(fields);
	}

	@SuppressWarnings("unchecked")
	public <X extends Item> ItemField<X> of(final Class<X> clazz)
	{
		for(final ItemField<? extends Item> field : fields)
		{
			if(field.getValueClass().isAssignableFrom(clazz))
			{
				return (ItemField<X>)field;
			}
		}
		throw new IllegalArgumentException("class >"+clazz+"< is not supported by "+this);
	}

	@SuppressWarnings("unchecked")
	private <K extends Item> Condition equal(final I interfaceItem, final ItemField<K> field)
	{
		return field.equal((K) interfaceItem);
	}

	public SetValue<I> map(final I value)
	{
		return SetValue.map(this, value);
	}

	@Override
	@SuppressWarnings(
	{"unchecked", "rawtypes"})
	public SetValue<?>[] execute(final I value, final Item exceptionItem)
	{
		final SetValue<?>[] result = new SetValue<?>[fields.size()];

		boolean valueSet = false;
		for(int i = 0; i<fields.size(); i++)
		{
			final ItemField field = fields.get(i);
			if(field.getValueClass().isInstance(value))
			{
				result[i] = field.map(field.getValueClass().cast(value));
				valueSet = true;
			}
			else
			{
				result[i] = field.mapNull();
			}
		}
		if(value!=null&&Item.class.isAssignableFrom(value.getClass())&&!valueSet)
		{
			final StringBuilder sb = new StringBuilder("value class should be on of <");
			for(final Iterator<ItemField<? extends Item>> it = fields.iterator(); it.hasNext();)
			{
				final ItemField field = it.next();
				sb.append(field.getValueClass().getSimpleName());
				if(it.hasNext())
					sb.append(",");
			}
			sb.append("> but was <");
			sb.append(value.getClass().getSimpleName());
			sb.append(">");
			throw new IllegalArgumentException(sb.toString());
		}
		if(isMandatory()&&!valueSet)
		{
			throw new IllegalArgumentException(this+" is mandatory");
		}
		return result;
	}

	public Condition isNull()
	{
		return nullCondition(false);
	}

	public Condition isNotNull()
	{
		return nullCondition(true);
	}

	private Condition nullCondition(final boolean not)
	{
		Condition c = null;
		for(final ItemField<? extends Item> field : fields)
		{
			final Condition part = not
					? field.isNotNull()
					: field.isNull();

			c = c==null
					? part
					: not
							? c.or(part)
							: c.and(part);
		}
		return c;
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public boolean isMandatory()
	{
		return mandatory;
	}

	public boolean isInitial()
	{
		return true;
	}

	public Class<I> getInitialType()
	{
		return commonInterface;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return getInitialExceptions(this);
	}

	public InterfaceItemField<I> optional()
	{
		return new InterfaceItemField<>(isFinal, true, unique, commonInterface, getClasses());
	}

	public InterfaceItemField<I> toFinal()
	{
		return new InterfaceItemField<>(true, !mandatory, unique, commonInterface, getClasses());
	}

	public InterfaceItemField<I> unique()
	{
		return new InterfaceItemField<>(isFinal, !mandatory, true, commonInterface, getClasses());
	}

	/**
	 * static helper methods
	 */

	private static <I> I get(final Settable<I> pattern, final Item item, final List<ItemField<? extends Item>> fields)
	{
		for(final ItemField<? extends Item> field : fields)
		{
			final Item value = field.get(item);
			if(value!=null)
			{
				@SuppressWarnings("unchecked")
				final I result = (I)value;
				return result;
			}
		}

		if(pattern.isMandatory())
			throw new NullPointerException(INTERFACEITEMFIELD+pattern+" is mandatory but has no value set");
		else
			return null;
	}

	private static Set<Class<? extends Throwable>> getInitialExceptions(final Settable<?> settable)
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
		if(settable.isMandatory())
			result.add(MandatoryViolationException.class);
		return result;
	}

	private static ArrayList<ItemField<? extends Item>> checkClass(
			final boolean isFinal,
			final boolean unique,
			final Class<?> commonInterface,
			final Class<? extends Item>[] classes
	)
	{
		if(classes.length<=1)
		{
			throw new IllegalArgumentException("must use at least 2 classes");
		}
		final ArrayList<ItemField<? extends Item>> fields = new ArrayList<>();
		for(int i = 0; i<classes.length; i++)
		{
			final Class<? extends Item> type = classes[i];
			if(type==null)
			{
				throw new NullPointerException("no null values for classes allowed");
			}
			if(!commonInterface.isAssignableFrom(type))
			{
				throw new IllegalArgumentException("common interface >"+commonInterface+"< must be assignable from class >"
						+type+"<");
			}

			// don't allow different mixin classes to (potentially) share instances
			// because:
			// - unique constraints on ItemFields wouldn't work
			// - searching source would need to be adapted
			for(int j = 0; j<classes.length; j++)
			{
				if(i!=j&&classes[i].isAssignableFrom(classes[j]))
					throw new IllegalArgumentException("Classes must not be super-classes of each other: "+classes[i]
							+" is assignable from "+classes[j]);
			}

			ItemField<? extends Item> field = ItemField.create(type, ItemField.DeletePolicy.CASCADE).optional();
			if(isFinal)
				field = field.toFinal();
			if(unique)
				field = field.unique();
			fields.add(field);
		}
		return fields;
	}

	private static Condition buildXORCondition(final List<ItemField<? extends Item>> fields, final Settable<?> settable)
	{
		final List<Condition> ors = new ArrayList<>(fields.size());
		for(final ItemField<? extends Item> i : fields)
		{
			final List<Condition> ands = new ArrayList<>(fields.size());
			for(final ItemField<? extends Item> j : fields)
			{
				if(i==j)
				{
					if(settable.isMandatory())
					{
						ands.add(j.isNotNull());
					}
				}
				else
				{
					ands.add(j.isNull());
				}
			}
			ors.add(Cope.and(ands));
		}
		return Cope.or(ors);
	}
}
