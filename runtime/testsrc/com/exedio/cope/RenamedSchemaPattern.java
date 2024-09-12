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

package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.Arrays;
import com.exedio.cope.misc.CopeSchemaNameElement;
import java.io.Serial;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;

class RenamedSchemaPattern extends Pattern
{
	@Serial
	private static final long serialVersionUID = 1l;

	final IntegerField veilSF;
	final IntegerField emptSF;
	final IntegerField bareSF;

	final StringField srcTField = new StringField();
	private Type<?> srcT = null;

	final StringField srcTtailField = new StringField();
	private Type<?> srcTtail = null;

	RenamedSchemaPattern()
	{
		this(true);
	}

	RenamedSchemaPattern(
			// prevents java.lang.IllegalArgumentException: longString must not be empty
			// at com.exedio.cope.Trimmer.trimString
			final boolean addSourceFeatureWithEmptySchemaName)
	{
		this.veilSF = addSourceFeature(
				new IntegerField(), "veilSF", CustomAnnotatedElement.create(CopeSchemaNameElement.get("coatSF")));
		this.emptSF = addSourceFeatureWithEmptySchemaName ? addSourceFeature(
				new IntegerField(), "emptSF", CustomAnnotatedElement.create(CopeSchemaNameElement.getEmpty())) : null;
		this.bareSF = addSourceFeature(
				new IntegerField(), "bareSF", new AnnotationSource("bareSF"));
	}

	private record AnnotationSource(String name) implements AnnotatedElement
	{
		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			//noinspection RedundantIfStatement
			if(TestAnnotation.class==annotationClass)
				return true;

			return false;
		}

		@Override
		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			if(TestAnnotation.class==annotationClass)
			{
				return annotationClass.cast(new TestAnnotation()
				{
					@Override
					public Class<? extends Annotation> annotationType()
					{
						return TestAnnotation.class;
					}

					@Override
					public String value()
					{
						return name + "-TestAnnotation";
					}
				});
			}

			return null;
		}

		@Override
		public Annotation[] getAnnotations()
		{
			throw new RuntimeException();
		}

		@Override
		public Annotation[] getDeclaredAnnotations()
		{
			throw new RuntimeException();
		}
	}

	@Override
	protected void onMount()
	{
		super.onMount();

		final Features features = new Features();
		features.put("field", srcTField);
		this.srcT = newSourceType(SourceType.class, SourceType::new, features);

		features.clear();
		features.put("field", srcTtailField);
		this.srcTtail = newSourceType(SourceType.class, SourceType::new, features, "tail");
	}

	Type<?> srcT()
	{
		if(srcT == null)
			throw new IllegalStateException();

		return srcT;
	}

	Type<?> srcTtail()
	{
		if(srcTtail == null)
			throw new IllegalStateException();

		return srcTtail;
	}

	@TestAnnotation("srcT-TestAnnotation")
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class SourceType extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private SourceType(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	static final class CustomAnnotatedElement
	{
		static AnnotatedElement create(final Annotation... annotations)
		{
			if(annotations==null)
				throw new NullPointerException("annotations");
			if(annotations.length==0)
				throw new IllegalArgumentException("annotations must not be empty");
			final HashMap<Class<?>, Annotation> annotationMap = new HashMap<>();
			for(int i = 0; i<annotations.length; i++)
			{
				final Annotation a = annotations[i];
				if(a==null)
					throw new NullPointerException("annotations" + '[' + i + ']');
				if(annotationMap.putIfAbsent(a.annotationType(), a)!=null)
					throw new IllegalArgumentException("duplicate " + a.annotationType());
			}

			return new AnnotationSource(Arrays.copyOf(annotations), annotationMap);
		}

		private record AnnotationSource(
				Annotation[] annotations,
				HashMap<Class<?>, Annotation> annotationMap)
				implements AnnotatedElement
		{
			@Override
			public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
			{
				return annotationMap.containsKey(annotationClass);
			}

			@Override
			public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
			{
				return annotationClass.cast(annotationMap.get(annotationClass));
			}

			@Override
			public Annotation[] getAnnotations()
			{
				return Arrays.copyOf(annotations);
			}

			@Override
			public Annotation[] getDeclaredAnnotations()
			{
				return Arrays.copyOf(annotations);
			}

			@Override
			public String toString()
			{
				return java.util.Arrays.toString(annotations);
			}
		}

		private CustomAnnotatedElement()
		{
			// prevent instantiation
		}
	}
}
