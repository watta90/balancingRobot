package computer;

public class Sendobject {
	public static int CMD_SET_PARAMETERS = 1;
	public static int CMD_SET_DIRECTION = 2;
	public int cmd;
	public int direction;
	public float[] params;

	public Sendobject(int cmd, int dir) {
		this.cmd = cmd;
		this.direction = dir;
	}

	public Sendobject(int cmd, float[] par) {
		this.cmd = cmd;
		this.params = par;
	}

	public Object getData() {
		if (cmd == CMD_SET_PARAMETERS) {
			return params;
		} else if (cmd == CMD_SET_DIRECTION) {
			return direction;
		}
		return null;
	}

	public int getCMD() {
		return cmd;
	}
}
