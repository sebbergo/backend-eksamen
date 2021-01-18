package facades;

import dto.ContactDTO;
import dto.CreateContactDTO;
import dto.OpportunityDTO;
import entities.Contact;
import entities.Opportunity;
import entities.User;
import errorhandling.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import security.errorhandling.AuthenticationException;

/**
 * @author lam@cphbusiness.dk
 */
public class ContactFacade {

    private static EntityManagerFactory emf;
    private static ContactFacade instance;

    private ContactFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static ContactFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ContactFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public ContactDTO createContact(ContactDTO contactDTO) {
        EntityManager em = emf.createEntityManager();

        try {
            Contact c = new Contact(contactDTO.name, contactDTO.email, contactDTO.company,
                    contactDTO.jobtitle, contactDTO.phone);

            em.getTransaction().begin();

            em.persist(c);

            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return contactDTO;
    }

    public List<ContactDTO> getAllContacts() {
        EntityManager em = emf.createEntityManager();
        List<ContactDTO> contactDTOList = new ArrayList();

        try {

            TypedQuery query = em.createQuery("SELECT c FROM Contact c", Contact.class);
            List<Contact> contactList = query.getResultList();
            
            for (Contact contact : contactList) {
                contactDTOList.add(new ContactDTO(contact));
            }

        } finally {
            em.close();
        }

        return contactDTOList;
    }

    public ContactDTO getContact(long id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();

        try {
            Contact contact = em.find(Contact.class, id);

            if(contact == null){
                throw new NotFoundException("Contact specified could not be found");
            }
            
            return new ContactDTO(contact);

        } finally {
            em.close();
        }
    }

    public CreateContactDTO editContact(CreateContactDTO contactDTO) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Contact contact = em.find(Contact.class, (long)contactDTO.id);

            contact.setName(contactDTO.name);
            contact.setEmail(contactDTO.email);
            contact.setCompany(contactDTO.company);
            contact.setJobtitle(contactDTO.jobtitle);
            contact.setPhone(contactDTO.phone);

            em.merge(contact);

            em.getTransaction().commit();

            return new CreateContactDTO(contact);

        } finally {
            em.close();
        }
    }

    public ContactDTO deleteContact(long id) {
        EntityManager em = emf.createEntityManager();

        try {
            
            em.getTransaction().begin();
            
            Contact contact = em.find(Contact.class, id);
            
            for (Opportunity opportunity : contact.getOpportunities()) {
                em.remove(opportunity);
            }
            
            em.remove(contact);
            
            em.getTransaction().commit();
            
            return new ContactDTO(contact);
            
        } finally {
            em.close();
        }
    }
    
    public OpportunityDTO addOpportunityToContact(OpportunityDTO OpportunityDTO, long id){
        EntityManager em = emf.createEntityManager();
        
        try{
            
            em.getTransaction().begin();
            
            Opportunity opportunity = new Opportunity(OpportunityDTO.name,
            OpportunityDTO.amount, OpportunityDTO.closeDate);
            
            Contact contact = em.find(Contact.class, id);
            
            contact.addOpportunity(opportunity);
            
            em.merge(contact);
            
            em.getTransaction().commit();
            
            return OpportunityDTO;
            
        }finally{
            em.close();
        }
    }
    
    public List<OpportunityDTO> getOpportunitiesFromContact(long id){
        EntityManager em = emf.createEntityManager();
        
        try{
            
            List<OpportunityDTO> opportunityDTOList = new ArrayList();
            
            em.getTransaction().begin();
            
            Contact contact = em.find(Contact.class, id);
            
            for (Opportunity opportunity : contact.getOpportunities()) {
                opportunityDTOList.add(new OpportunityDTO(opportunity));
            }
            
            return opportunityDTOList;
            
        }finally{
            em.close();
        }
    }
}
