
public class UserData {
	private int idx;
	private String username;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	UserData(int idx, String username){
		this.username = username;
		this.idx = idx;
	}
}
