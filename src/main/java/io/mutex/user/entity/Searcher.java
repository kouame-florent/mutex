/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "Searcher.findByLogin",
        query = "SELECT s FROM Searcher s WHERE s.login = :login"
    ),
//   @NamedQuery(
//        name = "Searcher.findBySpace",
//        query = "SELECT s FROM Searcher s WHERE s.group.space = :space"
//    ),
//   @NamedQuery(
//        name = "Searcher.findAllOrderBySpace",
//        query = "SELECT s FROM Searcher s ORDER BY s.name ASC "
//    ),
    
    @NamedQuery(
        name = "Searcher.findAllOrderByName",
        query = "SELECT s FROM Searcher s ORDER BY s.name ASC "
    ),
})
@Entity
public class Searcher extends User{

    private static final long serialVersionUID = 1L;

    public Searcher() {
    }

    public Searcher(String login, String password) {
        super(login, password);
    }
    
    
}
