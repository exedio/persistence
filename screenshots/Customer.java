import com.exedio.cope.*;
import com.exedio.cope.pattern.*;

/**
 * Defines a persistent class customer within
 * your application.
 */
public class Customer extends Item
{
   /**
    * The unique email address of the customer.
    */
   static final StringField email = new StringField().unique();

   /**
    * The password of the customer, transparently
    * md5-encoded.
    */
   static final MD5Hash password = new MD5Hash();
}