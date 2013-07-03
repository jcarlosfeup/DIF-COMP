import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class IntermediateRepresentation {
	public static final String attrnodename = "Attributes";
	public static final String subnodename = "SubGraphs";
	public static final String nodesNodename = "Nodes";
	public static final String edgesNodename = "Edges";
	public static final String inputNodename = "Inputs";
	public static final String outputNodename = "Outputs";
	public static final String parameterNodename = "Parameters";
	
	public static final String typeNodename = "Type";
	public static final String valNodename = "Value";


	private IRNode root;
	private SymbolTable table;

	// guarda o grafo em memoria para diminuir o tamanho do codigo
	private IRNode currGraphNode;
	private ArrayList<SubGraphRef> subgraphslist = new ArrayList<SubGraphRef>();

	public IntermediateRepresentation(SimpleNode AST,SymbolTable stable) {
		root = new IRNode("Start");
		table = stable;
		createIR(AST);
	}
	
	public IRNode getTree(){
		return root;
	}

	/**
	 * construtor de um grafo a partir do ficheiro "difir" 
	 * @param file - stream do ficheiro
	 */
	public IntermediateRepresentation(InputStream file) {
		DataInputStream in = new DataInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int iter = 0;
		IRNode currnode = null;
		// Read File Line By Line
		try {
			if ((strLine = br.readLine()) == null) {
				System.exit(0);
			} else {
				root = new IRNode(strLine);
				currnode = root;
			}
			while ((strLine = br.readLine()) != null) {
				char[] str = strLine.toCharArray();
				int length = str.length;
				while (iter >= length) {
					iter--;
					currnode = currnode.parent();
				}
				if (str[iter] == ' ') {
					iter++;
					currnode = new IRNode(strLine.substring(iter), currnode);
				} else if (str[iter - 1] == ' ') {
					currnode = new IRNode(strLine.substring(iter),
							currnode.parent());
				} else {
					// decrementado para minizar os calculos com
					// "while(str[whitespaceNumber-1]==' ');"
					iter--;
					do {
						iter--;
						currnode = currnode.parent();
					} while (str[iter] != ' ');
					iter++;
					currnode = new IRNode(strLine.substring(iter),
							currnode.parent());

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * cria a arvore de Representacao Intermedia AST
	 * @param AST - raiz da AST
	 */
	public void createIR(SimpleNode AST) {
		int num = AST.jjtGetNumChildren();
		for (int i = 0; i < num; ++i) {
			SimpleNode simplenode =  AST.jjtGetSimpleChild(i);
			// busca o nome do grafo
			String graphName = ( simplenode.jjtGetSimpleChild(0)).val.image;
			currGraphNode = new IRNode(graphName);
			createGraph(simplenode);
			root.appendNode(currGraphNode);
		}
		CreateSubGraphs();
	}

	/**
	 * cria uma arvore representando um grafo
	 * @param ASTNode - no da AST que representa o grafo
	 */
	private void createGraph(SimpleNode ASTNode) {
		int num = ASTNode.jjtGetNumChildren();
		for (int i = 0; i < num; ++i) {
			SimpleNode simplechildnode =  ASTNode.jjtGetSimpleChild(i);
			switch (simplechildnode.id) {
			case difparserTreeConstants.JJTTOPOLOGY:
				analyseTopology(simplechildnode);
				break;
			case difparserTreeConstants.JJTINTERFACE:
				analyseInterface(simplechildnode);
				break;
			case difparserTreeConstants.JJTBUILTINATTRIBUTE:
			case difparserTreeConstants.JJTATTRIBUTE:
				analyseAttributes(simplechildnode);
				break;
			case difparserTreeConstants.JJTREFINEMENT:
				analyseRefinement(simplechildnode);
				break;
			case difparserTreeConstants.JJTPARAMETERS:
				addParameters(simplechildnode);
				break;
			case difparserTreeConstants.JJTACTOR:
				currGraphNode.appendNode(convert_to_IR(simplechildnode));

			}
		}
	}

	/**
	 * cria as poratas input e output
	 * @param node
	 */
	private void analyseInterface(SimpleNode node) {
		int size = node.jjtGetNumChildren();
		for (int j = 0; j < size; j++) {
			analyseInterfaceExpression( node.jjtGetSimpleChild(j));
		}

	}

	private void analyseInterfaceExpression(SimpleNode node) {
		int size = node.jjtGetNumChildren();
		String irnodename = (node.id == difparserTreeConstants.JJTINPUTS) ? inputNodename : outputNodename;
		IRNode irNode = currGraphNode.getOrCreateChild(irnodename);
		for (int iter = 0; iter < size; iter++) {
			// no "Port"
			SimpleNode childNode =  node.jjtGetSimpleChild(iter);
			String portname = ( childNode.jjtGetSimpleChild(0)).val.image;
			String nodeName = ( childNode.jjtGetSimpleChild(1)).val.image;
			IRNode portnode = new IRNode(portname, irNode);
			IRNode nodenode = new IRNode("Node", portnode);
			new IRNode(nodeName, nodenode);
		}
	}

	/**
	 * analisa a topologia para criar os nos e as arestas
	 * @param ASTNode
	 */
	private void analyseTopology(SimpleNode ASTNode) {
		IRNode edgesnode = new IRNode(edgesNodename), nodesnode = new IRNode(nodesNodename);
		int num = ASTNode.jjtGetNumChildren();

		for (int i = 0; i < num; ++i) {
			SimpleNode simpleChildNode =  ASTNode.jjtGetSimpleChild(i);
			if (simpleChildNode.id == difparserTreeConstants.JJTEDGES) {
				int childnum = simpleChildNode.jjtGetNumChildren();
				for (int j = 0; j < childnum; ++j) {
					edgesnode.appendNode(newEdgeNode( simpleChildNode
									.jjtGetSimpleChild(j)));
				}
			}
			if (simpleChildNode.id == difparserTreeConstants.JJTNODES) {
				int childnum = simpleChildNode.jjtGetNumChildren();
				for (int j = 0; j < childnum; ++j) {
					IRNode node = new IRNode(
							( simpleChildNode.jjtGetSimpleChild(j)).val.image);
					nodesnode.appendNode(node);
				}
			}
		}
		if (!edgesnode.isleaf()) {
			currGraphNode.appendNode(edgesnode);
		}
		if (!nodesnode.isleaf()) {
			currGraphNode.appendNode(nodesnode);
		}
	}

	/**
	 * Analisa os atributos e mete-os na arvore os atributos serao filhos de nos
	 * de nos e de arestas da RI
	 * 
	 * @param node
	 */
	private void analyseAttributes(SimpleNode node) {
		int size = node.jjtGetNumChildren();
		String attributename = ( node.jjtGetSimpleChild(0)).val.image;

		for (int i = 1; i < size; i++) {
			SimpleNode childNode =  node.jjtGetSimpleChild(i);
			int childsize = childNode.jjtGetNumChildren();
			NodeInformation childinfo = ( childNode.jjtGetSimpleChild(0)).val;

			// cria ou busca o atributo para o no o elemento do grafo
			IRNode elementNode = searchCurrentGraphElemnt(childinfo.image);
			if (elementNode == null) {
				System.out.println("elementNode nao encontrado :"
						+ childinfo.image);
				System.out.println("Arvore em questao:");
				currGraphNode.dump("  ");

				System.exit(1);
			}
			IRNode attributesNode = elementNode.getOrCreateChild(attrnodename);
			IRNode attrNode = attributesNode.getOrCreateChild(attributename);

			// mete o(s) valor(es) no atributo
			for (int j = 1; j < childsize; j++) {
				SimpleNode grandchildNode = childNode.jjtGetSimpleChild(j);
				
				if (grandchildNode.id == difparserTreeConstants.JJTVALUE) {
					new IRNode(ASTStaticParser.parsevalue(grandchildNode), attrNode);
				} else if (grandchildNode.id == difparserTreeConstants.JJTNAME) {
					verifyparam(attrNode,grandchildNode);
				} else if (grandchildNode.id == difparserTreeConstants.JJTID_TAIL) {
					verifyparam(attrNode,grandchildNode.jjtGetSimpleChild(0));
				}
			}
		}
	}
	
	/**
	 * verifca se referencia a um parametro caso for cria o no com o seu o valor
	 * caso contrario cria com o texto escrito
	 * @param attrNode
	 * @param grandchildNode
	 */
	private void verifyparam(IRNode attrNode,SimpleNode grandchildNode){
		//verifica se é um parametro
		Symbol sym = table.getGraphElementSymbol(currGraphNode.toString(), grandchildNode.val.image);
		if(sym != null){
			if(sym.tipo == Symbol.PARAM && sym.info.containsKey("Value")){
				new IRNode(sym.info.get("Value").toString(), attrNode);
			}
			else{
				IRNode parent = attrNode.parent();
				parent.removechild(attrNode);
				//o no "Attributes" nao pode ser uma folha portanto remove-a se for
				if(parent.isleaf()) parent.parent().removechild(parent);
			}
		}
		else new IRNode(grandchildNode.val.image, attrNode);
	}
	
	
	private void addParameters(SimpleNode ASTNode){
		int size =  ASTNode.jjtGetNumChildren();
		for(int i=0 ; i < size ; ++i){
			SimpleNode node = ASTNode.jjtGetSimpleChild(i);
			IRNode Pars = currGraphNode.getOrCreateChild(parameterNodename);
			IRNode ParName = new IRNode(node.jjtGetSimpleChild(0).val.image,Pars);
			int nodesize = node.jjtGetNumChildren();
			for(int j=1; j < nodesize ;++j){
				SimpleNode childNode= node.jjtGetSimpleChild(j);
				switch(childNode.id){
				case difparserTreeConstants.JJTTYPE:{
					IRNode TypeName = ParName.getOrCreateChild(typeNodename);
					new IRNode(childNode.val.image,TypeName);
					break;
				}
				case difparserTreeConstants.JJTVALUE:{
					IRNode TypeName = ParName.getOrCreateChild(valNodename);
					new IRNode(ASTStaticParser.parsevalue(childNode),TypeName);
					break;
				}
				case difparserTreeConstants.JJTRANGE:{
					IRNode TypeName = ParName.getOrCreateChild(valNodename);
					new IRNode(ASTStaticParser.parseRange(childNode),TypeName);
					break;
				}
				default:
				}
			}
		}
	}

	/**
	 * Analisa o bloco refinement para identificar as subarvores
	 * sera guardado numa lista de referencia dos subgrafos
	 * que sera usado em CreateSubGraphs()
	 * @param ASTNode
	 */
	private void analyseRefinement(SimpleNode ASTNode) {
		int size = ASTNode.jjtGetNumChildren();
		String node_graph = null, node_node = null;
		HashMap<String, String> node_port = new HashMap<String, String>();
		for (int i = 0; i < size; i++) {
			SimpleNode childNode =  ASTNode.jjtGetSimpleChild(i);
			if (childNode.id == difparserTreeConstants.JJTDEFINITION) {
				node_graph = ( childNode.jjtGetSimpleChild(0)).val.image;
				node_node = ( childNode.jjtGetSimpleChild(1)).val.image;
			}
			if (childNode.id == difparserTreeConstants.JJTEXPRESSION) {
				int child_size = childNode.jjtGetNumChildren();
				for (int j = 0; j < child_size; j++) {

					String key = ( childNode.jjtGetSimpleChild(0)).val.image;
					String value = ( childNode.jjtGetSimpleChild(1)).val.image;
					node_port.put(key, value);

				}
			}
		}
		subgraphslist.add(new SubGraphRef(node_graph, currGraphNode.toString(),
				node_node, node_port));

	}

	/**
	 * cria uma arvore que representa uma aresta
	 * @param ASTEdgeNode - no da AST que representa uma aresta
	 * @return
	 */
	private IRNode newEdgeNode(SimpleNode ASTEdgeNode) {
		IRNode edge = new IRNode(( ASTEdgeNode.jjtGetSimpleChild(0)).val.image);
		IRNode nodes = new IRNode(nodesNodename,edge);
		new IRNode(( ASTEdgeNode.jjtGetSimpleChild(1)).val.image,nodes);
		new IRNode(( ASTEdgeNode.jjtGetSimpleChild(2)).val.image,nodes);
		return edge;
	}

	/**
	 * mostra a representacao da RI
	 * @param prefix
	 */
	public void dump(String prefix) {
		System.out.println(root.dump(prefix));
	}
	
	public String showRepresentation(String prefix) {
		return root.dump(prefix);
	}

	/**
	 * busca o elemento do grafo actual
	 * @param elname - nome do elemento
	 * @return - no da RI que representa o elemento se encontrado, se nao retorna null
	 */
	private IRNode searchCurrentGraphElemnt(String elname) {
		return searchGraphelementNode(elname, currGraphNode);
	}

	/**
	 * busca o elemento do grafo actual
	 * @param elname - nome do elemento
	 * @param node - no da RI que representa um grafo (ou subgrafo)
	 * @return - no da RI que representa o elemento se encontrado, se nao retorna null
	 */
	private IRNode searchGraphelementNode(String elname, IRNode node) {
		for (IRNode childnode : node.getChildren()) {
			IRNode elementNode = childnode.getchild(elname);
			if (elementNode != null)
				return elementNode;
		}
		return null;
	}
	
	/**
	 * busca um grafo (ou subgrafo) 
	 * @param graphname - nome do grafo
	 * @return - no IR de grafo ou subgrafo se encontrado, caso contrario retorna null
	 */
	private IRNode searchGraph(String graphname){
		for(IRNode childnode : root.getChildren()){
			if(childnode.toString().equals(graphname)) return childnode;
			IRNode subnode = searchRecursiveSubGraph(graphname,childnode);
			if(subnode != null) return subnode;
		}
		return null;
		
	}
	
	/**
	 * procura recursivamente o subgrafo
	 * 
	 * @param graphname - nome do grafo
	 * @param node - no que representa o grafo pai
	 * @return - no IR de grafo ou subgrafo se encontrado, caso contrario retorna null
	 */
	private IRNode searchRecursiveSubGraph(String graphname,IRNode node){
		IRNode nod = node.getchild(subnodename);
		if(nod == null) return null;
		for(IRNode childnode : nod.getChildren()){
			if(childnode.toString().equals(graphname)) return childnode;
			IRNode subnode = searchRecursiveSubGraph(graphname,childnode);
			if(subnode != null) return subnode;
		}
		return null;
	}
	
	
	/**
	 * this function will convert the AST tree or subtree to an IR one
	 * used for future reference
	 * 
	 * @param AST - AST  tree or subtree
	 * @return converted tree
	 */
	private IRNode convert_to_IR(SimpleNode AST){
		IRNode irnode = new IRNode(difparserTreeConstants.jjtNodeName[AST.id]);
		int size = AST.jjtGetNumChildren();
		for(int i=0; i < size; ++i){
			irnode.appendNode(convert_to_IR(AST.jjtGetSimpleChild(i)));
		}
		return irnode;
	}

	/**
	 * cria os subgrafos a partir das referencias
	 */
	private void CreateSubGraphs() {
		for (SubGraphRef ref : subgraphslist) {
			IRNode subgraph = searchGraph(ref.subgraph);
			IRNode graph = searchGraph(ref.graph);
			IRNode snode = searchGraphelementNode(ref.supernode, graph);
			IRNode subgraphs = graph.getOrCreateChild(subnodename);
			
			
			if (snode != null) {
				IRNode attr_node = snode.getchild(attrnodename);
				subgraph.appendNode(attr_node);
				snode.parent().removechild(snode);
			}
			
			for (Entry<String, String> entry:ref.node_port.entrySet()){
				Symbol subgrsym = table.getGraphElementSymbol(subgraph.toString(),entry.getKey());
				Symbol grsym = table.getGraphElementSymbol(graph.toString(),entry.getValue());
				if(grsym.tipo == Symbol.EDGE){
					IRNode edge = searchGraphelementNode(entry.getValue(),graph);
					IRNode NodeToModify=edge.getchild(nodesNodename).getchild((subgrsym.tipo == Symbol.INPUTPORT)?1:0);
					NodeToModify.setNodename(subgrsym.info.get("node").toString());
					
				}
			}
			
			subgraphs.appendNode(subgraph);
		}
	}

	/**
	 * classe que guarda uma referencia a um subgrafo
	 * @author omar
	 *
	 */
	public class SubGraphRef {
		public String subgraph;
		public String graph;
		public String supernode;
		public HashMap<String, String> node_port;

		public SubGraphRef(String subgr, String gr, String snode) {
			subgraph = subgr;
			graph = gr;
			supernode = snode;
		}

		public SubGraphRef(String subgr, String gr, String snode, HashMap<String, String> n_port) {
			subgraph = subgr;
			graph = gr;
			supernode = snode;
			node_port = n_port;
		}
	}
}
