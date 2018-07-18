public class Queen implements Comparable<Queen> {
	Point point;
	int strikes;

	public Queen(int row, int col) {
		point = new Point(row, col);
	}

	public Queen(Queen queen) {
		point = new Point(queen.point);
	}

	public int getRow() {
		return point.row;
	}

	public void setRow(int row) {
		point.row = row;
	}

	public int getColumn() {
		return point.col;
	}

	public void setColumn(int col) {
		point.col = col;
	}

	public String toString() {
		return "Q: " + point + ", strikes = " + strikes;
	}

	public int compareTo(Queen q) {
		if (point.row != q.point.row) return point.row - q.point.row;
		else return point.col - q.point.col;
	}
}