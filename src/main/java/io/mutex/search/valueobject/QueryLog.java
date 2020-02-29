/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import java.util.Set;


public final class QueryLog {

    private final String user;
    private final Set<String> groups;
    private final String query;

    public QueryLog(String user, Set<String> groups, String query) {
        this.user = user;
        this.groups = groups;
        this.query = query;
    }
        

    public String getUser() {
        return user;
    }

   
    public Set<String> getGroups() {
        return groups;
    }

   
    public String getQuery() {
        return query;
    }

   
}
