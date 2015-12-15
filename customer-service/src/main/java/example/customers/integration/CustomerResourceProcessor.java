/*
 * Copyright 2014 the original author or authors.
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
package example.customers.integration;

import example.customers.Customer;
import example.customers.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.hypermedia.DiscoveredResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Gierke
 */
@Component
public class CustomerResourceProcessor implements ResourceProcessor<Resource<Customer>> {

	private final DiscoveredResource storesByLocationResource;

	@Autowired
	public CustomerResourceProcessor(DiscoveredResource storesByLocationResource) {
		this.storesByLocationResource = storesByLocationResource;
	}

	@Override
	public Resource<Customer> process(Resource<Customer> resource) {

		Customer customer = resource.getContent();
		Location location = customer.getAddress().getLocation();

		Optional<Link> link = storesByLocationResource.getLink();

		link.ifPresent(it -> {

			if (location == null) {
				return;
			}

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("location", String.format("%s,%s", location.getLatitude(), location.getLongitude()));
			parameters.put("distance", "50km");

			resource.add(it.expand(parameters).withRel("stores-nearby"));
		});

		return resource;
	}
}
