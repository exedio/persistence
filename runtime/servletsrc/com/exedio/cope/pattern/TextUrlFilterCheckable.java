package com.exedio.cope.pattern;

import com.exedio.cope.Item;
import com.exedio.cope.pattern.MediaPath.NotFound;
import java.util.Set;

public interface TextUrlFilterCheckable
{

	public Set<String> check(Item item) throws NotFound;

}