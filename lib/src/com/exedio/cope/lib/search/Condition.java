
package com.exedio.cope.lib.search;

import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.Function;
import com.exedio.cope.lib.Join;
import com.exedio.cope.lib.Query;
import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.Type;

public abstract class Condition
{
	public abstract void appendStatement(Statement statment);
	
	public abstract void check(Query query);

	public final void check(final Function function, final Query query)
	{
		final Type functionType = function.getType();

		final Type queryType = query.getType();
		if(functionType==queryType)
			return;

		final List queryJoins = query.getJoins();
		if(queryJoins!=null)
		{
			for(Iterator i = queryJoins.iterator(); i.hasNext(); )
			{
				final Join join = (Join)i.next();
				if(functionType==join.getType())
					return;
			}
		}

		throw new RuntimeException(
			"function "
				+ function
				+ " belongs to type "
				+ functionType
				+ ", which is not a type of the query: "
				+ queryType
				+ ", "
				+ queryJoins);
	}

	public final void check(final Type type, final Query query)
	{
		final Type queryType = query.getType();
		if(type==queryType)
			return;

		final List queryJoins = query.getJoins();
		if(queryJoins!=null)
		{
			for(Iterator i = queryJoins.iterator(); i.hasNext(); )
			{
				final Join join = (Join)i.next();
				if(type==join.getType())
					return;
			}
		}

		throw new RuntimeException(
				type.toString()
				+ " is not a type of the query: "
				+ queryType
				+ ", "
				+ queryJoins);
	}

}
