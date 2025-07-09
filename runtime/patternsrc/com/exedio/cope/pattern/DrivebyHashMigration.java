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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import java.io.Serial;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class DrivebyHashMigration extends Pattern implements HashInterface
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Hash legacyHash;
	private final Hash targetHash;

	/**
	 * @param legacy the algorithm the passwords are currently hashed with
	 * @param target the algorithm the passwords are to be hashed with in the future
	 */
	public DrivebyHashMigration(final HashAlgorithm legacy, final HashAlgorithm target)
	{
		legacyHash = addSourceFeature(hash(legacy), "legacy", ComputedElement.get());
		targetHash = addSourceFeature(hash(target), "target");
		addSourceFeature(new CheckConstraint(
			Cope.or(
				legacyHash.isNull().and(targetHash.isNotNull()),
				legacyHash.isNotNull().and(targetHash.isNull())
			)),
			"xor");
	}

	private static Hash hash(final HashAlgorithm algorithm)
	{
		return new Hash(algorithm).optional();
	}

	public Hash getLegacyHash()
	{
		return legacyHash;
	}

	public Hash getTargetHash()
	{
		return targetHash;
	}

	@Wrap(order=10, doc=Wrap.HASH_CHECK_DOC)
	@Override
	public boolean check(@Nonnull final Item item, @Nullable final String plainText)
	{
		if(!legacyHash.isNull(item))
		{
			final boolean result = legacyHash.check(item, plainText);
			if(result && plainText!=null)
				set(item, plainText);
			return result;
		}
		else
		{
			return targetHash.check(item, plainText);
		}
	}

	@Override
	public boolean isNull(final Item item)
	{
		// needs actual implementation if there is optional()
		return false;
	}

	@Override
	public String getHash(final Item item)
	{
		final String targetHash = this.targetHash.getHash(item);
		if(targetHash!=null)
			return targetHash;

		return legacyHash.getHash(item);
	}

	@Wrap(order=20, doc={Wrap.HASH_BLIND_DOC_1, Wrap.HASH_BLIND_DOC_2})
	@Override
	public void blind(@Nullable final String plainText)
	{
		targetHash.blind(plainText);
	}

	@Wrap(order=30,
			doc=Wrap.SET_DOC,
			thrownGetter=InitialExceptionsSettableGetter.class)
	@Override
	public void set(@Nonnull final Item item, @Nonnull final String plainText)
	{
		MandatoryViolationException.requireNonNull(plainText, this, item);

		item.set(
				SetValue.map(legacyHash, null),
				SetValue.map(targetHash, plainText));
	}

	@Override
	public SetValue<?>[] execute(final String value, final Item exceptionItem)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, exceptionItem);

		return new SetValue<?>[]{
				assertSingleton(legacyHash.execute(null,  exceptionItem)),
				assertSingleton(targetHash.execute(value, exceptionItem))};
	}

	private static <T> T assertSingleton(final T[] array)
	{
		assert array.length == 1;
		return array[0];
	}

	@Override
	public boolean isFinal()
	{
		return targetHash.isFinal(); // TODO allow final as well
	}

	@Override
	public boolean isMandatory()
	{
		return true; // TODO allow optional as well
	}

	@Override
	public java.lang.reflect.Type getInitialType()
	{
		return String.class;
	}

	@Override
	public boolean isInitial()
	{
		return isMandatory() || isFinal();
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = targetHash.getInitialExceptions();
		if(isMandatory())
			result.add(MandatoryViolationException.class);
		return result;
	}
}
