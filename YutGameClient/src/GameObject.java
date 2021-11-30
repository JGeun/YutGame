public class GameObject{
	int idx;
	int posIdx;
	int userIdx;
	
	public GameObject(int idx, int posIdx, int userIdx){
		this.idx = idx; this.posIdx = posIdx; this.userIdx = userIdx; 
	}
	
	public int getIdx() {
		return idx;
	}
	
	public int getPosIdx() {
		return posIdx;
	}
	
	public int getUserIdx() {
		return userIdx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}
	
	public void setPosIdx(int posIdx) {
		this.posIdx = posIdx;
	}
	
	public void setUserIdx(int userIdx) {
		this.userIdx = userIdx;
	}
}
