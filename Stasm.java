import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Stasm {
	static String[] label = new String[4096];
	static ProgramCounter programCounter = new ProgramCounter();
	static String[] opcodes = new String[4096];
	static String[] opcodeString = new String[4096];
	static Short[] mem = new Short[4096];

	public static void main(String args[]) {
		if (args.length < 2) {
			System.out.println("USAGE:  java Stasm.java <source file> <object file> [-l]\r\n"
					+ " -l : print listing to standard error");

		// execute every instruction from program counter starting at 0
		}else {
		BufferedReader fileIn;
		BufferedReader fileIn2;
		FileWriter myWriter;
		int lineLength = 0;
		
		try {
			fileIn = new BufferedReader(new FileReader(args[0]));
			String line = fileIn.readLine();
			int pcIndex = 0;

			while (line != null) {
				boolean inc = true;
				String[] splitStr = line.split("\\s+");
				lineLength = splitStr.length;
				for (int i = 0; i < splitStr.length; i++) {
					String str = splitStr[i];
					//System.out.println(str);
						if (str.length() > 1) {
							if(i==0 && str.substring(0,1).equals(";")) {
								inc = false;
							}else {
								String sub = str.substring(str.length() - 1);
								if (sub.equals(":")) {
								// check if there is other stuff on the line
								if (lineLength > 1) {
									//check if next thing after label is a comment or opcode
									String str2 = splitStr[i+1];
									if(str2.substring(0,1).equals(";")) {
										inc = false;
									}else {
										label[pcIndex] = str.substring(0, str.length() - 1);
									}
									//System.out.println(pcIndex);
									//System.out.println(str.substring(0, (str.length() - 1)));
								} else {
									label[pcIndex] = str.substring(0, str.length() - 1);
									inc = false;
									//System.out.println(pcIndex + 1);
									//System.out.println(str.substring(0, (str.length() - 1)));
								}
							}

						}
					}

				}
				line = fileIn.readLine();
				if (inc) {pcIndex++;}
			}
			
			myWriter = new FileWriter(args[1]);
			fileIn2 = new BufferedReader(new FileReader(args[0]));
			line = fileIn2.readLine();
			myWriter.write("v2.0 raw\n");
			pcIndex = 0;
			String address;
			String immediate;
			String myOp;
			while (line != null) {
				String[] s = line.split("\\s+");
				for (int i = 0; i < s.length; i++) {
					String op = s[i];
					if (op.equals("PUSH") || op.equals("POP") || op.equals("JMP") || op.equals("JZ")
							|| op.equals("JNZ")) {
						address = s[i + 1];
						boolean j = false;
						int ii = 0;
						while (!j && ii < label.length) {
							if (label[ii] != null && label[ii].equals(address)) {
								j = true;
								String iihexVal = Integer.toHexString(ii);
								iihexVal = iihexVal.toUpperCase();
								while (iihexVal.length() < 3) {
									iihexVal = "0" + iihexVal;
								}
								//System.out.println(iihexVal);
								switch(op) {
								case "PUSH":
									myOp = "2" + iihexVal;
									myWriter.write(myOp + "\n");
									opcodes[pcIndex] = myOp;
									opcodeString[pcIndex]= op + " " + address;
									opcodes[pcIndex] = myOp;
									programCounter.addInstruction(pcIndex);
									pcIndex++;
									break;
								case "POP":
									myOp = "3" + iihexVal;
									myWriter.write(myOp + "\n");
									opcodes[pcIndex] = myOp;
									opcodeString[pcIndex]= op + " " + address;;
									opcodes[pcIndex] = myOp;
									programCounter.addInstruction(pcIndex);
									pcIndex++;
									break;
								case "JMP":
									myOp = "4" + iihexVal;
									myWriter.write(myOp + "\n");
									opcodes[pcIndex] = myOp;
									opcodeString[pcIndex]= op + " " + address;
									opcodes[pcIndex] = myOp;
									programCounter.addInstruction(pcIndex);
									pcIndex++;
									break;
								case "JZ":
									myOp = "5" + iihexVal;
									myWriter.write(myOp + "\n");
									opcodes[pcIndex] = myOp;
									opcodeString[pcIndex]= op + " " + address;
									opcodes[pcIndex] = myOp;
									programCounter.addInstruction(pcIndex);
									pcIndex++;
									break;
								case "JNZ":
									myOp = "6" + iihexVal;
									myWriter.write(myOp + "\n");
									opcodes[pcIndex] = myOp;
									opcodeString[pcIndex]= op + " " + address;
									opcodes[pcIndex] =myOp;
									programCounter.addInstruction(pcIndex);
									pcIndex++;
									break;
								default:
									System.out.println("something bad happened");
									break;
								}
							}
							ii++;
						}
					} else {
						switch (op) {
						case "NOP":
							opcodeString[pcIndex]="NOP";
							opcodes[pcIndex] = "0000";
							myWriter.write("0000\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "HALT":
							opcodeString[pcIndex]="HALT";
							opcodes[pcIndex] = "0F00";
							myWriter.write("0F00\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "PUSHPC":
							opcodeString[pcIndex]="PUSHPC";
							opcodes[pcIndex] = "0100";
							myWriter.write("0100\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "POPPC":
							opcodeString[pcIndex]="POPPC";
							opcodes[pcIndex] = "0200";
							myWriter.write("0200\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "LD":
							opcodeString[pcIndex]="LD";
							opcodes[pcIndex] = "0300";
							myWriter.write("0300\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "ST":
							opcodeString[pcIndex]="ST";
							opcodes[pcIndex] = "0400";
							myWriter.write("0400\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "DUP":
							opcodeString[pcIndex]="DUP";
							opcodes[pcIndex] = "0500";
							myWriter.write("0500\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "DROP":
							opcodeString[pcIndex]="DROP";
							opcodes[pcIndex] = "0600";
							myWriter.write("0600\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "OVER":
							opcodeString[pcIndex]="OVER";
							opcodes[pcIndex] = "0700";
							myWriter.write("0700\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "DNEXT":
							opcodeString[pcIndex]="DNEXT";
							opcodes[pcIndex] = "0800";
							myWriter.write("0800\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "SWAP":
							opcodeString[pcIndex]="SWAP";
							opcodes[pcIndex] = "0900";
							myWriter.write("0900\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "PUSHI":
							immediate = s[i + 1];
							int inum = Integer.parseInt(immediate);
							String hexVal = Integer.toHexString(inum);
							hexVal = hexVal.toUpperCase();
							while (hexVal.length() < 3) {
								hexVal = "0" + hexVal;
							}
							String opcode = "1" + hexVal;
							//System.out.println(opcode + "\n");
							opcodes[pcIndex] = opcode;
							opcodeString[pcIndex]=op+" " + immediate;
							myWriter.write(opcode + "\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "IN":
							opcodeString[pcIndex]="IN";
							opcodes[pcIndex] = "D000";
							myWriter.write("D000\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "OUT":
							opcodeString[pcIndex]="OUT";
							opcodes[pcIndex] = "E000";
							myWriter.write("E000\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "ADD":
							opcodeString[pcIndex]="ADD";
							opcodes[pcIndex] = "F000";
							myWriter.write("F000\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "SUB":
							opcodeString[pcIndex]="SUB";
							opcodes[pcIndex] = "F001";
							myWriter.write("F001\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "MUL":
							opcodeString[pcIndex]="MUL";
							opcodes[pcIndex] = "F002";
							myWriter.write("F002\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "DIV":
							opcodeString[pcIndex]="DIV";
							opcodes[pcIndex] = "F003";
							myWriter.write("F003\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "MOD":
							opcodeString[pcIndex]="MOD";
							opcodes[pcIndex] = "F004";
							myWriter.write("F004\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "SHL":
							opcodeString[pcIndex]="SHL";
							opcodes[pcIndex] = "F005";
							myWriter.write("F005\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "SHR":
							opcodeString[pcIndex]="SHR";
							opcodes[pcIndex] = "F006";
							myWriter.write("F006\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "BAND":
							opcodeString[pcIndex]="BAND";
							opcodes[pcIndex] = "F007";
							myWriter.write("F007\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "BOR":
							opcodeString[pcIndex]="BOR";
							opcodes[pcIndex] = "F008";
							myWriter.write("F008\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "BXOR":
							opcodeString[pcIndex]="BXOR";
							opcodes[pcIndex] = "F009";
							myWriter.write("F009\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "AND":
							opcodeString[pcIndex]="AND";
							opcodes[pcIndex] = "F00A";
							myWriter.write("F00A\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "OR":
							opcodeString[pcIndex]="OR";
							opcodes[pcIndex] = "F00B";
							myWriter.write("F00B\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "EQ":
							opcodeString[pcIndex]="EQ";
							opcodes[pcIndex] = "F00C";
							myWriter.write("F00C\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "NE":
							opcodeString[pcIndex]="NE";
							opcodes[pcIndex] = "F00D";
							myWriter.write("F00D\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "GE":
							opcodeString[pcIndex]="GE";
							opcodes[pcIndex] = "F00E";
							myWriter.write("F00E\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "LE":
							opcodeString[pcIndex]="LE";
							opcodes[pcIndex] = "F00F";
							myWriter.write("F00F\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "GT":
							opcodeString[pcIndex]="GT";
							opcodes[pcIndex] = "F010";
							myWriter.write("F010\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "LT":
							opcodeString[pcIndex]="LT";
							opcodes[pcIndex] = "F011";
							myWriter.write("F011\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "NEG":
							opcodeString[pcIndex]="NEG";
							opcodes[pcIndex] = "F012";
							myWriter.write("F012\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "BNOT":
							opcodeString[pcIndex]="BNOT";
							opcodes[pcIndex] = "F013";
							myWriter.write("F013\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "NOT":
							opcodeString[pcIndex]="NOT";
							opcodes[pcIndex] = "F014";
							myWriter.write("F014\n");
							programCounter.addInstruction(pcIndex);
							pcIndex++;
							break;
						case "DW":
							immediate = s[i + 1];
							int value = Integer.parseInt(immediate);
							opcodeString[pcIndex]="DW";
							opcodes[pcIndex] = "0000";
							myWriter.write("0000\n");
							programCounter.addInstruction(pcIndex);
							mem[pcIndex]= (short)value;
							pcIndex++;
							break;
						default:
							// System.out.println(opcode);
							// System.out.println("unknown command");
							break;
						}
					}
				}
				line = fileIn2.readLine();
			}
			myWriter.close();
			String index;
			if(args.length> 2 && args[2].equals("-l")) {
				System.out.println("*** LABEL LIST ***");
				for(int i = 0; i<label.length; i++) {
					if(label[i]!=null) {
						index = Integer.toString(i);
						while(index.length()<3) {
							index = "0"+index;
						}
						System.out.println(label[i]+"     "+index);
					}
				}
				System.out.println();
				System.out.println("*** MACHINE PROGRAM ***");
				for(int i = 0; i<programCounter.getLength(); i++) {
					index = Integer.toString(i);
					while(index.length()<3) {
						index = "0"+index;
					}
					System.out.println(index+":"+opcodes[i]+"     "+opcodeString[i]);
				}
			}
			
			//System.out.println(Arrays.toString(opcodeString));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println(opcodes.toString());
	}
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
	
	public Integer getLength() {
		return instructions.size();
	}

	public void pushprogramCounter(int newAdrress) {
		currInt = newAdrress;
	}

	public String toString() {
		return instructions.toString();
	}
}
