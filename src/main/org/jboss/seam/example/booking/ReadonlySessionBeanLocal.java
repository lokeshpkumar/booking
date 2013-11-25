/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.seam.example.booking;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author lokeshpkumar
 */
@Local
public interface ReadonlySessionBeanLocal
{
    public List<Hotel> getHotels(String pattern, int page, int pageSize);
}
