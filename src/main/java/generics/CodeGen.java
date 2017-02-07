package generics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeGen {
	
	static PrintStream debug = System.err;
	static PrintStream out = System.out;
	
	static String[] generics = new String[]{"Integer", "Long", "Float", "Double", "Short", "Character", "Byte"};
	static String[] natives = new String[]{"int", "long", "float", "double", "short", "char", "byte"};
	static String[] natNames = new String[]{"Int", "Long", "Float", "Double", "Short", "Char", "Byte"};
	
	public static void main(String[] args) {
		generateNativeVersionsForGenerics(
				new File("src/main/java/array/ProtoSlice.java"),
				"ProtoSlice",
				"Slice", 
				new File("src/main/java/array/Slice.java"));
	}

	
	static void generateNativeVersionsForGenerics(File f_proto, String protoName, String className, File outFile) {
		try(
				PrintStream out = new PrintStream(outFile);
		){
			generateNativeVersionsForGenerics(f_proto, protoName, className, out);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void generateNativeVersionsForGenerics(File f_proto, String protoName, String className) {
		generateNativeVersionsForGenerics(f_proto, protoName, className, System.out);
	}
	
	static void generateNativeVersionsForGenerics(File f_proto, String protoName, String className, PrintStream out) {
		TextFileIterable src = new TextFileIterable(f_proto);
		
		out.println("/* AUTOMATICALLY GENERATED FROM  "+ f_proto.getParent() +File.separator+ f_proto.getName() +" */");
		
//		String[] src = {
//				"////MKNATIVE>>>>\n",
//				"/*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/ clon = new /*RPLC:Generic*/GenericAccessor/**//*RM*/<>/**/(array);\n",
//				"////<<<<"
//		};
 		
 		final int FINDINGPROTO=10;
 		final int SCANNING=0;
 		int status = SCANNING;
 		
 		ArrayList<String> proto = new ArrayList<>();
 		
		for(String line: src){
			line = line.replaceAll(protoName, className);
			switch (status) {
			case FINDINGPROTO:
				if(line.contains("////<<<<")){
					status = SCANNING;
					processPrototype(out, proto);
					proto.clear();
				} else {
					proto.add(line);
				}
				break;
			default:
				if(line.contains("////MKNATIVE>>>>")){
					status = FINDINGPROTO;
				} else {
					out.print(line);
				}
				break;
			}
		}
		
	}
	
	static void processPrototype(PrintStream out, ArrayList<String> proto) {
		
		processPrototypeKeepGeneric(out, proto);
		out.println();
		for(int i = 0; i < 7; i++){
			processPrototype(out, proto, generics[i], natives[i], natNames[i]);
			out.println();
		}
	}
	
	static void processPrototypeKeepGeneric(PrintStream out, ArrayList<String> proto){
		for(String line: proto){
			for(String token: line.split(rg("/**/"))){
				token = token.replaceAll(rg("/*RM*/"), "");
				token = token.replaceAll(rg("/*G*/"), "");
				token = token.replaceAll(rg("/*N*/"), "");
				token = token.replaceAll(rg("/*RPLC:.+*/"), "");
				out.print(token);
			}
		}
	}

	static void processPrototype(PrintStream out, ArrayList<String> proto, String gen, String nat, String name) {
		for(String line: proto){
			String[] items = line.split(rg("/**/"));
			for(String item: items){
				item = item.replaceAll(rg("/*RM*/.+"), "");
				item = item.replaceAll(rg("/*G*/.+"), gen);
				item = item.replaceAll(rg("/*N*/.+"), nat);
				item = doReplacement(item, name);
				out.print(item);
			}
		}
	}

	static String rg(String regex){
		return regex.replaceAll("\\*", "\\\\\\*");
	}
	
	static String doReplacement(String s, String r){
		if(!s.contains("/*RPLC:"))
			return s;
		String toReplace = extractRPLC(s);
		s = s.replaceAll(rg("/*RPLC:.+*/"), "");
		s = s.replaceAll(toReplace, r);
		return s;
	}
	
	static final Pattern rplcGroup = Pattern.compile(rg("/*RPLC:(.+?)*/"));
	static String extractRPLC(String s){
		Matcher m = rplcGroup.matcher(s);
		m.find();
		return m.group(1);
	}
	
	
	static class TextFileIterable implements Iterable<String> {

		File f;
		
		public TextFileIterable(File file) {
			f=file;
		}

		@Override
		public Iterator<String> iterator() {
			try {
				final Scanner sc = new Scanner(f);
				return new Iterator<String>() {
					@Override
					public boolean hasNext() {
						boolean hasNext = sc.hasNextLine();
						if(!hasNext)
							sc.close();
						return hasNext;
					}

					@Override
					public String next() {
						return sc.nextLine()+"\n";
					}
				};
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
}
