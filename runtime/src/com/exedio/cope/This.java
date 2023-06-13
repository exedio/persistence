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

import java.lang.reflect.AnnotatedElement;
import java.util.function.Consumer;

public final class This<E extends Item> extends Feature
	implements ItemFunction<E>
{
	private static final long serialVersionUID = 1l;

	static final String NAME = "this";

	final Type<E> type;

	This(final Type<E> type)
	{
		assert type!=null;
		this.type = type;
	}

	@Override
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);
		assert this.type == type;
		assert NAME.equals(name);
	}

	@Override
	public void requireSupportForGet()
	{
		// always supported
	}

	@Override
	public E get(final Item item)
	{
		return type.cast(item);
	}

	@Override
	public Class<E> getValueClass()
	{
		return type.getJavaClass();
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void check(final TC tc, final Join join)
	{
		tc.check(this, join);
	}

	@Override
	public void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.appendPK(type, join);

		if(bf.typeColumnsRequired)
			appendTypeColumn(bf, join);
	}

	private void appendTypeColumn(final Statement bf, final Join join)
	{
		final IntegerColumn column = type.getTable().primaryKey;
		assert column.kind.primaryKey();

		final StringColumn typeColumn = column.table.typeColumn;
		if(typeColumn!=null)
		{
			bf.append(',').
				append(typeColumn, join);
		}
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendType(final Statement bf, final Join join)
	{
		bf.append(Statement.assertTypeColumn(type.getTable().typeColumn, type), join);
	}

	@Deprecated // OK: for internal use within COPE only
	public void appendParameter(final Statement bf, final E value)
	{
		bf.appendParameter(value.pk);
	}

	@Override
	public Type<E> getValueType()
	{
		return type;
	}

	@Override
	public boolean needsCheckTypeColumn()
	{
		return type.needsCheckTypeColumn();
	}

	@Override
	@Deprecated
	public int checkTypeColumn()
	{
		return toIntCapped(checkTypeColumnL());
	}

	@Override
	public long checkTypeColumnL()
	{
		ItemFunctionUtil.checkTypeColumnNeeded(this);
		return type.checkTypeColumn();
	}

	@Override
	public Statement checkTypeColumnStatement(final Statement.Mode mode)
	{
		ItemFunctionUtil.checkTypeColumnNeeded(this);
		return type.checkTypeColumnStatement(mode);
	}

	// Note about isNull/isNotNull: a primary key can become null in queries using outer joins.
}
