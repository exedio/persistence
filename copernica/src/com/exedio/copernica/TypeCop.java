package com.exedio.copernica;

import com.exedio.cope.lib.Type;

final class TypeCop extends Cop
{
	TypeCop(final Type type)
	{
		super("copernica.jsp?type="+type.getJavaClass().getName());
	}
	
}
