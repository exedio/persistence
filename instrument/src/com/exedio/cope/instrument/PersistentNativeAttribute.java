
package com.exedio.cope.instrument;

import java.util.HashMap;
import java.util.List;

import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.StringAttribute;

public class PersistentNativeAttribute extends PersistentAttribute
{
	private final String boxedType;
	private final boolean boxed;
	private final String boxingPrefix, boxingPostfix, unboxingPrefix, unboxingPostfix;

	public PersistentNativeAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final List initializerArguments, final boolean mapped,
			final List qualifiers)
	{
		super(javaAttribute, getPersistentType(typeClass), initializerArguments, mapped, qualifiers);

		final String persistentType = getPersistentType();
		final String nativeType = (String)toNativeTypeMapping.get(persistentType);
		if(notNull && nativeType!=null)
		{
			boxedType = nativeType;
			boxed = true;
			boxingPrefix = (String)toBoxingPrefixMapping.get(persistentType);
			boxingPostfix = (String)toBoxingPostfixMapping.get(persistentType);
			unboxingPrefix = (String)toUnboxingPrefixMapping.get(persistentType);
			unboxingPostfix = (String)toUnboxingPostfixMapping.get(persistentType);
		}
		else
		{
			boxedType = persistentType;
			boxed = false;
			boxingPrefix = boxingPostfix = unboxingPrefix = unboxingPostfix = null;
		}
	}

	private static final String getPersistentType(final Class typeClass)
	{
		if(IntegerAttribute.class.equals(typeClass))
			return "Integer";
		else if(DoubleAttribute.class.equals(typeClass))
			return "Double";
		else if(BooleanAttribute.class.equals(typeClass))
			return "Boolean";
		else if(StringAttribute.class.equals(typeClass))
			return "String";
		else
			throw new RuntimeException(typeClass.toString());
	}

	private static final HashMap toNativeTypeMapping = new HashMap(3);
	private static final HashMap toBoxingPrefixMapping = new HashMap(3);
	private static final HashMap toBoxingPostfixMapping = new HashMap(3);
	private static final HashMap toUnboxingPrefixMapping = new HashMap(3);
	private static final HashMap toUnboxingPostfixMapping = new HashMap(3);
	
	private static final void fillNativeTypeMap(final String persistentType, final String nativeType,
															  final String boxingPrefix, final String boxingPostfix,
															  final String unboxingPrefix, final String unboxingPostfix)
	{
		toNativeTypeMapping.put(persistentType, nativeType);
		toBoxingPrefixMapping.put(persistentType, boxingPrefix);
		toBoxingPostfixMapping.put(persistentType, boxingPostfix);
		toUnboxingPrefixMapping.put(persistentType, unboxingPrefix);
		toUnboxingPostfixMapping.put(persistentType, unboxingPostfix);
	}
	
	static
	{
		fillNativeTypeMap("Boolean", "boolean", "(",            "?Boolean.TRUE:Boolean.FALSE)","(", ").booleanValue()");
		fillNativeTypeMap("Integer", "int",     "new Integer(", ")",                           "(", ").intValue()");
		fillNativeTypeMap("Double", "double", "new Double(", ")",                       "(", ").doubleValue()");
	}

	public final String getBoxedType()
	{
		return boxedType;
	}
	
	public final boolean isBoxed()
	{
		return boxed;
	}
	
	public final String getBoxingPrefix()
	{
		return boxingPrefix;
	}
	
	public final String getBoxingPostfix()
	{
		return boxingPostfix;
	}
	
	public final String getUnBoxingPrefix()
	{
		return unboxingPrefix;
	}
	
	public final String getUnBoxingPostfix()
	{
		return unboxingPostfix;
	}
	
}
