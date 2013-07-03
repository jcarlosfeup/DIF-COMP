public class AnaliseSemantica {

	private SymbolTable table;
	private CodeGenerator generator;
	private String currentGraphName;
	private int numErrors = 0,numWarnings = 0;

	public AnaliseSemantica(SymbolTable _table,CodeGenerator _generator){
		table= _table;	
		generator = _generator;
	}
	
	public AnaliseSemantica(SymbolTable _table,CodeGenerator _generator,SimpleNode node){
		table= _table;
		generator = _generator;
		analyse(node);
	}

	public void analyse(SimpleNode node){
		int size=node.jjtGetNumChildren();
		for(int i = 0;i< size;i++){
			analyseGraph((SimpleNode) node.jjtGetChild(i));
		}
		if(numWarnings > 0){
			System.err.println(String.valueOf(numWarnings) + " warnings");
		}
		if(numErrors > 0){
			System.err.println(String.valueOf(numErrors) + " errors");
			System.exit(2);
		}
	}

	/**
	 * Analisa o bloco graph
	 * 
	 * Analisa o grafo todo
	 */
	protected void analyseGraph(SimpleNode node){
		NodeInformation graphNameinfo = ((SimpleNode) node.jjtGetChild(0)).val;
		if(!generator.isValidGraphName(graphNameinfo.image)){
			String newName = "_"+graphNameinfo.image + "_";
			printWarning(graphNameinfo.image + " is not a valid name for a graph ; changing to "+newName, graphNameinfo);
			graphNameinfo.image = newName;
		}
		currentGraphName = graphNameinfo.image;
		
		int size=node.jjtGetNumChildren();
		for(int i = 1;i< size;i++){
			SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);
			switch (childNode.id) {
			case difparserTreeConstants.JJTINTERFACE:
				analyseInterface(childNode);
				break;
			case difparserTreeConstants.JJTREFINEMENT:
				analyseRefinement(childNode);
				break;
			case difparserTreeConstants.JJTATTRIBUTE:
				analyseAtribute(childNode,false);
				break;
			case difparserTreeConstants.JJTBUILTINATTRIBUTE:
				analyseAtribute(childNode,true);
				break;
			case difparserTreeConstants.JJTACTOR:
				analyseActorBody(childNode);
				break;
			

			}
		}
	}


	/**
	 * analisa o bloco "Interface" do grafo 
	 * 
	 * o bloco "Interface" guarda a Interface do grafo, ou
	 * seja, guarda a informaÃ§Ã£o sobre os nÃ³s de entrada e saida
	 * 
	 * @param node - nÃ³ do tipo JJTGRAPH
	 */
	protected void analyseInterface(SimpleNode node){
				int childSize = node.jjtGetNumChildren();
				for(int j = 0;j< childSize;j++){
					analyseInterfaceExpression((SimpleNode) node.jjtGetChild(j));
				}
			
	}

	/**
	 * Analisa as expressoes da Interface os nos de entradas esperados tem que
	 *  ter os ids JJTINPUTS ou JJTOUTPUTS
	 * 
	 * 
	 * @param node - o nÃ³ tipo JJTINPUTS ou JJTOUTPUTS
	 * 
	 * @since o no vem com o id e os ids quando chegarem a esta fase
	 *  de compilaÃ§Ã£o tem de ser apenas o JJTINPUTS ou JJTOUTPUTS pode-se 
	 *  verificar se as portas sao de entrada ou de saida
	 */
	protected void analyseInterfaceExpression(SimpleNode node){
		int size=node.jjtGetNumChildren();
		for(int iter=0;iter<size;iter++){
			SimpleNode childNode = (SimpleNode) node.jjtGetChild(iter); 
			int childsize=childNode.jjtGetNumChildren();
			for(int childiter=0;childiter<childsize;childiter++){				
				NodeInformation nodeName=((SimpleNode) childNode.jjtGetChild(1)).val;
				if(!table.exists(nodeName.image,Symbol.NODE)){
					table.addnode(currentGraphName, nodeName.image);
					printWarning(nodeName.image +" undeclared, creating it",nodeName);
				}
				
			}
		}
	}

	

	/**
	 * Analisa o bloco "Refinement"
	 * @param node
	 */
	protected void analyseRefinement(SimpleNode node){

		int size = node.jjtGetNumChildren();
		for(int i = 0;i< size;i++){
			SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);

			if(childNode.id == difparserTreeConstants.JJTDEFINITION){

				SimpleNode node_graph = (SimpleNode) childNode.jjtGetChild(0);
				SimpleNode node_node = (SimpleNode) childNode.jjtGetChild(1);


				if(!table.exists(node_graph.val.image,Symbol.GRAPH)){
					printError("graph " + node_graph.val.image +" undeclared",node_graph.val);
				}
				else if(!table.exists(node_node.val.image,Symbol.NODE)){
					printError(node_node.val.image +" node undeclared",node_node.val);
				}
			
			}

			if(childNode.id == difparserTreeConstants.JJTEXPRESSION){
				int child_size = childNode.jjtGetNumChildren();
				for(int j = 0;j < child_size;j++){

					SimpleNode childExpr = (SimpleNode) childNode.jjtGetChild(j);

					if(childExpr.id == difparserTreeConstants.JJTPORT){
						checkPorts(childExpr);
					}
					else if(childExpr.id == difparserTreeConstants.JJTPARAMETERS){
						checkParams(childExpr);
					}
				}
			}
		}
	}

	protected void checkPorts(SimpleNode node){

		SimpleNode node_port = (SimpleNode) node.jjtGetChild(0);
		SimpleNode node_element = (SimpleNode) node.jjtGetChild(1);

		if(table.exists(node_port.val.image,Symbol.PORT) && table.exists(node_element.val.image,Symbol.ELEMENT)){

		}
		else{
			System.err.println("Erro!O bloco Ports n�o existe");
			System.exit(1);
		}
	}

	protected void checkParams(SimpleNode node){
		SimpleNode node_subparam = (SimpleNode) node.jjtGetChild(0);
		SimpleNode node_param  = (SimpleNode) node.jjtGetChild(1);

		if(!table.exists(node_subparam.val.image,Symbol.SUBPARAM)){
			printError("subparam" + node_subparam.val.image +" não declarado",node_subparam.val);
		}
		else if(!table.exists(node_param.val.image,Symbol.PARAM)){
			printError("param" + node_param.val.image +" não declarado",node_param.val);
		}
		

	}

	/**
	 * Analisa o bloco dos Atributos
	 * 
	 * @param node
	 * @param builtin - verifica se e um bloco BuiltInAtrribute
	 */
	protected void analyseAtribute(SimpleNode node,boolean builtin){

		NodeInformation attr_name_info = ((SimpleNode) node.jjtGetChild(0)).val;
		String attr_name = attr_name_info.image;
		if(builtin){
			if(!attr_name.equals("consumption") && !attr_name.equals("production") && !attr_name.equals("delay")){
				printError(attr_name+" is not a valid built-in attribute", attr_name_info);
			}
		}
		
		int size = node.jjtGetNumChildren();

		for(int i = 1;i< size;i++){
			SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);
			int childsize = childNode.jjtGetNumChildren();
			NodeInformation childname = ((SimpleNode) childNode.jjtGetChild(0)).val;
			Symbol sym = table.getGraphElementSymbol(currentGraphName, childname.image);
			if(!builtin){
			switch(sym.tipo){
				case Symbol.NODE:
					if(!generator.isValidAttribute(attr_name, CodeGenerator.NODE))
						printError("invalid atribute : "+attr_name,childname);
					break;
				case Symbol.EDGE:
					if(!generator.isValidAttribute(attr_name, CodeGenerator.EDGE))
						printError("invalid atribute : "+attr_name,childname);
					break;
				default:
					printError(childname.image + "does not poin to to a node or an edge",childname);
				}
			}
			for(int j = 1;j< childsize;j++){
				SimpleNode grandchildNode = (SimpleNode) childNode.jjtGetChild(j);
				if(grandchildNode.id == difparserTreeConstants.JJTNAME){

					/**Significa que é um reference*/
					if(childsize == 2){

						if(!table.exists(grandchildNode.val.image)){
							printError(grandchildNode.val.image +" element undeclared",grandchildNode.val);
						}
					
					}
					/** subelement_assign*/
					else if(j > 1){
						//TODO:

					}

				}
				else if(grandchildNode.id == difparserTreeConstants.JJTID_TAIL){

					SimpleNode grandchildNode2 = (SimpleNode) grandchildNode.jjtGetChild(0);

					if(!table.exists(grandchildNode2.val.image)){

						printError(grandchildNode2.val.image +" node undeclared",grandchildNode2.val);
					}

				}
			}
		}
	}



	/**
	 * Analisa o bloco "actor_body"
	 * @param node
	 */
	protected void analyseActorBody(SimpleNode node){

		int size = node.jjtGetNumChildren();

		for(int i = 3;i< size;i++){
			SimpleNode childNode = (SimpleNode) node.jjtGetChild(i);
			NodeInformation childname = ((SimpleNode) childNode.jjtGetChild(0)).val;
			if(!generator.isValidAttribute(childname.image, CodeGenerator.NODE))
				printWarning(childname.image + "is not a valid attribute for the compiling language",childname);
			
			//semantic checking ignored
		}
	}
	
	protected void analyseTopology(SimpleNode node) {
		int size = node.jjtGetNumChildren();
		for (int i = 0; i < size; i++) {
			SimpleNode FatherNode = (SimpleNode) node.jjtGetChild(i);
			if (FatherNode.id == difparserTreeConstants.JJTEDGES) {
				int sizeFather = FatherNode.jjtGetNumChildren();
				for (int j = 0; j < sizeFather; j++) {
					SimpleNode EdgeNode = (SimpleNode) FatherNode
							.jjtGetChild(i);
					NodeInformation startnode = ((SimpleNode) EdgeNode
							.jjtGetChild(1)).val;
					if (!table.exists(startnode,Symbol.NODE)) {
						printError(startnode.image +" node udeclared",startnode);
					}
					NodeInformation lastnode = ((SimpleNode) EdgeNode
							.jjtGetChild(2)).val;
					if (!table.exists(lastnode,Symbol.NODE)) {
						printError(lastnode +" node undeclared",lastnode);
				
					}
				}

			}

		}

	}
	
	private void printError(String output,NodeInformation info){
		System.err.println("error : " + output );
		printlinecolumn(info);
		numErrors++;
	}
	
	private void printWarning(String output,NodeInformation info){
		System.err.println("warning : " + output );
		printlinecolumn(info);
		numWarnings++;
	}
	
	private void printlinecolumn(NodeInformation info){
		System.err.println("line : " + String.valueOf(info.linePosition) 
				+ " , column : " + String.valueOf(info.columnPosition));
		System.err.println();
		}
}