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

public final class Join implements Serializable
{
	@Serial
	private static final long serialVersionUID = 1l;

	public enum Kind
	{
		INNER("join ", "JOIN "),
		OUTER_LEFT("left join ", "LEFT JOIN "),
		OUTER_RIGHT("right join ", "RIGHT JOIN ");

		final String toString;
		final String sql;

		Kind(final String toString, final String sql)
		{
			this.toString = toString;
			this.sql = sql;
		}
	}

	final int index;
	private final Kind kind;
	final Type<?> type;
	private Condition condition;

	Join(final int index, final Kind kind, final Type<?> type, final Condition condition)
	{
		this.index = index;
		this.kind = requireNonNull(kind, "kind");
		this.type = requireNonNull(type, "type");
		this.condition = condition;
	}

	public Kind getKind()
	{
		return kind;
	}

	public Type<?> getType()
	{
		return type;
	}

	public void setCondition(final Condition condition)
	{
		this.condition = condition;
	}

	public Condition getCondition()
	{
		return condition;
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof final Join o))
			return false;

		//noinspection NonFinalFieldReferenceInEquals
		return
			index==o.index &&
			kind==o.kind &&
			type==o.type &&
			Objects.equals(condition, o.condition);
	}

	@Override
	public int hashCode()
	{
		//noinspection NonFinalFieldReferencedInHashCode
		return
			index ^
			kind.hashCode() ^
			type.hashCode() ^
			Objects.hashCode(condition);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		toString(sb, false, null);
		return sb.toString();
	}

	void toString(final StringBuilder sb, final boolean key, final Type<?> defaultType)
	{
		sb.append(' ').
			append(kind.toString).
			append(type).
			append(' ').
			append(getToStringAlias());

		if(condition!=null)
		{
			sb.append(" on ");
			condition.toString(sb, key, defaultType);
		}
	}

	String getToStringAlias()
	{
		return String.valueOf(Character.toLowerCase(type.id.charAt(0))) + (index+1);
	}

	void check(final TC tc)
	{
		final Condition condition = this.condition;
		if(condition!=null)
			condition.check(tc);
	}

	void search(final Statement bf)
	{
		final Condition condition = this.condition;

		if(condition==null)
		{
			if(kind!=Kind.INNER)
				throw new RuntimeException("outer join must have join condition");

			bf.append(" CROSS JOIN ");
		}
		else
		{
			bf.append(' ').
				append(kind.sql);
		}

		bf.appendTypeDefinition(this, type, true);

		if(condition!=null)
		{
			bf.append(" ON ");
			condition.append(bf);
		}
	}
}
