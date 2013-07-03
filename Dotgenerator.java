import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Gerador de codigo dot
 * 
 * @author omar
 */
public class Dotgenerator extends CodeGenerator {
	/**
	 * Classe que guarda a informacao de um grafo (ou subgrafo) dot
	 * @author omar
	 *
	 */
	private class DotGraph{ 
		public String generated_code;
		private DotGraph parent;
		public String Name;
		public ArrayList<DotGraph> subGraphs = new ArrayList<DotGraph>();
		public String Nodes ="";
		public String Edges = "";
		public String Attributes = "";
		
		public DotGraph(String name){
			Name = name;
		}
		
		public void addSubGraph(DotGraph graph){
			graph.parent = this;
			subGraphs.add(graph);
		}
		
		public DotGraph parent(){return parent;}
		
		public String printcode(){
			String ret=Name + " {\n";
			for(DotGraph dot:subGraphs){
				ret += dot.printcode();
			}
			return ret + Nodes + Edges + Attributes + "}\n";
		}
		
	}
	
	private final String[] nodeattrs={
			"color","colorscheme",
			"comment","distortion",
			"fillcolor","fixedsize",
			"fontcolor","fontname",
			"fontsize",	"group",
			"height","id","image",
			"imagescale","label","labelloc",
			"layer","margin","nojustify",
			"orientation","penwidth",
			"peripheries","regular",
			"samplepoints",	"shape",
			"sides","skew","style",
			"target","tooltip","URL","width"
	};
	
	private final String[] edgeattrs={
			"arrowhead","arrowsize","arrowtail",
			"color","colorscheme","comment",
			"constraint","decorate","dir","edgeURL",
			"edgehref","edgetarget","edgetooltip","fontcolor",
			"fontname",	"fontsize","headclip","headhref","headlabel",
			"headport",	"headtarget","headtooltip","headURL","href",
			"id","label","labelangle","labeldistance","labelfloat",
			"labelfontcolor","labelfontname","labelfontsize","labelhref",
			"labelURL","labeltarget","labeltooltip","layer","lhead","ltail",
			"minlen","penwidth","samehead","sametail","style","tailclip",
			"tailhref","taillabel","tailport","tailtarget","tailtooltip",
			"tailURL","target","tooltip","weight"
	};
	
	private final String[] literals={
			"strict",
	        "graph",
	        "digraph",
	        "node",
	        "edge",
	        "subgraph"
	};
	
	DotGraph root,currentGraph;
	@Override
	public boolean isValidAttribute(String attributename, int type) {
		switch(type){
		case NODE:
			for(String t:nodeattrs){
				if(t.equals(attributename)) return true;
				};break;
		case EDGE:
			for(String t:edgeattrs){
				if(t.equals(attributename)) return true;
				};break;
		}
		return false;
	}
	

	@Override
	public boolean isValidGraphName(String graphname) {
		for(String t:literals){
			if(t.equalsIgnoreCase(graphname)) return false;
			}
		return true;
	}
	
	

	@Override
	public void createNode(String nodeName, HashMap<String, String> attributes) {
		if(attributes.isEmpty()) return;
		String str = "[";
		for(Entry<String,String> attr:attributes.entrySet()){
			str += attr.getKey() + "=" + attr.getValue() + ",";
		}
		str = str.substring(0, str.length()-1);
		str += "]";
		
		currentGraph.Nodes+=nodeName + " " + str + "\n";
	}

	@Override
	public void createEdge(String EdgeName, String startNodeName,
			String endNodeName, HashMap<String, String> attributes) {
		System.out.println("edge created");
		String nodesStr = startNodeName + " -> " + endNodeName;
		currentGraph.Edges += nodesStr;
			String str = "[label="+EdgeName;
			
			for(Entry<String,String> attr:attributes.entrySet()){
				str += "," +attr.getKey() + "=" + attr.getValue();
			}
			str += "]";
			currentGraph.Edges+= " " + str + "\n";
	}

	@Override
	public void startGraph(String graphname) {
		if(root != null){
			System.err.print("Code generation error: DOT does not allow you to create more than one graph \n");
			System.exit(1);
		}
		root = new DotGraph("digraph "+graphname);
		currentGraph = root;
	}

	@Override
	public void startSubGraph(String SubGraphName) {
		DotGraph sub = new DotGraph("subgraph cluster_"+SubGraphName);
		currentGraph.addSubGraph(sub);
		currentGraph = sub;
	}

	@Override
	public void addGraphAttributes(HashMap<String, String> attributes) {
		for(Entry<String,String> attr:attributes.entrySet()){
			currentGraph.Attributes += attr.getKey() + "=" + attr.getValue() + "\n";
		}

	}

	@Override
	public void endGraph() {

	}

	@Override
	public void endSubGraph() {
		currentGraph = currentGraph.parent();
	}

	@Override
	public String edgeStartLabelAttribute() {
		return "taillabel";
	}

	@Override
	public String edgeEndLabelAttribute() {
		return "headlabel";
	}

	@Override
	public String outputcode() {
		return root.printcode();
	}



}
