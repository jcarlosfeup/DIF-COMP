import java.util.ArrayList;


/**
 * Nó da Arvore da Representação intermédia
 * criado para facilitar a criação de nós da arvore
 * 
 * neste momento ja nao e preciso guardar informacoes da
 * linha de codigo e outras relacionadas com a verificacao de erros
 * pois por nesta fase o codigo nao deverá ter erros
 * 
 * @author ei08158
 *
 */
public class IRNode {
    private String data;
    private IRNode parent;
    private ArrayList<IRNode> children=new ArrayList<IRNode>();
    
    public IRNode(String data){	this.data=data; }
    public IRNode(String data, IRNode parent){
    	this.data=data;
    	this.parent = parent;
    	parent.children.add(this);
    }
    
    /**
     * Verifica se o nó é uma folha
     * @return true se e folha
     */
    public boolean isleaf(){return children.size()==0;}
    
    /**
     * retorna o pai do nó
     * @return
     */
    public IRNode  parent() { return parent;}
    public String toString(){ return data;  }
    public int numChildren(){return children.size();}
    public ArrayList<IRNode> getChildren(){return children;}

    public void appendNode(IRNode e){
    	if(e == null) return;
    	e.setParent(this);
    	children.add(e);
    }
    
    public void removechild(IRNode e){
    	this.children.remove(e);
    }
    
    public void setNodename(String name){
    	this.data = name;
    }
    
    
    public String dump(String prefix) {
    	String str=prefix +  data+"\n";
		for (IRNode node: children) {
			str+=node.dump(prefix + " ");
      }
		return str;
	}
    
    
	public IRNode getchild(String nodename){
		for(IRNode node:children){
			if(node.data.equals(nodename)) return node;
		}
		return null;
    }
	
	public IRNode getOrCreateChild(String nodename){
		IRNode node = getchild(nodename);
		return (node != null) ? node : new IRNode(nodename,this);
    }
	
	public IRNode getchild(int i){
		return children.get(i);
    }
	
	private void setParent(IRNode newParent){
		if(parent != null){
			parent.children.remove(this);
		}
		parent = newParent;
	}
  
}