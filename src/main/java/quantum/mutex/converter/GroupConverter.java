/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.converter;

import java.util.UUID;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.entity.Group;

/**
 *
 * @author Florent
 */
@FacesConverter(forClass=Group.class, managed=true)
public class GroupConverter implements Converter<Group>{
    
    @Inject GroupDAO groupDAO;

    @Override
    public Group getAsObject(FacesContext fc, UIComponent uic, String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        try {
            return groupDAO.findById(UUID.fromString(id)).successValue();
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