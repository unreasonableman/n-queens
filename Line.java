public class Line {
	Queen q1;
	Queen q2;

	public Line(Queen q1, Queen q2) {
		this.q1 = q1;
		this.q2 = q2;
	}

	// queen attack lines are vertical, horizontal and diagonal
	public boolean isAttackLine() {
		if (q1.point.col == q2.point.col || q1.point.row == q2.point.row) return true;

		int dx = q2.point.col - q1.point.col;
		int dy = q2.point.row - q1.point.row;

		return dx == dy || dx == -dy;
	}

	// points (a, b), (m, n) and (x, y) are colinear if and only if (n - b) * (x - m) == (y - n) * (m - a). Rewritten from fraction equation
	// to avoid divide by zero for vertical lines
	public boolean isColinear(Queen q3) {
		return (q2.point.row - q1.point.row) * (q3.point.col - q2.point.col) == (q3.point.row - q2.point.row) * (q2.point.col - q1.point.col);
	}

	public String toString() {
		return "line(" + q1.point + ", " + q2.point + ")";
	}
}