
package com.exedio.cope.instrument;

import java.util.HashMap;
import java.util.List;

import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.LongAttribute;
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

		final String nativeType = (String)toNativeTypeMapping.get(typeClass);
		if(notNull && nativeType!=null)
		{
			boxedType = nativeType;
			boxed = true;
			boxingPrefix = (String)toBoxingPrefixMapping.get(typeClass);
			boxingPostfix = (String)toBoxingPostfixMapping.get(typeClass);
			unboxingPrefix = (String)toUnboxingPrefixMapping.get(typeClass);
			unboxingPostfix = (String)toUnboxingPostfixMapping.get(typeClass);
		}
		else
		{
			boxedType = getPersistentType();
			boxed = false;
			boxingPrefix = boxingPostfix = unboxingPrefix = unboxingPostfix = null;
		}
	}

	private static final String getPersistentType(final Class typeClass)
	{
		final String result = (String)toPersistentTypeMapping.get(typeClass);

		if(result==null)
			throw new RuntimeException(typeClass.toString());

		return result;
	}

	private static final HashMap toPersistentTypeMapping = new HashMap(3);
	private static final HashMap toNativeTypeMapping = new HashMap(3);
	private static final HashMap toBoxingPrefixMapping = new HashMap(3);
	private static final HashMap toBoxingPostfixMapping = new HashMap(3);
	private static final HashMap toUnboxingPrefixMapping = new HashMap(3);
	private static final HashMap toUnboxingPostfixMapping = new HashMap(3);
	
	private static final void fillNativeTypeMap(final Class typeClass, final String persistentType, final String nativeType,
															  final String boxingPrefix, final String boxingPostfix,
															  final String unboxingPrefix, final String unboxingPostfix)
	{
		toPersistentTypeMapping.put(typeClass, persistentType);
		toNativeTypeMapping.put(typeClass, nativeType);
		toBoxingPrefixMapping.put(typeClass, boxingPrefix);
		toBoxingPostfixMapping.put(typeClass, boxingPostfix);
		toUnboxingPrefixMapping.put(typeClass, unboxingPrefix);
		toUnboxingPostfixMapping.put(typeClass, unboxingPostfix);
	}
	
	static
	{
		fillNativeTypeMap(BooleanAttribute.class, "Boolean", "boolean", "(", "?Boolean.TRUE:Boolean.FALSE)", "(", ").booleanValue()");
		fillNativeTypeMap(LongAttribute.class, "Long", "long", "new Long(", ")", "(", ").longValue()");
		fillNativeTypeMap(IntegerAttribute.class, "Integer", "int", "new Integer(", ")", "(", ").intValue()");
		fillNativeTypeMap(DoubleAttribute.class, "Double", "double", "new Double(", ")", "(", ").doubleValue()");
		fillNativeTypeMap(StringAttribute.class, "String", null, null, null, null, null);
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
		if(!boxed)
			throw new RuntimeException();

		return boxingPrefix;
	}
	
	public final String getBoxingPostfix()
	{
		if(!boxed)
			throw new RuntimeException();

		return boxingPostfix;
	}
	
	public final String getUnBoxingPrefix()
	{
		if(!boxed)
			throw new RuntimeException();

		return unboxingPrefix;
	}
	
	public final String getUnBoxingPostfix()
	{
		if(!boxed)
			throw new RuntimeException();

		return unboxingPostfix;
	}
	
}
