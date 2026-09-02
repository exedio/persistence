package com.exedio.cope.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

@NullMarked
class TypeWriterTest
{
	private static final TypeWriter FQN = new TypeWriter(null, true);

	@SuppressWarnings("unused") // OK: via reflection
	private @Nullable List<String> parameterized;
	@SuppressWarnings("unused") // OK: via reflection
	private @Nullable List<? extends Number> upperBound;
	@SuppressWarnings("unused") // OK: via reflection
	private @Nullable List<? super Integer> lowerBound;
	@SuppressWarnings("unused") // OK: via reflection
	private List<String> @Nullable [] genericArray;

	@Test
	void write() throws NoSuchFieldException
	{
		assertEquals("java.lang.String", FQN.write(String.class, false));
		assertEquals("java.lang.String[]", FQN.write(String[].class, false));
		assertEquals("java.lang.String...", FQN.write(String[].class, true));
		assertEquals("java.util.List<java.lang.String>", FQN.write(typeOf("parameterized"), false));
		assertEquals("java.util.List<java.lang.String>[]", FQN.write(typeOf("genericArray"), false));
		assertEquals("java.util.List<java.lang.String>...", FQN.write(typeOf("genericArray"), true));
		assertEquals("java.util.List<? extends java.lang.Number>", FQN.write(typeOf("upperBound"), false));
		assertEquals("java.util.List<? super java.lang.Integer>", FQN.write(typeOf("lowerBound"), false));
	}

	@Test
	void nameWithWildcards()
	{
		assertEquals("T<?>", TypeWriter.nameWithWildcards("T", 1));
		assertEquals("T<?,?>", TypeWriter.nameWithWildcards("T", 2));
	}

	private static Type typeOf(final String fieldName) throws NoSuchFieldException
	{
		return TypeWriterTest.class.getDeclaredField(fieldName).getGenericType();
	}
}
