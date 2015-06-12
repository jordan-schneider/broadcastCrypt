import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class SingleThreadWithKeys {

	public static void main(String[] args) {
		BufferedWriter out;

		try {
			out = new BufferedWriter(new FileWriter(new File("summary.txt")));
			
			for (int n = 2; n < 8; n++) {
				for (int s = n - 1; s <= Math.round(Math.pow(2, n - 1)); s++) {
					for (int w = (s + n) - 1; w <= (Math.round(Math.pow(2, n)) - n - 1); w++) {
						out.write("n=" + n + " s=" + s + " w=" + w + " b=" + findBandwidth(n, s, w) + "\n");
					}
				}
			}
			
			
			System.out.println(findBandwidth(2, 1, 1));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int findBandwidth(int n, int s, int w) {
		BufferedWriter out;

		int minBandwidthForSet = Integer.MAX_VALUE;

		try {
			out = new BufferedWriter(new FileWriter(new File("data[" + n + "," + s + "," + w + "].txt")));

			Integer[] userSet = new Integer[n];
			for (int i = 0; i < n; i++) {
				userSet[i] = i;
			}

			Integer[] keySet = new Integer[w];
			for (int i = 0; i < w; i++) {
				keySet[i] = i;
			}

			ICombinatoricsVector<Integer> keyVector = Factory.createVector(keySet);
			Generator<Integer> blockGenerator = Factory.createSimpleCombinationGenerator(keyVector, s);
			ICombinatoricsVector<ICombinatoricsVector<Integer>> blockFamily = Factory.createVector(blockGenerator.generateAllObjects());
			Generator<ICombinatoricsVector<Integer>> keyAssignmentGenerator = Factory.createSimpleCombinationGenerator(blockFamily, n);

			// for each key assignment
			for (ICombinatoricsVector<ICombinatoricsVector<Integer>> keyAssignmentVector : keyAssignmentGenerator) {
				int maxBandwidth = 0;
				Integer[][] assignment = vectorToArray(keyAssignmentVector);

				boolean assignmentWorks = true;

				// for each sender in the assignment
				sender: for (int sender = 0; sender < assignment.length; sender++) {
					boolean[] revokedSets = new boolean[(int) Math.round(Math.pow(2, n - 1))];

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
					out.flush();
					out.close();
					return 1;
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return minBandwidthForSet;
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
