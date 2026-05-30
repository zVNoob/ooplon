package com.hashvis.model.collision;

import java.util.ArrayList;

import com.hashvis.model.table.Row;
import com.hashvis.model.table.Table;
import java.util.ArrayList;
import java.util.List;
import com.hashvis.model.hashfunc.*;
import com.hashvis.model.table.*;
abstract class ActionProcessor extends HashResolver{
  	protected String key;
  	protected Table table;

	abstract protected String 	getcurrent_ResolverType();
	abstract protected void 	uniqueInitalize(HashAction action);
	abstract protected Result  	handleTraversal();
	abstract protected Result  	firstStep();
	protected String initalizePseudocode(){	return "step = 0 ; i = base =hash(k,n)";}
	protected ArrayList<String> caseInsert(){
		ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.add("if (keycount == size of HT) stop insertion");
		pseudocode.add("while (HT[i] != EMPTY)");
		pseudocode.add(" if (HT[i] == DELETED) mark the suitable space");
		pseudocode.add(" ++step; if HT[i] == key || step == size of HT , stop insertion");
		pseudocode.add(getcurrent_ResolverType());
		pseudocode.add("found insertion point, insert key at suitable space or HT[i] ");
		return pseudocode;
	}
	protected ArrayList<String> caseDelete(){
		ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.add("while (HT[i] != EMPTY)");
		pseudocode.add(" if (HT[i] == key) HT[i] = DELETED ; break");
		pseudocode.add(" ++step; if step == size of HT, stop deletion");
		pseudocode.add(getcurrent_ResolverType());
		pseudocode.add("return 'deletad the target value' ");
		return pseudocode;
	}
	protected ArrayList<String> caseSearch(){
		ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.add("while (HT[i] != EMPTY)");
		pseudocode.add(" if (HT[i] == key) return 'found at index i'");
		pseudocode.add(" ++step; if step == size of HT, stop searching");
		pseudocode.add(getcurrent_ResolverType());
		pseudocode.add("return 'not found' ");
		return pseudocode;
	}
  	protected ArrayList<String> getPseudocode(HashAction action) {
    	ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.add(initalizePseudocode());
        switch (action) {
            case (HashAction.INSERT) -> {pseudocode.addAll(caseInsert());}
            case (HashAction.DELETE) -> {pseudocode.addAll(caseDelete());}
            case (HashAction.SEARCH) -> {pseudocode.addAll(caseSearch());}
            default  -> {return new ArrayList<String>();}
        }
    	return pseudocode;
  	}
	@Override
	public List<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
		this.key = key;
		this.table = table;
		return getAlgorithm(action);
	}
	protected ArrayList<String> getAlgorithm(HashAction action) {
		uniqueInitalize(action);
	    return getPseudocode(action);
	}
	@Override
  	public Result nextStep() {
		System.out.println("1");
    	Result initialization = firstStep();
    	if(initialization!=null){
      		return initialization;
    	}
    	return handleTraversal();
  	}
}