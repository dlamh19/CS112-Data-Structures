package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {

	// prevent instantiation
	private Trie(){ }

	public static TrieNode buildTrie(String[] allWords) 
	{
		TrieNode root = new TrieNode(null, null, null);

		if(allWords.length == 0)
			return root;

		root.firstChild = new TrieNode(new Indexes(0,  (short)(0), (short)(allWords[0].length() - 1)), null, null);

		TrieNode ptr = root.firstChild;
		TrieNode savePtr = root.firstChild;
		
		int similarChar = -1;
		int startIndex = -1;
		int endIndex = -1;
		int wordIndex = -1;

		for(int index = 1; index < allWords.length; index++) 
		{
			String word = allWords[index];

			while(ptr != null)
			{
				startIndex = ptr.substr.startIndex;
				endIndex = ptr.substr.endIndex;
				wordIndex = ptr.substr.wordIndex;

				if(startIndex > word.length()) 
				{
					savePtr = ptr;
					ptr = ptr.sibling;
					continue;
				}
				similarChar = similarUpTo(allWords[wordIndex].substring(startIndex, endIndex+1), word.substring(startIndex)); //Find index up to which strings are similar

				if(similarChar != -1)
					similarChar += startIndex;

				if(similarChar == -1) 
				{ 
					savePtr = ptr;
					ptr = ptr.sibling;
				}
				else 
				{
					if(similarChar == endIndex) 
					{ 
						savePtr = ptr;
						ptr = ptr.firstChild;
					}
					else if (similarChar < endIndex)
					{ 
						savePtr = ptr;
						break;
					}

				}

			}

			if(ptr == null) 
			{
				Indexes indexes = new Indexes(index, (short)startIndex, (short)(word.length()-1));
				savePtr.sibling = new TrieNode(indexes, null, null);
			} 
			else 
			{
				Indexes indexHolder = savePtr.substr; 
				TrieNode childHolder = savePtr.firstChild; 

				Indexes newIndex = new Indexes(indexHolder.wordIndex, (short)(similarChar+1), indexHolder.endIndex);
				indexHolder.endIndex = (short)similarChar; 

				savePtr.firstChild = new TrieNode(newIndex, null, null);
				savePtr.firstChild.firstChild = childHolder;
				savePtr.firstChild.sibling = new TrieNode(new Indexes((short)index, (short)(similarChar+1), (short)(word.length()-1)), null, null);
			}
			ptr = savePtr = root.firstChild;

			similarChar = startIndex = endIndex = wordIndex = -1;

		}
		return root;

	}

	private static int similarUpTo(String inTrie, String insert) {

		//Placeholder return statement

		int upTo = 0;

		while(upTo < inTrie.length() && upTo < insert.length() && inTrie.charAt(upTo) == insert.charAt(upTo))
			upTo++;

		return (upTo-1);

	}

	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) 
	{
		if(root == null) 
			return null;

		ArrayList<TrieNode> matches = new ArrayList<>();
		TrieNode ptr = root;

		while(ptr != null) 
		{
			if(ptr.substr == null) 				
				ptr = ptr.firstChild;

			String comparedWord = allWords[ptr.substr.wordIndex];
			String newWord = comparedWord.substring(0, ptr.substr.endIndex+1);

			if(comparedWord.startsWith(prefix) || prefix.startsWith(newWord))
			{
				if(ptr.firstChild != null) 
				{ 
					matches.addAll(completionList(ptr.firstChild, allWords, prefix));
					ptr = ptr.sibling;
				} 
				else 
				{ 
					matches.add(ptr);
					ptr = ptr.sibling;
				}
			} 
			else
			{
				ptr = ptr.sibling;
			}
		}
		return matches;
	}

	public static void print(TrieNode root, String[] allWords) 
	{
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}

	private static void print(TrieNode root, int indent, String[] words) 
	{
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}

		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
					.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}

		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}

		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
}
