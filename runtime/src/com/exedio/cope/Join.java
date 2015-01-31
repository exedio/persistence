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

public final class Join implements Serializable
{
	private static final long serialVersionUID = 1l;

	public static enum Kind
	{
		INNER("join "),
		OUTER_LEFT("left join "),
		OUTER_RIGHT("right join ");

		final String sql;

		Kind(final String sql)
		{
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
		if(!(other instanceof Join))
			return false;

		final Join o = (Join)other;
		return
			index==o.index &&
			kind==o.kind &&
			type==o.type &&
			condition==null ? o.condition==null : condition.equals(o.condition);
	}

	@Override
	public int hashCode()
	{
		return
			index ^
			kind.hashCode() ^
			type.hashCode() ^
			(condition==null ? 0 : condition.hashCode());
	}

	@Override
	public String toString()
	{
		final StringBuilder bf = new StringBuilder();
		toString(bf, false, null);
		return bf.toString();
	}

	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		bf.append(' ').
			append(kind.sql).
			append(type).
			append(' ').
			append(getToStringAlias());

		if(condition!=null)
		{
			bf.append(" on ");
			condition.toString(bf, key, defaultType);
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
			if(this.kind!=Join.Kind.INNER)
				throw new RuntimeException("outer join must have join condition");

			bf.append(" cross join ");
		}
		else
		{
			bf.append(' ').
				append(this.kind.sql);
		}

		bf.appendTypeDefinition(this, this.type, true);

		if(condition!=null)
		{
			bf.append(" on ");
			condition.append(bf);
		}
	}
}
