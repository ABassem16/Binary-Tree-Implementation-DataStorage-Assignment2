
public class Node 
{
	private int value;
	private int offset;
	private int left;
 	private int right;
	Node(int val)
	{
		setValue(val);
		setOffset(0);
		setRight(0);
		setLeft(0);
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getRight() {
		return right;
	}
	public void setRight(int right) {
		this.right = right;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
}
