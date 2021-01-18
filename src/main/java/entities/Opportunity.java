/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author sebas
 */
@Entity
public class Opportunity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String amount;
    private String closeDate;
    
    @ManyToOne
    private Contact contact;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    private OpportunityStatus opportunityStatus;

    public Opportunity() {
    }

    public Opportunity(String name, String amount, String closeDate) {
        this.name = name;
        this.amount = amount;
        this.closeDate = closeDate;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public OpportunityStatus getOpportunityStatus() {
        return opportunityStatus;
    }

    public void setOpportunityStatus(OpportunityStatus opportunityStatus) {
        this.opportunityStatus = opportunityStatus;
    }
    
}
