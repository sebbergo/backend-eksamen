/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

/**
 *
 * @author sebas
 */
public class CountriesDTO {
    
    public String Country;
    public String Slug;

    public CountriesDTO(String Country, String Slug) {
        this.Country = Country;
        this.Slug = Slug;
    }

}
