import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/*
 * Author: Angelica Altamirano 214497284
 * Date: 11/20/2020
 * CSC 137 CPU Lab
 * 
 */

public class Stumped {
	static Stack<Short> stack = new Stack<Short>();
	public static boolean halt = false;
	static Memory storage = new Memory();
	static ProgramCounter programCounter = new ProgramCounter();
	static Short inputbuffer;

	public static void main(String args[]) {
		if (args.length < 1)
			System.out.println("USAGE: java Stumped.java <object file> [integer in value]");

		// execute every instruction from program counter starting at 0

		BufferedReader fileIn;
		try {
			fileIn = new BufferedReader(new FileReader(args[0]));
			if(args.length>1) {
			inputbuffer = ((short) Integer.parseInt(args[1]));
			//System.out.println(inputbuffer);
			}
			String line = fileIn.readLine();
			line = fileIn.readLine();
			int pcIndex = 0;

			while (line != null) {
				programCounter.addInstruction(pcIndex);
				//System.out.println(line);
				Short shortLine = (short) Integer.parseInt(line, 16);
				//System.out.println(shortLine);
				storage.addopcode(shortLine, pcIndex);
				pcIndex++;
				line = fileIn.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(storage.toString());

		while (!halt) {
			// getnext instruction
			// get that from memory
			// when i get it from mem its short, so to send decode -> string
			// decode
			short nextInstruction = storage.getopcode(programCounter.getNextInstruction());
			//System.out.println(nextInstruction);
			String hex = Integer.toHexString(nextInstruction & 0xffff);
			//System.out.println(hex);
			if (hex.length() < 4) {
				hex = "0" + hex;
			}
			hex = hex.toUpperCase();
			//System.out.println(hex);
			executeCycle(hex);
			//System.out.println(stack.toString());
			// System.out.println(programCounter.toString());
		}
		//System.out.println("End");

	}

	private static void executeCycle(String opcode) {
		String check = opcode.substring(0, 1);
		Short next;
		Short top;
		if (check.equals("1") || check.equals("2") || check.equals("3") || check.equals("4") || check.equals("5")
				|| check.equals("6") || check.equals("D") || check.equals("E")) {
			String important = opcode.substring(1);
			// System.out.println(important);
			Short inputAddress = (short) Integer.parseInt(important, 16);
			// System.out.println(inputAddress);
			switch (check) {
			case "1":
				short immediateValue;
				String binaryNum = Integer.toBinaryString(Integer.parseInt(important, 16));

				if (binaryNum.length() < 12) {
					while (binaryNum.length() < 16) {
						binaryNum = "0" + binaryNum;
					}
				} else {
					while (binaryNum.length() < 16) {
						binaryNum = "1" + binaryNum;
					}
				}
				immediateValue = (short) Integer.parseInt(binaryNum, 2);
				stack.push(immediateValue);
				break;
			case "2":
				stack.push(storage.getopcode(inputAddress));
				break;
			case "3":
				top = stack.pop();
				storage.addopcode(top, inputAddress);
				break;
			case "4":
				programCounter.pushprogramCounter(inputAddress);
				break;
			case "5":
				top = stack.pop();
				if (top == 0) {
					programCounter.pushprogramCounter(inputAddress);
				}
				break;
			case "6":
				top = stack.pop();
				if (top != 0) {
					programCounter.pushprogramCounter(inputAddress);
				}
				break;
			case "D":
				stack.push((short) inputbuffer);
				break;
			case "E":
				System.out.println(stack.pop());
				break;
			default:
				break;
			}

		} else {
			switch (opcode) {
			case "00":
				//System.out.println("NOP happened");
				break;
			case "0F00":
				halt = true;
				break;
			case "0100":
				stack.push(programCounter.currInstruction().shortValue());
				break;
			case "0200":
				top = stack.pop();
				programCounter.pushprogramCounter((short) top);
				break;
			case "0300":
				top = stack.pop();
				stack.push(storage.getopcode(top));
				break;
			case "0400":
				top = stack.pop();
				next = stack.pop();
				storage.addopcode(top, next);
				break;
			case "0500":
				top = stack.pop();
				stack.push(top);
				stack.push(top);
				break;
			case "0600":
				stack.pop();
				break;
			case "0700":
				top = stack.pop();
				next = stack.pop();
				stack.push(next);
				stack.push(top);
				stack.push(next);
				break;
			case "0800":
				top = stack.pop();
				next = stack.pop();
				stack.push(top);
				break;
			case "0900":
				top = stack.pop();
				next = stack.pop();
				stack.push(top);
				stack.push(next);
				break;
			case "F000":
				top = stack.pop();
				next = stack.pop();
				stack.push((short) (top + next));
				break;
			case "F001":
				top = stack.pop();
				next = stack.pop();
				stack.push((short) (next - top));
				break;
			case "F002":
				stack.push((short) (stack.pop() * stack.pop()));
				break;
			case "F003":
				top = stack.pop();
				next = stack.pop();
				if (top != 0) {
					stack.push((short) (next / top));
				} else {
					halt = true;
				}
				break;
			case "F004":
				top = stack.pop();
				next = stack.pop();
				stack.push((short) (next % top));
				break;
			case "F005":
				top = stack.pop();
				next = stack.pop();
				stack.push((short) (next<<top));
				break;
			case "F006":
				top = stack.pop();
				next = stack.pop();
				Short idk = (short)(next>>top);
				//System.out.println(idk);
				stack.push(idk);
				break;
			case "F007":
				top = stack.pop();
				next = stack.pop();
				stack.push((short) (next & top));
				break;
			case "F008":
				top = stack.pop();
				next = stack.pop();
				stack.push((short) (next | top));
				break;
			case "F009":
				top = stack.pop();
				next = stack.pop();
				stack.push((short) (next ^ top));
				break;
			case "F00A":
				top = stack.pop();
				next = stack.pop();
				if (top != 0 && next != 0) {
					stack.push((short) 1);
				} else {
					stack.push((short) 0);
				}
				break;
			case "F00B":
				top = stack.pop();
				next = stack.pop();
				if (top != 0 || next != 0) {
					stack.push((short) 1);
				} else {
					stack.push((short) 0);
				}
				break;
			case "F00C":
				top = stack.pop();
				next = stack.pop();
				if (next == top) {
					stack.push((short) 1);
				} else {
					stack.push((short) 0);
				}
				break;
			case "F00D":
				top = stack.pop();
				next = stack.pop();
				if (next != top) {
					stack.push((short) 1);
				} else {
					stack.push((short) 0);
				}
				break;
			case "F00E":
				top = stack.pop();
				next = stack.pop();
				if (next >= top)
					stack.push((short) 1);
				else
					stack.push((short) 0);
				break;
			case "F00F":
				top = stack.pop();
				next = stack.pop();
				if (next <= top)
					stack.push((short) 1);
				else
					stack.push((short) 0);
				break;
			case "F010":
				top = stack.pop();
				next = stack.pop();
				if (next > top)
					stack.push((short) 1);
				else
					stack.push((short) 0);
				break;
			case "F011":
				top = stack.pop();
				next = stack.pop();
				if (next < top)
					stack.push((short) 1);
				else
					stack.push((short) 0);
				break;
			case "F012":
				top = stack.pop();
				stack.push((short) (top * -1));
				break;
			case "F013":
				top = stack.pop();
				stack.push((short) (~top));
				break;
			case "F014":
				top = stack.pop();
				if (top == 0)
					stack.push((short) 1);
				else
					stack.push((short) 0);
				break;
			default:
				//System.out.println(opcode);
				System.out.println("unknown command");
				break;
			}
		}
	}
}

class Memory {
	Short[] mem = new Short[4096];

	public void addopcode(Short value, int index) {
		mem[index] = value;
	}

	public Short getopcode(int index) {
		return mem[index];
	}
}

class ProgramCounter {
	ArrayList<Integer> instructions = new ArrayList<Integer>();
	int currInt = 0;

	public Integer currInstruction() {
		return (currInt - 1);
	}

	public void addInstruction(int address) {
		instructions.add(address);
	}

	public Integer getInstruction(int index) {
		return instructions.get(index);
	}

	public Integer getNextInstruction() {
		return instructions.get(currInt++);
	}

	public void pushprogramCounter(int newAdrress) {
		currInt = newAdrress;
	}

	public String toString() {
		return instructions.toString();
	}
}
