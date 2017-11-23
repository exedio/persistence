/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.misc.Arrays;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GenericResolverTest
{
	@Test public void testIt()
	{
		@SuppressWarnings("rawtypes")
		final GenericResolver<Interface> gr = neW(Interface.class);
		final Type type1 = new MyType("type1");
		final Type type2 = new MyType("type2");

		assertEquals(
				asList(Reader.class, Writer.class),
				asList(gr.get(AllImplementation.class)));
		assertEquals(
				asList(Reader.class, Writer.class),
				asList(gr.get(AllImplementationImp.class)));
		assertEquals(
				asList(Reader.class, Writer.class),
				asList(gr.get(AllImplementationImpExt.class)));
		assertEquals(
				asList(Reader.class, Writer.class),
				asList(gr.get(AllImplementationExtImp.class)));
		assertEquals(
				asList(type1, type2),
				asList(gr.get(NoneImplementation.class, type1, type2)));
		assertEquals(
				asList(type2, type1),
				asList(gr.get(NoneReverseImplementation.class, type1, type2)));
		assertEquals(
				asList(Reader.class, type2),
				asList(gr.get(HalfImplementation.class, type2)));
		assertEquals(
				asList(Reader.class, generic(List.class, Integer.class)),
				asList(gr.get(NestedImplementation.class)));
		assertEquals(
				asList(Reader.class, generic(List.class, type2)),
				asList(gr.get(NestedParamImplementation.class, type2)));
		assertEquals(
				asList(Reader.class, Writer.class),
				asList(gr.get(IndirectAllImplementation.class)));
		assertEquals(
				asList(Writer.class, Reader.class),
				asList(gr.get(IndirectAllReverseImplementation.class)));
		assertEquals(
				asList(type1, type2),
				asList(gr.get(IndirectNoneImplementation.class, type1, type2)));
		assertEquals(
				asList(type2, type1),
				asList(gr.get(IndirectNoneReverseImplementation.class, type1, type2)));
		assertEquals(
				asList(Reader.class, type2),
				asList(gr.get(IndirectHalfImplementation.class, type2)));
		assertEquals(
				asList(type2, Reader.class),
				asList(gr.get(IndirectHalfReverseImplementation.class, type2)));
		assertEquals(
				asList(Reader.class, Writer.class),
				asList(gr.get(Indirect2AllImplementation.class)));
		assertEquals(
				asList(Reader.class, Writer.class),
				asList(gr.get(Indirect2All2Implementation.class)));
	}

	/**
	 * @param <K> just for tests
	 * @param <V> just for tests
	 */
	@SuppressWarnings("MarkerInterface")
	interface Interface<K,V>
	{
		// empty
	}

	static class AllImplementation implements Serializable, Interface<Reader, Writer>, Cloneable
	{
		private static final long serialVersionUID = 1l;
	}

	interface AllInterface extends Serializable, Interface<Reader, Writer>, Cloneable
	{
		// empty
	}

	static class AllImplementationImp implements AllInterface
	{
		private static final long serialVersionUID = 1l;
	}

	static class AllImplementationImpExt extends AllImplementationImp
	{
		private static final long serialVersionUID = 1l;
	}

	@SuppressWarnings("MarkerInterface")
	interface AllInterfaceExt extends AllInterface
	{
		// empty
	}

	static class AllImplementationExtImp implements AllInterfaceExt
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
		final String name;

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

	private static ParameterizedType generic(final Class<?> rawType, final Type... actualTypeArguments)
	{
		assert rawType!=null;
		assert rawType.getTypeParameters().length==actualTypeArguments.length;

		return new ParameterizedType()
		{
			@Override
			public Type getRawType()
			{
				return rawType;
			}

			@Override
			public Type getOwnerType()
			{
				return null;
			}

			@Override
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
