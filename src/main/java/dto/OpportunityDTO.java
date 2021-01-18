/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Opportunity;
import entities.OpportunityStatus;

/**
 *
 * @author sebas
 */
public class OpportunityDTO {
    public String name;
    public String amount;
    public String closeDate;
    public OpportunityStatus opportunityStatus;

    public OpportunityDTO(Opportunity opportunity) {
        this.name = opportunity.getName();
        this.amount = opportunity.getAmount();
        this.closeDate = opportunity.getCloseDate();
        this.opportunityStatus = opportunity.getOpportunityStatus();
    }
}
