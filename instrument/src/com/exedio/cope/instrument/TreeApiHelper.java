package com.exedio.cope.instrument;

import com.sun.source.tree.ModifiersTree;
import javax.lang.model.element.Modifier;

class TreeApiHelper
{
	static int toModifiersInt(final ModifiersTree modifiers)
	{
		int result=0;
		for (final Modifier flag: modifiers.getFlags())
		{
			result |= toModifiersInt(flag);
		}
		return result;
	}

	static private int toModifiersInt(final Modifier flag)
	{
		switch (flag)
		{
			case ABSTRACT: return java.lang.reflect.Modifier.ABSTRACT;
			case DEFAULT: throw new RuntimeException("unexpected DEFAULT modifier");
			case FINAL: return java.lang.reflect.Modifier.FINAL;
			case NATIVE: return java.lang.reflect.Modifier.NATIVE;
			case PRIVATE: return java.lang.reflect.Modifier.PRIVATE;
			case PROTECTED: return java.lang.reflect.Modifier.PROTECTED;
			case PUBLIC: return java.lang.reflect.Modifier.PUBLIC;
			case STATIC: return java.lang.reflect.Modifier.STATIC;
			case STRICTFP: return java.lang.reflect.Modifier.STRICT;
			case SYNCHRONIZED: return java.lang.reflect.Modifier.SYNCHRONIZED;
			case TRANSIENT: return java.lang.reflect.Modifier.TRANSIENT;
			case VOLATILE: return java.lang.reflect.Modifier.VOLATILE;
			default: throw new RuntimeException(flag.toString());
		}
	}
}
