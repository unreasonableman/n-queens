// Attack maps are stored in an NxN grid. Free cells are set to 0, attacked
// cells have non-zero values. When a new piece is placed on the grid. attack
// lines are drawn horizontally, vertically and diagonally using a unique ID for
// each piece. Attack lines for a piece can be removed in a LIFO fashion without
// disturbing attack lines for other pieces.
public class AttackMap {
	private char[] buf;
	private int length;
	private int id;

	public AttackMap(int length) {
		if (length >= 65536) {
			throw new IllegalArgumentException("n must be less than 65536");
		}

		this.length = length;
		buf = new char[length * length];
	}

	public void placePiece(int x, int y) {
		setAxes(x, y, (char)0, (char)++id);
	}

	public void removeLastPiece(int x, int y) {
		setAxes(x, y, (char)id--, (char)0);
	}

	private void setAxes(int x, int y, char oldValue, char newValue) {
		int n, xx, yy;

		for (xx=0; xx<length; xx++) {
			n = y * length + xx;
			if (buf[n] == oldValue) buf[n] = newValue;
		}

		for (yy=0; yy<length; yy++) {
			n = yy * length + x;
			if (buf[n] == oldValue) buf[n] = newValue;
		}

		// nw to se diagonal
		xx = x;
		yy = y;

		while (xx > 0 && yy > 0) {
			xx--;
			yy--;
		}

		while (xx < length && yy < length) {
			n = yy * length + xx;
			if (buf[n] == oldValue) buf[n] = newValue;

			xx++;
			yy++;
		}

		// sw to ne diagonal
		xx = x;
		yy = y;

		while (xx > 0 && yy < length - 1) {
			xx--;
			yy++;
		}

		while (xx < length && yy >= 0) {
			n = yy * length + xx;
			if (buf[n] == oldValue) buf[n] = newValue;

			xx++;
			yy--;
		}
	}

	private void dump() {
		for (int y=0; y<length; y++) {
			for (int x=0; x<length; x++) {
				System.out.print((int)buf[y * length + x]);
			}

			System.out.println();
		}

		System.out.println();
	}

	public void reset() {
		for (int i=0; i<buf.length; i++) {
			buf[i] = 0;
		}

		id = 0;
	}

	public boolean isAvailable(int x, int y) {
		return buf[y * length + x] == 0;
	}
}