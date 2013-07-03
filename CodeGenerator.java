import java.util.ArrayList;
import java.util.HashMap;


/**
 * Classe Abrstracta para criar geradores de codigo para grafos em varias linguagens
 * (neste caso para cria o gerador dot)
 * 
 * esta classe é usada para gerar codigo em que ela chama as funcoes abstractas
 * que deverão ser usadas na criação do codigo
 *  
 * @author omar
 *
 */
public abstract class CodeGenerator {
	
	/**
	 * tipo no (usado para identifica os atributos)
	 */
	public static final int NODE = 0;
	/**
	 * tipo aresta (usado para identifica os atributos)
	 */
	public static final int EDGE = 1;

	/**
	 * Verifica se o nome do grafo é valido <br />
	 * 
	 * é valido se nao houver classes em que esteja sintaticamente correcto <br />
	 * por exemplo, o dot nao pode aceitar nomes como "graph ou digraph" pois eles
	 * sao tokens usados para criar grafos
	 * 
	 * @param graphname - nome do grafo
	 * @return true se nome valido, false caso contrario
	 */
	public abstract boolean isValidGraphName(String graphname);
	
	/**
	 * Verifica se o atributo e valido para ser gerado
	 * 
	 * 
	 * @param attributename - nome do atributo
	 * @param type - tipo do objecto (no ou aresta)
	 * @return
	 */
	public abstract boolean isValidAttribute(String attributename,int type);
	
	/**
	 * Cria um no 
	 * 
	 * @param nodeName - nome do no
	 * @param attributes - os atributos do no
	 */
	public abstract void createNode(String nodeName,  HashMap<String,String> attributes);
	
	/**
	 * 
	 * Cria uma aresta
	 * 
	 * @param EdgeName - nome da aresta
	 * @param startNodeName - no inicial
	 * @param endNodeName - no final
	 * @param attributes - os atributos das arestas
	 */
	public abstract void createEdge(String EdgeName,String startNodeName, String endNodeName, HashMap<String,String> attributes);
	
	/**
	 * inicia a criacao um grafo
	 * 
	 * e obrigatorio criar o grafo antes de inserir nos ,arestas e grafos
	 * 
	 * @param graphname -nome do grafo
	 */
	public abstract void startGraph(String graphname);
	
	/**
	 * inicia a criacao de um subgrafo
	 * 
	 * devera ser chamado depois de startGraph()
	 * 
	 * @param SubGraphName
	 */
	public abstract void startSubGraph(String SubGraphName);
	/**
	 * adiciona atributos para o grafo ou subgrafo
	 * @param attributes
	 */
	public abstract void addGraphAttributes(HashMap<String,String> attributes);
	
	/**
	 * adiciona paramteros para o grafo ou subgrafo
	 * @param arrayList
	 */
	public abstract void addParameters(ArrayList<String[]> arrayList);
	/**
	 * finaliza a criacao do grafo
	 * devem ser finalizado a criacao de todos os subgrafos antes de finalizar o grafo
	 */
	public abstract void endGraph();
	/**
	 * finaliza a cria do sub grafo
	 */
	public abstract void endSubGraph();
	/**
	 * busca o atributo para o rotulo do inicio da aresta (para o atributo incorporado "production")
	 * @return atributo substituto do "production"
	 */
	public abstract String edgeStartLabelAttribute();
	/**
	 * busca o atributo para o rotulo do fim da aresta (para o atributo incorporado "consumption")
	 * @return atributo substituto do "consumption"
	 */
	public abstract String edgeEndLabelAttribute();
	
	/**
	 * mostra o codigo final
	 * @return
	 */
	public abstract String outputcode();
	
	/**
	 * gera o codigo final
	 * @param IR
	 */
	public void generate(IRNode IR){
		for(IRNode graph: IR.getChildren()){
			startGraph(graph.toString());
			for(IRNode type: graph.getChildren()){
				String ntype = type.toString();
				if(ntype.equals(IntermediateRepresentation.nodesNodename)){
					for(IRNode nodes:type.getChildren()){
						createNode(nodes.toString(),getAttributes(nodes));
					}
				}
				else if(ntype.equals(IntermediateRepresentation.edgesNodename)){
					for(IRNode edges:type.getChildren()){
						IRNode nodes = edges.getchild(IntermediateRepresentation.nodesNodename);
						createEdge(edges.toString(),nodes.getchild(0).toString(),
								nodes.getchild(1).toString(),getAttributes(edges));
					}
				}
				else if(ntype.equals(IntermediateRepresentation.subnodename)){
					for(IRNode subgraph:type.getChildren()){
						generateSub(subgraph);
					}
				}
				else if(ntype.equals(IntermediateRepresentation.parameterNodename)){
					addParameters(getParamteters(type));
				}
			}
			endGraph();
		}
	}
	
	/**
	 * gera o codigo do subgrafo indetificado pelo no da Representacao Intermedia
	 * @param subg - no RI que representa um subgrafo
	 */
	private void generateSub(IRNode subg){
		startSubGraph(subg.toString());
		
		for(IRNode type: subg.getChildren()){
			String ntype = type.toString();
			if(ntype.equals(IntermediateRepresentation.nodesNodename)){
				for(IRNode nodes:type.getChildren()){
					createNode(nodes.toString(),getAttributes(nodes));
				}
			}
			else if(ntype.equals(IntermediateRepresentation.edgesNodename)){
				for(IRNode edges:type.getChildren()){
					IRNode nodes = edges.getchild(IntermediateRepresentation.nodesNodename);
					createEdge(edges.toString(),nodes.getchild(0).toString(),
							nodes.getchild(1).toString(),getAttributes(edges));
				}
			}
			else if(ntype.equals(IntermediateRepresentation.subnodename)){
				for(IRNode subgraph:type.getChildren()){
					generateSub(subgraph);
				}
			}
			else if(ntype.equals(IntermediateRepresentation.attrnodename)){
				addGraphAttributes(getAttributes(subg));
			}
			else if(ntype.equals(IntermediateRepresentation.parameterNodename)){
				addParameters(getParamteters(type));
			}
		}
		
		endSubGraph();
	}
	
	/**
	 * busca os nomes e valores dos atributos 
	 * 
	 * @param node - no RI que representam os atributos
	 * @return mapa com os atributos
	 */
	private HashMap<String,String> getAttributes(IRNode node){
		HashMap<String,String> ret = new HashMap<String,String>();
		IRNode Attrs = node.getchild(IntermediateRepresentation.attrnodename);
		if(Attrs != null){
			for(IRNode Attr: Attrs.getChildren()){
				String attrname = Attr.toString();
				if(attrname.equals("production")) attrname = edgeStartLabelAttribute();
				else if(attrname.equals("consumption")) attrname = edgeEndLabelAttribute();
				ret.put(attrname, Attr.getchild(0).toString());
			}
		}
		return ret;
	}
	
	private ArrayList<String[]> getParamteters(IRNode parNode){
		ArrayList<String[]>  ret = new ArrayList<String[]> ();
		for(IRNode par:parNode.getChildren()){
			String[] str = {par.toString(),"",""}; 
			IRNode type = par.getchild(IntermediateRepresentation.typeNodename),
					val = par.getchild(IntermediateRepresentation.valNodename);
			if(type != null)str[1] = type.toString();
			if(val != null)str[2] = val.toString();
		}
		return ret;
	}


}
