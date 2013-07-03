import java.util.HashMap;

/**
 * Classe onde guarda a tabela de simbolos <br>
 *<b>estrutura:</b>
 *
 * <ul>
 * <li>cada grafo terá uma tabela de simbolos própria</li>
 * <li>cada tabela estará guardada numa tabela de simbolos gerais nomeado "Table"</li>
 * <li>na classe será usado 2 iteradores que serão usadas ao longos dos metodos que serão
 * usados na analise da AST sendo eles "subTableIterator" e "graphNameIter" 
 * </ul>
 * </li>
 * </ul>
 * 
 * <b>regras:</b>
 * <ul>
 * <li>cada grafo deverá ter um nome unico</li>
 * <li>cada no, aresta e o id do grafo deverá ter um nome unico</li>
 * </ul>
 *
 * 
 * @author ei08158
 *
 */
public class SymbolTable {
	/**
	 * informacao da tabela de simbolos
	 */
	private HashMap<String,HashMap<String,Symbol> > Table = new HashMap<String,HashMap<String,Symbol> >();
	/**
	 * subtabela de simbolos
	 */
	private HashMap<String,Symbol> subTableIterator;
	/**
	 * iterador do nome do grafo
	 */
	private String graphNameIter;
	
	/**
	 * Constructor da tabla de Simbolos
	 */
	public SymbolTable(){
		
	}
	
	/**
	 * Constructor que por conveniência analisa a arvore depois de construida
	 * @param node - raiz da arvore sintatica
	 */
	public SymbolTable(SimpleNode node){
		analyse(node);
	}
	
	public void analyse(SimpleNode node){
			int childsize=node.jjtGetNumChildren();
		
		for(int iter=0;iter<childsize;iter++){
			SimpleNode childnod = (SimpleNode) node.jjtGetChild(iter);
			analyseGraph(childnod);	
			
		}
		
	}
	
	
	
