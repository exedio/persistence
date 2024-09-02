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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.WrapperType;
import java.io.Serial;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Makes it easy to create a type with just one StringField for tests.
 * Needed, because databases are limited to certain maximum row sizes.
 */
public final class SchemaTypeStringField extends Pattern
{
	@Serial
	private static final long serialVersionUID = 1l;

	final StringField sourceField;
	private Type<StringItem> sourceTypeIfMounted = null;

	public SchemaTypeStringField(final int maximumLength)
	{
		this.sourceField = new StringField().toFinal().lengthRange(0, maximumLength);
	}

	@Override
	protected void onMount()
	{
		super.onMount();

		final Features features = new Features();
		features.put("element", sourceField, new AnnotationProxy(this));
		sourceTypeIfMounted = newSourceType(StringItem.class, StringItem::new, features);
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class StringItem extends Item
	{
		String get()
		{
			return getPattern().get(this);
		}

		private SchemaTypeStringField getPattern()
		{
			return (SchemaTypeStringField)getCopeType().getPattern();
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private StringItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	Type<StringItem> sourceType()
	{
		return requireMounted(sourceTypeIfMounted);
	}

	String get(final StringItem item)
	{
		return sourceField.get(item);
	}

	StringItem add(final String element)
	{
		return sourceType().newItem(SetValue.map(sourceField, element));
	}

	List<StringItem> searchEqual(final String value)
	{
		return search(sourceField.equal(value));
	}

	List<StringItem> searchNotEqual(final String value)
	{
		return search(sourceField.notEqual(value));
	}

	List<StringItem> searchIn(final String... value)
	{
		return search(sourceField.in(value));
	}

	private List<StringItem> search(final Condition condition)
	{
		final Query<StringItem> q = sourceTypeIfMounted.newQuery(condition);
		q.setOrderBy(sourceTypeIfMounted.getThis(), true);
		return q.search();
	}

	String getSchemaType()
	{
		return
				getType().getModel().getSchema().
				getTable(getTableName(sourceType())).
				getColumn(getColumnName(sourceField)).getType();
	}

	static List<SchemaTypeStringField> get(final Type<?> type)
	{
		final ArrayList<SchemaTypeStringField> result = new ArrayList<>();
		for(final Feature feature : type.getFeatures())
			if(feature instanceof SchemaTypeStringField)
				result.add((SchemaTypeStringField)feature);
		return result;
	}

	static final class AnnotationProxy implements AnnotatedElement
	{
		private final Pattern source;

		AnnotationProxy(final Pattern source)
		{
			this.source = requireNonNull(source);
		}

		@Override
		@SuppressWarnings("SimplifiableConditionalExpression")
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			return
				(MysqlExtendedVarchar.class==annotationClass)
				? source.isAnnotationPresent(annotationClass)
				: false;
		}

		@Override
		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			return
				(MysqlExtendedVarchar.class==annotationClass)
				? source.getAnnotation(annotationClass)
				: null;
		}

		@Override
		public Annotation[] getAnnotations()
		{
			throw new RuntimeException(source.toString());
		}

		@Override
		public Annotation[] getDeclaredAnnotations()
		{
			throw new RuntimeException(source.toString());
		}

		@Override
		public String toString()
		{
			return source + "-annotations";
		}
	}
}
