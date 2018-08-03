import java.util.ArrayList;
import java.util.List;

public class NQueens2 {
	private static final String N_ARG = "-n";
	private static final String RESULT_ARG = "-display-results";
	private static final String COLINEAR_ARG = "-allow-colinear-boards";
	private static final String SOLUTION_ARG = "-all-solutions";

	private static boolean displayResults;
	private static boolean colinearBoards;
	private static boolean fundamentalSolutions = true;

	private int[] xyList;
	private List<int[]> solutionList;
	private int[] outList;

	// place queens on board avoiding both direct attacks and
	// instances of 3 or more colinear queens. One queen is placed per
	// row. Backtracking occurs when a queen can't be placed in a row.
	public NQueens2(int n) {
		long then = System.currentTimeMillis();

		solutionList = new ArrayList<int[]>();
		outList = new int[n];

		AttackMap attackMap = new AttackMap(n);
		int[] queenIndexList = new int[n];
		int currentQueenIndex = 0;
		int i, x;

		xyList = new int[(n - 1) * n * 2];

		for (i=0; i<n; i++) {
			queenIndexList[i] = -1;
		}

		for (;;) {
			if (currentQueenIndex >= n) {
				//dumpBoard(queenIndexList);
				checkSolutionForUniqueness(queenIndexList);

				currentQueenIndex--;
				x = queenIndexList[currentQueenIndex];
				attackMap.removeLastPiece(x, currentQueenIndex);
			}

			x = ++queenIndexList[currentQueenIndex];

			if (x >= n) {
				queenIndexList[currentQueenIndex] = -1;

				if (currentQueenIndex == 0) break;

				currentQueenIndex--;
				x = queenIndexList[currentQueenIndex];
				attackMap.removeLastPiece(x, currentQueenIndex);
			} else {
				if (attackMap.isAvailable(x, currentQueenIndex) && (colinearBoards || !hasColinearPoints(queenIndexList, currentQueenIndex + 1, n))) {
					attackMap.placePiece(x, currentQueenIndex);
					currentQueenIndex++;
				}
			}
		}

		System.out.println(solutionList.size() + " solutions for " + n + " queens found in " + (System.currentTimeMillis() - then) + " ms.\n");

		if (displayResults) {
			for (int[] solution : solutionList) {
				dumpBoard(solution);
			}
		}
	}

	private boolean indexListsEqual(int[] list1, int[] list2) {
		if (list1.length != list2.length) return false;

		for (int i=0; i<list1.length; i++) {
			if (list1[i] != list2[i]) return false;
		}

		return true;
	}

	private void dumpIndexList(String prefix, int[] list) {
		System.out.print(prefix);

		for (int n : list) {
			System.out.print(n + " ");
		}

		System.out.println();
	}

	private boolean doesRotationSolve(int[] list, int[] solution) {
		if (indexListsEqual(solution, list)) {
			return true;
		}

		flipIndexListHorizontally(list, outList);

		if (indexListsEqual(solution, outList)) {
			return true;
		}

		flipIndexListVertically(list, outList);

		if (indexListsEqual(solution, outList)) {
			return true;
		}

		return false;
	}

	// x' = n - 1 - y
	// y' = x
	private void rotateIndexListClockWise(int[] inList, int[] outList) {
		//System.out.println("NQueens2.rotateIndexListClockWise()");
		//dumpIndexList("- inList: ", inList);

		for (int i=0; i<inList.length; i++) {
			outList[inList[i]] = inList.length - 1 - i;
		}

		//dumpIndexList("- outList: ", outList);
	}

	private void flipIndexListVertically(int[] inList, int[] outList) {
		for (int i=0; i<inList.length; i++) {
			outList[i] = inList[inList.length - 1 - i];
		}
	}

	private void flipIndexListHorizontally(int[] inList, int[] outList) {
		for (int i=0; i<inList.length; i++) {
			outList[i] = inList.length - 1 - inList[i];
		}
	}

	private void checkSolutionForUniqueness(int[] source) {
		//System.out.println("NQueens2.checkSolutionForUniqueness()");
		//System.out.println("- solutionList.size(): " + solutionList.size());

		int[] list = new int[source.length];
		System.arraycopy(source, 0, list, 0, source.length);

		if (fundamentalSolutions) {
			int[] tmp;


			//dumpIndexList("- list: ", list);

			for (int[] solution : solutionList) {
				//dumpIndexList("-- solution: ", solution);

				for (int i=0; i<4; i++) {
					if (doesRotationSolve(solution, list)) {
						return;
					}

					if (i == 3) break;

					rotateIndexListClockWise(list, outList);

					tmp = list;
					list = outList;
					outList = tmp;
				}
			}

			//System.out.println("* rotations complete");
			//dumpIndexList("- list: ", list);

			solutionList.add(list);
		} else {
			for (int[] solution : solutionList) {
				if (indexListsEqual(solution, list)) return;
			}

			solutionList.add(list);
		}
	}

	private boolean hasColinearPoints(int[] list, int count, int n) {
		int index = 0;
		int i, j;

		// enumerate all distinct point pairs
		for (i=0; i<count; i++) {
			for (j=i+1; j<count; j++) {
				xyList[4 * index + 0] = list[i];
				xyList[4 * index + 1] = i;
				xyList[4 * index + 2] = list[j];
				xyList[4 * index + 3] = j;

				index++;
			}
		}

		// for all point pairs check if any of the remaining
		// points on the board is colinear
		for (i=0; i<index; i++) {
			int x1 = xyList[4 * i + 0];
			int y1 = xyList[4 * i + 1];
			int x2 = xyList[4 * i + 2];
			int y2 = xyList[4 * i + 3];

			for (j=0; j<count; j++) {
				int x = list[j];
				int y = j;

				if ((x == x1 && y == y1) || (x == x2 && y == y2)) {
					continue;
				}

				if (isColinear(x1, y1, x2, y2, x, y)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isColinear(int x1, int y1, int x2, int y2, int x3, int y3) {
		// fractional slope equation rewritten to avoid divide by 0 issue
		// for vertical lines
		return (y2 - y1) * (x3 - x2) == (y3 - y2) * (x2 - x1);
	}


	private void dumpBoard(int[] queenIndexList) {
		int n = queenIndexList.length;

		for (int y=0; y<n; y++) {
			for (int x=0; x<n; x++) {
				if (queenIndexList[y] == x) {
					System.out.print("Q");
				} else {
					System.out.print(".");
				}
			}

			System.out.println();
		}

		System.out.println();
	}

	private static void usage() {
		System.err.println("NQueens2 [-display-results] [-n <#>] [-all-solutions] [-allow-colinear-boards]");
		System.exit(-1);
	}

	public static void main(String[] arg) {
		int n = 8;

		for (int i=0; i<arg.length; i++) {
			if (arg[i].equals(N_ARG) && arg.length > i + 1) {
				n = Integer.parseInt(arg[++i]);
				continue;
			}

			if (arg[i].equals(RESULT_ARG)) {
				displayResults = true;
				continue;
			}

			if (arg[i].equals(COLINEAR_ARG)) {
				colinearBoards = true;
				continue;
			}

			if (arg[i].equals(SOLUTION_ARG)) {
				fundamentalSolutions = false;
				continue;
			}

			usage();
		}

		//Debug.log("- fundamentalSolutions: " + fundamentalSolutions);
		//Debug.log("- colinearBoards: " + colinearBoards);
		//Debug.log("- displayResults: " + displayResults);

		new NQueens2(n);
	}
}
