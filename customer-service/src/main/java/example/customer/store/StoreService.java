package example.customer.store;

import java.util.Collection;

import example.customer.config.CustomerProperties;
import example.customer.domain.Customer;
import example.customer.domain.Location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Stephane Nicoll
 */
@Service
public class StoreService {

	private final RestOperations restOperations;
	private final CustomerProperties customerProperties;

	@Autowired
	public StoreService(RestOperations restOperations, CustomerProperties customerProperties) {
		this.restOperations = restOperations;
		this.customerProperties = customerProperties;
	}

	public Collection<Store> fetchStoreNearbyFor(Customer customer, String distance) {
		Location location = customer.getAddress().getLocation();
		String locationParam = String.format("%s,%s",
				location.getLatitude(), location.getLongitude());


		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(customerProperties.getStore().getSearchUrlFor("findByAddressLocationNear"))
				.queryParam("size", 5)
				.queryParam("location", locationParam)
				.queryParam("distance", distance);
		RequestEntity<Void> request = RequestEntity
				.get(builder.build().encode().toUri())
				.accept(MediaTypes.HAL_JSON)
				.build();

		ResponseEntity<PagedResources<Store>> result = restOperations.exchange(request,
				new ParameterizedTypeReference<PagedResources<Store>>() {
				});

		return result.getBody().getContent();
	}

}
