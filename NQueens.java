import java.io.*;
import java.util.*;

// attempt to find a solution for N x N queen problems. Initialize the board so that
// each queen is in a different column and a different row. Use minimal-conflict heuristic
// to update board at each step. Check if number of conflicts is zero. If so, done. Repeat.
//
// This code often gets stuck in repeating board patterns, but sometimes finds solutions
// pretty quickly. It found a 50 x 50 solution in 358 steps.
public class NQueens {
	private static final byte EMPTY = (byte)0;
	private static final byte QUEEN = (byte)1;

	private byte[] column;

	public NQueens(int n) {
		column = new byte[n];

		List<Queen> queenList = new ArrayList<Queen>();
		List<Integer> rowList = new ArrayList<Integer>();
		int i;

		for (i=0; i<n; i++) {
			rowList.add(new Integer(i));
		}

		for (i=0; i<n; i++) {
			queenList.add(new Queen(removeRandomItem(rowList), i));
		}

		dumpBoard(queenList);

		for (i=0; i<Integer.MAX_VALUE; i++) {
			evaluate(queenList);

			if (solutionIsValid(queenList)) {
				break;
			}

			rearrange(queenList);
		}

		System.out.println("\nsteps: " + i + "\n");
		dumpBoard(queenList);
	}

	private void dumpBoard(List<Queen> list) {
		if (list.isEmpty()) return;

		List<Queen> slist = cloneList(list);
		Collections.sort(slist);
		int n = list.size();

		for (int y=0; y<n; y++) {

			for (int x=0; x<n; x++) {
				Queen queen = null;

				if (!slist.isEmpty()) {
					queen = slist.get(0);
				}

				if (queen != null && queen.getColumn() == x && queen.getRow() == y) {
					System.out.print("Q");
					slist.remove(0);
				} else {
					System.out.print(".");
				}
			}

			System.out.println();
		}

		System.out.println();
	}

	// evaluate a board position. Start by creating a list of all distinct pairs.
	// Queen pairs that attack each other and queen triplets that are colinear accumulate strikes
	private void evaluate(List<Queen> list) {
		for (Queen q : list) {
			q.strikes  = 0;
		}

		List<Line> lineList = enumerateLines(list);

		for (Line line : lineList) {

			if (line.isAttackLine()) {
				line.q1.strikes++;
				line.q2.strikes++;
			} else {
				for (Queen q3 : list) {
					if (q3.point.equals(line.q1.point) || q3.point.equals(line.q2.point)) continue;

					if (line.isColinear(q3)) {
						line.q1.strikes++;
						line.q2.strikes++;
						q3.strikes++;
					}
				}
			}
		}
	}

	private List<Queen> cloneList(List<Queen> list) {
		List<Queen> result = new ArrayList<Queen>();

		for (Queen q : list) {
			result.add(new Queen(q));
		}

		return result;
	}

	// create a list of boards based on an input board, one board for each available row value
	// for max strike queen
	private List<List<Queen>> createColumnOptions(List<Queen> list, int obstructingQueenIndex) {
		Queen queen, obstructingQueen;
		int i;

		List<List<Queen>> result = new ArrayList<List<Queen>>();

		obstructingQueen = list.get(obstructingQueenIndex);

		for (i=0; i<list.size(); i++) {
			queen = list.get(i);

			if (queen.getColumn() == obstructingQueen.getColumn()) {
				column[queen.getRow()] = QUEEN;
			}
		}

		for (i=0; i<column.length; i++) {
			if (column[i] == EMPTY) {
				List<Queen> ql = cloneList(list);
				ql.get(obstructingQueenIndex).setRow(i);
				result.add(ql);
			}
		}

		return result;
	}

	// change the board by moving the queen with the most strikes to
	// a square in the same column that has the least strikes
	private void rearrange(List<Queen> list) {
		int obstructingQueenIndex = -1;
		int i, maxStrikes;
		Queen queen;

		for (i=0; i<column.length; i++) {
			column[i] = EMPTY;
		}

		maxStrikes = -1;

		// find the highest number of strikes
		for (i=0; i<list.size(); i++) {
			queen = list.get(i);

			if (queen.strikes > maxStrikes) {
				maxStrikes = queen.strikes;
			}
		}

		List<Queen> maxList = new ArrayList<Queen>();

		// create a list of queens that have maxStrikes strikes.
		// This list will often have few or even just one element.
		for (Queen q : list) {
			if (q.strikes == maxStrikes) {
				maxList.add(q);
			}
		}

		// randomly choose one list element. This helps reduce the
		// chance of the code getting trapped in a loop of recurring
		// steps
		int index = (int)(Math.random() * maxList.size());
		queen = maxList.get(index);

		for (i=0; i<list.size(); i++) {
			Queen q = list.get(i);

			if (q == queen) {
				obstructingQueenIndex = i;
				break;
			}
		}

		// create a list of boards that enumerates all possible row positions for the
		// queen with the most strikes
		List<List<Queen>> optionList = createColumnOptions(list, obstructingQueenIndex);

		for (List<Queen> option : optionList) {
			evaluate(option);
		}

		int listIndex = -1;
		int minStrikes = list.size() + 1;

		// iterate over the variants of the max strike queen in the board versions
		// and find the lowest strike count
		for (i=0; i<optionList.size(); i++) {
			List<Queen> option = optionList.get(i);
			queen = option.get(obstructingQueenIndex);

			if (queen.strikes < minStrikes) {
				minStrikes = queen.strikes;
			}
		}

		List<List<Queen>> minList = new ArrayList<List<Queen>>();

		// create list of all queens with that strike value
		for (List<Queen> option : optionList) {
			queen = option.get(obstructingQueenIndex);

			if (queen.strikes == minStrikes) {
				minList.add(option);
			}
		}

		index = (int)(Math.random() * minList.size());

		// randomly choose one queen from that list
		for (i=0; i<optionList.size(); i++) {
			if (optionList.get(i) == minList.get(index)) {
				listIndex = i;
				break;
			}
		}

		// update row of max strike queen in original board
		queen = list.get(obstructingQueenIndex);
		queen.setRow(optionList.get(listIndex).get(obstructingQueenIndex).getRow());
	}

	private boolean solutionIsValid(List<Queen> list) {
		for (Queen q : list) {
			if (q.strikes > 0) return false;
		}

		return true;
	}

	private List<Line> enumerateLines(List<Queen> list) {
		List<Line> result = new ArrayList<Line>();

		for (int i=0; i<list.size()-1; i++) {
			for (int j=i+1; j<list.size(); j++) {
				result.add(new Line(list.get(i), list.get(j)));
			}
		}

		return result;
	}

	private int removeRandomItem(List<Integer> list) {
		if (list.isEmpty()) return -1;

		int index = (int)(Math.random() * list.size());
		return list.remove(index).intValue();
	}

	private static void usage() {
		System.err.println("usage: NQueens <n>");
		System.exit(-1);
	}

	public static void main(String[] arg) {
		if (arg.length == 0) usage();

		int n = -1;

		try {
			n = Integer.parseInt(arg[0]);
		} catch (NumberFormatException e) {
			usage();
		}

		if (n < 0) {
			usage();
		}

		new NQueens(n);
	}
}