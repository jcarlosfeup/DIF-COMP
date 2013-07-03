import java.util.HashMap;

public class Symbol {
	
	public static final int GRAPH =0;
	public static final int NODE =1;
	public static final int EDGE=2;
	public static final int INPUTPORT =3;
	public static final int OUTPUTPORT =4;
	public static final int PORT = 5;
	public static final int ELEMENT = 6;
	public static final int PARAM = 7;
	public static final int SUBPARAM = 8;
	public static final int ID = 9;

	
	public static final String[] nomeTipo = {
		"Grafo",
		"No",
		"Aresta",
		"porta de entrada",
		"porta de saida",
		"porta",
		"elemento",
		"Parametro",
		"SubParametro",
		"ID"
	};
	
	
	
	public int tipo;
	public String nome;
	public HashMap<String,Object> info;
	
	public Symbol(String _nome,int _tipo){
		
		this.tipo = _tipo;
		this.nome = _nome;
	}
	
public Symbol(String _nome, int _tipo, HashMap<String,Object> _info){
		
		this.tipo = _tipo;
		this.nome = _nome;
		this.info = _info;
	}

public Symbol(NodeInformation node, int _tipo, HashMap<String,Object> _info){
	
	this.tipo = _tipo;
	this.nome = node.image;
	this.info = _info;
}

public void dump(String ident){
	System.out.println(ident + "nome: " + this.nome);
	System.out.println(ident + "tipo: " + nomeTipo[this.tipo]);
	dumpinfo(ident);
}

private void dumpinfo(String ident){
	if(this.info == null) return;
	System.out.println(ident + "info:");
	for (String nome: info.keySet()){
		System.out.println(ident+"  " + nome +": "+info.get(nome).toString());
	}
}
	
	
}
