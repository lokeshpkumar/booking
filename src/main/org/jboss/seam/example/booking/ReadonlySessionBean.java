/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.seam.example.booking;

import java.util.List;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author lokeshpkumar
 */
@Stateless
@Local( ReadonlySessionBeanLocal.class )
public class ReadonlySessionBean implements ReadonlySessionBeanLocal
{

    private static final Logger log = Logger.getLogger(ReadonlySessionBean.class);
    
    @PersistenceContext
    private EntityManager em;
    
    public List<Hotel> getHotels(String pattern, int page, int pageSize)
    {
        log.info("LKP: Pattern: " + pattern + ", page: " + page + ", pageSize: " + pageSize);
        List<Hotel> results = em.createQuery("select h from Hotel h where lower(h.name) like ?1 or "
                + "lower(h.city) like ?2 or lower(h.zip) like ?3 or lower(h.address) like ?4")
                                .setParameter(1, pattern)
                                .setParameter(2, pattern)
                                .setParameter(3, pattern)
                                .setParameter(4, pattern)
                                .setMaxResults(pageSize+1)
                                .setFirstResult(page * pageSize)
                                .getResultList();
        return results;
    }
    
}
