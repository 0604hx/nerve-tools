package org.nerve.boot.repository;

import java.io.Serializable;

/**
 * Created by zengxm on 2016/1/20 0020.
 */
public interface IdEntity extends Serializable {
	String getId();

	default boolean isUsed(){
		return getId()!=null && getId().length()>0;
	}
}
