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
package org.springframework.cloud.hypermedia;

import java.util.Optional;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A REST resource that is defined by a service reference and a traversal operation within that service.
 *
 * @author Oliver Gierke
 */
public class DiscoveredResource {

	private static final Logger logger = LoggerFactory.getLogger(DiscoveredResource.class);

	private final ServiceInstanceProvider provider;
	private final RestOperations restOperations = new RestTemplate();
	private final TraversalDefinition traversal;

	private Optional<Link> link = Optional.empty();

	public DiscoveredResource(ServiceInstanceProvider provider, TraversalDefinition traversal) {
		this.provider = provider;
		this.traversal = traversal;
	}

	public ServiceInstanceProvider getProvider() {
		return provider;
	}

	public RestOperations getRestOperations() {
		return restOperations;
	}

	public TraversalDefinition getTraversal() {
		return traversal;
	}

	public Optional<Link> getLink() {
		return link;
	}

	/**
	 * Verifies the link to the currently discovered resource or triggers rediscovery.
	 */
	@HystrixCommand(fallbackMethod = "markInavailable")
	public void verify() {
		this.link = link.map(it -> verify(it)).orElseGet(() -> discoverLink());
	}

	/**
	 * Resets the resource location to indicate unavailability. Used by Hystrix from {@link #verify()}.
	 */
	@SuppressWarnings("unused")
	private void markInavailable() {
		this.link = Optional.empty();
	}

	/**
	 * Verifies the given {@link Link} by issuing an HTTP HEAD request to the resource.
	 * 
	 * @param link must not be {@literal null}.
	 * @return
	 */
	private Optional<Link> verify(Link link) {

		Assert.notNull(link, "Link must not be null!");

		try {

			String uri = link.expand().getHref();

			logger.debug("Verifying link pointing to {}…", uri);
			restOperations.headForHeaders(uri);
			logger.debug("Successfully verified link!");

			return Optional.of(link);

		} catch (RestClientException o_O) {

			logger.debug("Verification failed, marking as outdated!");
			throw o_O;
		}
	}

	/**
	 * Discovers the resource location by executing the configured {@link TraversalDefinition} on the
	 * {@link ServiceInstance} provided through the {@link ServiceInstanceProvider}.
	 * 
	 * @return
	 */
	private Optional<Link> discoverLink() {

		ServiceInstance service = provider.getServiceInstance();

		logger.debug("Discovered {} system at {}. Discovering resource…", service.getServiceId(), service.getUri());

		Traverson traverson = new Traverson(service.getUri(), MediaTypes.HAL_JSON);
		Link link = traversal.buildTraversal(traverson).asTemplatedLink();

		logger.debug("Found link pointing to {}.", link.getHref());

		return Optional.of(link);
	}
}
