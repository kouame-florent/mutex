/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;


import javax.annotation.PostConstruct;
import quantum.mutex.domain.entity.BaseEntity;


/**
 *
 * @author Florent
 * @param <T>
 */
public abstract class GenericEditEntityBacking<T extends BaseEntity> extends BaseBacking{
    
    public abstract void init();
    public abstract void viewAction();
    public abstract void persist();
    public abstract void close();
}
