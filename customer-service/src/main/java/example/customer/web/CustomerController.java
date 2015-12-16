package example.customer.web;

import java.util.Collection;

import example.customer.domain.Customer;
import example.customer.domain.CustomerRepository;
import example.customer.store.Store;
import example.customer.store.StoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Stephane Nicoll
 */
@RestController
public class CustomerController {

	private final CustomerRepository customerRepository;

	private final StoreService storeService;

	@Autowired
	public CustomerController(CustomerRepository customerRepository, StoreService storeService) {
		this.customerRepository = customerRepository;
		this.storeService = storeService;
	}

	@RequestMapping("/profile/{id}")
	public Profile showProfile(@PathVariable Long id) {
		Customer customer = customerRepository.findOne(id);
		Collection<Store> stores = storeService.fetchStoreNearbyFor(customer, "50km");

		return new Profile(customer, stores);
	}

	static class Profile {

		private final Customer customer;

		private final Collection<Store> nearestStores;

		public Profile(Customer customer, Collection<Store> nearestStores) {
			this.customer = customer;
			this.nearestStores = nearestStores;
		}

		public Customer getCustomer() {
			return customer;
		}

		public Collection<Store> getNearestStores() {
			return nearestStores;
		}
	}
}
