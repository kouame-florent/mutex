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
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.user.domain.entity.User;

/**
 *
 * @author Florent
 */
@FacesConverter(value="UserConverter", managed=true)
public class UserConverter implements Converter<User>{
    
    @Inject UserDAO userDAO;

    @Override
    public User getAsObject(FacesContext fc, UIComponent uic, String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        try {
            return userDAO.findById(id).orElseGet(() -> null);
        }
        catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage("Invalid User ID"), e);
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, User user) {
         if(user == null){
            return "";
        }
        if(user.getUuid() != null){
            return user.getUuid();
        }
        else {
            throw new ConverterException(new FacesMessage("Invalid User ID"));
        }
    }
    
}
