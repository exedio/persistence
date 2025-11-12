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

import static com.exedio.cope.Intern.intern;

import com.exedio.cope.ConnectProperties.TrimClass;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Serial;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class UniqueConstraint extends AbstractFeature implements Copyable
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final FunctionField<?>[] fields;
	private final List<FunctionField<?>> fieldList;
	private String databaseID;

	private UniqueConstraint(final FunctionField<?>[] fields)
	{
		this.fields = fields;
		this.fieldList = List.of(fields);
		for(final FunctionField<?> f : fields)
			//noinspection ThisEscapedInObjectConstruction
			f.registerUniqueConstraint(this);
	}

	/**
	 * Is not public, because one should use {@link FunctionField#unique()} etc.
	 */
	UniqueConstraint(final FunctionField<?> field)
	{
		this(new FunctionField<?>[]{field});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2)
	{
		this(new FunctionField<?>[]{field1, field2});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3)
	{
		this(new FunctionField<?>[]{field1, field2, field3});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9, final FunctionField<?> field10)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9, final FunctionField<?> field10, final FunctionField<?> field11)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
	}

	/** @deprecated use {@link UniqueConstraint#create} */
	@Deprecated
	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9, final FunctionField<?> field10, final FunctionField<?> field11, final FunctionField<?> field12)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
	}

	/**
	 * Create a UniqueConstraint on two or more fields.
	 * Use {@link FunctionField#unique()} to create an UniqueConstraint on a single field.
	 */
	public static UniqueConstraint create(final FunctionField<?>... fields)
	{
		return switch (fields.length)
		{
			case 0 ->
				throw new IllegalArgumentException("must provide at least two fields");
			case 1 ->
				throw new IllegalArgumentException("use FunctionField#unique() to create an unique constraint on a single field");
			default ->
				new UniqueConstraint(com.exedio.cope.misc.Arrays.copyOf(fields));
		};
	}

	@Override
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);
		for(final FunctionField<?> f : fields)
			if (f.getType()!=type) throw new IllegalArgumentException("UniqueConstraint "+this+" cannot include field "+f);
	}

	@Override
	public UniqueConstraint copy(final CopyMapper mapper)
	{
		return new UniqueConstraint(mapper.get(fields));
	}

	public List<FunctionField<?>> getFields()
	{
		return fieldList;
	}

	static final String IMPLICIT_UNIQUE_SUFFIX = "ImplicitUnique";

	void connect(final Table table)
	{
		if(databaseID!=null)
			throw new RuntimeException();

		final String schemaName =
			(fields.length==1)
			? fields[0].getDeclaredSchemaName() + POSTFIX
			: table.database.properties.redundantUnq(getDeclaredSchemaName());
		databaseID = intern(table.makeGlobalID(TrimClass.standard, schemaName));

		table.database.executor.addUniqueConstraint(databaseID, this);
	}

	static final String POSTFIX = "_Unq";

	void disconnect()
	{
		if(databaseID==null)
			throw new RuntimeException();

		databaseID = null;
	}

	String getDatabaseID()
	{
		if(databaseID==null)
			throw new RuntimeException();

		return databaseID;
	}

	void makeSchema(final com.exedio.dsmf.Table dsmf)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append('(');
		for(int i = 0; i<fields.length; i++)
		{
			if(i>0)
				sb.append(',');
			final FunctionField<?> f = fields[i];
			sb.append(f.getColumn().quotedID);
		}
		sb.append(')');

		dsmf.newUnique(
				(fields.length==1) ? dsmf.getColumn(fields[0].getColumn().id) : null,
				getDatabaseID(), sb.toString());
	}

	@Override
	void toStringNotMounted(final StringBuilder sb, final Type<?> defaultType)
	{
		sb.append("unique(");
		fields[0].toString(sb, defaultType);
		for(int i = 1; i<fields.length; i++)
		{
			sb.append(',');
			fields[i].toString(sb, defaultType);
		}
		sb.append(')');
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 */
	public Item search(final Object... values)
	{
		return getType().searchSingleton(buildCondition(values));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	public Item searchStrict(final Object... values) throws IllegalArgumentException
	{
		return getType().searchSingletonStrict(buildCondition(values));
	}

	private Condition buildCondition(final Object[] values) throws RuntimeException
	{
		// TODO: search natively for unique constraints
		final List<FunctionField<?>> fields = getFields();
		if(fields.size()!=values.length)
			throw new RuntimeException(String.valueOf(fields.size())+'-'+values.length);
		for(int i = 0; i<values.length; i++)
			if(values[i]==null)
				throw new NullPointerException("cannot search uniquely for null on " + getID() + " for " + fields.get(i).getID());
		final Iterator<FunctionField<?>> fieldIter = fields.iterator();
		final Condition[] conditions = new Condition[fields.size()];
		//noinspection ForLoopThatDoesntUseLoopVariable
		for(int j = 0; fieldIter.hasNext(); j++)
			conditions[j] = fieldIter.next().isCasted(values[j]);
		return Cope.and(conditions);
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 */
	@Wrap(order=10, name=Wrap.FOR_NAME, optionTagname="finder",
			varargsFeatures=SearchVarargs.class,
			doc="Finds a {2} by its unique fields.",
			docReturn=Wrap.FOR_RETURN)
	@Nullable
	public <P extends Item> P search(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM, nullability=FixedNonnull.class) final Object... values)
	{
		return
				requireParentClass(typeClass, "typeClass").
				searchSingleton(buildCondition(values));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=20, name=Wrap.FOR_STRICT_NAME, optionTagname="finderStrict",
			varargsFeatures=SearchVarargs.class,
			doc="Finds a {2} by its unique fields.",
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc=Wrap.FOR_STRICT_THROWN))
	@Nonnull
	public <P extends Item> P searchStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM, nullability=FixedNonnull.class) final Object... values)
		throws IllegalArgumentException
	{
		return
				requireParentClass(typeClass, "typeClass").
				searchSingletonStrict(buildCondition(values));
	}

	void check(final FieldValues fieldValues)
	{
		if(!isAffectedBy(fieldValues))
			return;

		final Object[] values = new Object[fields.length];
		int i = 0;

		for(final FunctionField<?> f : fields)
		{
			final Object value = fieldValues.get(f);
			if(value==null)
				return;
			values[i++] = value;
		}

		final Item collision = search(values);
		final Item item = fieldValues.getBackingItem();
		if(collision!=null && !collision.equals(item))
			throw new UniqueViolationException(this, item, null);
	}

	private boolean isAffectedBy(final FieldValues fieldValues)
	{
		for(final FunctionField<?> field : fields)
			if(fieldValues.isDirty(field))
				return true;

		return false;
	}
}
