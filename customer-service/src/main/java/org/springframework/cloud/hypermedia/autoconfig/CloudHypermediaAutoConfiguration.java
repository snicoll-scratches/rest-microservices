/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.hypermedia.autoconfig;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.hypermedia.DiscoveredResource;
import org.springframework.cloud.hypermedia.DiscoveredResourceRefresher;
import org.springframework.cloud.hypermedia.autoconfig.CloudHypermediaAutoConfiguration.CloudHypermediaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Registers a default {@link DiscoveredResourceRefresher} if at least one {@link DiscoveredResource} is declared in the
 * system and applies verification timings defined in the application properties.
 * 
 * @author Oliver Gierke
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(CloudHypermediaProperties.class)
public class CloudHypermediaAutoConfiguration {

	@Autowired(required = false) List<DiscoveredResource> discoveredResources = Collections.emptyList();
	@Autowired CloudHypermediaProperties properties;

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(type = "org.springframework.cloud.hypermedia.DiscoveredResource")
	public DiscoveredResourceRefresher discoveredResourceRefresher() {
		return new DiscoveredResourceRefresher(properties.getRefresh().getFixedDelay(),
				properties.getRefresh().getInitialDelay());
	}

	@ConfigurationProperties(prefix = "cloud.hypermedia")
	public static class CloudHypermediaProperties {

		private Refresh refresh = new Refresh();

		public Refresh getRefresh() {
			return refresh;
		}

		public static class Refresh {

			private int fixedDelay = 5000;
			private int initialDelay = 10000;

			public int getFixedDelay() {
				return fixedDelay;
			}

			public void setFixedDelay(int fixedDelay) {
				this.fixedDelay = fixedDelay;
			}

			public int getInitialDelay() {
				return initialDelay;
			}

			public void setInitialDelay(int initialDelay) {
				this.initialDelay = initialDelay;
			}
		}
	}
}
