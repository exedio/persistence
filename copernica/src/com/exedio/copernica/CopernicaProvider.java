
package com.exedio.copernica;

import java.util.Collection;

import com.exedio.cope.lib.Feature;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Type;

public interface CopernicaProvider
{

	public Model getModel();

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

	public String getDisplayName(Language displayLanguage, Feature feature);
	
	public String getDisplayName(Language displayLanguage, Item item);

	public String getIconURL(Type type);

	// TODO: rename to ByCopernicaID
	public Language findLanguageByUniqueID(String uniqueID);
	
	public User findUserByCopernicaID(String copernicaID);

	// TODO: rename to ByCopernicaID
	public Category findCategoryByUniqueID(String uniqueID);
	
	// TODO: rename to ByCopernicaID
	public Section findSectionByUniqueID(String uniqueID);

	public void initializeExampleSystem();
}
