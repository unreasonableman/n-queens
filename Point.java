public class Point {
	int row;
	int col;

	public Point(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public Point(Point p) {
		this.row = p.row;
		this.col = p.col;
	}

	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point op = (Point)o;
			return row == op.row && col == op.col;
		}

		return false;
	}

	public int hashCode() {
		return ("" + row + "" + col).hashCode();
	}

	public String toString() {
		return "(" + col + ", " + row + ")";
	}
}