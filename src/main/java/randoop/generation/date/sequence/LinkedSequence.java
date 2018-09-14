package randoop.generation.date.sequence;

import java.util.ArrayList;

import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.util.SimpleList;

public class LinkedSequence extends Sequence {
	
	PseudoSequenceContainer container = null;
	
	ArrayList<PseudoVariable> variables = null;
	
	public LinkedSequence(PseudoSequenceContainer container, SimpleList<Statement> statements, ArrayList<PseudoVariable> variables) {
		super(statements, computeHashcode(statements), computeNetSize(statements));
		this.container = container;
		this.variables = variables;
	}
	
	public PseudoSequenceContainer GetPseudoSequenceContainer() {
		return container;
	}
	
	public PseudoVariable GetPseudoVariable(int i) {
		return variables.get(i);
	}
	
}
