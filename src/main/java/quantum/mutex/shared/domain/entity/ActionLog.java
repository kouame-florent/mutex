/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.shared.domain.entity;

import quantum.mutex.user.domain.entity.BusinessEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Florent
 */
@Table(name = "mx_action_log")
@Entity
public class ActionLog extends BusinessEntity{
    
}
