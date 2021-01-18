package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ContactDTO;
import dto.CreateContactDTO;
import entities.User;
import facades.ContactFacade;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.*;


/**
 * @author lam@cphbusiness.dk
 */
@Path("contact")
public class ContactResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final ExecutorService ES = Executors.newCachedThreadPool();
    private static final ContactFacade facade = ContactFacade.getUserFacade(EMF);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static String cachedResponse;
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database has been setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("allUsers")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery ("select u from User u",User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }
    
    @Path("setup")
    @GET
    @Produces ({MediaType.APPLICATION_JSON})
    public void setupTestUsers() {
        SetupTestUsers setup = new SetupTestUsers();
        setup.setupUsers();
    }
    
    @Path("create")
    @POST
    @Produces ({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String getCreateContact(String contactDTO) {
        ContactDTO contactDTOResult = gson.fromJson(contactDTO, ContactDTO.class);
        
        facade.createContact(contactDTOResult);
        
        return gson.toJson(contactDTOResult);
    }
    
    @Path("all")
    @GET
    @Produces ({MediaType.APPLICATION_JSON})
    public String getContactList() {
        List<ContactDTO> list = facade.getAllContacts();
        
        return gson.toJson(list);
    }
    
    @Path("get/{id}")
    @GET
    @Produces ({MediaType.APPLICATION_JSON})
    public String getContact(@PathParam("id") long id) {
        ContactDTO contactDTO = facade.getContact(id);
        
        return gson.toJson(contactDTO);
    }
    
    @Path("edit")
    @PUT
    @Produces ({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String getContact(String contactDTO) {
        ContactDTO contactDTOResult = gson.fromJson(contactDTO, ContactDTO.class);
        
        facade.editContact(contactDTOResult);
        
        return gson.toJson(contactDTOResult);
    }
    
    @Path("delete/{id}")
    @DELETE
    @Produces ({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String deleteContact(@PathParam("id") long id) {
        ContactDTO contactDTO = facade.deleteContact(id);
        
        return gson.toJson(contactDTO);
    }
}