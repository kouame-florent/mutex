/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.user.entity.Group;
import io.mutex.user.service.GroupService;
import io.mutex.user.valueobject.ContextIdParamKey;
import java.io.Serializable;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author root
 */
@Named(value = "deleteGroupBacking")
@ViewScoped
public class DeleteGroupBacking extends QuantumDeleteBacking<Group> implements Serializable{
    
    @Inject GroupService groupService;
    
    @PostConstruct
    @Override
    protected void postConstruct() {
        iniCtxtParamKey(ContextIdParamKey.GROUP_UUID);
    }
    
    @Override
    public void delete() {
        Optional.ofNullable(entityUUID)
                .flatMap(groupService::findByUuid)
                .ifPresent(groupService::delete);
        closeDeleteView();
    }

}
