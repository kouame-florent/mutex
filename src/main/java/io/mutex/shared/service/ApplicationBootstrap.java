/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.shared.service;

import javax.annotation.PostConstruct;

/**
 *
 * @author florent
 */
public interface ApplicationBootstrap {

    void createDefaultRoles();

    @PostConstruct  void init();
    
}
