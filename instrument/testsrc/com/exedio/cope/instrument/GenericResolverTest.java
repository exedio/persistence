/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.GenericResolver.neW;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Arrays;

public class GenericResolverTest extends CopeAssert
{
	public void testIt()
	{
		final Type type1 = new MyType("type1");
		final Type type2 = new MyType("type2");

		assertEquals(
				list(Reader.class, Writer.class),
				neW(Interface.class).get(AllImplementation.class));
		assertEquals(
				list(type1, type2),
				neW(Interface.class).get(NoneImplementation.class, type1, type2));
		assertEquals(
				list(type2, type1),
				neW(Interface.class).get(NoneReverseImplementation.class, type1, type2));
		assertEquals(
				list(Reader.class, type2),
				neW(Interface.class).get(HalfImplementation.class, type2));
		assertEquals(
				list(Reader.class, generic(List.class, Integer.class)),
				neW(Interface.class).get(NestedImplementation.class));
		assertEquals(
				list(Reader.class, generic(List.class, type2)),
				neW(Interface.class).get(NestedParamImplementation.class, type2));
		assertEquals(
				list(Reader.class, Writer.class),
				neW(Interface.class).get(IndirectAllImplementation.class));
		assertEquals(
				list(Writer.class, Reader.class),
				neW(Interface.class).get(IndirectAllReverseImplementation.class));
		assertEquals(
				list(type1, type2),
				neW(Interface.class).get(IndirectNoneImplementation.class, type1, type2));
		assertEquals(
				list(type2, type1),
				neW(Interface.class).get(IndirectNoneReverseImplementation.class, type1, type2));
		assertEquals(
				list(Reader.class, type2),
				neW(Interface.class).get(IndirectHalfImplementation.class, type2));
		assertEquals(
				list(type2, Reader.class),
				neW(Interface.class).get(IndirectHalfReverseImplementation.class, type2));
		assertEquals(
				list(Reader.class, Writer.class),
				neW(Interface.class).get(Indirect2AllImplementation.class));
		assertEquals(
				list(Reader.class, Writer.class),
				neW(Interface.class).get(Indirect2All2Implementation.class));
	}

	interface Interface<K,V>
	{
		// empty
	}

	static class AllImplementation implements Serializable, Interface<Reader, Writer>, Cloneable
	{
		private static final long serialVersionUID = 1l;
	}

	static class NoneImplementation<A, B> implements Interface<A, B>
	{
		// empty
	}

	static class NoneReverseImplementation<B, A> implements Interface<A, B>
	{
		// empty
	}

	static class HalfImplementation<B> implements Interface<Reader, B>
	{
		// empty
	}

	static class NestedImplementation implements Interface<Reader, List<Integer>>
	{
		// empty
	}

	static class NestedParamImplementation<B> implements Interface<Reader, List<B>>
	{
		// empty
	}

	static class IndirectAllImplementation extends NoneImplementation<Reader, Writer>
	{
		// empty
	}

	static class IndirectAllReverseImplementation extends NoneReverseImplementation<Reader, Writer>
	{
		// empty
	}

	static class IndirectNoneImplementation<AA,BB> extends NoneImplementation<AA,BB>
	{
		// empty
	}

	static class IndirectNoneReverseImplementation<AA,BB> extends NoneReverseImplementation<AA,BB>
	{
		// empty
	}

	static class IndirectHalfImplementation<BB> extends NoneImplementation<Reader,BB>
	{
		// empty
	}

	static class IndirectHalfReverseImplementation<BB> extends NoneReverseImplementation<Reader,BB>
	{
		// empty
	}

	static class Indirect2AllImplementation extends IndirectHalfImplementation<Writer>
	{
		// empty
	}

	static class Indirect2All2Implementation extends IndirectAllImplementation
	{
		// empty
	}

	static class MyType implements Type
	{
		String name;

		MyType(final String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	private ParameterizedType generic(final Class rawType, final Type... actualTypeArguments)
	{
		assert rawType!=null;
		assert rawType.getTypeParameters().length==actualTypeArguments.length;

		return new ParameterizedType()
		{
			public Type getRawType()
			{
				return rawType;
			}

			public Type getOwnerType()
			{
				return null;
			}

			public Type[] getActualTypeArguments()
			{
				return Arrays.copyOf(actualTypeArguments);
			}

			@Override
			public boolean equals(final Object other)
			{
				if(!(other instanceof ParameterizedType))
					return false;

				final ParameterizedType o = (ParameterizedType)other;
				return
					rawType.equals(o.getRawType()) &&
					o.getOwnerType()==null &&
					java.util.Arrays.equals(actualTypeArguments, o.getActualTypeArguments());
			}

			@Override
			public int hashCode()
			{
				return
					rawType.hashCode() ^
					java.util.Arrays.hashCode(actualTypeArguments);
			}

			@Override
			public String toString()
			{
				return
					rawType.getName() +
					'<' + java.util.Arrays.toString(actualTypeArguments) + '>';
			}
		};
	}
}
