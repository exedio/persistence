package com.exedio.copernica;

import com.exedio.cope.lib.Type;

final class TypeCop extends Cop
{
	final Type type;

	TypeCop(final Type type)
	{
		super("copernica.jsp?type="+type.getJavaClass().getName());
		this.type = type;
	}
	
}
