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

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.vault.VaultNotFoundException;
import java.io.IOException;
import java.io.Serial;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultNotFoundExceptionTest
{
	@Test void notAnonymous()
	{
		final VaultNotFoundException e = new VaultNotFoundException("abcdefghijklmnop");
		assertEquals("abcdefghijklmnop", e.getHashComplete());
		assertEquals("abcdefghijklmnop", e.getHashAnonymous());
		assertEquals("hash not found in vault: abcdefghijklmnop", e.getMessage());
		assertEquals(null, e.getCause());
	}

	@Test void anonymous()
	{
		final VaultNotFoundException e = new VaultNotFoundException("abcdefghijklmnopq");
		assertEquals("abcdefghijklmnopq", e.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", e.getHashAnonymous());
		assertEquals("hash not found in vault: abcdefghijklmnopxx17", e.getMessage());
		assertEquals(null, e.getCause());
	}

	@Test void testAnonymiseHash()
	{
		assertEquals("abcdefghijklmnopxx17", anonymiseHash("abcdefghijklmnopq"));
		assertSame("abcdefghijklmnop", anonymiseHash("abcdefghijklmnop"));
		assertSame("", anonymiseHash(""));
		assertSame(null, anonymiseHash(null));
	}

	@SuppressWarnings("ThrowableNotThrown")
	@Test void constructor1HashNull()
	{
		try
		{
			new VaultNotFoundException(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@SuppressWarnings("ThrowableNotThrown")
	@Test void constructor2HashNull()
	{
		final IOException cause = new IOException();
		try
		{
			new VaultNotFoundException(null, cause);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void constructor2CauseNull()
	{
		final VaultNotFoundException e = new VaultNotFoundException("myHash", null);
		assertEquals("myHash", e.getHashComplete());
		assertEquals(null, e.getCause());
	}

	@SuppressWarnings("ThrowableNotThrown")
	@Test void constructor2BothNull()
	{
		try
		{
			new VaultNotFoundException(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void anonymousCauseSkippedBecauseMessage()
	{
		final Cause causeOrigin = newCause("PRE-abcdefghijklmnopz-IN-abcdefghijklmnopz-POST", null);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		assertSame(causeOrigin, notFound.getCause());
	}

	@Test void anonymousCauseSkippedBecauseHash()
	{
		final Cause causeOrigin = newCause("PRE-abcdefghijklmnopq-IN-abcdefghijklmnopq-POST", null);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnop", causeOrigin);

		assertEquals("abcdefghijklmnop", notFound.getHashComplete());
		assertEquals("abcdefghijklmnop", notFound.getHashAnonymous());

		assertSame(causeOrigin, notFound.getCause());
	}

	@Test void anonymousCause()
	{
		final Cause causeOrigin = newCause("PRE-abcdefghijklmnopq-IN-abcdefghijklmnopq-POST", null);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause = notFound.getCause();
		assertNotSame(causeOrigin, cause);
		assertEquals(
				causeName + ": PRE-abcdefghijklmnopxx17-IN-abcdefghijklmnopxx17-POST",
				cause.getMessage());
		assertEquals(notFoundAnonymous, cause.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause.getStackTrace()[0]);
		assertNull(cause.getCause());
	}

	@Test void anonymousCauseNested()
	{
		final Cause causeOrigin1 = newCause("1=abcdefghijklmnopq", null);
		final Cause causeOrigin2 = newCause("2=abcdefghijklmnopq", causeOrigin1);
		final Cause causeOrigin3 = newCause("3=abcdefghijklmnopq", causeOrigin2);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin3);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause3 = notFound.getCause();
		assertNotSame(causeOrigin1, cause3);
		assertNotSame(causeOrigin2, cause3);
		assertNotSame(causeOrigin3, cause3);
		assertEquals(causeName + ": 3=abcdefghijklmnopxx17", cause3.getMessage());
		assertEquals(notFoundAnonymous, cause3.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause3.getStackTrace()[0]);

		final Throwable cause2 = cause3.getCause();
		assertNotSame(causeOrigin1, cause2);
		assertNotSame(causeOrigin2, cause2);
		assertNotSame(causeOrigin3, cause2);
		assertEquals(causeName + ": 2=abcdefghijklmnopxx17", cause2.getMessage());
		assertEquals(notFoundAnonymous, cause2.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause2.getStackTrace()[0]);

		final Throwable cause1 = cause2.getCause();
		assertNotSame(causeOrigin1, cause1);
		assertNotSame(causeOrigin2, cause1);
		assertNotSame(causeOrigin3, cause1);
		assertEquals(causeName + ": 1=abcdefghijklmnopxx17", cause1.getMessage());
		assertEquals(notFoundAnonymous, cause1.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause1.getStackTrace()[0]);

		assertNull(cause1.getCause());
	}

	@Test void anonymousCauseNestedMiddle()
	{
		final Cause causeOrigin1 = newCause("1=abcdefghijklmnopz", null);
		final Cause causeOrigin2 = newCause("2=abcdefghijklmnopq", causeOrigin1);
		final Cause causeOrigin3 = newCause("3=abcdefghijklmnopz", causeOrigin2);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin3);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause3 = notFound.getCause();
		assertNotSame(causeOrigin1, cause3);
		assertNotSame(causeOrigin2, cause3);
		assertNotSame(causeOrigin3, cause3);
		assertEquals(causeName + ": 3=abcdefghijklmnopz", cause3.getMessage());
		assertEquals(notFoundAnonymous, cause3.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause3.getStackTrace()[0]);

		final Throwable cause2 = cause3.getCause();
		assertNotSame(causeOrigin1, cause2);
		assertNotSame(causeOrigin2, cause2);
		assertNotSame(causeOrigin3, cause2);
		assertEquals(causeName + ": 2=abcdefghijklmnopxx17", cause2.getMessage());
		assertEquals(notFoundAnonymous, cause2.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause2.getStackTrace()[0]);

		assertSame(causeOrigin1, cause2.getCause());
	}

	@Test void anonymousCauseNullMessage()
	{
		final Cause causeOrigin1 = newCause(null, null);
		final Cause causeOrigin2 = newCause("2=abcdefghijklmnopq", causeOrigin1);
		final Cause causeOrigin3 = newCause(null, causeOrigin2);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin3);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause3 = notFound.getCause();
		assertNotSame(causeOrigin1, cause3);
		assertNotSame(causeOrigin2, cause3);
		assertNotSame(causeOrigin3, cause3);
		assertEquals(causeName, cause3.getMessage());
		assertEquals(notFoundAnonymous, cause3.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause3.getStackTrace()[0]);

		final Throwable cause2 = cause3.getCause();
		assertNotSame(causeOrigin1, cause2);
		assertNotSame(causeOrigin2, cause2);
		assertNotSame(causeOrigin3, cause2);
		assertEquals(causeName + ": 2=abcdefghijklmnopxx17", cause2.getMessage());
		assertEquals(notFoundAnonymous, cause2.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause2.getStackTrace()[0]);

		assertSame(causeOrigin1, cause2.getCause());
	}

	@Test void anonymousCausePostfix()
	{
		final Cause causeOrigin = newCause("PRE-klmnopq-IN-klmnopq-POST", null);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause = notFound.getCause();
		assertNotSame(causeOrigin, cause);
		assertEquals(
				causeName + ": PRE-klmnopxx17-IN-klmnopxx17-POST",
				cause.getMessage());
		assertEquals(notFoundAnonymous, cause.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause.getStackTrace()[0]);
		assertNull(cause.getCause());
	}

	@Test void anonymousCausePostfixTooShort()
	{
		final Cause causeOrigin = newCause("PRE-lmnopq-IN-lmnopq-POST", null);
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		assertSame(causeOrigin, notFound.getCause());
	}

	@Test void anonymousCauseLoopBoth()
	{
		final Cause causeOrigin1 = newCause("1=abcdefghijklmnopq");
		final Cause causeOrigin2 = newCause("2=abcdefghijklmnopq", causeOrigin1);
		causeOrigin1.setCauseInTest(causeOrigin2);
		assertSame(causeOrigin2, causeOrigin1.getCause());
		assertSame(causeOrigin1, causeOrigin2.getCause());
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin2);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause2 = notFound.getCause();
		assertNotSame(causeOrigin1, cause2);
		assertNotSame(causeOrigin2, cause2);
		assertEquals(causeName + ": 2=abcdefghijklmnopxx17", cause2.getMessage());
		assertEquals(notFoundAnonymous, cause2.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause2.getStackTrace()[0]);

		final Throwable cause1 = cause2.getCause();
		assertNotSame(causeOrigin1, cause1);
		assertNotSame(causeOrigin2, cause1);
		assertEquals(causeName + ": 1=abcdefghijklmnopxx17", cause1.getMessage());
		assertEquals(notFoundAnonymous, cause1.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause1.getStackTrace()[0]);

		assertNull(cause1.getCause());
	}

	@Test void anonymousCauseLoopFirst()
	{
		final Cause causeOrigin1 = newCause("1=abcdefghijklmnopq");
		final Cause causeOrigin2 = newCause("2=abcdefghijklmnopz", causeOrigin1);
		causeOrigin1.setCauseInTest(causeOrigin2);
		assertSame(causeOrigin2, causeOrigin1.getCause());
		assertSame(causeOrigin1, causeOrigin2.getCause());
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin2);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause2 = notFound.getCause();
		assertNotSame(causeOrigin1, cause2);
		assertNotSame(causeOrigin2, cause2);
		assertEquals(causeName + ": 2=abcdefghijklmnopz", cause2.getMessage());
		assertEquals(notFoundAnonymous, cause2.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause2.getStackTrace()[0]);

		final Throwable cause1 = cause2.getCause();
		assertNotSame(causeOrigin1, cause1);
		assertNotSame(causeOrigin2, cause1);
		assertEquals(causeName + ": 1=abcdefghijklmnopxx17", cause1.getMessage());
		assertEquals(notFoundAnonymous, cause1.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause1.getStackTrace()[0]);

		assertNull(cause1.getCause());
	}

	@Test void anonymousCauseLoopSecond()
	{
		final Cause causeOrigin1 = newCause("1=abcdefghijklmnopz");
		final Cause causeOrigin2 = newCause("2=abcdefghijklmnopq", causeOrigin1);
		causeOrigin1.setCauseInTest(causeOrigin2);
		assertSame(causeOrigin2, causeOrigin1.getCause());
		assertSame(causeOrigin1, causeOrigin2.getCause());
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin2);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause2 = notFound.getCause();
		assertNotSame(causeOrigin1, cause2);
		assertNotSame(causeOrigin2, cause2);
		assertEquals(causeName + ": 2=abcdefghijklmnopxx17", cause2.getMessage());
		assertEquals(notFoundAnonymous, cause2.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause2.getStackTrace()[0]);

		final Throwable cause1 = cause2.getCause();
		assertNotSame(causeOrigin1, cause1);
		assertNotSame(causeOrigin2, cause1);
		assertEquals(causeName + ": 1=abcdefghijklmnopz", cause1.getMessage());
		assertEquals(notFoundAnonymous, cause1.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause1.getStackTrace()[0]);

		assertNull(cause1.getCause());
	}

	@Test void anonymousCauseLoopSelf()
	{
		final Cause causeOrigin = newCause("1=abcdefghijklmnopq");
		causeOrigin.setCauseInTest(causeOrigin);
		assertSame(causeOrigin, causeOrigin.getCause());
		final VaultNotFoundException notFound =
				new VaultNotFoundException("abcdefghijklmnopq", causeOrigin);

		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause = notFound.getCause();
		assertNotSame(causeOrigin, cause);
		assertEquals(causeName + ": 1=abcdefghijklmnopxx17", cause.getMessage());
		assertEquals(notFoundAnonymous, cause.getClass());
		assertStacktrace(VaultNotFoundExceptionTest.class, "newCause", cause.getStackTrace()[0]);

		assertNull(cause.getCause());
	}


	private static Cause newCause(final String message, final Throwable cause)
	{
		return new Cause(message, cause);
	}

	private static Cause newCause(final String message) // requires to set the cause later
	{
		return new Cause(message);
	}

	private static final class Cause extends Exception
	{
		private final String message;
		@SuppressWarnings("NonFinalFieldOfException")
		private Throwable cause;
		@SuppressWarnings("NonFinalFieldOfException")
		private boolean causeSet;

		Cause(final String message, final Throwable cause)
		{
			super(null, null); // makes sure, super.initCause is not allowed anymore
			this.message = message;
			this.cause = cause;
			this.causeSet = true;
		}

		Cause(final String message)
		{
			super(null, null); // makes sure, super.initCause is not allowed anymore
			this.message = message;
			this.cause = null;
			this.causeSet = false;
		}

		void setCauseInTest(final Throwable cause)
		{
			assertFalse(this.causeSet, "causeSet");
			this.cause = cause;
			this.causeSet = true;
		}

		@Override public String getMessage()
		{
			assertTrue(causeSet, "causeSet");
			return message;
		}

		@SuppressWarnings("NonSynchronizedMethodOverridesSynchronizedMethod")
		@Override public Throwable getCause()
		{
			assertTrue(causeSet, "causeSet");
			return cause;
		}

		@Override public String getLocalizedMessage() { throw new AssertionFailedError(); }
		@SuppressWarnings("NonSynchronizedMethodOverridesSynchronizedMethod")
		@Override public Throwable initCause(final Throwable cause) { throw new AssertionFailedError(); }
		@Override public String toString() { throw new AssertionFailedError(); }
		@Override public void setStackTrace(final StackTraceElement[] stackTrace) { throw new AssertionFailedError(); }

		@Serial
		private static final long serialVersionUID = 1l;
	}

	private static final String causeName = Cause.class.getName();

	private static void assertStacktrace(
			final Class<?> className,
			final String methodName,
			final StackTraceElement element)
	{
		assertAll(
				() -> assertEquals(className.getName(), element.getClassName(), "className"),
				() -> assertEquals(methodName, element.getMethodName(), "methodName"));
	}

	private static final Class<?> notFoundAnonymous = classForName(
			"com.exedio.cope.vault.VaultNotFoundException$Anonymous");

	private static Class<?> classForName(final String className)
	{
		try
		{
			return Class.forName(className);
		}
		catch(final ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
}
