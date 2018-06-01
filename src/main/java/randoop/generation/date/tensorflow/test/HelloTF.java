package randoop.generation.date.tensorflow.test;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.Tensors;

public class HelloTF {

	public static void main(String[] args) throws Exception {
		try (Graph g = new Graph()) {
			final String value = "Hello from " + TensorFlow.version();
			// Construct the computation graph with a single operation, a constant
			// named "MyConst" with a value "value".
//			System.err.println(t.getClass().toString());
			Tensor<?> t = Tensors.create(value.getBytes("UTF-8"));
//			System.err.println(t.getClass().getGenericSuperclass());
			// The Java API doesn't yet include convenience functions for adding operations.
			g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
//			Output<?> op_out = op.output(0);
//			Output<Object> sub_out = g.opBuilder("Sub", "test_sub_1").addInput(op_out).addInput("y", 1).build().output(0);
			// Execute the "MyConst" operation in a Session.
			try (Session s = new Session(g); Tensor<?> output = s.runner().fetch("MyConst").run().get(0)) {
				System.out.println(new String(output.bytesValue(), "UTF-8")); // output.floatValue()
			}
		}
	}

}
