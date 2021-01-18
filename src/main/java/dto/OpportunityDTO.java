/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Opportunity;

/**
 *
 * @author sebas
 */
public class OpportunityDTO {
    public long id;
    public String name;
    public String amount;
    public String closeDate;

    public OpportunityDTO(Opportunity opportunity) {
        this.id = opportunity.getId();
        this.name = opportunity.getName();
        this.amount = opportunity.getAmount();
        this.closeDate = opportunity.getCloseDate();
    }
}
