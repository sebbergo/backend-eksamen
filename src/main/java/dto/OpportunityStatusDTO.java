/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Opportunity;
import entities.OpportunityStatus;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebas
 */
public class OpportunityStatusDTO {
    public String name;

    public OpportunityStatusDTO(OpportunityStatus opportunityStatus) {
        this.name = opportunityStatus.getName();
    }
    
}
