package utils;

import entities.Contact;
import entities.Opportunity;
import entities.OpportunityStatus;
import entities.Role;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SetupTestUsers {

    public void setupUsers() {

        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        // IMPORTAAAAAAAAAANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // This breaks one of the MOST fundamental security rules in that it ships with default users and passwords
        // CHANGE the three passwords below, before you uncomment and execute the code below
        // Also, either delete this file, when users are created or rename and add to .gitignore
        // Whatever you do DO NOT COMMIT and PUSH with the real passwords
        User user = new User("user", "testuser");
        User admin = new User("admin", "testadmin");
        User both = new User("user_admin", "testuseradmin");
        Contact c1 = new Contact("Sebastian", "test@email.dk", "company", "koder", "12345678");
        Contact c2 = new Contact("Chris", "test@email.dk", "company", "koder", "12345678");
        Opportunity op1 = new Opportunity("salg1", "10000", "10-1-2021");
        Opportunity op2 = new Opportunity("salg2", "11000", "10-2-2023");
        Opportunity op3 = new Opportunity("salg3", "12000", "10-3-2024");
        OpportunityStatus active = new OpportunityStatus("active");
        OpportunityStatus won = new OpportunityStatus("won");
        OpportunityStatus lost = new OpportunityStatus("lost");
        OpportunityStatus inactive = new OpportunityStatus("inactive");

        if (admin.getUserPass().equals("test") || user.getUserPass().equals("test") || both.getUserPass().equals("test")) {
            throw new UnsupportedOperationException("You have not changed the passwords");
        }

        em.getTransaction().begin();
        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        op1.setOpportunityStatus(won);
        op2.setOpportunityStatus(inactive);
        op3.setOpportunityStatus(active);

        c1.addOpportunity(op1);
        c1.addOpportunity(op2);
        c2.addOpportunity(op3);

        user.addRole(userRole);
        admin.addRole(adminRole);

        both.addRole(userRole);
        both.addRole(adminRole);

        em.persist(userRole);
        em.persist(adminRole);
        em.persist(user);
        em.persist(admin);
        em.persist(both);
        em.persist(c1);
        em.persist(c2);

        em.getTransaction().commit();

        System.out.println("PW: " + user.getUserPass());
        System.out.println("Testing user with OK password: " + user.verifyPassword("testuser"));
        System.out.println("Testing user with wrong password: " + user.verifyPassword("forkert"));
        System.out.println("Created TEST Users");

    }

}