	public boolean exists(String nome){
		if (Table.containsKey(nome))
			return true;
		else{
			for(String key:Table.keySet()){
				if(Table.get(key).containsKey(nome)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean exists(NodeInformation node){
		return exists(node.image);
	}
	
	public boolean exists(String nome,int tipo){
		if (Table.containsKey(nome))
			return true;
		else{
			for(String key:Table.keySet()){
				if(Table.get(key).containsKey(nome) && Table.get(key).get(nome).tipo == tipo){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean exists(NodeInformation node,int tipo){
		return exists(node.image,tipo);
	}
	
	/**
	 * mostra a representacao da tabela de simbolos num formato
	 * compativel com YAML
	 * 
	 * @param ident
	 */
	public void dump(String ident){
		for(String key:Table.keySet()){
			System.out.println(ident+key+":");
			for(String subkey:Table.get(key).keySet()){
				System.out.println(ident+"  "+subkey+":");
				Table.get(key).get(subkey).dump("    ");
			}
		}
	}
	
	private void checkDuplicate(NodeInformation key){
		if(subTableIterator.containsKey(key)){
			System.err.println("Error: "+key+" declared muliple times");
			System.err.println("line : " + String.valueOf(key.linePosition) 
					+ " , column : " + String.valueOf(key.columnPosition));
			System.err.println();
			System.exit(1);
		}
	}
	
	
	public void addSymbol(NodeInformation node,int tipo,HashMap<String,Object> info){
		checkDuplicate(node);
		Symbol sym= new Symbol(node,tipo,info);
		subTableIterator.put(sym.nome, sym);
	}
	
	public void addSymbol(NodeInformation nome,int tipo){
		addSymbol(nome,tipo,null);
	}
	
	
	
	public void addnode(String graphname,String nodename){
		subTableIterator=Table.get(graphname);
		Symbol sym= new Symbol(nodename,Symbol.NODE);
		subTableIterator.put(sym.nome, sym);
	}
	

	
	public Symbol getGraphElementSymbol(String graph,String port){
		return Table.get(graph).get(port); 
	}
	
	/**
	 * Analisa um grafo
	 * 
	 * 
	 * @param node
	 */
	public void analyseGraph(SimpleNode node){
		subTableIterator = new HashMap<String,Symbol>();
		NodeInformation info=((SimpleNode) node.jjtGetChild(0)).val;
		graphNameIter = info.image;
		addSymbol(info,Symbol.GRAPH);
		int size=node.jjtGetNumChildren();
		
		//faz a primeira verificação para verificar a topologia e a interface
		for(int iter=1;iter<size;iter++){
			SimpleNode childnode = (SimpleNode) node.jjtGetChild(iter);
			switch(childnode.id){
			case difparserTreeConstants.JJTTOPOLOGY: analyseTopology(childnode);	break;
			case difparserTreeConstants.JJTBASEDON:  analyseBasedon(childnode);  break;
			case difparserTreeConstants.JJTINTERFACE: analyseInterface(childnode); break;
			case difparserTreeConstants.JJTPARAMETERS: addParameters(childnode); break;
			}
		}
		Table.put(graphNameIter, subTableIterator);
		
		
	}
	
	private void addParameters(SimpleNode ASTNode){
		int size =  ASTNode.jjtGetNumChildren();
		for(int i=0 ; i < size ; ++i){
			SimpleNode node = ASTNode.jjtGetSimpleChild(i);
			HashMap<String,Object> info= new HashMap<String,Object>();
			
			int nodesize = node.jjtGetNumChildren();
			for(int j=1; j < nodesize ;++j){
				SimpleNode childNode= node.jjtGetSimpleChild(j);
				switch(childNode.id){
				case difparserTreeConstants.JJTTYPE:
					info.put("Type", childNode.val.image);
					break;
				
				case difparserTreeConstants.JJTVALUE:
					info.put("Type", ASTStaticParser.parsevalue(childNode));
					break;
				case difparserTreeConstants.JJTRANGE:
					info.put("Type", ASTStaticParser.parseRange(childNode));
					break;
				default:
				}
			}
			addSymbol(node.jjtGetSimpleChild(0).val, Symbol.PARAM , info);
		}
	}
	
	public static String parsevalue(SimpleNode valuenode){
		if(valuenode.jjtGetNumChildren() > 0){
			return ((SimpleNode) valuenode.jjtGetChild(0)).val.image;
		}
		else return valuenode.val.image;
	}

	/**
	 * analisa o bloco "Topology" do grafo
	 * 
	 * O bloco da topologia guarda os nós e as arestas do grafo 
	 *  
	 * {@link http://www.ece.umd.edu/DSPCAD/dif/difgrammar.html#basedon_body } 
	 * @param node - nó do tipo JJTGRAPH
	 */
	public void analyseTopology(SimpleNode node){
		int childsize=node.jjtGetNumChildren();
		
		for(int iter=0;iter<childsize;iter++){
			SimpleNode childnod = (SimpleNode) node.jjtGetChild(iter);
			if(childnod.id == difparserTreeConstants.JJTNODES){
				addNodes(childnod);
			}
			else if(childnod.id == difparserTreeConstants.JJTEDGES){
				addEdges(childnod);
			}
		}
	}
	
	/**
	 * Adiciona os nós para a tabela de símbolos.<br>
	 * as informações guardadas nas arestas são:<br>
	 * <ul>
	 * <li><b>graph</b> - o nome do grafo (para referencias futuras)</li>
	 * </ul>
	 * 
	  
	 * @param node - o nó da AST onde contem os nós
	 */
	protected void addNodes(SimpleNode node){
		int size=node.jjtGetNumChildren();
		for(int iter=0;iter<size;iter++){
			NodeInformation nodeinfo=((SimpleNode) node.jjtGetChild(iter)).val;
			HashMap<String,Object> info= new HashMap<String,Object>();
			info.put("graph", graphNameIter);
			addSymbol(nodeinfo, Symbol.NODE,info);
		}
	}
	/**
	 * Adiciona as arestas para a tabela de símbolos<br>
	 * 
	 * as informações guardadas nas arestas são:<br>
	 * <ul>
	 * <li><b>start</b> - o no inicial</li>
	 * <li><b>end</b> - o no final</li>
	 * <li><b>graph</b> - o nome do grafo (para referencias futuras)</li>
	 * </ul>
	 * 
	 * @param node - o nó da AST onde contem as arestas
	 */
	protected void addEdges(SimpleNode node){
		int size=node.jjtGetNumChildren();
		for(int iter=0;iter<size;iter++){
			// nó "Edge"
			SimpleNode childNode = (SimpleNode) node.jjtGetChild(iter); 
			NodeInformation edgeinfo=((SimpleNode) childNode.jjtGetChild(0)).val;
			String startNodeName=((SimpleNode) childNode.jjtGetChild(1)).val.image;
			String endNodeName=((SimpleNode) childNode.jjtGetChild(2)).val.image;
			
			HashMap<String,Object> info= new HashMap<String,Object>();
			info.put("start",startNodeName);
			info.put("end", endNodeName);
			info.put("graph", graphNameIter);
			addSymbol(edgeinfo,Symbol.EDGE,info); 
		
		}
	}
	
	protected void analyseInterface(SimpleNode node){
				int size = node.jjtGetNumChildren();
				for(int j = 0;j< size;j++){
					analyseInterfaceExpression((SimpleNode) node.jjtGetChild(j));
				}
			
		
	}
	
	protected void analyseInterfaceExpression(SimpleNode node){
		int size=node.jjtGetNumChildren();
		int nodeType = (node.id == difparserTreeConstants.JJTINPUTS)?Symbol.INPUTPORT:Symbol.OUTPUTPORT;
		for(int iter=0;iter<size;iter++){
			// no "Port"
			SimpleNode childNode = (SimpleNode) node.jjtGetChild(iter); 
			NodeInformation portname=((SimpleNode) childNode.jjtGetChild(0)).val;
			String nodeName=((SimpleNode) childNode.jjtGetChild(1)).val.image;
			HashMap<String,Object> info= new HashMap<String,Object>();
			info.put("node", nodeName);
			info.put("graph", graphNameIter);
			addSymbol(portname, nodeType,info);
			
		}
	}
	
	/**
	 * analisa o bloco "Basedon" do grafo
	 * 
	 * O bloco basedon guarda o identificador do grafo
	 * 
	 * @param node - nÃ³ do tipo JJTGRAPH
	 */
	protected void analyseBasedon(SimpleNode node){
		//Ã© esperado que o bloco Basedon tenha apenas um filho
		addSymbol(((SimpleNode) node.jjtGetChild(0)).val,Symbol.ID);
	}
	
}
