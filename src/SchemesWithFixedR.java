import java.util.List;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

public class SchemesWithFixedR {
	public static void main(String[] args) {
		int r = 2;
		for (int c = 1; c < 16; c++) {
			for (int n = 4; n < Math.pow(2, 10); n *= 2) {
				System.out.println("For n=" + n + " and r=" + r + " and c=" + c + " the minimum schemes are " + findAssignments(n, r, c));
			}
		}

	}

	public static int findAssignments(int n, int r, int c) {
		int currentMax = (c + n) - 1;
		boolean found = false;

		while (!found) {
			Integer[] set = new Integer[currentMax];
			for (int i = 0; i < currentMax; i++) {
				set[i] = i;
			}

			ICombinatoricsVector<Integer> vectorOfSchemes = Factory.createVector(set);
			Generator<Integer> genPossibilities = Factory.createSimpleCombinationGenerator(vectorOfSchemes, c);

			List<ICombinatoricsVector<Integer>> possibilities = genPossibilities.generateAllObjects();
			ICombinatoricsVector<ICombinatoricsVector<Integer>> vectorOfAssignments = Factory.createVector(possibilities);
			Generator<ICombinatoricsVector<Integer>> genAssignments = Factory.createSimpleCombinationGenerator(vectorOfAssignments, n);

			for (ICombinatoricsVector<ICombinatoricsVector<Integer>> assignment : genAssignments) {
				if (isValid(assignment, r)) {
					return currentMax;
				}
			}

			System.err.println("Current Maximum number of schemes is " + currentMax);
			currentMax++;
		}

		return -1;
	}

	private static boolean isValid(ICombinatoricsVector<ICombinatoricsVector<Integer>> assignment, int r) {
		int[][] assignments = new int[assignment.getSize()][assignment.getValue(0).getSize()];
		int outerCounter = 0;
		for (ICombinatoricsVector<Integer> possibility : assignment) {
			int innerCounter = 0;
			for (Integer value : possibility) {
				assignments[outerCounter][innerCounter] = value;
				innerCounter++;
			}
			outerCounter++;
		}
		return isValid(assignments, r);
	}

	private static boolean isValid(int[][] assignments, int r) {
		boolean valid = true;

		a: for (int i = 0; i < assignments.length; i++) {
			for (int first = 0; first < assignments.length; first++) {
				if (first == i) {
					continue;
				}
				for (int second = first + 1; second < assignments.length; second++) {
					if (second == i) {
						continue;
					}
					if (isSubset(assignments[i], union(assignments[first], assignments[second]))) {
						valid = false;
						break a;
					}
				}
			}
		}

		return valid;
	}

	private static boolean isSubset(int[] cs, int[] union) {
		boolean isSubset = true;
		for (int element : cs) {
			boolean isThisIn = false;
			for (int element2 : union) {
				if (element == element2) {
					isThisIn = true;
				}
			}
			if (!isThisIn) {
				isSubset = false;
				break;
			}
		}
		return isSubset;
	}

	private static int[] union(int[] assignments, int[] assignments2) {
		int[] union = new int[assignments.length + assignments2.length];
		for (int i = 0; i < assignments.length; i++) {
			union[i] = assignments[i];
		}
		int counter = assignments.length;
		for (int i = 0; i < assignments2.length; i++) {
			if (!isSubset(assignments2[i], union)) {
				union[counter] = assignments2[i];
				counter++;
			}
		}
		return union;
	}

	private static boolean isSubset(int c, int[] union) {
		int[] first = new int[1];
		first[0] = c;
		return isSubset(first, union);
	}
}
