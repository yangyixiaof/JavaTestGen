package randoop.generation.date.sequence;

import java.util.ArrayList;

import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.util.SimpleList;

public class LinkedSequence extends Sequence {
	
	ArrayList<PseudoVariable> variables = null;
	
	public LinkedSequence(SimpleList<Statement> statements, ArrayList<PseudoVariable> variables) {
		super(statements, computeHashcode(statements), computeNetSize(statements));
		this.variables = variables;
	}
	
	public PseudoVariable GetPseudoVariable(int i) {
		return variables.get(i);
	}
	
}
