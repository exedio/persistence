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

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.util.Cast;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class UniqueConstraint extends Feature implements Copyable
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<?>[] fields;
	private final List<FunctionField<?>> fieldList;
	private String databaseID;

	// cannot make this method public, because the instrumentor (i.e. beanshell) does not work with varargs
	private UniqueConstraint(final FunctionField<?>[] fields)
	{
		this.fields = fields;
		this.fieldList = Collections.unmodifiableList(Arrays.asList(fields));
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

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2)
	{
		this(new FunctionField<?>[]{field1, field2});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3)
	{
		this(new FunctionField<?>[]{field1, field2, field3});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9, final FunctionField<?> field10)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9, final FunctionField<?> field10, final FunctionField<?> field11)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6, final FunctionField<?> field7, final FunctionField<?> field8, final FunctionField<?> field9, final FunctionField<?> field10, final FunctionField<?> field11, final FunctionField<?> field12)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
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

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // fieldList is unmodifiable
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
			? fields[0].getDeclaredSchemaName()
			: getDeclaredSchemaName();
		databaseID = intern(table.makeGlobalID(TrimClass.ForeignKeyUniqueConstraint, schemaName + "_Unq"));

		table.database.executor.addUniqueConstraint(databaseID, this);
	}

	void disconnect()
	{
		if(databaseID==null)
			throw new RuntimeException();

		databaseID = null;
	}

	private String getDatabaseID()
	{
		if(databaseID==null)
			throw new RuntimeException();

		return databaseID;
	}

	void makeSchema(final com.exedio.dsmf.Table dsmf)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('(');
		for(int i = 0; i<fields.length; i++)
		{
			if(i>0)
				bf.append(',');
			final FunctionField<?> f = fields[i];
			bf.append(f.getColumn().quotedID);
		}
		bf.append(')');

		dsmf.newUnique(
				(fields.length==1) ? dsmf.getColumn(fields[0].getColumn().id) : null,
				getDatabaseID(), bf.toString());
	}

	@Override
	void toStringNotMounted(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append("unique(");
		fields[0].toString(bf, defaultType);
		for(int i = 1; i<fields.length; i++)
		{
			bf.append(',');
			fields[i].toString(bf, defaultType);
		}
		bf.append(')');
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 */
	public Item search(final Object... values)
	{
		final Condition condition = buildCondition(values);
		return getType().searchSingleton(condition);
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	public Item searchStrict(final Object... values) throws IllegalArgumentException
	{
		final Condition condition = buildCondition(values);
		return getType().searchSingletonStrict(condition);
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
			conditions[j] = Cope.equalAndCast(fieldIter.next(), values[j]);
		return Cope.and(conditions);
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 */
	@Wrap(order=10, name="for{0}", optionTagname="finder",
			varargsFeatures=SearchVarargs.class,
			doc="Finds a {2} by it''s unique fields.",
			docReturn="null if there is no matching item.")
	@Nullable
	public <P extends Item> P search(
			final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.", nullability=FixedNonnull.class) final Object... values)
	{
		return Cast.verboseCast(typeClass, search(values));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=20, name="for{0}Strict", optionTagname="finderStrict",
			varargsFeatures=SearchVarargs.class,
			doc="Finds a {2} by its unique fields.",
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc="if there is no matching item."))
	@Nonnull
	public <P extends Item> P searchStrict(
			final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.", nullability=FixedNonnull.class) final Object... values)
		throws IllegalArgumentException
	{
		return Cast.verboseCast(typeClass, searchStrict(values));
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

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<FunctionField<?>> getUniqueAttributes()
	{
		return getFields();
	}

	/**
	 * @deprecated Use {@link #search(Object[])} instead
	 */
	@Deprecated
	public Item searchUnique(final Object... values)
	{
		return search(values);
	}

	/**
	 * @deprecated Use {@link #search(Class,Object[])} instead
	 */
	@Deprecated
	public <P extends Item> P searchUnique(final Class<P> typeClass, final Object... values)
	{
		return search(typeClass, values);
	}
}
