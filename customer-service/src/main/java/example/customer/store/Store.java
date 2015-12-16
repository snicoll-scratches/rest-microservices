package example.customer.store;

/**
 * Store representation.
 *
 * @author Stephane Nicoll
 */
public class Store {

	private String id;

	private String name;

	private Address address;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Store{" + "id='" + id + '\'' +
				", name='" + name + '\'' +
				", address=" + address +
				'}';
	}

	static class Address {
		private String street, city, zip;

		private Point location;

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}

		public Point getLocation() {
			return location;
		}

		public void setLocation(Point location) {
			this.location = location;
		}

		@Override
		public String toString() {
			return "{" + "street='" + street + '\'' +
					", city='" + city + '\'' +
					", zip='" + zip + '\'' +
					", location=" + location +
					'}';
		}
	}

	static class Point {
		private double x;

		private double y;

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		@Override
		public String toString() {
			return "{x=" + x + ", y=" + y + "}";
		}
	}

}
