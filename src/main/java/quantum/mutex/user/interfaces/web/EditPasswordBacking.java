/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.interfaces.web;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import quantum.mutex.shared.interfaces.web.BaseBacking;

/**
 *
 * @author Florent
 */
@Named(value = "editPasswordBacking")
@ViewScoped
public class EditPasswordBacking extends BaseBacking implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
}
