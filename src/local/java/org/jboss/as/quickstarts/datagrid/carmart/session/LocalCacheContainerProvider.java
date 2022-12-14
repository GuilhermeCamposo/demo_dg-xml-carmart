/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.datagrid.carmart.session;

import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.util.concurrent.IsolationLevel;

/**
 * {@link CacheContainerProvider}'s implementation creating a DefaultCacheManager 
 * which is configured programmatically. Infinispan's libraries need to be bundled 
 * with the application - this is called "library" mode.
 * 
 * 
 * @author Martin Gencur
 * 
 */
@ApplicationScoped
public class LocalCacheContainerProvider extends CacheContainerProvider {
    private final Logger log = Logger.getLogger(this.getClass().getName());

   // private BasicCache<String, Object> cache;
    DefaultCacheManager  manager;

    public BasicCache<String, Object> getCache(String cacheName) {

        if (manager == null) {

            log.info("=== Using DefaultCacheManager (library mode) ===");
            GlobalConfiguration global = new GlobalConfigurationBuilder()
                .nonClusteredDefault() //Helper method that gets you a default constructed GlobalConfiguration, preconfigured for use in LOCAL mode
                .jmx().enable() //This method allows enables the jmx statistics of the global configuration.
                .build(); //Builds  the GlobalConfiguration object

              manager = new DefaultCacheManager(global, true);
        }

            Configuration builder = new ConfigurationBuilder()
                    .statistics().enable() //Enable JMX statistics
                    .clustering().cacheMode(CacheMode.LOCAL) //Set Cache mode to LOCAL - Data is not replicated.
                    .locking().isolationLevel(IsolationLevel.REPEATABLE_READ) //Sets the isolation level of locking
                    .build(); //Builds the Configuration object


        return  manager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache(cacheName, builder);
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}
