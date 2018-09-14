package randoop.generation.date.influence;

public class ObjectAddressSameConstraint extends ObjectAddressConstraint {
	
	int address1 = -1;
	int address2 = -1;
	
	public ObjectAddressSameConstraint(int address1, int address2) {
		this.address1 = address1;
		this.address2 = address2;
	}
	
	public int GetAddressOne() {
		return address1;
	}
	
	public int GetAddressTwo() {
		return address2;
	}
	
}
