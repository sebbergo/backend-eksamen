/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author sebas
 */
@Entity
public class OpportunityStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "opportunityStatus")
    private List<Opportunity> opportunities;

    public OpportunityStatus() {
    }

    public OpportunityStatus(String name) {
        this.name = name;
        this.opportunities = new ArrayList();
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

    public List<Opportunity> getOpportunities() {
        return opportunities;
    }

    public void addOpportunity(Opportunity opportunity) {
        if (opportunity != null) {
            this.opportunities.add(opportunity);
            opportunity.setOpportunityStatus(this);
        }
    }

}
