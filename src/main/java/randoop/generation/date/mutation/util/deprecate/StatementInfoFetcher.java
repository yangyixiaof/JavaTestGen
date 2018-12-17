package randoop.generation.date.mutation.util.deprecate;

import randoop.generation.date.sequence.TraceableSequence;

public class StatementInfoFetcher {
	
	public static int FetchVariableDeclareStatementIndex(TraceableSequence sequence, int statement_index, int variable_index) {
		int variable_declare_statement_index = sequence.getStatement(statement_index).getInputs().get(variable_index).index + statement_index;
		return variable_declare_statement_index;
	}
	
}
