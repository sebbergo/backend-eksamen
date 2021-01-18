/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Contact;
import entities.Opportunity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebas
 */
public class ContactDTO {
    public Long id;
    public String name;
    public String email;
    public String company;
    public String jobtitle;
    public String phone;

    public ContactDTO(Contact contact) {
        this.name = contact.getName();
        this.email = contact.getEmail();
        this.company = contact.getCompany();
        this.jobtitle = contact.getJobtitle();
        this.phone = contact.getPhone();
    }
}
