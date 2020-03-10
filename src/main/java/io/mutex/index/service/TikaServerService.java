/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.core.Response;

/**
 *
 * @author florent
 */
public interface TikaServerService {

    Optional<Response> getContent(InputStream inputStream, Map<String, String> metas);

    Optional<Response> getMetas(InputStream inputStream);
    
}
