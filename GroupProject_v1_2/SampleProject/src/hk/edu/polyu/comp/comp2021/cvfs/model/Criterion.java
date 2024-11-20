package hk.edu.polyu.comp.comp2021.cvfs.model;
import java.io.Serializable;

public class Criterion implements Serializable {
    private static final long SerialVersionUID = 1L;
    private String attrName;
    private String op;
    private String val;

    public Criterion(String attrName, String op,String val) {
        this.attrName = attrName;
        this.op = op;
        this.val = val;
    }

    public boolean evaluate(File f){
        switch(attrName){
            case "name": return evaluateName(f);
            case"type": return evluateType(f);
            case"size": return evaluateSize(f);
            default: return false;
        }
    }
    
    //检查是否包含字符串
    private boolean evaluateName(File f){
        if(op.equals("contains")) return f.getName().contains(val);
        return false;
    }
    //检查是否是该类型的文件
    private boolean evluateType(File f){
        if(op.equals("equals")&& isDocument(f)) return f.getType().contains(val);
        return false;
    }

    //检查大小是否匹配对应的表达式
    private boolean evaluateSize(File f){
        int f_size= f.getSize();
        int crit_size = Integer.parseInt(val);
        switch (op) {
            case ">":
                return f_size> crit_size;
            case "<":
                return f_size < crit_size;
            case ">=":
                return f_size >= crit_size;
            case "<=":
                return f_size <= crit_size;
            case "==":
                return f_size == crit_size;
            case "!=":
                return f_size != crit_size;
            default:
                return false;
        }
    }
    public static boolean isDocument(File f){return f instanceof Document;}

    @Override
    public String toString() {
        return "Criterion{"+"attrName="+attrName+",op="+op+",val="+val+'}';
    }
}
