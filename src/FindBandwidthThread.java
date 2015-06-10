import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class FindBandwidthThread implements Runnable {
	int				n, s, w, b;
	BufferedWriter	summaryOut;

	public FindBandwidthThread(int n, int s, int w, BufferedWriter out) {
		this.n = n;
		this.s = s;
		this.w = w;
		this.summaryOut = out;
	}

	@Override
	public void run() {
		BufferedWriter out;

		int minBandwidthForSet = Integer.MAX_VALUE;

		try {
			out = new BufferedWriter(new FileWriter(new File("data[" + this.n + "," + this.s + "," + this.w + "].txt")));

			Integer[] userSet = new Integer[this.n];
			for (int i = 0; i < this.n; i++) {
				userSet[i] = i;
			}

			Integer[] keySet = new Integer[this.w];
			for (int i = 0; i < this.w; i++) {
				keySet[i] = i;
			}

			ICombinatoricsVector<Integer> keyVector = Factory.createVector(keySet);
			Generator<Integer> blockGenerator = Factory.createSimpleCombinationGenerator(keyVector, this.s);
			ICombinatoricsVector<ICombinatoricsVector<Integer>> blockFamily = Factory.createVector(blockGenerator.generateAllObjects());
			Generator<ICombinatoricsVector<Integer>> keyAssignmentGenerator = Factory.createSimpleCombinationGenerator(blockFamily, this.n);

			// for each key assignment
			top:
				for (ICombinatoricsVector<ICombinatoricsVector<Integer>> keyAssignmentVector : keyAssignmentGenerator) {
					int maxBandwidth = 0;
					Integer[][] assignment = vectorToArray(keyAssignmentVector);

					boolean assignmentWorks = true;

					// for each sender in the assignment
					sender: for (int sender = 0; sender < assignment.length; sender++) {
						boolean[] revokedSets = new boolean[(int) Math.round(Math.pow(2, this.n - 1))];

						ICombinatoricsVector<Integer> block = Factory.createVector(assignment[sender]);
						Generator<Integer> broadcastKeySetGenerator = Factory.createSubSetGenerator(block);

						// for each set of keys to broadcast with find the set of revoked users
						for (ICombinatoricsVector<Integer> broadcastKeySet : broadcastKeySetGenerator) {
							Integer[] revokedUsers = userSet.clone();

							// for each user remove that user if they share a key with the sender
							for (int i = 0; i < revokedUsers.length; i++) {
								searchForKey: for (int key = 0; key < assignment[i].length; key++) {
									if (broadcastKeySet.contains(assignment[i][key])) {
										revokedUsers[i] = -1;
										break searchForKey;
									}
								}
							}

							if (!revokedSets[convert(revokedUsers, sender)] && (broadcastKeySet.getSize() > maxBandwidth)) {
								maxBandwidth = broadcastKeySet.getSize();
							}
							revokedSets[convert(revokedUsers, sender)] = true;
						}

						for (int i = 0; i < revokedSets.length; i++) {
							if (!revokedSets[i]) {
								assignmentWorks = false;
								break sender;
							}
						}
					}
					if (assignmentWorks) {
						out.write(print(assignment) + ", " + maxBandwidth + "\n");
						if (maxBandwidth < minBandwidthForSet) {
							minBandwidthForSet = maxBandwidth;
						}
					}
					if (minBandwidthForSet == 1) {
						this.b = 1;
						break top;
					}
				}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.b = minBandwidthForSet;
		try {
			this.summaryOut.write("n=" + this.n + " s=" + this.s + " w=" + this.w + " b=" + this.b + "\n");
			System.out.println("n=" + this.n + " s=" + this.s + " w=" + this.w + " b=" + this.b);
			this.summaryOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String print(Integer[][] assignment) {
		String out = "[";
		for (Integer[] element : assignment) {
			out = out + Arrays.toString(element);
		}
		out = out + "]";
		return out;
	}

	private static Integer[][] vectorToArray(ICombinatoricsVector<ICombinatoricsVector<Integer>> vector) {
		Integer[][] array = new Integer[vector.getSize()][vector.getValue(0).getSize()];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				array[i][j] = vector.getValue(i).getValue(j);
			}
		}
		return array;
	}

	private static int convert(Integer[] revokedUsers, int sender) {
		int index = 0;
		int currentPower = 1;
		for (int i = 0; i < revokedUsers.length; i++) {
			if (i != sender) {
				if (revokedUsers[i] != -1) {
					index += currentPower;
				}
				currentPower *= 2;
			}
		}
		return index;
	}
}
