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
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

class RenamedSchemaPattern extends Pattern
{
	private static final long serialVersionUID = 1l;

	final IntegerField sourceFeature;

	final StringField sourceTypeField = new StringField();
	private Type<?> sourceType = null;

	final StringField sourceTypePostfixField = new StringField();
	private Type<?> sourceTypePostfix = null;

	RenamedSchemaPattern()
	{
		this.sourceFeature = addSourceFeature(
				new IntegerField(), "sourceFeature", new AnnotationSource("sourceFeature"));
	}

	private static final class AnnotationSource implements AnnotatedElement
	{
		final String name;

		AnnotationSource(final String name)
		{
			this.name = name;
		}

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
		features.put("field", sourceTypeField);
		this.sourceType = newSourceType(SourceType.class, features);

		features.clear();
		features.put("field", sourceTypePostfixField);
		this.sourceTypePostfix = newSourceType(SourceType.class, features, "tail");
	}

	Type<?> getSourceType()
	{
		if(sourceType==null)
			throw new IllegalStateException();

		return sourceType;
	}

	Type<?> getSourceTypePostfix()
	{
		if(sourceTypePostfix==null)
			throw new IllegalStateException();

		return sourceTypePostfix;
	}

	@TestAnnotation("sourceType-TestAnnotation")
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class SourceType extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private SourceType(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
