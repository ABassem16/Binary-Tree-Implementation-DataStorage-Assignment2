import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
public class File
{
	static String filename="Data.bin";
	static void createFile(String f)
	{
		try
		{
		RandomAccessFile raf=new RandomAccessFile(f,"rw");
		System.out.println("File Created");
		raf.close();
		}
		catch(Exception e)
		{
			System.out.println("File Not Found");
		}
	}
	static void DisplayIndexFileContent(String f)
	{
		try
		{
			int counter=0;
			RandomAccessFile raf=new RandomAccessFile(f,"r");
			long len=raf.length();
			raf.seek(len);
			long eof=raf.getFilePointer();
			raf.seek(0);
			System.out.println("       Value | Offset | Left | Right");
			while(raf.getFilePointer()!=eof)
			{
				System.out.print("Node " + counter + ": ");
				System.out.print("   "+raf.readInt());
				System.out.print("      " + raf.readInt());
				System.out.print("        " + raf.readInt());
				System.out.println("    " + raf.readInt());
				counter++;
			}
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception Display");
		}
	}
	static void writeNode(Node n, String f)
	{
		try
		{
		RandomAccessFile raf=new RandomAccessFile(f,"rw");
		raf.seek(n.getOffset());
		raf.writeInt(n.getValue());
		raf.writeInt(n.getOffset());
		raf.writeInt(n.getLeft());
		raf.writeInt(n.getRight());
		raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception Write Node");
		}
	}
	static void createRecordsFile(String filename,int numberOfRecords)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			raf.seek(0);
			for(int i=0;i<numberOfRecords-1;i++)
			{
				raf.writeInt(i+1);
				raf.writeInt(0);
				raf.writeInt(0);
				raf.writeInt(0);
			}
			raf.writeInt(-1);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Create File");
		}
	}
	static boolean isEmpty(String f)
	{
		try
		{
		RandomAccessFile raf=new RandomAccessFile(f,"rw");
		if(raf.length()==0)
		{
			raf.close();
			return true;
		}
		else
		{
			raf.close();
			return false;
		}
		}
		catch(Exception e)
		{
			System.out.println("Error Checking file if empty.");
			return false;
		}
	}
	static int InsertNewRecordAtIndex(String filename,int key,int byteoffset)
	{
		try
		{
			int nodenum=-1;
			Node n=new Node(key);
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			long len=raf.length();
			int recs=(int)len/16;
			raf.seek(0);
			while(raf.getFilePointer()!=len)
			{
				 int read= raf.readInt();
				if(read>0 && read <recs)
				{
					if(read==1)
					{
						raf.seek(read*16);
						n.setOffset((int)raf.getFilePointer());
						n.setLeft(-1);
						n.setRight(-1);
						nodenum=raf.readInt();
						writeNode(n,filename);
						incrementnode(filename);
						raf.close();
						return nodenum;
					}
					else
					{
						raf.seek(read*16);
						n.setOffset((int)raf.getFilePointer());
						n.setLeft(-1);
						n.setRight(-1);
						updateChild(filename,key,byteoffset);
						nodenum=raf.readInt();
						writeNode(n,filename);
						incrementnode(filename);
						raf.close();
						return nodenum;
					}
				}
				else if(read==-1)
				{
					System.out.println("Cannot Insert\n"
							+ "Not Enough Space");
					raf.close();
					return nodenum;
				}
				else
				{
					byteoffset+=16;
					InsertNewRecordAtIndex(filename,key,byteoffset);
				}
			}
			raf.close();
			return nodenum;
		}
		catch(Exception e)
		{
			System.out.println("Cannot Insert Record");
			return -1;
		}
	}
	static void incrementnode(String f)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(f,"rw");
			int len=(int)raf.length();
			int recs=len/16;
			raf.seek(0);
			int node=raf.readInt();
			node++;
			raf.seek(0);
			if(recs==node)
			{
				node=-1;
			}
			raf.writeInt(node);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("NO");
		}
	}
	static void emptyFile(String f)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(f, "rw");
			raf.setLength(0);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Empty File");
		}
	}
	static int SearchRecordIndex(String filename,int key)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			int pointer=16;
			Node n=new Node(0);
			raf.seek(pointer);
			while(raf.getFilePointer()<raf.length() || n.getValue()!=key)
			{
				n=readNode(raf,pointer);
				if(key==n.getValue())
				{
					System.out.println("Value = " + n.getValue());
					System.out.println("Offset = " + n.getOffset());
					System.out.println("Left Child = " + n.getLeft());
					System.out.println("Right Child = " + n.getRight());
					raf.close();
					return n.getValue();
				}
				else if(key>n.getValue())
				{
					pointer=n.getRight()*16;
					raf.seek(pointer);
					continue;
				}
				else if(key<n.getValue())
				{
					pointer=n.getLeft()*16;
					raf.seek(pointer);
					continue;
				}
			}
			raf.close();
			return -1;
		}
		catch(Exception e)
		{
			System.out.println("Cannot Search");
			return -1;
		}
	}
	static boolean updateChild(String filename, int key,int byteoffset)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename, "rw");
			raf.seek(byteoffset);
			Node n=readNode(raf,byteoffset);
			raf.seek(0);
			int nodevalue=raf.readInt();
			raf.seek(n.getOffset());
			System.out.println("Key : " + key);
			System.out.println("Value " + n.getValue());
			if(key>n.getValue())
			{
				if(n.getRight()==-1)
				{
					raf.seek(n.getOffset()+12);
					n.setRight(key);
					raf.writeInt(nodevalue);
					return true;
				}
				else
				{
					byteoffset=n.getRight()*16;
					updateChild(filename,key,byteoffset);
				}
			}
			else if(key<n.getValue())
			{
				if(n.getLeft()==-1)
				{
					raf.seek(n.getOffset()+8);
					n.setLeft(key);
					raf.writeInt(nodevalue);
					return true;
				}
				else
				{
					byteoffset=n.getLeft()*16;
					updateChild(filename,key,byteoffset);
				}
			}
			else
			{
				System.out.println("Cannot Insert Same Value twice.");
			}
			raf.close();
			return false;
		}
		catch(Exception e)
		{
			System.out.println("Cannot Update Children");
			return false;
		}
	}
	static Node readNode(RandomAccessFile filename,int offset)
	{
		try
		{
			filename.seek(offset);
			int key=filename.readInt();
			Node n=new Node(key);
			n.setOffset(filename.readInt());
			n.setLeft(filename.readInt());
			n.setRight(filename.readInt());
			return n;
		}
		catch(IOException e)
		{
			System.out.println("Cannot Print Node");
			return null;
		}
	}
	static void TraverseBinarySearchTreeInOrder(String filename)
	{
		try
		{
			ArrayList<Integer> nodes=new ArrayList<>();
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			int pointer=16;
			while(raf.getFilePointer()<raf.length())
			{
				Node n=readNode(raf,pointer);
				Node left=null;
				Node right=null;
				while(n.getLeft()!=-1)
				{
					raf.seek(pointer);
					left=readNode(raf,n.getLeft()*16);
					System.out.println(raf.getFilePointer());
					right=readNode(raf,n.getRight()*16);
					System.out.println(raf.getFilePointer());
					nodes.add(left.getValue());
					nodes.add(n.getValue());
					nodes.add(right.getValue());
					pointer+=16;
				}
			}
			for(int i=0;i<nodes.size();i++)
			{
				System.out.println(nodes.get(i));
			}
			
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Traverse");
		}
	}
	public static void main(String[] args)
	{
		Scanner scan=new Scanner(System.in);
		while(true)
		{
			System.out.println("1- Create Record File"
					+ "\n2- Insert Record"
					+ "\n3- Search Record"
					+ "\n4- Traverse Tree"
					+ "\n5- Display Whole File"
					+ "\n6- Empty File"
					+ "\n7- Exit");
			int x=scan.nextInt();
			if(x==1)
			{
				if(isEmpty(filename))
				{
					System.out.println("Enter Number of Records");
					int recs=scan.nextInt();
					createRecordsFile(filename, recs);
				}
				else
				{
					System.out.println("File Already Exists\nCannot Create New File\n");
				}
			}
			else if(x==2)
			{
				System.out.println("Enter Key");
				int key=scan.nextInt();
				InsertNewRecordAtIndex(filename,key,16);
			}
			else if(x==3)
			{
				System.out.println("Enter Value to be Searched on.");
				int key=scan.nextInt();
				int search=SearchRecordIndex(filename,key);
				if(search==-1)
				{
					System.out.println("Cannot Find Key");
				}
			}
			else if(x==4)
			{
				TraverseBinarySearchTreeInOrder(filename);
			}
			else if(x==5)
				DisplayIndexFileContent(filename);
			else if(x==6)
				emptyFile(filename);
			else if(x==7)
				break;
		}
		scan.close();
	}
}
