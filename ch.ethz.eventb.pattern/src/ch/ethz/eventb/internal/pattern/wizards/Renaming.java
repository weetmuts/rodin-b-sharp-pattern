package ch.ethz.eventb.internal.pattern.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IEvent;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IParameter;
import org.eventb.core.IWitness;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class Renaming<T extends IInternalElement> {
	List<String> parents;
	List<String> sources;
	List<String> renames;
		
	public Renaming() {
		parents = new ArrayList<String>();
		sources = new ArrayList<String>();
		renames = new ArrayList<String>();
	}
	
	public void addPair(String src, String ren) {
		int index = sources.indexOf(src);
		if (index>= 0)
			renames.set(index, ren);
		else {
			sources.add(src);
			renames.add(ren);
		}
	}
	
	public void addPair(String src, String par, String ren) {
		int index;
		for (index = 0 ; index < sources.size() ; index++)
			if (sources.get(index).equals(src) && parents.get(index).equals(par))
				break;
		if (index==sources.size())
			index = -1;
		if (index>= 0)
			renames.set(index, ren);
		else {
			sources.add(src);
			parents.add(par);
			renames.add(ren);
		}
	}
	
	public void addPair(T src, String ren) {
		if (src instanceof IWitness){
			try {
				addPair(((IWitness)src).getLabel(),((IEvent)src.getParent()).getLabel(),ren);
			} catch (RodinDBException e) {
				return;
			}
		}
		else if (src instanceof ILabeledElement){
			try {
				addPair(((ILabeledElement)src).getLabel(),ren);
			} catch (RodinDBException e) {
				return;
			}
		}
		else if (src instanceof IIdentifierElement){
			try {
				addPair(((IIdentifierElement)src).getIdentifierString(),ren);
			} catch (RodinDBException e) {
				return;
			}
		}
		else{
			addPair(src.getElementName(),ren);
		}
	}
	
	public void removePair(String src) {
		int index = sources.indexOf(src);
		if (index>= 0){
			sources.remove(index);
			renames.remove(index);
		}
	}

	public void removePair(String src, String par) {
		int index;
		for (index = 0 ; index < sources.size() ; index++)
			if (sources.get(index).equals(src) && parents.get(index).equals(par))
				break;
		if (index==sources.size())
			index = -1;
		if (index>= 0){
			sources.remove(index);
			renames.remove(index);
		}
	}
	
	public void removePair(T src) {
		if (src instanceof IWitness){
			try {
				removePair(((IWitness)src).getLabel(),((IEvent)src.getParent()).getLabel());
			} catch (RodinDBException e) {
				return;
			}
		}
		else if (src instanceof ILabeledElement){
			try {
				removePair(((ILabeledElement)src).getLabel());
			} catch (RodinDBException e) {
				return;
			}
		}
		else if (src instanceof IIdentifierElement){
			try {
				removePair(((IIdentifierElement)src).getIdentifierString());
			} catch (RodinDBException e) {
				return;
			}
		}
		else
			removePair(src.getElementName());
	}
	
	public String getRenamingOfElement(String src){
		int index = sources.indexOf(src);
		if (index>= 0)
			return renames.get(index);
		else
			return "";
	}
	
	public String getRenamingOfElement(String src, String par){
		int index;
		for (index = 0 ; index < sources.size() ; index++)
			if (sources.get(index).equals(src) && parents.get(index).equals(par))
				break;
		if (index==sources.size())
			index = -1;
		if (index>= 0)
			return renames.get(index);
		else
			return "";
	}
	
	public String getRenamingOfElement(T src){
		if (src instanceof IWitness){
			try {
				return getRenamingOfElement(((IWitness)src).getLabel(),((IEvent)src.getParent()).getLabel());
			} catch (RodinDBException e) {
				return "";
			}
		}
		else if (src instanceof ILabeledElement){
			try {
				return getRenamingOfElement(((ILabeledElement)src).getLabel());
			} catch (RodinDBException e) {
				return "";
			}
		}
		else if (src instanceof IIdentifierElement){
			try {
				return getRenamingOfElement(((IIdentifierElement)src).getIdentifierString());
			} catch (RodinDBException e) {
				return "";
			}
		}
		else
			return getRenamingOfElement(src.getElementName());
	}
	
	public void setRenamingOfElement(String src, String ren){
		int index = sources.indexOf(src);
		if (index>= 0)
			renames.set(index, ren);
	}
	
	public void setRenamingOfElement(String src, String par, String ren){
		int index;
		for (index = 0 ; index < sources.size() ; index++)
			if (sources.get(index).equals(src) && parents.get(index).equals(par))
				break;
		if (index==sources.size())
			index = -1;
		if (index>= 0)
			renames.set(index, ren);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < sources.size(); i++) {
			buf.append("\n");
			buf.append(sources.get(i));
			buf.append(" --> ");
			buf.append(renames.get(i));
		}
		return buf.toString();
	}
	
	public List<String> getSourceList() {
		return sources;
	}
	public List<String> getParentList() {
		return parents;
	}
	
	public List<String> getRenameList() {
		return renames;
	}
	
	public int size(){
		return sources.size();
	}
}