package com.exedio.copernica;

import com.exedio.cope.lib.Type;

final class TypeCop
{
	final String url;
	
	TypeCop(final Type type)
	{
		this.url = "copernica.jsp?type="+type.getJavaClass().getName();
	}
	
}
