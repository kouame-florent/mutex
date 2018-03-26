/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Florent
 */
@Table(name = "group")
@Entity
public class Group extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
}
