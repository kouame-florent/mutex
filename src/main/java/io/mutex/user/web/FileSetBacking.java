/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.index.entity.Inode;
import io.mutex.shared.valueobject.ContextIdParamKey;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author florent
 */
@Named(value = "tenantBacking")
@ViewScoped
public class FileSetBacking extends QuantumBaseBacking{

    private final ContextIdParamKey tenantParamKey = ContextIdParamKey.GROUP_UUID;
   
}
