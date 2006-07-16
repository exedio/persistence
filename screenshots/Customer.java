import com.exedio.cope.*;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.pattern.MD5Hash;

/**
 * Defines a persistent class customer within your application .
 */
public class Customer extends Item
{
	/**
	 * The unique email address of the customer. 
	 */
	public static final StringAttribute email =
		new StringAttribute(UNIQUE);
	
	/**
	 * The password of the customer, transparently md5-encoded.
	 */
	public static final MD5Hash password = new MD5Hash();

}
