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
	 * Função que define o nome do ficheiro que é gerado na função Flush()
	 * @param name
	 */
	public static void SetFileName(String name){
		_fileName = name;
	}
	
	/**
	 * Função usada para iniciar um grafo e dar o nome a esse grafo
	 * se não for chamada esta função o grafo é criado com o defaultName
	 * @param nome
	 */
	public static void GraphBegin(String nome){
		_graphName = nome;
	}
	
	/**
	 * Função usada para adicionar um novo nó ao grafo sem nenhum atributo associado
	 * não volta a adicionar se esse nó ja existir
	 * @param nome
	 */
	public static void NewNode(String nome){
		if(!nodes.containsKey(nome)){
			HashMap<String,String> attributes = new HashMap<String, String>();
			nodes.put(nome, attributes);
		}
	}
	
	/**
	 * Função que adiciona um atributo do tipo attributeName e com valor attributeValue ao nó node
	 * caso esse node ainda não tenha sido criado ele cria-o nesta função.
	 * Assim sendo quando se quer criar um nó ja com um atributo basta chamar esta função
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
	 * Função que adicionada um edge ao grafo com o formato node1 -> node2 caso ele não exista
	 * Caso os nós usados neste edge não tenham sido criados anteriormente não 
	 * tras problema ao grafo porque o dotty não requer que os nós sejam instanciados anteriormente
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
	 * Função que adiciona um atributo do tipo attributeName e com valor attributeValue ao
	 * edge node1 -> node2
	 * Se este edge não foi criado anteriormente ele fa-lo nesta função
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
	 * Indica o fim do grafo que por sua vez chama a função Flush()
	 */
	public static void GraphEnd(){
		Flush();
	}
	
	/**
	 * Função que escreve para um ficheiro de texto o grafo criado
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
