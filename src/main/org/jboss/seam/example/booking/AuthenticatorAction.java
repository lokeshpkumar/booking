package org.jboss.seam.example.booking;

import static org.jboss.seam.ScopeType.SESSION;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.security.Identity;

@Stateless
@Name("authenticator")
public class AuthenticatorAction 
    implements Authenticator
{
    @PersistenceContext 
    private EntityManager em;

    @In(required=false)   
    @Out(required=false, scope = SESSION)
    private User user;
    
    private static final Logger log = Logger.getLogger(AuthenticatorAction.class.getSimpleName());
    
    @In
    private Identity identity;
   
    public boolean authenticate()
    {
	List results = em.createQuery("select u from User u where u.username=#{identity.username} and u.password=#{identity.password}")
                         .getResultList();
        
        log.info("LKP: Username: " + identity.getCredentials().getUsername() + ", Password: " + identity.getCredentials().getPassword() );
      
	if (results.size()==0) {
	    return false;
	} else {
	    user = (User) results.get(0);
	    return true;
	}
    }
    
}
