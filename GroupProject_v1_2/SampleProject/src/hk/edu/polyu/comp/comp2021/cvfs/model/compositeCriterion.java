package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.io.Serializable;

public class compositeCriterion extends Criterion implements Serializable {
    private Criterion cri1;
    private Criterion cri2;
    private String logicOp;
    private boolean isNegation;
    public compositeCriterion(String criName1,Criterion cri1,Criterion cri2,String logicOp,boolean isNegation) {
        super(criName1,null,null);
        this.cri1=cri1;
        this.cri2 = cri2;
        this.logicOp=logicOp;
        this.isNegation=isNegation;
    }
    @Override
    public boolean evaluate(File f){
        boolean result1 = cri1.evaluate(f);
        //cri2 不为空的时候返回cri2 evaluate的结果，否则赋值true
        boolean result2 = cri2 != null ? cri2.evaluate(f) : true;

        boolean result = result1;
        if(isNegation && logicOp==null) return !result1;
        switch (logicOp) {
            case "&&": result = result1 &&result2; break;
            case "||": result = result1 ||result2; break;
        }
        //isNegation 是True 取反 result，
        //isNegation 是False result
        return result;
    }
    @Override
    public String toString() {
        if(isNegation) return "!("+cri1.toString()+")";
        else return "("+cri1.toString()+logicOp+" "+cri2.toString()+")";
    }
}
