package com.exedio.cope.pattern;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Set;

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
import com.exedio.cope.misc.TypeIterator;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.util.JobContext;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class NestingHashMigration extends Pattern implements HashInterface
{
	private static final long serialVersionUID = 1l;

	private final Hash oldHash;
	private final Hash newHash;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final HashAlgorithm newAlgorithm;

	/**
	 * @param oldAlgorithm the algorithm the passwords are currently hashed with
	 * @param newAlgorithm the algorithm the passwords are to be hashed with in the future
	 */
	public NestingHashMigration(final HashAlgorithm oldAlgorithm, final HashAlgorithm newAlgorithm)
	{
		this.newAlgorithm = newAlgorithm;
		addSource(oldHash = new Hash(oldAlgorithm).optional(), "old", ComputedElement.get());
		addSource(newHash = new Hash(NestedHashAlgorithm.create(newAlgorithm, oldAlgorithm)).optional(), "new");
		addSource(new CheckConstraint(
			Cope.or(
				oldHash.isNull().and(newHash.isNotNull()),
				oldHash.isNotNull().and(newHash.isNull())
			)),
			"xor");
	}

	public Hash getOldHash()
	{
		return oldHash;
	}

	public Hash getNewHash()
	{
		return newHash;
	}

	@Wrap(order=10,
			doc="Returns whether the given value corresponds to the hash in {0}.")
	public boolean check(final Item item, final String actualPlainText)
	{
		return select(item).check(item, actualPlainText);
	}

	@Wrap(order=20,
			doc={"Wastes (almost) as much cpu cycles, as a call to <tt>check{3}</tt> would have needed.",
					"Needed to prevent Timing Attacks."})
	@Override
	public void blind(final String actualPlainText)
	{
		newHash.blind(actualPlainText);
	}

	@Override
	public String newRandomPassword(final SecureRandom random)
	{
		return newHash.newRandomPassword(random);
	}

	@Wrap(order=30,
			doc="Sets a new value for {0}.",
			thrownGetter=InitialExceptionsSettableGetter.class)
	public void set(final Item item, final String plaintext)
	{
		if(plaintext==null)
			throw MandatoryViolationException.create(this, item);

		item.set(
				oldHash.map(null),
				newHash.map(plaintext));
	}

	@Wrap(order=60, doc="Re-hashes all old passwords to new ones.")
	public <P extends Item> void migrate(final Class<P> parent, @Parameter("context") final JobContext context)
	{
		final Type<P> type = getType().as(parent);
		final Iterator<P> it = TypeIterator.iterateTransactionally(type, getOldHash().getStorage().isNotNull(), 100);
		while(it.hasNext())
		{
			context.stopIfRequested();
			final Model model = type.getModel();
			try
			{
				final P item = it.next();
				model.startTransaction(getClass().getSimpleName() + ".migrate(): " + getID() + ", " + item.getCopeID());
				item.set(oldHash.map(null), newHash.getStorage().map(newAlgorithm.hash(oldHash.getHash(item))));
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
			if (context.supportsProgress())
				context.incrementProgress();
		}
	}

	private Hash select(final Item item)
	{
		if (oldHash.getHash(item) != null)
			return oldHash;
		else
			return newHash;
	}

	@Override
	public SetValue<String> map(final String plaintext)
	{
		return SetValue.map(this, plaintext);
	}

	@Override
	public SetValue<?>[] execute(final String value, final Item exceptionItem)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, exceptionItem);

		return new SetValue<?>[]{assertSingleton(newHash.execute(value, exceptionItem)), assertSingleton(oldHash.execute(null, exceptionItem))};
	}

	private static <T> T assertSingleton(final T[] array)
	{
		assert array.length == 1;
		return array[0];
	}

	@Override
	public boolean isFinal()
	{
		return newHash.isFinal(); // TODO allow final as well
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
		final Set<Class<? extends Throwable>> result = newHash.getInitialExceptions();
		if(isMandatory())
			result.add(MandatoryViolationException.class);
		return result;
	}
}
