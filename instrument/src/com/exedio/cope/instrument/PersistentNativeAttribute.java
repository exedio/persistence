
package com.exedio.cope.instrument;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.DateAttribute;
import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.IntegerFunction;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.StringFunction;

public class PersistentNativeAttribute extends PersistentAttribute
{
	private final String boxedType;
	private final boolean boxed;
	private final String boxingPrefix, boxingPostfix, unboxingPrefix, unboxingPostfix;

	public PersistentNativeAttribute(
			final JavaAttribute javaAttribute,
			Class typeClass,
			final List initializerArguments,
			final String setterOptionString,
			final List qualifiers)
		throws InjectorParseException
	{
		super(javaAttribute, typeClass, getPersistentType(typeClass), initializerArguments, setterOptionString, qualifiers);

		typeClass = normalizeTypeClass(typeClass);
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
			boxedType = persistentType;
			boxed = false;
			boxingPrefix = boxingPostfix = unboxingPrefix = unboxingPostfix = null;
		}
	}
	
	private static final Class normalizeTypeClass(final Class typeClass)
	{
		if(StringFunction.class.isAssignableFrom(typeClass))
			return StringFunction.class;
		else if(IntegerFunction.class.isAssignableFrom(typeClass))
			return IntegerFunction.class;
		else
			return typeClass;
	}

	private static final String getPersistentType(final Class typeClass)
	{
		final String result = (String)toPersistentTypeMapping.get(normalizeTypeClass(typeClass));

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
		fillNativeTypeMap(IntegerFunction.class, "Integer", "int", "new Integer(", ")", "(", ").intValue()");
		fillNativeTypeMap(DoubleAttribute.class, "Double", "double", "new Double(", ")", "(", ").doubleValue()");
		fillNativeTypeMap(StringFunction.class, "String", null, null, null, null, null);
		fillNativeTypeMap(DateAttribute.class, Date.class.getName(), null, null, null, null, null);
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
