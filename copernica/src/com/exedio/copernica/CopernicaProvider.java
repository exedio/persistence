
package com.exedio.copernica;

import java.util.Collection;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Type;

public interface CopernicaProvider
{
	/**
	 * @return a collection of {@link Language languages}.
	 */
	public Collection getDisplayLanguages();
	
	/**
	 * @return a collection of {@link Category categories}.
	 */
	public Collection getRootCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Attribute attributes}.
	 */
	public Collection getMainAttributes(Type type);

	/**
	 * @return a collection of {@link Section sections}.
	 */
	public Collection getSections(Type type);
	
	public String getDisplayName(Language displayLanguage, Type type);

	public String getDisplayName(Language displayLanguage, Attribute attribute);
	
	public String getIconURL(Type type);

	public Language findLanguageByUniqueID(String uniqueID);
	
	public Category findCategoryByUniqueID(String uniqueID);
	
	public Section findSectionByUniqueID(String uniqueID);
}
