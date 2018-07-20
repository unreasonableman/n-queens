public class NQueens2 {
	private int[] xyList;

	// place queens on board avoiding both direct attacks and
	// instances of 3 or more colinear queens. One queen is placed per
	// row. Backtracking occurs when a queen can't be placed in a row.
	public NQueens2(int n) {
		long then = System.currentTimeMillis();

		AttackMap attackMap = new AttackMap(n);
		int[] queenIndexList = new int[n];
		int currentQueenIndex = 0;
		int i, x;

		xyList = new int[(n - 1) * n * 2];

		for (i=0; i<n; i++) {
			queenIndexList[i] = -1;
		}

		for (;;) {
			if (currentQueenIndex >= n) break;

			x = ++queenIndexList[currentQueenIndex];

			if (x >= n) {
				queenIndexList[currentQueenIndex] = -1;

				if (currentQueenIndex == 0) break;

				currentQueenIndex--;
				x = queenIndexList[currentQueenIndex];
				attackMap.removeLastPiece(x, currentQueenIndex);
			} else {
				if (attackMap.isAvailable(x, currentQueenIndex) && !hasColinearPoints(queenIndexList, currentQueenIndex + 1, n)) {
					attackMap.placePiece(x, currentQueenIndex);
					currentQueenIndex++;
				}
			}
		}

		if (currentQueenIndex == 0) {
			System.out.println("no solution found");
		} else {
			System.out.println("solution found in " + (System.currentTimeMillis() - then) + " ms.");
			dumpBoard(queenIndexList);
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

	public static void main(String[] arg) {
		new NQueens2(Integer.parseInt(arg[0]));
	}
}