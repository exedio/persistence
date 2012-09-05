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

package com.exedio.cope;

import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.ComputedElement;

final class PatternComputedPattern extends Pattern
{
	private static final long serialVersionUID = 1l;

	final StringField virgnSource = new StringField();
	final StringField compuSource = new StringField();

	PatternComputedPattern()
	{
		addSource(virgnSource, "virgnSource");
		addSource(compuSource, "compuSource", ComputedElement.get());
	}

	Type<?> virgnType = null;
	Type<?> compuType = null;

	@Override
	protected void onMount()
	{
		super.onMount();
		final Features features = new Features();
		this.virgnType = newSourceType(VirgnType.class, features, "virgn");
		this.compuType = newSourceType(CompuType.class, features, "compu");
	}

	static final class VirgnType extends Item
	{
		private static final long serialVersionUID = 1l;
		private VirgnType(final ActivationParameters ap) { super(ap); }
	}

	@Computed
	static final class CompuType extends Item
	{
		private static final long serialVersionUID = 1l;
		private CompuType(final ActivationParameters ap) { super(ap); }
	}
}
