package friends;

import java.util.ArrayList;
import java.util.Arrays;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) 
	{
		String start = p1;
		String end = p2;
		int endIndex = g.map.get(end);


		Queue<Integer> queue = new Queue<>();
		ArrayList<String> shortestPath = new ArrayList<>();
		Stack<String> path = new Stack<>();


		int[] distance = new int[g.members.length];
		int[] pred = new int[g.members.length];
		boolean[] visited = new boolean[g.members.length];

		Arrays.fill(visited, false);				//fill visited with all false
		Arrays.fill(distance, Integer.MAX_VALUE);	//fill distance with infinite
		Arrays.fill(pred, 0); 						//fill predecessor with all 0's

		start = start.toLowerCase();
		end = end.toLowerCase();

		//check if they all exist
		if(g == null || start == null || end == null || g.map.get(start) == null || g.map.get(end) == null)
			return null;

		//equals itself so shortest path is itself
		if(start.equals(end))
		{
			shortestPath.add(g.members[g.map.get(start)].name);
			return shortestPath;
		}

		int startIndex = g.map.get(start);

		//mark start index as visited
		visited[startIndex] = true;

		//enqueue start index
		queue.enqueue(startIndex);

		//set startIndex Distance to 0
		distance[startIndex] = 0;

		while(!queue.isEmpty())
		{
			int temp = queue.dequeue();
			Person person = g.members[temp];

			//Loop through all neighbors
			for(Friend ptr = person.first; ptr != null; ptr = ptr.next)
			{
				//friend number
				int friendNum = ptr.fnum;

				if(visited[friendNum] == false)
				{
					distance[friendNum] = distance[temp] + 1;	//one edge greater than
					pred[friendNum] = temp;						//update predecessor		
					visited[friendNum] = true;					//change visited to true
					queue.enqueue(friendNum); 					//enqueue newFriend
				}
			}
		}

		if (!visited[endIndex]) {

			return null;
		}


		//go through all predecessors
		while (endIndex != 0)
		{
			path.push(g.members[endIndex].name);
			endIndex = pred[endIndex];
		}

		//traverse stack and add to arraylist
		while (!path.isEmpty())
		{
			shortestPath.add(path.pop());
		}

		return shortestPath;
	}

	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school)
	{
		ArrayList<ArrayList <String>> answers = new ArrayList<>();
		boolean[] visited = new boolean[g.members.length];

		school = school.toLowerCase(); 

		Arrays.fill(visited, false);

		//Check if any parameters given are null
		if (g == null || school == null || school.length() == 0)
		{
			return null;
		}

		//visit every person in the graph
		for (Person member : g.members)
		{
			if (!visited[g.map.get(member.name)] && member.school != null && member.school.equals(school))
			{

				Queue <Integer> queue = new Queue<>();
				ArrayList <String> clique = new ArrayList<>();

				int current = g.map.get(member.name);
				visited[current] = true;

				queue.enqueue(current);
				clique.add(member.name);

				while (!queue.isEmpty())
				{

					int holder = queue.dequeue(); 

					Person person = g.members[holder];

					//iterate through every neighbor of a person
					for (Friend ptr = person.first; ptr != null; ptr = ptr.next) 
					{
						int frontNumber = ptr.fnum;
						Person fr = g.members[frontNumber];

						if (!visited[frontNumber] && fr.school != null && fr.school.equals(school)) 
						{
							visited[frontNumber] = true;
							queue.enqueue(frontNumber);
							clique.add(g.members[frontNumber].name);
						}
					}
				}
				answers.add(clique);
			}
		}
		return answers;
	}

	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) 
	{
		boolean[] visited = new boolean[g.members.length]; 
		int[] dfsnum = new int[g.members.length];
		int[] back = new int[g.members.length];
		boolean[] last =new boolean[g.members.length];

		ArrayList<String> answer = new ArrayList<>();
		Arrays.fill(visited, false);				//fill visited with all false

		//visit every member of the graph
		for(int i = 0; i < g.members.length; i++)
		{
			//if the graph num has not been visited
			if(!visited[i])
				dfs(g,visited, dfsnum, back, answer, last, i, i, i);
		}

		return answer;
	}



	//recursive dfs method 
	private static void dfs(Graph g, boolean[] visited, int[] dfsnum, int[] back, ArrayList<String> cons, boolean[] backend, int first, int current, int prev)
	{
		if(visited[current])
		{
			return;
		}

		//change visited to true
		visited[current] = true;

		//update dfsnum
		dfsnum[current] = dfsnum[prev]+1;

		//update end to be compared
		back[current] = dfsnum[current];

		//ptr of the first friend 
		Friend ptr = g.members[current].first;

		while(ptr != null)
		{
			//if the friend has already been visited which means it was backed out
			if(visited[ptr.fnum])
			{

				back[current] = Math.min(back[current],dfsnum[ptr.fnum] );
			}
			else
			{	
				//call dfs again
				dfs(g, visited, dfsnum, back, cons, backend, first, ptr.fnum, current);

				if(dfsnum[current] <= back[ptr.fnum] && !cons.contains(g.members[current].name))
				{
					if(current != first || backend[current] == true){
						cons.add(g.members[current].name);
					}
				}
				if(dfsnum[current] > back[ptr.fnum])
				{
					back[current] = Math.min(back[current], back[ptr.fnum]);
				}
				backend[current] = true;
			}
			ptr = ptr.next;
		}
	}
}
