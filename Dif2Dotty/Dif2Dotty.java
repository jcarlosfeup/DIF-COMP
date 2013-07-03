import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Class utilizada para gerar um ficheiro .txt de linguagem dotty
 * a partir da linguagem dif
 *
 */
public class Dif2Dotty {
	private static String _graphName = "defaultName";
	private static HashMap<String, HashMap<String, String>> nodes = new HashMap<String, HashMap<String,String>>();
	private static HashMap<HashMap<String, String>, HashMap<String, String>> edges = new HashMap<HashMap<String, String>, HashMap<String,String>>();
	private static String _fileName = "dotty.txt";
	
	/**
	 * Fun��o que define o nome do ficheiro que � gerado na fun��o Flush()
	 * @param name
	 */
	public static void SetFileName(String name){
		_fileName = name;
	}
	
	/**
	 * Fun��o usada para iniciar um grafo e dar o nome a esse grafo
	 * se n�o for chamada esta fun��o o grafo � criado com o defaultName
	 * @param nome
	 */
	public static void GraphBegin(String nome){
		_graphName = nome;
	}
	
	/**
	 * Fun��o usada para adicionar um novo n� ao grafo sem nenhum atributo associado
	 * n�o volta a adicionar se esse n� ja existir
	 * @param nome
	 */
	public static void NewNode(String nome){
		if(!nodes.containsKey(nome)){
			HashMap<String,String> attributes = new HashMap<String, String>();
			nodes.put(nome, attributes);
		}
	}
	
	/**
	 * Fun��o que adiciona um atributo do tipo attributeName e com valor attributeValue ao n� node
	 * caso esse node ainda n�o tenha sido criado ele cria-o nesta fun��o.
	 * Assim sendo quando se quer criar um n� ja com um atributo basta chamar esta fun��o
	 * @param node
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void AddAttributeToNode(String node, String attributeName, String attributeValue){
		if(!nodes.containsKey(node))
			NewNode(node);
		
		HashMap<String, String> attr = nodes.get(node);
		
		if(!attr.containsKey(attributeName)){
			attr.put(attributeName, attributeValue);
		}
	}
	
	/**
	 * Fun��o que adicionada um edge ao grafo com o formato node1 -> node2 caso ele n�o exista
	 * Caso os n�s usados neste edge n�o tenham sido criados anteriormente n�o 
	 * tras problema ao grafo porque o dotty n�o requer que os n�s sejam instanciados anteriormente
	 * @param node1
	 * @param node2
	 */
	public static void NewEdge(String node1, String node2){
		HashMap<String, String> edge = new HashMap<String, String>();
		edge.put(node1, node2);
		if(!edges.containsKey(edge)){
			HashMap<String,String> attributes = new HashMap<String, String>();
			edges.put(edge, attributes);
		}
	}
	
	/**
	 * Fun��o que adiciona um atributo do tipo attributeName e com valor attributeValue ao
	 * edge node1 -> node2
	 * Se este edge n�o foi criado anteriormente ele fa-lo nesta fun��o
	 * @param node1
	 * @param node2
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void AddAttributeToEdge(String node1, String node2, String attributeName, String attributeValue){
		HashMap<String, String> edge = new HashMap<String, String>();
		edge.put(node1, node2);
		
		if(!edges.containsKey(edge))
			NewEdge(node1, node2);
			
		
		HashMap<String, String> attri = edges.get(edge);
		
		
		if(!attri.containsKey(attributeName)){
			attri.put(attributeName, attributeValue);
		}
	}
	
	/**
	 * Indica o fim do grafo que por sua vez chama a fun��o Flush()
	 */
	public static void GraphEnd(){
		Flush();
	}
	
	/**
	 * Fun��o que escreve para um ficheiro de texto o grafo criado
	 */
	public static void Flush(){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(_fileName));
			
			//inicia o grafo
			out.write("digraph " + _graphName + " {\n");
			
			//Escreve todos os nodes guardados
			for (Iterator<String> it = nodes.keySet().iterator(); it.hasNext();) {  
				String key = (String) it.next();
				HashMap<String, String> value = nodes.get(key);
				out.write("\t" + key);
				if(!value.isEmpty()){
					out.write(" [");
					String attr = "";
					for (Iterator<String> it2 = value.keySet().iterator(); it2.hasNext();) {  
						String key2 = (String) it2.next();
						String value2 = value.get(key2);
						attr+= key2 + "=" + value2 + ",";
					}
					attr = attr.substring(0, attr.length()-1);
					out.write(attr);
					out.write("]");
				}
				out.write(";\n");
				
			}
			
			//Escreve todos os edges guardados
			for (Iterator<HashMap<String, String>> it = edges.keySet().iterator(); it.hasNext();) {  
				HashMap<String, String> key = (HashMap<String, String>) it.next();
				HashMap<String, String> value = edges.get(key);
				String tmpkey = key.keySet().iterator().next();
				out.write("\t" + tmpkey + " -> " + key.get(tmpkey));
				if(!value.isEmpty()){
					out.write(" [");
					String attr = "";
					for (Iterator<String> it2 = value.keySet().iterator(); it2.hasNext();) {  
						String key2 = (String) it2.next();
						String value2 = value.get(key2);
						attr+= key2 + "=" + value2 + ",";
					}
					attr = attr.substring(0, attr.length()-1);
					out.write(attr);
					out.write("]");
				}
				out.write(";\n");
			}
			
			//fecha o grafo e o fd
			out.write("}");
			out.close();
			
		} catch (IOException e) {
			System.out.println("Could not open file\n " + e.toString() );
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Exemplo de um grafo
		GraphBegin("testG");
		AddAttributeToNode("node1", "size", "2");
		AddAttributeToNode("node1", "color", "red");
		AddAttributeToEdge("node1", "node2", "size", "5");
		AddAttributeToEdge("node1", "node2", "style", "filled");
		AddAttributeToEdge("node2", "node1", "color", "pink");
		GraphEnd();

	}

}
