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

import javax.annotation.Nonnull;

/**
 * ChangeHooks allow to listen to changes of persistent data.
 * They are registered via
 * {@link ModelBuilder#changeHooks(ChangeHook.Factory...) ModelBuilder.changeHooks}
 * when creating the model.
 * Registering hooks later is not supported.
 * <p>
 * <b>Think twice before using ChangeHooks!</b>
 * They are a poor  way for implementing constraints or defaults.
 * For constraints you are better off with
 * {@link CheckConstraint check constraints},
 * {@link FunctionField#copyFrom(ItemField) copy constraints}, or
 * simple constraints such as
 * {@link IntegerField#min(int) ranges},
 * {@link StringField#lengthRange(int, int) length ranges}, or
 * {@link StringField#charSet(com.exedio.cope.util.CharSet) character sets}.
 * For defaults use
 * {@link FunctionField#defaultTo(Object) constants},
 * {@link DateField#defaultToNow() now},
 * {@link IntegerField#defaultToNext(int) sequence}, or
 * {@link LongField#defaultToRandom(java.util.Random) random}.
 * <p>
 * Methods of hooks are called synchronously when the change actually occurs,
 * by the thread that does the change.
 * If a method of a hook fails with an exception,
 * that exception is "thrown through" to the call causing the change.
 * If you want to postpone your action until the current transaction commits,
 * use either
 * {@link Model#addPreCommitHookIfAbsent(Runnable) pre}- or
 * {@link Model#addPostCommitHookIfAbsent(Runnable) post}-commit hooks.
 * <p>
 * Note that any <i>new</i> change to persistent data (i.e. other than
 * returning modified {@code setValues}) done in one of the methods will
 * cause this hook to be called again.
 * Without care you may end up with infinite recursion.
 * <p>
 * <b>Synchronization</b><br>
 * There is only one instance of the ChangeHook for each model,
 * and calls to ChangeHooks are not synchronized.
 * If multiple threads concurrently change persistent data,
 * then methods of this hook are called concurrently.
 * <p>
 * It is highly recommended to override {@link Object#toString() toString}
 * with an informative message about the hook.
 * This message is returned by {@link Model#getChangeHookString()}.
 *
 * @see com.exedio.cope.misc.ChangeHooks
 */
public interface ChangeHook
{
	// needed to avoid problems with generic arrays
	@FunctionalInterface
	interface Factory
	{
		@Nonnull ChangeHook create(@Nonnull Model model);
	}

	/**
	 * Is called before any item creation.
	 * You may change the values of the newly created item
	 * by returning changed {@code setValues}.
	 * The default implementation does nothing and
	 * returns {@code setValues} unmodified.
	 */
	default SetValue<?>[] beforeNew(final Type<?> type, final SetValue<?>[] setValues)
	{
		return setValues; // empty default implementation
	}

	/**
	 * Is called after any item creation.
	 * The default implementation does nothing.
	 *
	 * @see Item#afterNewCopeItem()
	 */
	default void afterNew(final Item item)
	{
		// empty default implementation
	}

	/**
	 * Is called before any item modification.
	 * The default implementation does nothing.
	 *
	 * @param setValues is never null and never empty
	 * @return must not return null
	 *
	 * @see Item#beforeSetCopeItem(SetValue[])
	 */
	default SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] setValues)
	{
		return setValues; // empty default implementation
	}

	/**
	 * Is called before any item deletion.
	 * The default implementation does nothing.
	 *
	 * @see Item#beforeDeleteCopeItem()
	 */
	default void beforeDelete(final Item item)
	{
		// empty default implementation
	}
}
