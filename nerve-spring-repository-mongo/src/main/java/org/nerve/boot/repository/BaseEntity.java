package org.nerve.boot.repository;

import org.springframework.data.annotation.Id;

/**
 * Created by zengxm on 2015/10/21 0021.
 */
public abstract class BaseEntity implements IdEntity {
	@Id
	private String id;

	public String getId() {
		return id;
	}

	public BaseEntity setId(String id) {
		this.id = id;
		return this;
	}

	public boolean isUsed(){
		return id != null && id.length()>0;
	}
}
