package rest;

import com.nimbusds.jose.shaded.json.JSONObject;
import entities.Contact;
import entities.Opportunity;
import entities.OpportunityStatus;
import entities.Role;
import entities.User;
import facades.ContactFacade;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
//@Disabled

public class ContactEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private User user;
    private User admin;
    private User both;
    private OpportunityStatus active;
    private OpportunityStatus won;
    private OpportunityStatus lost;
    private OpportunityStatus inactive;
    private Opportunity op1;
    private Opportunity op2;
    private Opportunity op3;
    private Contact c1;
    private Contact c2;
    private static String securityToken;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    //Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
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
    
    private static void login(String userName, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", userName, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when().post("/login")
                .then()
                .extract().path("token");
    }

    @Test
    public void testServerIsUp() {
        given().when().get("/contact").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/contact/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello anonymous"));
    }

    @Test
    public void testCreateContact() throws Exception {
        JSONObject request = new JSONObject();
        request.put("name", "Oliver");
        request.put("email", "olizan@gmail.com");
        request.put("company", "svenskerland");
        request.put("jobtitle", "svensker");
        request.put("phone", "923821321");

        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(request.toJSONString())
                .when()
                .post("/contact/create")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());

    }

    @Test
    public void testGetAllContact() {
        given()
                .contentType("application/json")
                .get("/contact/all")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void testGetContact() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/contact/get/" + c1.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void testEditContact() throws Exception {
        JSONObject request = new JSONObject();
        request.put("id", "1");
        request.put("name", "Oliver");
        request.put("email", "olizan@gmail.com");
        request.put("company", "svenskerland");
        request.put("jobtitle", "svensker");
        request.put("phone", "923821321");

        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(request.toJSONString())
                .when()
                .put("/contact/edit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());

    }
    
    @Test
    public void testDeleteContact() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .delete("/contact/delete/" + c1.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }
    
    @Test
    public void testAddOpportunity(){
        JSONObject request = new JSONObject();
        request.put("name", "Danske Bank");
        request.put("amount", "2000000");
        request.put("closeDate", "03-04-2024");
        
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(request.toJSONString())
                .put("/contact/addOpportunity/" + c2.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }
    
    @Test
    public void testGetOpportunitiesFromContact() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/contact/getOpportunities/" + c1.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }
}
