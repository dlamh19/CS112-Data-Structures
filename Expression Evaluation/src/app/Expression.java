package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is created 
	 * and stored, even if it appears more than once in the expression.
	 * At this time, values for all variables and all array items are set to
	 * zero - they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr The expression
	 * @param vars The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) 
	{
		String[] tokenHolder = expr.split("[^a-zA-Z\\[]+");
		int index = 0;
		
		for (int i = 0; i < tokenHolder.length; i++)
		{
			{
				if (tokenHolder[i].contains("["))
				{
					StringBuilder string = new StringBuilder("");
					for (int j = 0; j < tokenHolder[i].length(); j++)
					{
						if (tokenHolder[i].charAt(j) == '[')
						{
							Array arrayTemp = new Array(string.toString());
							if (arrays.isEmpty() || arrays.indexOf(arrayTemp) == -1)
								arrays.add(index++, arrayTemp);

							string.setLength(0);
						}
						else
							string.append(tokenHolder[i].charAt(j));
						
					}
					if (string.length() > 0)
					{
						Variable variableObject1 = new Variable(string.toString());
						
						if (vars.isEmpty() || vars.indexOf(variableObject1) == -1)
							vars.add(variableObject1);
					}

				}
				else
				{
					Variable variableObject2 = new Variable(tokenHolder[i]);
					if (vars.isEmpty() || vars.indexOf(variableObject2) == -1)
						vars.add(variableObject2);
				}
			}
		}
	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input 
	 * @param vars The variables array list, previously populated by makeVariableLists
	 * @param arrays The arrays array list - previously populated by makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) throws IOException 
	{
		while (sc.hasNextLine()) 
		{
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok," (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;              
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars The variables array list, with values for all variables in the expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) 
	{
		expr.trim();
		Stack<Character> operatorStackk = new Stack<>();
		Stack<Float> operandStack = new Stack<>();
		Stack<String> arrayStk = new Stack<>();

		StringBuffer stringBuffer = new StringBuffer("");

		float operand = 0;
		int temp = 0;

		while (temp < expr.length())
		{
			switch (expr.charAt(temp))
			{
			case '(':
				operatorStackk.push(expr.charAt(temp));
				break;
			case ')':
				while (!operatorStackk.isEmpty() && !operandStack.isEmpty()
						&& (operatorStackk.peek() != '('))
				{
					findOperationResult(operatorStackk, operandStack);
				}
				if (operatorStackk.peek() == '(')
				{
					operatorStackk.pop();
				}
				break;
			case '[':
				arrayStk.push(stringBuffer.toString());
				stringBuffer.setLength(0);
				operatorStackk.push(expr.charAt(temp));
				break;
			case ']':
				while (!operatorStackk.isEmpty() && !operandStack.isEmpty() && (operatorStackk.peek() != '['))
					findOperationResult(operatorStackk, operandStack);
				if (operatorStackk.peek() == '[')
					operatorStackk.pop();

				int idx = operandStack.pop().intValue();

				Iterator<Array> itr = arrays.iterator();
				while (itr.hasNext())
				{
					Array arr = itr.next();
					if (arr.name.equals(arrayStk.peek()))
					{
						operandStack.push((float) arr.values[idx]);
						arrayStk.pop();
						break;
					}
				}

				break;
			case ' ':
				break;
			case '+':
			case '-':
			case '*':
			case '/':
				while (!operatorStackk.isEmpty() && (operatorStackk.peek() != '(') && (operatorStackk.peek() != '[') && isPrecedenceLow(expr.charAt(temp), operatorStackk.peek()))
				{
					findOperationResult(operatorStackk, operandStack);
				}
				operatorStackk.push(expr.charAt(temp));
				break;
			default:
				if ((expr.charAt(temp) >= 'a' && expr.charAt(temp) <= 'z') || (expr.charAt(temp) >= 'A' && expr.charAt(temp) <= 'Z'))
				{
					stringBuffer.append(expr.charAt(temp));

					if (temp + 1 < expr.length())
					{
						if (expr.charAt(temp + 1) == '+' || expr.charAt(temp + 1) == '-' || expr.charAt(temp + 1) == '*' || expr.charAt(temp + 1) == '/' || expr.charAt(temp + 1) == ')' || expr.charAt(temp + 1) == ']' || expr.charAt(temp + 1) == ' ')
						{
							Variable var = new Variable(stringBuffer.toString());
							int indexVariable1 = vars.indexOf(var);
							operand = vars.get(indexVariable1).value;
							operandStack.push(operand);
							stringBuffer.setLength(0);
						}
					}
					else
					{
						Variable var = new Variable(stringBuffer.toString());
						int indexVariable2 = vars.indexOf(var);
						operand = vars.get(indexVariable2).value;
						operandStack.push(operand);
						stringBuffer.setLength(0);
					}

				}
				else if (expr.charAt(temp) >= '0' && expr.charAt(temp) <= '9')
				{
					stringBuffer.append(expr.charAt(temp));
					if (temp + 1 < expr.length())
					{
						if (expr.charAt(temp + 1) == '+' || expr.charAt(temp + 1) == '-' || expr.charAt(temp + 1) == '*' || expr.charAt(temp + 1) == '/' || expr.charAt(temp + 1) == ')' || expr.charAt(temp + 1) == ']' || expr.charAt(temp + 1) == ' ')
						{
							operand = Integer.parseInt(stringBuffer.toString());
							operandStack.push(operand);
							stringBuffer.setLength(0);
						}
					}
					else
					{
						operand = Float.parseFloat(stringBuffer.toString());
						operandStack.push(operand);
						stringBuffer.setLength(0);
					}
				}
				break;
			}
			temp++;
		}

		Float result = Float.valueOf(0);
		if (temp == expr.length())
		{
			while (operatorStackk.size() > 0 && operandStack.size() > 1)
			{
				findOperationResult(operatorStackk, operandStack);
			}
			if (operandStack.size() > 0)
			{
				result = operandStack.pop();
			}
		}
		return result.floatValue();
	}

	private static void findOperationResult(Stack<Character> operatorStk, Stack<Float> operandStk) 
	{
		Float result = Float.valueOf(0);
		if (operatorStk.size() > 0 && operandStk.size() > 1)
		{
			Float operand1 = operandStk.pop().floatValue();
			Float operand2 = operandStk.pop().floatValue();
			switch (operatorStk.pop())
			{
			case '+':
				result = operand2 + operand1;
				break;
			case '-':
				result = operand2 - operand1;
				break;
			case '*':
				result = operand2 * operand1;
				break;
			case '/':
				result = operand2 / operand1;
				break;
			}
			operandStk.push(result);
		}
	}

	private static boolean isPrecedenceLow(char ch1, char ch2)
	{
		if ((ch1 == '*' || ch1 == '/') && (ch2 == '+' || ch2 == '-'))
		{
			return false;
		}
		return true;
	}
}

