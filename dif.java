import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class dif {
	public static void main(String args[]) throws ParseException {

		int mid = args[0].lastIndexOf(".");
		int slash = args[0].lastIndexOf("/");
		String ext = args[0].substring(mid + 1, args[0].length());
		FileInputStream input_file = null;
		
		difparser parser = null;
		IntermediateRepresentation IR = null;
		SimpleNode AST = null;
		SymbolTable table = null;
		CodeGenerator generator = new DotGenerator();
		try {
			input_file = new FileInputStream(args[0]);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (ext.equalsIgnoreCase("difir")) {
			IR = new IntermediateRepresentation(input_file);
		} else {
			parser = new difparser(input_file);
			@SuppressWarnings("static-access")
			SimpleNode parse = parser.sintax_analyse();
			AST = parse;
			table = new SymbolTable(AST);
			new AnaliseSemantica(table, generator, AST);
			IR = new IntermediateRepresentation(AST, table);
		}
		
		if (args.length >= 2)
		{
			if(table != null){
				if (args[1].equals("AST")) {
					AST.dump("");	System.exit(0);
				} 
				if (args[1].equals("ST")) {
					table.dump("");	System.exit(0);
				}
			}
			if (args[1].equals("IR")) {
				IR.dump(""); System.exit(0);
			} 
			if(args[1].equals("code")){
				generator.generate(IR.getTree());
				System.out.println(generator.outputcode());
				System.exit(0);
			}
			if (args[1].equals("SaveIR")) {
				try {
					FileOutputStream output_file;
					output_file = new FileOutputStream(args[0].substring(
							slash + 1, mid) + ".difir");
					output_file.write(IR.showRepresentation("").getBytes());
					output_file.close();
					System.exit(0);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
		}
		generator.generate(IR.getTree());
		try {
			FileOutputStream output_file;
			output_file = new FileOutputStream(args[0].substring(
					slash + 1, mid) + ".dot");
			output_file.write(generator.outputcode().getBytes());
			output_file.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	
}
