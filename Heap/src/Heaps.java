public class Heaps 
{

	public static void main(String[] args) 
	{
		int[] a = {1,2,3,4};
		int[] b = a;
		int[] c = {13,14};
		int index = 0;

		
		if(checkMinHeap(a, index))
			System.out.println("minHeap");
		else
			System.out.println("Not minHeap");
		 

		
		convertToMax(b);
		for(int i = 0; i < b.length; i++)
		{	
			if(i < b.length - 1)
				System.out.print(b[i] + ", ");
			else
				System.out.print(b[i]);
		}
		System.out.println();
		
		
		int[] merged = mergeHeap(a, c);
		
		for(int i = 0; i < merged.length; i++)
		{	
			if(i < merged.length - 1)
				System.out.print(merged[i] + ", ");
			else
				System.out.print(merged[i]);
		}
		
	}
	//merge heap into max heap
	public static int[] mergeHeap(int[] a, int[] b)
	{
		int[] answer = new int[a.length + b.length];
		
		for(int i = 0; i < a.length; i++)
			answer[i] = a[i];
		for(int i = a.length; i < a.length + b.length; i++)
			answer[i] = b[i-a.length];

		for(int i = answer.length; i >= 0; i--)
			maxHeapify(answer, i);
		
		return answer;
	}

	public static boolean checkMinHeap(int[] a, int i)
	{
		int leftChild = 2 * i + 1;
		int rightChild = 2 * i + 2;


		//node is a child so true
		//a child is a heap by itself
		if(leftChild > a.length || rightChild > a.length)
			return true;

		//check if at a[i] that it is greater than left and right Child
		//means it is a minHeap
		if(a[i] > leftChild || a[i] > rightChild)
			return false;

		checkMinHeap(a, leftChild);
		checkMinHeap(a, rightChild);

		return true;		
	}

	//makes min heap to max heap
	public static void maxHeapify(int[] a, int index)
	{
		int leftChild = lc(index);
		int rightChild = rc(index);

		int x = index;

		if(leftChild < a.length && a[leftChild] > a[index])
			x = leftChild;
		if(rightChild < a.length && a[rightChild] > a[x])
			x = rightChild;

		if(x != index)
		{
			swap(a, index, x);

			//re heapify the tree just in case there is more
			maxHeapify(a, x);
		}
	}

	public static void swap(int[] array, int a, int b)
	{
		int temp = array[a];

		array[a] = array[b];
		array[b] = temp;
	}

	public static void convertToMax(int[] a)
	{
		int i = (a.length)/ 2;

		for(int x = i; x >= 0; x--)
		{
			maxHeapify(a, x);
		}
	}

	private static int lc(int i)
	{
		return (2 * i) + 1;
	}

	private static int rc(int i)
	{
		return (2 * i) + 2;

	}
}


