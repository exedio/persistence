package com.exedio.cope.pattern;

import com.exedio.cope.StringField;
import com.exedio.cope.pattern.HashAlgorithm;

public final class NestedHashAlgorithm
{
	public static final HashAlgorithm create(final HashAlgorithm outer, final HashAlgorithm inner)
	{
		if(outer==null)
			throw new NullPointerException();
		if(inner==null)
			throw new NullPointerException();

		final String plainText = "1234";
		if(!inner.hash(plainText).equals(inner.hash(plainText)))
			throw new IllegalArgumentException("inner algorithm must be deterministic (i.e. unsalted), but was " + inner.getDescription());

		return new Algorithm(outer, inner);
	}

	private static final class Algorithm implements HashAlgorithm
	{
		final HashAlgorithm outer;
		final HashAlgorithm inner;

		Algorithm(final HashAlgorithm outer, final HashAlgorithm inner)
		{
			this.outer = outer;
			this.inner = inner;
		}

		@Override
		public String getID()
		{
			return outer.getID() + '-' + inner.getID();
		}

		@Override
		public String getDescription()
		{
			return outer.getDescription() + '-' + inner.getDescription();
		}

		@Override
		public StringField constrainStorage(final StringField storage)
		{
			return outer.constrainStorage(storage);
		}

		@Override
		public String hash(final String plainText)
		{
			return outer.hash(inner.hash(plainText));
		}

		@Override
		public boolean check(final String plainText, final String hash)
		{
			return outer.check(inner.hash(plainText), hash);
		}
	}

	private NestedHashAlgorithm()
	{
		// prevent instantiation
	}
}
