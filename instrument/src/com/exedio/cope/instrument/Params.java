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

package com.exedio.cope.instrument;

import static com.exedio.cope.misc.Check.requireNonNegative;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class Params
{
	List<File> sourceFiles;
	/** in {@link #sourceFiles}, the files to look at for understanding source code, but not instrument */
	final List<File> ignoreFiles = new ArrayList<>();
	boolean verify = false;
	Charset charset = StandardCharsets.US_ASCII;

	private int maxwarns = 10000;

	void setMaxwarns(final int value)
	{
		maxwarns = requireNonNegative(value, "maxwarns");
	}

	String getMaxwarns()
	{
		return String.valueOf(maxwarns);
	}

	ConfigurationByJavadocTags configByTags = ConfigurationByJavadocTags.warn;
	boolean longJavadoc = true; // non-default is deprecated
	boolean finalArgs = true; // non-default is deprecated
	boolean nullabilityAnnotations = false;
	boolean suppressUnusedWarningOnPrivateActivationConstructor = false;
	boolean serialVersionUID = true;
	IntegerTypeSuffix serialVersionUIDSuffix = IntegerTypeSuffix.small;
	boolean genericSetValueArray = true; // non-default is deprecated
	boolean directSetValueMap = false;
	String hidingWarningSuppressor = null;
	boolean parenthesesOnEmptyMemberAnnotations = false; // non-default is deprecated
	boolean deprecatedFullyQualified = true; // non-default is deprecated
	boolean overrideOnSeparateLine = true; // non-default is deprecated
	HintFormat hintFormat = HintFormat.forAnnotations;
	boolean verbose = false;
	File timestampFile = null;

}
