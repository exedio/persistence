/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.misc.TypeIterator.iterateTransactionally;
import static com.exedio.cope.pattern.NestedHashAlgorithm.create;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.Iterables;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.util.JobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NestedHashMigration extends Pattern implements HashInterface
{
	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	private static final long serialVersionUID = 1l;

	private final Hash legacyHash;
	private final Hash targetHash;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final HashAlgorithm targetAlgorithm;

	/**
	 * @param legacy the algorithm the passwords are currently hashed with
	 * @param target the algorithm the passwords are to be hashed with in the future
	 */
	public NestedHashMigration(final HashAlgorithm legacy, final HashAlgorithm target)
	{
		this.targetAlgorithm = target;
		addSource(legacyHash = hash(       legacy         ), "legacy", ComputedElement.get());
		addSource(targetHash = hash(create(legacy, target)), "target");
		addSource(new CheckConstraint(
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

	@Wrap(order=10,
			doc="Returns whether the given value corresponds to the hash in {0}.")
	public boolean check(final Item item, final String actualPlainText)
	{
		return select(item).check(item, actualPlainText);
	}

	private Hash select(final Item item)
	{
		if(legacyHash.getHash(item)!=null)
			return legacyHash;
		else
			return targetHash;
	}

	public boolean isNull(final Item item)
	{
		// needs actual implementation if there is optional()
		return false;
	}

	public String getHash(final Item item)
	{
		final String targetHash = this.targetHash.getHash(item);
		if(targetHash!=null)
			return targetHash;

		return legacyHash.getHash(item);
	}

	@Wrap(order=20,
			doc={"Wastes (almost) as much cpu cycles, as a call to <tt>check{3}</tt> would have needed.",
					"Needed to prevent Timing Attacks."})
	@Override
	public void blind(final String actualPlainText)
	{
		targetHash.blind(actualPlainText);
	}

	@Override
	public String newRandomPassword(final SecureRandom random)
	{
		return targetHash.newRandomPassword(random);
	}

	@Wrap(order=30,
			doc="Sets a new value for {0}.",
			thrownGetter=InitialExceptionsSettableGetter.class)
	public void set(final Item item, final String plainText)
	{
		if(plainText==null)
			throw MandatoryViolationException.create(this, item);

		item.set(
				legacyHash.map(null),
				targetHash.map(plainText));
	}

	@Wrap(order=60,
			doc="Re-hashes all legacy passwords to target ones.")
	public void migrate(@Parameter("ctx") final JobContext ctx)
	{
		if(ctx==null)
			throw new NullPointerException("ctx");

		final Type<?> type = getType();
		final Model model = type.getModel();
		final String id = getID();

		for(final Item item : Iterables.once(
				iterateTransactionally(type, getLegacyHash().isNotNull(), 100)))
		{
			ctx.stopIfRequested();
			final String itemID = item.getCopeID();
			try
			{
				model.startTransaction(id + " migrate " + itemID);

				final String legacyHashValue = legacyHash.getHash(item);
				if(legacyHashValue==null)
				{
					if(logger.isInfoEnabled())
						logger.info( MessageFormat.format(
								"Already migrated {1} by {0}, probably due to concurrent migrating.",
								id, itemID ) );
					continue;
				}

				item.set(
						legacyHash.map(null),
						targetHash.getStorage().map(targetAlgorithm.hash(legacyHashValue)));

				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
			ctx.incrementProgress();
		}
	}

	@Override
	public SetValue<String> map(final String value)
	{
		return SetValue.map(this, value);
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
	public boolean isInitial()
	{
		return isMandatory() || isFinal();
	}

	@Deprecated
	@Override
	public java.lang.reflect.Type getInitialType()
	{
		return String.class;
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
