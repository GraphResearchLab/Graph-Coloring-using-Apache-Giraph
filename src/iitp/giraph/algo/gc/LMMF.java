package iitp.giraph.algo.gc;

import java.io.IOException;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.LongWritable;

public class LMMF
		extends
		BasicComputation<LongWritable, LongWritable, LongWritable, LongWritable> {

	public static long savemax = Long.MAX_VALUE;
	public static long count = 0;

	@Override
	public void compute(
			Vertex<LongWritable, LongWritable, LongWritable> vertex,
			Iterable<LongWritable> msg) throws IOException {
		boolean MaxVal = true;
		boolean MinVal = true;
		if (getSuperstep() == 0) {
			vertex.setValue(new LongWritable(-1));
			/*
			 * for(Edge<LongWritable, LongWritable> edge : vertex.getEdges()) {
			 * sendMessage(edge.getTargetVertexId(), new
			 * LongWritable(vertex.getId().get())); }
			 */
			sendMessageToAllEdges(vertex,
					new LongWritable(vertex.getId().get()));
		} else {
			if (vertex.getValue().get() == -1) {
				for (LongWritable message : msg) {
					if (vertex.getId().get() < message.get()) {
						MaxVal = false;
					}
					if (vertex.getId().get() > message.get()) {
						MinVal = false;
					}
				}
				if (MaxVal == true) {
					vertex.setValue(new LongWritable(2 * getSuperstep() - 1));
				} else if (MinVal == true) {
					vertex.setValue(new LongWritable(2 * getSuperstep()));
				} else {
					/*
					 * for (Edge<LongWritable, LongWritable> edge :
					 * vertex.getEdges()) {
					 * sendMessage(edge.getTargetVertexId(), new
					 * LongWritable(vertex.getId().get())); }
					 */
					sendMessageToAllEdges(vertex, new LongWritable(vertex
							.getId().get()));
				}

			}
		}

		vertex.voteToHalt();
	}
}
