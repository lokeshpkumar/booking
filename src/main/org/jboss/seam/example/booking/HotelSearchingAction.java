//$Id: HotelSearchingAction.java 8998 2008-09-16 03:08:11Z shane.bryzak@jboss.com $
package org.jboss.seam.example.booking;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.security.Restrict;

@Stateful
@Name("hotelSearch")
@Scope(ScopeType.SESSION)
@Restrict("#{identity.loggedIn}")
public class HotelSearchingAction implements HotelSearching
{
    @PersistenceContext
    private EntityManager em;
    
    private String searchString;
    private int pageSize = 10;
    private int page;
    private boolean nextPageAvailable;
   
    @DataModel
    private List<Hotel> hotels;
    
    @EJB(mappedName = "java:app/booking.jar/ReadonlySessionBean")
    private ReadonlySessionBeanLocal readOnlySessionBean;
    
    private static final Logger log = Logger.getLogger(HotelSearchingAction.class);
   
    public void find() 
    {
        page = 0;
        queryHotels();
    }

    public void nextPage() 
    {
        page++;
        queryHotels();
    }
    
    private void queryHotels() {
        List<Hotel> results = readOnlySessionBean.getHotels(this.getSearchPattern(), page, pageSize);
        
        nextPageAvailable = results.size() > pageSize;
        if (nextPageAvailable) 
        {
            hotels = new ArrayList<Hotel>(results.subList(0,pageSize));
        } else {
            hotels = results;
        }
        
        try
        {
            ModelNode op = new ModelNode();
            op.get("operation").set("read-attribute");

            ModelNode address = op.get("address");
            address.add("core-service", "server-environment");

            op.get("name").set("home-dir");
            op.get("operations").set(true);

            ModelControllerClient client = ModelControllerClient.Factory.create(InetAddress.getByName("localhost"), 9999 );
            
            ModelNode result = client.execute(op);
            
            String homeDir = result.get("result").toString();
            
            log.info("LKP: Home Directory of Jboss: " + homeDir);
        }
        catch ( Exception ex )
        {
            log.error("LKP: Failed to load Jboss home dir", ex);
        }
    }

    public boolean isNextPageAvailable()
    {
        return nextPageAvailable;
    }
   
   public int getPageSize() {
      return pageSize;
   }
   
   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }
   
   @Factory(value="pattern", scope=ScopeType.EVENT)
   public String getSearchPattern()
   {
      return searchString==null ? 
            "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
   }
   
   public String getSearchString()
   {
      return searchString;
   }
   
   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }
   
   @Remove
   public void destroy() {}
}
