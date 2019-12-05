/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web;

import java.util.UUID;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import io.mutex.repository.GroupDAO;
import io.mutex.domain.entity.Group;

/**
 *
 * @author Florent
 */
@FacesConverter(value="GroupConverter", managed=true)
public class GroupConverter implements Converter<Group>{

    @Inject GroupDAO groupDAO;

    @Override
    public Group getAsObject(FacesContext fc, UIComponent uic, String id) {
        
        if (id == null || id.isEmpty()) {
            return null;
        }
        try {
            return groupDAO.findById(id).orElseGet(() -> null);
        }
        catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage("Invalid group ID"), e);
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Group group) {
        
        if(group == null){
            return "";
        }
        if(group.getUuid() != null){
            return group.getUuid().toString();
        }
        else {
            throw new ConverterException(new FacesMessage("Invalid group ID"));
        }
    }
    
}
