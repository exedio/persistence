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

package com.exedio.cope.pattern;

import static com.exedio.cope.misc.Conditions.unisonNull;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.MysqlExtendedVarchar;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.CopeSchemaNameElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import com.exedio.cope.util.CharSet;
import java.io.Serial;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class CaseInsensitiveUniqueField extends Pattern implements Settable<String>
{
	public static CaseInsensitiveUniqueField create(final StringField pristine)
	{
		return new CaseInsensitiveUniqueField(pristine);
	}

	private final StringField pristine, lowerCase;
	private final CheckConstraint unisonNull, unisonCase;

	private CaseInsensitiveUniqueField(final StringField pristine)
	{
		if(pristine.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("pristine must not be unique");

		this.pristine = addSourceFeature(pristine, "pristine", CustomAnnotatedElement.create(CopeSchemaNameElement.getEmpty()));
		final CharSet charSet = pristine.getCharSet();
		//noinspection AnonymousInnerClassMayBeStatic
		this.lowerCase = addSourceFeature(pristine.unique().charSet(
				charSet==null
				? new CharSet('\u0000', (char)('A'-1), (char)('Z'+1), '\uFFFF')
				: charSet.remove('A', 'Z')),
				"lowerCase",
				CustomAnnotatedElement.create(new MysqlExtendedVarchar() { @Override public Class<? extends Annotation> annotationType() { return MysqlExtendedVarchar.class; } }));
		this.unisonNull =
				pristine.isMandatory()
				? null
				: addSourceFeature(new CheckConstraint(unisonNull(List.of(this.pristine, this.lowerCase))), "unisonNull");
		this.unisonCase = addSourceFeature(new CheckConstraint(this.pristine.toLowerCase().is(this.lowerCase)), "unisonCase");
	}

	public StringField getPristine()
	{
		return pristine;
	}

	public StringField getLowerCase()
	{
		return lowerCase;
	}

	public CheckConstraint getUnisonNull()
	{
		return unisonNull;
	}

	public CheckConstraint getUnisonCase()
	{
		return unisonCase;
	}

	@Override
	public SetValue<?>[] execute(final String value, final Item exceptionItem)
	{
		if(pristine.isMandatory())
			MandatoryViolationException.requireNonNull(value, this, exceptionItem);

		return new SetValue<?>[] {
				SetValue.map(pristine, value),
				SetValue.map(lowerCase, toLowerCase(value))
		};
	}

	@Override
	public boolean isFinal()
	{
		return pristine.isFinal();
	}

	@Override
	public boolean isMandatory()
	{
		return pristine.isMandatory();
	}

	@Override
	public Class<?> getInitialType()
	{
		return String.class;
	}

	@Override
	public boolean isInitial()
	{
		return pristine.isInitial();
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> exceptions = pristine.getInitialExceptions();
		exceptions.addAll(lowerCase.getInitialExceptions());
		return exceptions;
	}

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	public String get(@Nonnull final Item item)
	{
		return pristine.get(item);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final String value)
	{
		item.set(SetValue.map(this, value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 * @throws NullPointerException if value is null.
	 */
	@Wrap(order=30, name=Wrap.FOR_NAME,
			doc="Finds a {2} by its {0}, ignoring case.",
			docReturn=Wrap.FOR_RETURN)
	@Nullable
	public <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}, ignoring case.") @Nonnull final String value)
	{
		return lowerCase.searchUnique(typeClass, toLowerCase(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws NullPointerException if value is null.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=40, name=Wrap.FOR_STRICT_NAME,
			doc="Finds a {2} by its {0}, ignoring case.",
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc=Wrap.FOR_STRICT_THROWN))
	@Nonnull
	public <P extends Item> P searchUniqueStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}, ignoring case.") @Nonnull final String value)
			throws IllegalArgumentException
	{
		return lowerCase.searchUniqueStrict(typeClass, toLowerCase(value));
	}

	public Condition isCaseSensitive(final String value) { return pristine .is(            value ); }
	public Condition isIgnoreCase   (final String value) { return lowerCase.is(toLowerCase(value)); }

	public Condition startsWithCaseSensitive(final String value) { return pristine .startsWith(            value ); }
	public Condition startsWithIgnoreCase   (final String value) { return lowerCase.startsWith(toLowerCase(value)); }

	public Condition endsWithCaseSensitive(final String value) { return pristine .endsWith(            value ); }
	public Condition endsWithIgnoreCase   (final String value) { return lowerCase.endsWith(toLowerCase(value)); }

	public Condition containsCaseSensitive(final String value) { return pristine .contains(            value ); }
	public Condition containsIgnoreCase   (final String value) { return lowerCase.contains(toLowerCase(value)); }


	private static String toLowerCase(final String value)
	{
		return value!=null ? value.toLowerCase(Locale.ROOT) : null;
	}

	@Serial
	private static final long serialVersionUID = 1L;
}
