package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ContactDTO;
import dto.CreateContactDTO;
import dto.OpportunityDTO;
import entities.Contact;
import entities.Opportunity;
import entities.OpportunityStatus;
import utils.EMF_Creator;
import entities.Role;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class ContactFacadeTest {

    private static EntityManagerFactory emf;
    private static ContactFacade facade;
    private static User user;
    private static User admin;
    private static User both;
    private static Gson GSON;
    private static Contact c1;
    private static Contact c2;
    private static Opportunity op1;
    private static Opportunity op2;
    private static Opportunity op3;
    private static OpportunityStatus active;
    private static OpportunityStatus won;
    private static OpportunityStatus lost;
    private static OpportunityStatus inactive;

    public ContactFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = ContactFacade.getUserFacade(emf);
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.createNativeQuery("delete from OPPORTUNITY").executeUpdate();
            em.createNativeQuery("delete from OPPORTUNITYSTATUS").executeUpdate();
            em.createNativeQuery("delete from CONTACT").executeUpdate();
            em.createNativeQuery("delete from user_roles").executeUpdate();
            em.createNativeQuery("delete from roles").executeUpdate();
            em.createNativeQuery("delete from users").executeUpdate();
            em.createNativeQuery("ALTER TABLE CONTACT AUTO_INCREMENT = 0").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user = new User("user", "test");
            admin = new User("admin", "test");
            both = new User("user_admin", "test");
            active = new OpportunityStatus("active");
            won = new OpportunityStatus("won");
            lost = new OpportunityStatus("lost");
            inactive = new OpportunityStatus("inactive");
            op1 = new Opportunity("salg1", "10000", "10-1-2021");
            op2 = new Opportunity("salg2", "11000", "10-2-2023");
            op3 = new Opportunity("salg3", "12000", "10-3-2024");
            c1 = new Contact("Sebastian", "test@email.dk", "company", "koder", "12345678");
            c2 = new Contact("Chris", "test@email.dk2", "company2", "koder2", "123456782");

            user.addRole(userRole);
            admin.addRole(adminRole);
            both.addRole(userRole);
            both.addRole(adminRole);

            op1.setOpportunityStatus(won);
            op2.setOpportunityStatus(inactive);
            op3.setOpportunityStatus(active);

            c1.addOpportunity(op1);
            c1.addOpportunity(op2);
            c2.addOpportunity(op3);

            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            em.persist(c1);
            em.persist(c2);
            
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testCreateContact() throws AuthenticationException {
        Contact contact = new Contact("Luke", "test@yahoo.com", "kodegudere", "kodeguden", "87654321");

        ContactDTO contactDTO = new ContactDTO(contact);

        ContactDTO contactResult = facade.createContact(contactDTO);

        assertEquals(contactDTO.name, contactResult.name);
        assertEquals(contactDTO.email, contactResult.email);
        assertEquals(contactDTO.company, contactResult.company);
        assertEquals(contactDTO.jobtitle, contactResult.jobtitle);
        assertEquals(contactDTO.phone, contactResult.phone);
    }

    @Test
    public void testGetAllContacts() {
        List<ContactDTO> contactList = new ArrayList();

        contactList = facade.getAllContacts();

        assertEquals(2, contactList.size());

    }

    @Test
    public void testGetContact() {
        ContactDTO contactDTO = facade.getContact(c1.getId());

        assertEquals(contactDTO.name, c1.getName());
        assertEquals(contactDTO.email, c1.getEmail());
        assertEquals(contactDTO.company, c1.getCompany());
        assertEquals(contactDTO.jobtitle, c1.getJobtitle());
        assertEquals(contactDTO.phone, c1.getPhone());
    }

    @Test
    public void testDeleteContact() {
        ContactDTO contactDTO = facade.deleteContact(c1.getId());

        assertEquals(contactDTO.name, c1.getName());
        assertEquals(contactDTO.email, c1.getEmail());
        assertEquals(contactDTO.company, c1.getCompany());
        assertEquals(contactDTO.jobtitle, c1.getJobtitle());
        assertEquals(contactDTO.phone, c1.getPhone());
    }

    @Test
    public void testEditContact() {
        Contact contact = new Contact("Sumit", "test@gmail.org", "kernen", "CEO", "91827302");
        CreateContactDTO contactDTO = new CreateContactDTO(contact);
        contactDTO.id = (long) 1;

        CreateContactDTO contactResult = facade.editContact(contactDTO);

        assertEquals(contactDTO.name, contactResult.name);
        assertEquals(contactDTO.email, contactResult.email);
        assertEquals(contactDTO.company, contactResult.company);
        assertEquals(contactDTO.jobtitle, contactResult.jobtitle);
        assertEquals(contactDTO.phone, contactResult.phone);
    }
    
    @Test
    public void testAddOpportunityToContact(){
        Opportunity opportunity = new Opportunity("desing", "10000", "07-3-2021");
        opportunity.setId((long)5);
        OpportunityDTO opportunityDTO = new OpportunityDTO(opportunity);
        long id = (long)c2.getId();
        
        OpportunityDTO opportunityDTOResult = facade.addOpportunityToContact(opportunityDTO, id);
        
        assertEquals(opportunityDTOResult.name, opportunity.getName());
        assertEquals("Chris", c2.getName());
    }

    @Test
    public void testGetOpportunitiesFromContact(){
        List<OpportunityDTO> opportunityDTOList = facade.getOpportunitiesFromContact(c1.getId());
        
        assertEquals(opportunityDTOList.size(), c1.getOpportunities().size());
    }
}
