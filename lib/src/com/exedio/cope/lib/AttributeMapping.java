
package persistence;

public interface AttributeMapping
{
	public Object mapJava(Item item, final Object[] qualifiers);
	
	public String mapSQL();
	
}
