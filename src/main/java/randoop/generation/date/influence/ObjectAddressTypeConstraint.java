package randoop.generation.date.influence;

public class ObjectAddressTypeConstraint extends ObjectAddressConstraint {
	
	boolean obligatory = false;
	int address = -1;
	Class<?> type = null;
	
	public ObjectAddressTypeConstraint(boolean obligatory, int address, Class<?> type) {
		this.obligatory = obligatory;
		this.address = address;
		this.type = type;
	}
	
	public boolean IsObligatory() {
		return obligatory;
	}
	
	public int GetObjectAddress() {
		return address;
	}
	
	public Class<?> GetType() {
		return type;
	}
	
}
